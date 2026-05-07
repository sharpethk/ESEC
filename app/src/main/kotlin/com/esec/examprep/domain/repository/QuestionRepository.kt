package com.esec.examprep.domain.repository

import com.esec.examprep.domain.model.ExamCategory
import com.esec.examprep.domain.model.Question
import com.esec.examprep.domain.model.Subject
import com.esec.examprep.domain.model.WrongAnswerEntry
import com.esec.examprep.domain.model.YearStat
import kotlinx.coroutines.flow.Flow

interface QuestionRepository {
    fun getSubjects(): Flow<List<Subject>>
    fun getSubjectsByCategory(category: ExamCategory): Flow<List<Subject>>
    suspend fun getQuestionsBySubject(profileId: String, subjectId: String, limit: Int? = null): List<Question>
    suspend fun getQuestionsByYear(profileId: String, subjectId: String, year: Int): List<Question>
    suspend fun getQuestionById(profileId: String, id: String): Question?
    suspend fun getQuestionsByIds(profileId: String, ids: List<String>): List<Question>
    suspend fun getYearStatsForSubject(subjectId: String): List<YearStat>
    suspend fun getQuestionCount(subjectId: String): Int
    suspend fun isDataLoaded(): Boolean
    suspend fun loadQuestionsFromEncryptedAsset()
    suspend fun forceReseedFromEncryptedAsset()
    suspend fun setBookmark(profileId: String, questionId: String, bookmarked: Boolean)
    fun observeBookmarkedQuestions(profileId: String): Flow<List<Question>>
    fun observeWrongAnswers(profileId: String): Flow<List<WrongAnswerEntry>>
}
