package com.esec.examprep.domain.model

import java.time.Instant

data class ExamSession(
    val id: String,
    val profileId: String,
    val subjectId: String,
    val mode: ExamMode,
    val questions: List<Question>,
    val answers: Map<String, String>,          // questionId -> selectedOptionId
    val startedAt: Instant,
    val finishedAt: Instant?,
    val timeLimitSeconds: Int?,
    val year: Int? = null,
)

enum class ExamMode { TIMED, PRACTICE, REVIEW, PRACTICE_CUSTOM, DAILY }
