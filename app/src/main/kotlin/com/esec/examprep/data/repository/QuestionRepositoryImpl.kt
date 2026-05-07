package com.esec.examprep.data.repository

import com.esec.examprep.data.crypto.QuestionBankDecryptor
import com.esec.examprep.data.json.QuestionBankDto
import com.esec.examprep.data.local.dao.QuestionDao
import com.esec.examprep.data.local.dao.SubjectDao
import com.esec.examprep.data.mapper.toDomain
import com.esec.examprep.data.mapper.toEntity
import com.esec.examprep.domain.model.Question
import com.esec.examprep.domain.model.Subject
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
    private val decryptor: QuestionBankDecryptor,
    private val gson: Gson,
) : QuestionRepository {

    override fun getSubjects(): Flow<List<Subject>> =
        subjectDao.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun getQuestionsBySubject(subjectId: String, limit: Int?): List<Question> {
        val entities = if (limit != null)
            questionDao.getRandomBySubject(subjectId, limit)
        else
            questionDao.getAllBySubject(subjectId)
        return entities.map { it.toDomain() }
    }

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

    override suspend fun setBookmark(questionId: String, bookmarked: Boolean) {
        questionDao.setBookmark(questionId, bookmarked)
    }

    override fun observeBookmarkedQuestions(): Flow<List<Question>> =
        questionDao.observeBookmarked().map { list -> list.map { it.toDomain() } }
}
