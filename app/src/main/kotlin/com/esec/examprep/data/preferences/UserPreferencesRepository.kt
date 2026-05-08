package com.esec.examprep.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
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
            language = runCatching {
                AppLanguage.valueOf(p[KEY_LANGUAGE] ?: AppLanguage.SYSTEM.name)
            }.getOrDefault(AppLanguage.SYSTEM),
        )
    }

    val activeProfileId: Flow<String?> = dataStore.data.map { it[KEY_ACTIVE_PROFILE] }

    val parentPinHash: Flow<String?> = dataStore.data.map { it[KEY_PARENT_PIN_HASH] }

    suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { it[KEY_THEME] = mode.name }
    }

    suspend fun setExamLength(n: Int) {
        dataStore.edit { it[KEY_LENGTH] = n }
    }

    suspend fun setTimerMinutes(n: Int) {
        dataStore.edit { it[KEY_TIMER] = n }
    }

    suspend fun setLanguage(lang: AppLanguage) {
        dataStore.edit { it[KEY_LANGUAGE] = lang.name }
    }

    suspend fun setActiveProfileId(id: String?) {
        dataStore.edit {
            if (id == null) it.remove(KEY_ACTIVE_PROFILE) else it[KEY_ACTIVE_PROFILE] = id
        }
    }

    suspend fun getActiveProfileId(): String? = activeProfileId.first()

    suspend fun getParentPinHash(): String? = parentPinHash.first()

    suspend fun setParentPinHash(hash: String?) {
        dataStore.edit {
            if (hash == null) it.remove(KEY_PARENT_PIN_HASH) else it[KEY_PARENT_PIN_HASH] = hash
        }
    }

    suspend fun getQuestionBankVersion(): Int =
        dataStore.data.map { it[KEY_BANK_VERSION] ?: 0 }.first()

    suspend fun setQuestionBankVersion(version: Int) {
        dataStore.edit { it[KEY_BANK_VERSION] = version }
    }

    private companion object {
        val KEY_THEME = stringPreferencesKey("theme_mode")
        val KEY_LENGTH = intPreferencesKey("default_exam_length")
        val KEY_TIMER = intPreferencesKey("default_timer_minutes")
        val KEY_BANK_VERSION = intPreferencesKey("question_bank_version")
        val KEY_ACTIVE_PROFILE = stringPreferencesKey("active_profile_id")
        val KEY_LANGUAGE = stringPreferencesKey("app_language")
        val KEY_PARENT_PIN_HASH = stringPreferencesKey("parent_pin_hash")
    }
}
