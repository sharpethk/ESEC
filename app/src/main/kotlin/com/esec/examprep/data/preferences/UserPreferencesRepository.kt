package com.esec.examprep.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    val preferences: Flow<UserPreferences> = dataStore.data.map { p ->
        UserPreferences(
            themeMode = runCatching {
                ThemeMode.valueOf(p[KEY_THEME] ?: ThemeMode.SYSTEM.name)
            }.getOrDefault(ThemeMode.SYSTEM),
            defaultExamLength = p[KEY_LENGTH] ?: 40,
            defaultTimerMinutes = p[KEY_TIMER] ?: 30,
        )
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { it[KEY_THEME] = mode.name }
    }

    suspend fun setExamLength(n: Int) {
        dataStore.edit { it[KEY_LENGTH] = n }
    }

    suspend fun setTimerMinutes(n: Int) {
        dataStore.edit { it[KEY_TIMER] = n }
    }

    private companion object {
        val KEY_THEME = stringPreferencesKey("theme_mode")
        val KEY_LENGTH = intPreferencesKey("default_exam_length")
        val KEY_TIMER = intPreferencesKey("default_timer_minutes")
    }
}
