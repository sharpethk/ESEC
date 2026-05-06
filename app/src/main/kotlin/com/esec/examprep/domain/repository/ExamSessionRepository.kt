package com.esec.examprep.domain.repository

import com.esec.examprep.domain.model.ExamResult
import com.esec.examprep.domain.model.ExamSession
import com.esec.examprep.domain.model.UserProgress
import kotlinx.coroutines.flow.Flow

interface ExamSessionRepository {
    suspend fun saveSession(session: ExamSession): ExamResult
    suspend fun getSessionById(id: String): ExamSession?
    fun getAllResults(): Flow<List<ExamResult>>
    fun getProgressBySubject(): Flow<List<UserProgress>>
    suspend fun getRecentResults(limit: Int = 10): List<ExamResult>
    suspend fun clearAllProgress()
}
