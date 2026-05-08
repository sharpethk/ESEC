package com.esec.examprep.domain.model

data class ParentProfileSummary(
    val profile: Profile,
    val totalExams: Int,
    val avgScorePercent: Float,
    val weightedGpa: Float,
    val streakDays: Int,
    val recentExams: List<ExamResult>,
    val weakSubjects: List<WeakTopic>,
    val avgSecondsPerQuestion: Double,
)
