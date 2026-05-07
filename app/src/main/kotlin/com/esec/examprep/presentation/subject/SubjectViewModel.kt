package com.esec.examprep.presentation.subject

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esec.examprep.domain.model.YearStat
import com.esec.examprep.domain.usecase.GetSubjectsUseCase
import com.esec.examprep.domain.usecase.GetYearStatsForSubjectUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubjectViewModel @Inject constructor(
    getSubjects: GetSubjectsUseCase,
    private val getYearStats: GetYearStatsForSubjectUseCase,
) : ViewModel() {

    val subjects = getSubjects()
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
