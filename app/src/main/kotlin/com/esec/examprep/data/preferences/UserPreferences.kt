package com.esec.examprep.data.preferences

enum class ThemeMode { LIGHT, DARK, SYSTEM }

data class UserPreferences(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val defaultExamLength: Int = 40,
    val defaultTimerMinutes: Int = 30,
)
