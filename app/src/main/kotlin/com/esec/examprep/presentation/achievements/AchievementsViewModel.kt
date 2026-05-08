package com.esec.examprep.presentation.achievements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esec.examprep.domain.model.Achievement
import com.esec.examprep.domain.usecase.EvaluateAchievementsUseCase
import com.esec.examprep.domain.usecase.ObserveAchievementsUseCase
import com.esec.examprep.presentation.common.ActiveProfileHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AchievementsState(
    val items: List<Achievement> = emptyList(),
    val isLoading: Boolean = true,
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class AchievementsViewModel @Inject constructor(
    observeAchievements: ObserveAchievementsUseCase,
    private val evaluateAchievements: EvaluateAchievementsUseCase,
    private val activeProfile: ActiveProfileHolder,
) : ViewModel() {

    val state: StateFlow<AchievementsState> =
        activeProfile.activeProfile
            .flatMapLatest { profile ->
                if (profile == null) flowOf(emptyList())
                else observeAchievements(profile.id)
            }
            .map { AchievementsState(items = it, isLoading = false) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AchievementsState())

    init {
        viewModelScope.launch {
            val pid = activeProfile.activeProfile.value?.id ?: return@launch
            runCatching { evaluateAchievements(pid, null) }
        }
    }
}
