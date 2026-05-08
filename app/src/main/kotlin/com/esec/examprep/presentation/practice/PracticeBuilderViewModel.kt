package com.esec.examprep.presentation.practice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esec.examprep.domain.usecase.BuildPracticeExamUseCase
import com.esec.examprep.domain.usecase.GetSubjectsUseCase
import com.esec.examprep.presentation.common.ActiveProfileHolder
import com.esec.examprep.presentation.common.PracticeConfigHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PracticeBuilderViewModel @Inject constructor(
    private val getSubjects: GetSubjectsUseCase,
    private val activeProfile: ActiveProfileHolder,
    private val practiceConfigHolder: PracticeConfigHolder,
) : ViewModel() {

    private val _state = MutableStateFlow(PracticeBuilderState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            activeProfile.activeProfile
                .flatMapLatest { profile ->
                    if (profile == null) flowOf(emptyList()) else getSubjects(profile.examCategory)
                }
                .collect { subjects ->
                    _state.update { it.copy(subjects = subjects, isLoading = false) }
                }
        }
    }

    fun toggleSubject(subjectId: String) {
        _state.update { s ->
            val next = s.selectedSubjectIds.toMutableSet()
            if (!next.add(subjectId)) next.remove(subjectId)
            s.copy(selectedSubjectIds = next)
        }
    }

    fun selectAllSubjects() {
        _state.update { s -> s.copy(selectedSubjectIds = s.subjects.map { it.id }.toSet()) }
    }

    fun clearSubjects() {
        _state.update { it.copy(selectedSubjectIds = emptySet()) }
    }

    fun setEasyPercent(value: Int) {
        _state.update { it.copy(easyPercent = value.coerceIn(0, 100)) }
    }

    fun setMediumPercent(value: Int) {
        _state.update { it.copy(mediumPercent = value.coerceIn(0, 100)) }
    }

    fun setHardPercent(value: Int) {
        _state.update { it.copy(hardPercent = value.coerceIn(0, 100)) }
    }

    fun setCount(value: Int) {
        _state.update { it.copy(count = value.coerceIn(1, 200)) }
    }

    /** Stages the practice config; the navigator should then route to the Exam screen with PRACTICE_CUSTOM mode. */
    fun stageAndStart(): Boolean {
        val s = _state.value
        if (!s.canStart) return false
        practiceConfigHolder.set(
            BuildPracticeExamUseCase.Config(
                subjectIds = s.selectedSubjectIds.toList(),
                easyPercent = s.easyPercent,
                mediumPercent = s.mediumPercent,
                hardPercent = s.hardPercent,
                count = s.count,
            )
        )
        return true
    }
}
