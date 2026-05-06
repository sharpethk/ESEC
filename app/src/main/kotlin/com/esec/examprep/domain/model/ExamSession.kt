package com.esec.examprep.domain.model

import java.time.Instant

data class ExamSession(
    val id: String,
    val subjectId: String,
    val mode: ExamMode,
    val questions: List<Question>,
    val answers: Map<String, String>,          // questionId -> selectedOptionId
    val startedAt: Instant,
    val finishedAt: Instant?,
    val timeLimitSeconds: Int?,
)

enum class ExamMode { TIMED, PRACTICE }
