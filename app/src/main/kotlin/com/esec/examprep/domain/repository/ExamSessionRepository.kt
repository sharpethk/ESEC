package com.esec.examprep.domain.repository

import com.esec.examprep.domain.model.ExamResult
import com.esec.examprep.domain.model.ExamSession
import com.esec.examprep.domain.model.SubjectTrend
import com.esec.examprep.domain.model.UserProgress
import com.esec.examprep.domain.model.WeakTopic
import kotlinx.coroutines.flow.Flow

interface ExamSessionRepository {
    suspend fun saveSession(session: ExamSession): ExamResult
    suspend fun getSessionById(id: String): ExamSession?
    fun getAllResults(profileId: String): Flow<List<ExamResult>>
    fun getProgressBySubject(profileId: String): Flow<List<UserProgress>>
    suspend fun getRecentResults(profileId: String, limit: Int = 10): List<ExamResult>
    suspend fun clearAllProgress(profileId: String)

    fun observeWeakTopics(profileId: String): Flow<List<WeakTopic>>
    suspend fun getSubjectTrend(profileId: String, subjectId: String, limit: Int = 10): SubjectTrend
    suspend fun getAvgSecondsPerQuestion(profileId: String): Double
}
