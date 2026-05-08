package com.esec.examprep.presentation.exam

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esec.examprep.data.preferences.UserPreferencesRepository
import com.esec.examprep.domain.model.ExamMode
import com.esec.examprep.domain.model.ExamSession
import com.esec.examprep.domain.model.ExamCategory
import com.esec.examprep.domain.usecase.BuildPracticeExamUseCase
import com.esec.examprep.domain.usecase.CompleteDailyChallengeUseCase
import com.esec.examprep.domain.usecase.GetQuestionsForExamUseCase
import com.esec.examprep.domain.usecase.GetSubjectsUseCase
import com.esec.examprep.domain.usecase.GetWrongAnswerQuestionsUseCase
import com.esec.examprep.domain.usecase.SubmitExamUseCase
import com.esec.examprep.domain.usecase.ToggleBookmarkUseCase
import com.esec.examprep.presentation.common.ActiveProfileHolder
import com.esec.examprep.presentation.common.DailyChallengeRunHolder
import com.esec.examprep.presentation.common.PracticeConfigHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ExamViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getQuestions: GetQuestionsForExamUseCase,
    private val getSubjects: GetSubjectsUseCase,
    private val submitExam: SubmitExamUseCase,
    private val toggleBookmarkUseCase: ToggleBookmarkUseCase,
    private val getWrongAnswerQuestions: GetWrongAnswerQuestionsUseCase,
    private val buildPracticeExam: BuildPracticeExamUseCase,
    private val practiceConfigHolder: PracticeConfigHolder,
    private val dailyChallengeRunHolder: DailyChallengeRunHolder,
    private val completeDailyChallenge: CompleteDailyChallengeUseCase,
    private val prefsRepo: UserPreferencesRepository,
    private val activeProfile: ActiveProfileHolder,
) : ViewModel() {

    private val subjectId: String = checkNotNull(savedStateHandle["subjectId"])
    private val modeArg: String   = checkNotNull(savedStateHandle["mode"])
    private val yearArg: Int      = savedStateHandle.get<Int>("year") ?: -1
    private val year: Int?        = yearArg.takeIf { it > 0 }

    private val _state = MutableStateFlow(ExamState())
    val state = _state.asStateFlow()

    private var timerJob: Job? = null
    private val sessionId = UUID.randomUUID().toString()
    private val startedAt = Instant.now()
    private lateinit var profileIdSnapshot: String
    private lateinit var categorySnapshot: ExamCategory
    private var dailyChallengeDate: java.time.LocalDate? = null

    init {
        loadQuestions()
    }

    private fun loadQuestions() {
        viewModelScope.launch {
            val profile = activeProfile.activeProfile.value
                ?: run {
                    _state.update { it.copy(isLoading = false) }
                    return@launch
                }
            profileIdSnapshot = profile.id
            categorySnapshot = profile.examCategory

            val mode = runCatching { ExamMode.valueOf(modeArg) }.getOrDefault(ExamMode.PRACTICE)
            val prefs = prefsRepo.preferences.first()
            val questions = when (mode) {
                ExamMode.REVIEW -> {
                    val filter = subjectId.takeUnless { it == ALL_SUBJECTS_TOKEN }
                    getWrongAnswerQuestions(profileIdSnapshot, filter)
                }
                ExamMode.PRACTICE_CUSTOM -> {
                    val cfg = practiceConfigHolder.take()
                    if (cfg != null) buildPracticeExam(profileIdSnapshot, cfg) else emptyList()
                }
                ExamMode.DAILY -> {
                    val run = dailyChallengeRunHolder.take()
                    if (run != null) {
                        dailyChallengeDate = run.date
                        run.questions
                    } else emptyList()
                }
                else -> getQuestions(profileIdSnapshot, subjectId, count = prefs.defaultExamLength, year = year)
            }
            val subjectName = when (mode) {
                ExamMode.REVIEW -> ""
                ExamMode.PRACTICE_CUSTOM -> ""
                ExamMode.DAILY -> ""
                else -> getSubjects(categorySnapshot).first().firstOrNull { it.id == subjectId }?.name.orEmpty()
            }
            val timerMinutes = when {
                mode == ExamMode.REVIEW || mode == ExamMode.PRACTICE_CUSTOM || mode == ExamMode.DAILY ->
                    maxOf(prefs.defaultTimerMinutes, questions.size)
                year != null               -> maxOf(prefs.defaultTimerMinutes, questions.size)
                prefs.defaultExamLength <= 0 -> maxOf(prefs.defaultTimerMinutes, questions.size)
                else                        -> prefs.defaultTimerMinutes
            }
            val timerSeconds = timerMinutes * 60
            _state.update {
                it.copy(
                    questions = questions,
                    mode = mode,
                    isLoading = false,
                    timeLimitSeconds = timerSeconds,
                    remainingSeconds = timerSeconds,
                    subjectName = subjectName,
                    year = year,
                )
            }
            if (mode == ExamMode.TIMED) startTimer()
        }
    }

    fun selectAnswer(optionId: String) {
        val questionId = _state.value.currentQuestion?.id ?: return
        _state.update { it.copy(answers = it.answers + (questionId to optionId)) }
    }

    fun nextQuestion() {
        _state.update { s ->
            if (s.currentIndex < s.questions.lastIndex)
                s.copy(currentIndex = s.currentIndex + 1)
            else s
        }
    }

    fun previousQuestion() {
        _state.update { s ->
            if (s.currentIndex > 0) s.copy(currentIndex = s.currentIndex - 1) else s
        }
    }

    fun jumpToQuestion(index: Int) {
        _state.update { it.copy(currentIndex = index.coerceIn(0, it.questions.lastIndex)) }
    }

    fun submitExam() {
        timerJob?.cancel()
        viewModelScope.launch {
            val s = _state.value
            val session = ExamSession(
                id               = sessionId,
                profileId        = profileIdSnapshot,
                subjectId        = subjectId,
                mode             = s.mode,
                questions        = s.questions,
                answers          = s.answers,
                startedAt        = startedAt,
                finishedAt       = Instant.now(),
                timeLimitSeconds = if (s.isTimedMode) s.timeLimitSeconds else null,
                year             = year,
            )
            val result = submitExam(session)
            val dailyDate = dailyChallengeDate
            if (s.mode == ExamMode.DAILY && dailyDate != null) {
                val correct = s.questions.count { q ->
                    s.answers[q.id] != null && s.answers[q.id] == q.correctOptionId
                }
                val pct = if (s.questions.isEmpty()) 0f else correct * 100f / s.questions.size
                val durationSeconds = ((Instant.now().toEpochMilli() - startedAt.toEpochMilli()) / 1000).toInt()
                completeDailyChallenge(profileIdSnapshot, dailyDate, pct, durationSeconds)
            }
            _state.update { it.copy(isFinished = true, resultSessionId = result.sessionId) }
        }
    }

    fun showExitDialog(show: Boolean) = _state.update { it.copy(showExitDialog = show) }

    fun showReviewDialog(show: Boolean) = _state.update { it.copy(showReviewDialog = show) }

    fun toggleBookmark(questionId: String) {
        val newValue = _state.value.questions.firstOrNull { it.id == questionId }
            ?.let { !it.isBookmarked } ?: return
        _state.update { s ->
            s.copy(questions = s.questions.map {
                if (it.id == questionId) it.copy(isBookmarked = newValue) else it
            })
        }
        viewModelScope.launch { toggleBookmarkUseCase(profileIdSnapshot, questionId, newValue) }
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (_state.value.remainingSeconds > 0 && !_state.value.isFinished) {
                delay(1_000)
                _state.update { it.copy(remainingSeconds = it.remainingSeconds - 1) }
            }
            if (_state.value.remainingSeconds <= 0) submitExam()
        }
    }

    override fun onCleared() {
        timerJob?.cancel()
        super.onCleared()
    }

    companion object {
        const val ALL_SUBJECTS_TOKEN = "all"
    }
}
