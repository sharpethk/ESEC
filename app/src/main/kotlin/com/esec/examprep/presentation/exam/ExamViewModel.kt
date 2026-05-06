package com.esec.examprep.presentation.exam

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esec.examprep.domain.model.ExamMode
import com.esec.examprep.domain.model.ExamSession
import com.esec.examprep.domain.usecase.GetQuestionsForExamUseCase
import com.esec.examprep.domain.usecase.SubmitExamUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ExamViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getQuestions: GetQuestionsForExamUseCase,
    private val submitExam: SubmitExamUseCase,
) : ViewModel() {

    private val subjectId: String = checkNotNull(savedStateHandle["subjectId"])
    private val modeArg: String   = checkNotNull(savedStateHandle["mode"])

    private val _state = MutableStateFlow(ExamState())
    val state = _state.asStateFlow()

    private var timerJob: Job? = null
    private val sessionId = UUID.randomUUID().toString()
    private val startedAt = Instant.now()

    init {
        loadQuestions()
    }

    private fun loadQuestions() {
        viewModelScope.launch {
            val mode = runCatching { ExamMode.valueOf(modeArg) }.getOrDefault(ExamMode.PRACTICE)
            val questions = getQuestions(subjectId, count = 40)
            _state.update {
                it.copy(
                    questions = questions,
                    mode = mode,
                    isLoading = false,
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
                subjectId        = subjectId,
                mode             = s.mode,
                questions        = s.questions,
                answers          = s.answers,
                startedAt        = startedAt,
                finishedAt       = Instant.now(),
                timeLimitSeconds = if (s.isTimedMode) s.timeLimitSeconds else null,
            )
            val result = submitExam(session)
            _state.update { it.copy(isFinished = true, resultSessionId = result.sessionId) }
        }
    }

    fun showExitDialog(show: Boolean) = _state.update { it.copy(showExitDialog = show) }

    fun showReviewDialog(show: Boolean) = _state.update { it.copy(showReviewDialog = show) }

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
}
