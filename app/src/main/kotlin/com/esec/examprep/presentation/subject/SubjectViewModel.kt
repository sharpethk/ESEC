package com.esec.examprep.presentation.subject

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esec.examprep.domain.model.Subject
import com.esec.examprep.domain.model.YearStat
import com.esec.examprep.domain.usecase.GetSubjectsUseCase
import com.esec.examprep.domain.usecase.GetYearStatsForSubjectUseCase
import com.esec.examprep.presentation.common.ActiveProfileHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SubjectViewModel @Inject constructor(
    getSubjects: GetSubjectsUseCase,
    private val getYearStats: GetYearStatsForSubjectUseCase,
    activeProfile: ActiveProfileHolder,
) : ViewModel() {

    val subjects = activeProfile.activeProfile
        .flatMapLatest { profile ->
            if (profile == null) flowOf(emptyList<Subject>())
            else getSubjects(profile.examCategory)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _yearStats = MutableStateFlow<Map<String, List<YearStat>>>(emptyMap())
    val yearStats = _yearStats.asStateFlow()

    fun loadYearStats(subjectId: String) {
        if (_yearStats.value.containsKey(subjectId)) return
        viewModelScope.launch {
            val stats = getYearStats(subjectId)
            _yearStats.update { it + (subjectId to stats) }
        }
    }
}
