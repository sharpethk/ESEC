package com.esec.examprep.presentation.settings

import com.esec.examprep.data.preferences.UserPreferences

data class SettingsState(
    val preferences: UserPreferences = UserPreferences(),
    val isLoading: Boolean = true,
    val showClearHistoryDialog: Boolean = false,
)
