package com.esec.examprep.presentation.exam

import com.esec.examprep.domain.model.ExamMode
import com.esec.examprep.domain.model.Question

data class ExamState(
    val questions: List<Question>        = emptyList(),
    val currentIndex: Int                = 0,
    val answers: Map<String, String>     = emptyMap(),   // questionId → selectedOptionId
    val mode: ExamMode                   = ExamMode.PRACTICE,
    val timeLimitSeconds: Int            = DEFAULT_TIME_LIMIT,
    val remainingSeconds: Int            = DEFAULT_TIME_LIMIT,
    val isFinished: Boolean              = false,
    val isLoading: Boolean               = true,
    val resultSessionId: String?         = null,
    val showExitDialog: Boolean          = false,
    val showReviewDialog: Boolean        = false,
    val subjectName: String              = "",
    val year: Int?                       = null,
) {
    val currentQuestion get() = questions.getOrNull(currentIndex)
    val progress        get() = if (questions.isEmpty()) 0f else (currentIndex + 1f) / questions.size
    val answeredCount   get() = answers.size
    val isLastQuestion  get() = currentIndex == questions.lastIndex
    val isTimedMode     get() = mode == ExamMode.TIMED

    companion object {
        const val DEFAULT_TIME_LIMIT = 60 * 40  // 40 minutes
    }
}
