package com.esec.examprep.data.repository

import com.esec.examprep.data.local.dao.ExamResultDao
import com.esec.examprep.data.local.dao.QuestionAttemptDao
import com.esec.examprep.data.local.entity.QuestionAttemptEntity
import com.esec.examprep.data.mapper.toDomain
import com.esec.examprep.data.mapper.toEntity
import com.esec.examprep.domain.model.ExamResult
import com.esec.examprep.domain.model.ExamSession
import com.esec.examprep.domain.model.SubjectTrend
import com.esec.examprep.domain.model.UserProgress
import com.esec.examprep.domain.model.WeakTopic
import com.esec.examprep.domain.repository.ExamSessionRepository
import com.esec.examprep.domain.usecase.CalculateScoreUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExamSessionRepositoryImpl @Inject constructor(
    private val dao: ExamResultDao,
    private val attemptDao: QuestionAttemptDao,
    private val calculateScore: CalculateScoreUseCase,
) : ExamSessionRepository {

    override suspend fun saveSession(session: ExamSession): ExamResult {
        val result = calculateScore(session, subjectName = session.subjectId)
        dao.insert(result.toEntity())

        val finishedAt = (session.finishedAt ?: java.time.Instant.now()).epochSecond
        val attempts = session.questions.map { q ->
            val selected = session.answers[q.id]
            QuestionAttemptEntity(
                sessionId = session.id,
                questionId = q.id,
                subjectId = q.subjectId,
                selectedOptionId = selected,
                isCorrect = selected != null && selected == q.correctOptionId,
                attemptedAt = finishedAt,
            )
        }
        if (attempts.isNotEmpty()) attemptDao.insertAll(attempts)

        return result
    }

    override suspend fun getSessionById(id: String): ExamSession? = null

    override fun getAllResults(): Flow<List<ExamResult>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun getProgressBySubject(): Flow<List<UserProgress>> =
        dao.observeProgressBySubject().map { list -> list.map { it.toDomain() } }

    override suspend fun getRecentResults(limit: Int): List<ExamResult> =
        dao.getRecent(limit).map { it.toDomain() }

    override suspend fun clearAllProgress() {
        dao.deleteAll()
        attemptDao.deleteAll()
    }

    override fun observeWeakTopics(): Flow<List<WeakTopic>> =
        attemptDao.observeWeakTopics().map { rows ->
            rows.map { WeakTopic(it.subjectId, it.errorRate, it.attempts) }
        }

    override suspend fun getSubjectTrend(subjectId: String, limit: Int): SubjectTrend {
        val recentDesc = dao.getRecentScoresForSubject(subjectId, limit)
        return SubjectTrend(subjectId = subjectId, recentScores = recentDesc.reversed())
    }

    override suspend fun getAvgSecondsPerQuestion(): Double = dao.getAvgSecondsPerQuestion()
}
