package com.esec.examprep.domain.model

data class SubjectTrend(
    val subjectId: String,
    val recentScores: List<Float>,
)
