package com.esec.examprep.data.repository

import com.esec.examprep.data.crypto.QuestionBankDecryptor
import com.esec.examprep.data.json.QuestionBankDto
import com.esec.examprep.data.local.dao.BookmarkDao
import com.esec.examprep.data.local.dao.QuestionDao
import com.esec.examprep.data.local.dao.SubjectDao
import com.esec.examprep.data.local.entity.BookmarkEntity
import com.esec.examprep.data.mapper.toDomain
import com.esec.examprep.data.mapper.toEntity
import com.esec.examprep.domain.model.ExamCategory
import com.esec.examprep.domain.model.Question
import com.esec.examprep.domain.model.Subject
import com.esec.examprep.domain.model.YearStat
import com.esec.examprep.domain.repository.QuestionRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuestionRepositoryImpl @Inject constructor(
    private val subjectDao: SubjectDao,
    private val questionDao: QuestionDao,
    private val bookmarkDao: BookmarkDao,
    private val decryptor: QuestionBankDecryptor,
    private val gson: Gson,
) : QuestionRepository {

    override fun getSubjects(): Flow<List<Subject>> =
        subjectDao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun getSubjectsByCategory(category: ExamCategory): Flow<List<Subject>> =
        subjectDao.observeByCategory(category.storageKey).map { list -> list.map { it.toDomain() } }

    override suspend fun getQuestionsBySubject(profileId: String, subjectId: String, limit: Int?): List<Question> {
        val entities = if (limit != null)
            questionDao.getRandomBySubject(subjectId, limit)
        else
            questionDao.getAllBySubject(subjectId)
        val bookmarks = bookmarkDao.getQuestionIds(profileId).toSet()
        return entities.map { it.toDomain(isBookmarked = it.id in bookmarks) }
    }

    override suspend fun getQuestionsByYear(profileId: String, subjectId: String, year: Int): List<Question> {
        val entities = questionDao.getBySubjectAndYear(subjectId, year)
        val bookmarks = bookmarkDao.getQuestionIds(profileId).toSet()
        return entities.map { it.toDomain(isBookmarked = it.id in bookmarks) }
    }

    override suspend fun getQuestionById(profileId: String, id: String): Question? {
        val entity = questionDao.getById(id) ?: return null
        val bookmarked = bookmarkDao.isBookmarked(profileId, id)
        return entity.toDomain(isBookmarked = bookmarked)
    }

    override suspend fun getYearStatsForSubject(subjectId: String): List<YearStat> =
        questionDao.getYearCountsForSubject(subjectId)
            .map { YearStat(year = it.year, questionCount = it.questionCount) }

    override suspend fun getQuestionCount(subjectId: String): Int =
        questionDao.countBySubject(subjectId)

    override suspend fun isDataLoaded(): Boolean =
        questionDao.totalCount() > 0

    override suspend fun loadQuestionsFromEncryptedAsset() {
        val json = decryptor.decryptToJson()
        val bank = gson.fromJson(json, QuestionBankDto::class.java)

        val validQuestions = bank.questions.filter { q ->
            q.correctOptionId.isNotBlank() &&
                q.options.any { it.id == q.correctOptionId }
        }

        val questionsBySubject = validQuestions.groupBy { it.subjectId }
        val subjectEntities = bank.subjects.map { dto ->
            dto.toEntity(totalQuestions = questionsBySubject[dto.id]?.size ?: 0)
        }
        val questionEntities = validQuestions.map { it.toEntity() }

        subjectDao.insertAll(subjectEntities)
        questionDao.insertAll(questionEntities)
    }

    override suspend fun setBookmark(profileId: String, questionId: String, bookmarked: Boolean) {
        if (bookmarked) {
            bookmarkDao.insert(
                BookmarkEntity(
                    profileId = profileId,
                    questionId = questionId,
                    createdAt = System.currentTimeMillis() / 1000,
                )
            )
        } else {
            bookmarkDao.delete(profileId, questionId)
        }
    }

    override fun observeBookmarkedQuestions(profileId: String): Flow<List<Question>> =
        bookmarkDao.observeForProfile(profileId).map { list ->
            list.map { it.toDomain(isBookmarked = true) }
        }
}
