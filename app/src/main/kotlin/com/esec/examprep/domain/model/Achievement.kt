package com.esec.examprep.domain.model

import java.time.Instant

/**
 * Static achievement catalog. The enum name is the persisted [code]; titles/descriptions live in code
 * for now (can be moved to string resources later). New entries can only be appended.
 */
enum class AchievementCode(
    val title: String,
    val description: String,
    val iconKey: String,
) {
    FIRST_EXAM(
        title = "First step",
        description = "Complete your first exam.",
        iconKey = "school",
    ),
    HUNDRED_QUESTIONS(
        title = "Century",
        description = "Answer 100 questions.",
        iconKey = "numbers",
    ),
    TEN_IN_A_ROW(
        title = "On a roll",
        description = "Answer 10 questions correctly in one exam.",
        iconKey = "trending_up",
    ),
    GPA_3_5(
        title = "Top of the class",
        description = "Reach a weighted GPA of 3.5 or higher.",
        iconKey = "star",
    ),
    ALL_SUBJECTS(
        title = "Well rounded",
        description = "Attempt every subject in your category.",
        iconKey = "auto_awesome",
    ),
    STREAK_7(
        title = "Week strong",
        description = "Keep a 7-day daily-challenge streak.",
        iconKey = "local_fire_department",
    ),
    NOTEBOOK_CLEARED(
        title = "Clean slate",
        description = "Clear every question from your wrong-answer notebook.",
        iconKey = "check_circle",
    ),
}

data class Achievement(
    val code: AchievementCode,
    val unlockedAt: Instant?,
) {
    val isUnlocked: Boolean get() = unlockedAt != null
}
