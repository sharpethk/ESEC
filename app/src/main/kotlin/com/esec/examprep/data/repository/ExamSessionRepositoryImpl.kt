package com.esec.examprep.data.repository

import com.esec.examprep.data.local.dao.ExamResultDao
import com.esec.examprep.data.mapper.toDomain
import com.esec.examprep.data.mapper.toEntity
import com.esec.examprep.domain.model.ExamResult
import com.esec.examprep.domain.model.ExamSession
import com.esec.examprep.domain.model.UserProgress
import com.esec.examprep.domain.repository.ExamSessionRepository
import com.esec.examprep.domain.usecase.CalculateScoreUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExamSessionRepositoryImpl @Inject constructor(
    private val dao: ExamResultDao,
    private val calculateScore: CalculateScoreUseCase,
) : ExamSessionRepository {

    override suspend fun saveSession(session: ExamSession): ExamResult {
        // We need the subject name — fetch from session or use subjectId as fallback
        val result = calculateScore(session, subjectName = session.subjectId)
        dao.insert(result.toEntity())
        return result
    }

    override suspend fun getSessionById(id: String): ExamSession? = null   // sessions are ephemeral; only results persist

    override fun getAllResults(): Flow<List<ExamResult>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun getProgressBySubject(): Flow<List<UserProgress>> =
        dao.observeProgressBySubject().map { list -> list.map { it.toDomain() } }

    override suspend fun getRecentResults(limit: Int): List<ExamResult> =
        dao.getRecent(limit).map { it.toDomain() }

    override suspend fun clearAllProgress() = dao.deleteAll()
}
