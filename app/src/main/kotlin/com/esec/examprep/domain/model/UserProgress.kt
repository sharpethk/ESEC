package com.esec.examprep.domain.model

data class UserProgress(
    val subjectId: String,
    val subjectName: String,
    val totalAttempts: Int,
    val bestScore: Float,
    val averageScore: Float,
    val totalQuestionsAttempted: Int,
    val totalCorrect: Int,
)
