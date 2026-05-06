package com.esec.examprep.domain.model

import java.time.Instant

data class ExamResult(
    val sessionId: String,
    val subjectId: String,
    val subjectName: String,
    val totalQuestions: Int,
    val correctAnswers: Int,
    val incorrectAnswers: Int,
    val skippedAnswers: Int,
    val scorePercent: Float,
    val passed: Boolean,
    val durationSeconds: Long,
    val completedAt: Instant,
    val questionBreakdown: List<QuestionResult>,
)

data class QuestionResult(
    val question: Question,
    val selectedOptionId: String?,
    val isCorrect: Boolean,
)
