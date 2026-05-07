package com.esec.examprep.presentation.dashboard

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esec.examprep.domain.model.UserProgress
import com.esec.examprep.domain.model.WeakTopic
import com.esec.examprep.domain.repository.ExamSessionRepository
import com.esec.examprep.domain.usecase.GetProgressUseCase
import com.esec.examprep.domain.usecase.GetRecentExamsUseCase
import com.esec.examprep.domain.usecase.GetTimeStatsUseCase
import com.esec.examprep.domain.usecase.GetWeakTopicsUseCase
import com.esec.examprep.presentation.common.ActiveProfileHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private data class DashFlows(val progress: List<UserProgress>, val weak: List<WeakTopic>)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getProgress: GetProgressUseCase,
    private val getWeakTopics: GetWeakTopicsUseCase,
    private val getRecentExams: GetRecentExamsUseCase,
    private val getTimeStats: GetTimeStatsUseCase,
    private val repository: ExamSessionRepository,
    private val activeProfile: ActiveProfileHolder,
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            activeProfile.activeProfile
                .flatMapLatest { profile ->
                    if (profile == null) {
                        flowOf(DashFlows(emptyList(), emptyList()))
                    } else {
                        combine(getProgress(profile.id), getWeakTopics(profile.id)) { p, w ->
                            DashFlows(p, w)
                        }
                    }
                }
                .catch { e ->
                    Log.e(TAG, "Dashboard flow failed", e)
                    _state.update { it.copy(isLoading = false) }
                }
                .collect { d ->
                    _state.update { it.copy(progress = d.progress, weakTopics = d.weak, isLoading = false) }
                    refreshOneShots()
                }
        }
    }

    private fun refreshOneShots() {
        val profileId = activeProfile.activeProfile.value?.id ?: return
        viewModelScope.launch {
            val recent = runCatching { getRecentExams(profileId, limit = 10) }
                .onFailure { Log.e(TAG, "getRecentExams failed", it) }
                .getOrDefault(emptyList())
            val avgTime = runCatching { getTimeStats(profileId) }
                .onFailure { Log.e(TAG, "getTimeStats failed", it) }
                .getOrDefault(0.0)
            _state.update { it.copy(recent = recent, avgTimePerQuestion = avgTime) }
        }
    }

    fun clearAllProgress() {
        val profileId = activeProfile.activeProfile.value?.id ?: return
        viewModelScope.launch {
            runCatching { repository.clearAllProgress(profileId) }
                .onFailure { Log.e(TAG, "clearAllProgress failed", it) }
            _state.update { it.copy(recent = emptyList(), avgTimePerQuestion = 0.0) }
        }
    }

    private companion object {
        const val TAG = "DashboardViewModel"
    }
}
