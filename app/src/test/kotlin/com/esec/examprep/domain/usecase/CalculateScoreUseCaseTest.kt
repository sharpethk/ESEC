package com.esec.examprep.domain.usecase

import com.esec.examprep.domain.model.DifficultyLevel
import com.esec.examprep.domain.model.ExamMode
import com.esec.examprep.domain.model.ExamSession
import com.esec.examprep.domain.model.Option
import com.esec.examprep.domain.model.Question
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class CalculateScoreUseCaseTest {

    private val useCase = CalculateScoreUseCase()

    private fun makeQuestion(id: String, correctId: String) = Question(
        id = id, subjectId = "sub1", year = 2023,
        text = "Q $id",
        options = listOf(Option("a", "A"), Option("b", "B"), Option(correctId, "C")),
        correctOptionId = correctId,
        explanation = null,
        difficultyLevel = DifficultyLevel.MEDIUM,
    )

    @Test
    fun `100% correct answers produces score 100 and passed`() {
        val questions = listOf(
            makeQuestion("q1", "a"),
            makeQuestion("q2", "b"),
        )
        val session = ExamSession(
            id = "s1", subjectId = "sub1", mode = ExamMode.TIMED,
            questions = questions, answers = mapOf("q1" to "a", "q2" to "b"),
            startedAt = Instant.now(), finishedAt = Instant.now(), timeLimitSeconds = 600,
        )
        val result = useCase(session, "Math")
        assertEquals(100f, result.scorePercent)
        assertTrue(result.passed)
        assertEquals(2, result.correctAnswers)
        assertEquals(0, result.incorrectAnswers)
        assertEquals(0, result.skippedAnswers)
    }

    @Test
    fun `zero correct answers produces score 0 and failed`() {
        val questions = listOf(makeQuestion("q1", "a"))
        val session = ExamSession(
            id = "s2", subjectId = "sub1", mode = ExamMode.PRACTICE,
            questions = questions, answers = mapOf("q1" to "b"),
            startedAt = Instant.now(), finishedAt = Instant.now(), timeLimitSeconds = null,
        )
        val result = useCase(session, "Physics")
        assertEquals(0f, result.scorePercent)
        assertFalse(result.passed)
    }

    @Test
    fun `skipped questions are counted correctly`() {
        val questions = listOf(makeQuestion("q1", "a"), makeQuestion("q2", "a"))
        val session = ExamSession(
            id = "s3", subjectId = "sub1", mode = ExamMode.TIMED,
            questions = questions, answers = emptyMap(),
            startedAt = Instant.now(), finishedAt = Instant.now(), timeLimitSeconds = 600,
        )
        val result = useCase(session, "History")
        assertEquals(0f, result.scorePercent)
        assertEquals(2, result.skippedAnswers)
        assertFalse(result.passed)
    }
}
