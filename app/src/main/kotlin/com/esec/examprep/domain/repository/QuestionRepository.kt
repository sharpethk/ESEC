package com.esec.examprep.domain.repository

import com.esec.examprep.domain.model.Question
import com.esec.examprep.domain.model.Subject
import com.esec.examprep.domain.model.YearStat
import kotlinx.coroutines.flow.Flow

interface QuestionRepository {
    fun getSubjects(): Flow<List<Subject>>
    suspend fun getQuestionsBySubject(subjectId: String, limit: Int? = null): List<Question>
    suspend fun getQuestionsByYear(subjectId: String, year: Int): List<Question>
    suspend fun getQuestionById(id: String): Question?
    suspend fun getYearStatsForSubject(subjectId: String): List<YearStat>
    suspend fun getQuestionCount(subjectId: String): Int
    suspend fun isDataLoaded(): Boolean
    suspend fun loadQuestionsFromEncryptedAsset()
    suspend fun setBookmark(questionId: String, bookmarked: Boolean)
    fun observeBookmarkedQuestions(): Flow<List<Question>>
}
