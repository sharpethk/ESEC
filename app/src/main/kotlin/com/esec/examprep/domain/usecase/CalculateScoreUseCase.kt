package com.esec.examprep.domain.usecase

import com.esec.examprep.domain.model.ExamResult
import com.esec.examprep.domain.model.ExamSession
import com.esec.examprep.domain.model.QuestionResult
import java.time.Instant
import javax.inject.Inject

private const val PASS_THRESHOLD = 50f

class CalculateScoreUseCase @Inject constructor() {
    operator fun invoke(session: ExamSession, subjectName: String): ExamResult {
        val breakdown = session.questions.map { q ->
            val selected = session.answers[q.id]
            QuestionResult(
                question = q,
                selectedOptionId = selected,
                isCorrect = selected == q.correctOptionId,
            )
        }
        val correct = breakdown.count { it.isCorrect }
        val skipped = breakdown.count { it.selectedOptionId == null }
        val total = session.questions.size
        val score = if (total > 0) (correct.toFloat() / total) * 100f else 0f
        val duration = session.finishedAt?.epochSecond?.minus(session.startedAt.epochSecond) ?: 0L

        return ExamResult(
            sessionId = session.id,
            subjectId = session.subjectId,
            subjectName = subjectName,
            totalQuestions = total,
            correctAnswers = correct,
            incorrectAnswers = total - correct - skipped,
            skippedAnswers = skipped,
            scorePercent = score,
            passed = score >= PASS_THRESHOLD,
            durationSeconds = duration,
            completedAt = session.finishedAt ?: Instant.now(),
            questionBreakdown = breakdown,
            year = session.year,
        )
    }
}
