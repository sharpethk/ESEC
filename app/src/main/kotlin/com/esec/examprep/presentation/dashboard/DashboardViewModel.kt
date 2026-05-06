package com.esec.examprep.presentation.dashboard

import android.util.Log
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
import kotlinx.coroutines.flow.catch
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
                .catch { e ->
                    Log.e(TAG, "Dashboard flow failed", e)
                    _state.update { it.copy(isLoading = false) }
                }
                .collect { (p, w) ->
                    _state.update { it.copy(progress = p, weakTopics = w, isLoading = false) }
                }
        }
        refreshOneShots()
    }

    private fun refreshOneShots() {
        viewModelScope.launch {
            val recent = runCatching { getRecentExams(limit = 10) }
                .onFailure { Log.e(TAG, "getRecentExams failed", it) }
                .getOrDefault(emptyList())
            val avgTime = runCatching { getTimeStats() }
                .onFailure { Log.e(TAG, "getTimeStats failed", it) }
                .getOrDefault(0.0)
            _state.update { it.copy(recent = recent, avgTimePerQuestion = avgTime) }
        }
    }

    fun clearAllProgress() {
        viewModelScope.launch {
            runCatching { repository.clearAllProgress() }
                .onFailure { Log.e(TAG, "clearAllProgress failed", it) }
            _state.update { it.copy(recent = emptyList(), avgTimePerQuestion = 0.0) }
        }
    }

    private companion object {
        const val TAG = "DashboardViewModel"
    }
}
