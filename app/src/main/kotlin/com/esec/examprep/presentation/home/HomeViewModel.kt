package com.esec.examprep.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esec.examprep.domain.model.DailyChallenge
import com.esec.examprep.domain.usecase.EnsureDataLoadedUseCase
import com.esec.examprep.domain.usecase.GetTodayChallengeUseCase
import com.esec.examprep.domain.usecase.ObserveStreakUseCase
import com.esec.examprep.presentation.common.ActiveProfileHolder
import com.esec.examprep.presentation.common.DailyChallengeRunHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val ensureDataLoaded: EnsureDataLoadedUseCase,
    activeProfile: ActiveProfileHolder,
    getTodayChallenge: GetTodayChallengeUseCase,
    observeStreak: ObserveStreakUseCase,
    private val dailyChallengeRunHolder: DailyChallengeRunHolder,
) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    val todayChallenge = activeProfile.activeProfile
        .flatMapLatest { p -> if (p == null) flowOf(null) else getTodayChallenge(p.id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val streak = activeProfile.activeProfile
        .flatMapLatest { p -> if (p == null) flowOf(0) else observeStreak(p.id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            runCatching { ensureDataLoaded() }
                .onFailure { _error.value = it.message }
                .also { _isLoading.value = false }
        }
    }

    fun retryLoad() {
        _error.value = null
        _isLoading.value = true
        loadData()
    }

    /** Stages today's challenge for ExamScreen. Returns true if a run is ready. */
    fun stageDailyChallenge(): Boolean {
        val challenge: DailyChallenge = todayChallenge.value ?: return false
        if (challenge.questions.isEmpty()) return false
        dailyChallengeRunHolder.set(
            DailyChallengeRunHolder.Run(date = challenge.date, questions = challenge.questions)
        )
        return true
    }
}
