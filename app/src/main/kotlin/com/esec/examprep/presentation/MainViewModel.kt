package com.esec.examprep.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esec.examprep.data.preferences.UserPreferences
import com.esec.examprep.data.preferences.UserPreferencesRepository
import com.esec.examprep.domain.model.Profile
import com.esec.examprep.domain.repository.ProfileRepository
import com.esec.examprep.domain.usecase.EnsureDataLoadedUseCase
import com.esec.examprep.presentation.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    prefs: UserPreferencesRepository,
    private val profileRepo: ProfileRepository,
    private val ensureDataLoaded: EnsureDataLoadedUseCase,
) : ViewModel() {

    val preferences: StateFlow<UserPreferences> = prefs.preferences.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = UserPreferences(),
    )

    private val _startDestination = MutableStateFlow<String?>(null)
    val startDestination: StateFlow<String?> = _startDestination.asStateFlow()

    init {
        viewModelScope.launch {
            // Seed the question bank before mounting the nav graph so any tab
            // (Exam included) sees a populated DB on first navigation.
            runCatching { ensureDataLoaded() }
                .onFailure { Log.e("MainViewModel", "ensureDataLoaded failed", it) }

            val profiles = profileRepo.observeProfiles().first()
            val active = profileRepo.getActiveProfile()
            // Auto-select the only profile *before* exposing startDestination,
            // so ExamViewModel never sees a null active profile on first launch.
            if (profiles.size == 1) {
                profileRepo.setActiveProfile(profiles.first().id)
            }
            _startDestination.value = decideStart(profiles, active)
        }
    }

    private fun decideStart(profiles: List<Profile>, active: Profile?): String = when {
        profiles.isEmpty() -> Screen.Home.route
        profiles.size == 1 -> Screen.Home.route
        active != null && profiles.any { it.id == active.id && !it.hasPin } -> Screen.Home.route
        else -> Screen.ProfilePicker.route
    }
}
