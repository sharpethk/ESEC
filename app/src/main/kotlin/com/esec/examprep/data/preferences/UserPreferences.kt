package com.esec.examprep.data.preferences

enum class ThemeMode { LIGHT, DARK, SYSTEM }

enum class AppLanguage(val tag: String, val label: String) {
    SYSTEM("", "System"),
    ENGLISH("en", "English"),
    TIGRINYA("ti", "ትግርኛ"),
}

data class UserPreferences(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val defaultExamLength: Int = 40,
    val defaultTimerMinutes: Int = 30,
    val language: AppLanguage = AppLanguage.SYSTEM,
    val remindersEnabled: Boolean = false,
    val reminderHour: Int = 18,
    val reminderMinute: Int = 0,
)
