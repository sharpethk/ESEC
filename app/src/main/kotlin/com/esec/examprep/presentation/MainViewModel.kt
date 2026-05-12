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
import kotlinx.coroutines.withTimeoutOrNull
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
            // Hardened launch sequence: every step is failure-tolerant so a bad
            // disk/preferences/decryption state can never leave the splash stuck.
            try {
                // Seed the question bank before mounting the nav graph so any tab
                // (Exam included) sees a populated DB on first navigation. A 25s
                // ceiling guarantees the splash dismisses even on very slow I/O.
                withTimeoutOrNull(25_000L) {
                    runCatching { ensureDataLoaded() }
                        .onFailure { Log.e("MainViewModel", "ensureDataLoaded failed", it) }
                }

                val profiles = runCatching { profileRepo.observeProfiles().first() }
                    .onFailure { Log.e("MainViewModel", "observeProfiles failed", it) }
                    .getOrDefault(emptyList())
                val active = runCatching { profileRepo.getActiveProfile() }
                    .onFailure { Log.e("MainViewModel", "getActiveProfile failed", it) }
                    .getOrNull()

                // Auto-select the only profile *before* exposing startDestination,
                // so ExamViewModel never sees a null active profile on first launch.
                if (profiles.size == 1) {
                    runCatching { profileRepo.setActiveProfile(profiles.first().id) }
                        .onFailure { Log.e("MainViewModel", "setActiveProfile failed", it) }
                }
                _startDestination.value = decideStart(profiles, active)
            } catch (t: Throwable) {
                Log.e("MainViewModel", "Fatal init failure; falling back to Home", t)
                _startDestination.value = Screen.Home.route
            }
        }
    }

    private fun decideStart(profiles: List<Profile>, active: Profile?): String = when {
        profiles.isEmpty() -> Screen.Home.route
        profiles.size == 1 -> Screen.Home.route
        active != null && profiles.any { it.id == active.id && !it.hasPin } -> Screen.Home.route
        else -> Screen.ProfilePicker.route
    }
}
