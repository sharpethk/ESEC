package com.esec.examprep.domain.repository

import com.esec.examprep.domain.model.Question
import com.esec.examprep.domain.model.Subject
import kotlinx.coroutines.flow.Flow

interface QuestionRepository {
    fun getSubjects(): Flow<List<Subject>>
    suspend fun getQuestionsBySubject(subjectId: String, limit: Int? = null): List<Question>
    suspend fun getQuestionCount(subjectId: String): Int
    suspend fun isDataLoaded(): Boolean
    suspend fun loadQuestionsFromEncryptedAsset()
}
