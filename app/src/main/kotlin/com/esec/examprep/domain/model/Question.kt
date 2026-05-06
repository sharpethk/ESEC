package com.esec.examprep.domain.model

data class Question(
    val id: String,
    val subjectId: String,
    val year: Int,
    val text: String,
    val options: List<Option>,
    val correctOptionId: String,
    val explanation: String?,
    val difficultyLevel: DifficultyLevel,
    val isBookmarked: Boolean = false,
)

enum class DifficultyLevel { EASY, MEDIUM, HARD }
