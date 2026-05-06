package com.esec.examprep.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esec.examprep.domain.repository.ExamSessionRepository
import com.esec.examprep.domain.usecase.GetProgressUseCase
import com.esec.examprep.domain.usecase.GetRecentExamsUseCase
import com.esec.examprep.domain.usecase.GetTimeStatsUseCase
import com.esec.examprep.domain.usecase.GetWeakTopicsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    getProgress: GetProgressUseCase,
    getWeakTopics: GetWeakTopicsUseCase,
    private val getRecentExams: GetRecentExamsUseCase,
    private val getTimeStats: GetTimeStatsUseCase,
    private val repository: ExamSessionRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(getProgress(), getWeakTopics()) { p, w -> p to w }
                .collect { (p, w) ->
                    _state.update { it.copy(progress = p, weakTopics = w, isLoading = false) }
                }
        }
        refreshOneShots()
    }

    private fun refreshOneShots() {
        viewModelScope.launch {
            val recent = getRecentExams(limit = 10)
            val avgTime = getTimeStats()
            _state.update { it.copy(recent = recent, avgTimePerQuestion = avgTime) }
        }
    }

    fun clearAllProgress() {
        viewModelScope.launch {
            repository.clearAllProgress()
            _state.update { it.copy(recent = emptyList(), avgTimePerQuestion = 0.0) }
        }
    }
}
