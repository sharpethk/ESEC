package com.esec.examprep.domain.model

import java.time.LocalDate

data class DailyChallenge(
    val date: LocalDate,
    val questions: List<Question>,
    val completedAt: java.time.Instant? = null,
    val scorePercent: Float? = null,
) {
    val isCompleted: Boolean get() = completedAt != null
}
