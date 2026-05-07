package com.esec.examprep.domain.model

import java.time.Instant

/**
 * One entry in the Wrong Answer Notebook: a question whose most recent attempt
 * for the active profile was incorrect.
 */
data class WrongAnswerEntry(
    val question: Question,
    val lastAttemptedAt: Instant,
    val attemptCount: Int,
    val lastSelectedOptionId: String?,
)
