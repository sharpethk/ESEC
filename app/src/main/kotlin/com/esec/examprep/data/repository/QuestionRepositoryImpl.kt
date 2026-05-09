package com.esec.examprep.data.repository

import android.util.Log
import com.esec.examprep.data.crypto.QuestionBankDecryptor
import com.esec.examprep.data.json.QuestionBankDto
import com.esec.examprep.data.local.dao.BookmarkDao
import com.esec.examprep.data.local.dao.QuestionAttemptDao
import com.esec.examprep.data.local.dao.QuestionDao
import com.esec.examprep.data.local.dao.SubjectDao
import com.esec.examprep.data.local.db.AppDatabase
import com.esec.examprep.data.local.entity.BookmarkEntity
import com.esec.examprep.data.mapper.toDomain
import com.esec.examprep.data.mapper.toEntity
import com.esec.examprep.domain.model.ExamCategory
import com.esec.examprep.domain.model.Question
import com.esec.examprep.domain.model.Subject
import com.esec.examprep.domain.model.WrongAnswerEntry
import com.esec.examprep.domain.model.YearStat
import com.esec.examprep.domain.repository.QuestionRepository
import com.google.gson.Gson
import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuestionRepositoryImpl @Inject constructor(
    private val database: AppDatabase,
    private val subjectDao: SubjectDao,
    private val questionDao: QuestionDao,
    private val bookmarkDao: BookmarkDao,
    private val attemptDao: QuestionAttemptDao,
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

    override suspend fun getQuestionsByIds(profileId: String, ids: List<String>): List<Question> {
        if (ids.isEmpty()) return emptyList()
        val entities = questionDao.getByIds(ids)
        val bookmarks = bookmarkDao.getQuestionIds(profileId).toSet()
        val byId = entities.associateBy { it.id }
        // Preserve caller's order; drop any IDs that no longer exist in the bank.
        return ids.mapNotNull { id ->
            byId[id]?.toDomain(isBookmarked = id in bookmarks)
        }
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

        // Mirrors validation in tools/encrypt_bank.py: drop questions that
        // would render as broken in the UI rather than seeding them.
        val validQuestions = bank.questions.filter { q ->
            q.correctOptionId.isNotBlank() &&
                q.options.size >= 2 &&
                q.options.all { it.id.isNotBlank() && it.text.isNotBlank() } &&
                q.options.distinctBy { it.id }.size == q.options.size &&
                q.options.any { it.id == q.correctOptionId }
        }

        val droppedCount = bank.questions.size - validQuestions.size
        if (droppedCount > 0) {
            val validIds = validQuestions.mapTo(HashSet(validQuestions.size)) { it.id }
            val sampleIds = bank.questions.asSequence()
                .filter { it.id !in validIds }
                .take(5)
                .joinToString(", ") { it.id }
            Log.w(
                "QuestionBank",
                "Dropped $droppedCount/${bank.questions.size} malformed questions during seed. Sample IDs: $sampleIds",
            )
            val subjectsWithValid = validQuestions.mapTo(HashSet()) { it.subjectId }
            val emptySubjects = bank.subjects.map { it.id }.filter { it !in subjectsWithValid }
            if (emptySubjects.isNotEmpty()) {
                Log.w("QuestionBank", "Subjects with zero valid questions after filtering: $emptySubjects")
            }
        }

        val questionsBySubject = validQuestions.groupBy { it.subjectId }
        val subjectEntities = bank.subjects.map { dto ->
            dto.toEntity(totalQuestions = questionsBySubject[dto.id]?.size ?: 0)
        }
        val questionEntities = validQuestions.map { it.toEntity() }

        // Upsert current bank, then drop rows that disappeared from it.
        // Question CASCADE wipes attempts/bookmarks for removed questions only;
        // questions still present (and their user data) are untouched.
        val keepSubjectIds = subjectEntities.map { it.id }.toSet()
        val keepQuestionIds = questionEntities.map { it.id }.toSet()

        database.withTransaction {
            subjectDao.insertAll(subjectEntities)
            questionDao.insertAll(questionEntities)

            val orphanQuestionIds = questionDao.getAllIds().filterNot { it in keepQuestionIds }
            if (orphanQuestionIds.isNotEmpty()) questionDao.deleteByIds(orphanQuestionIds)

            val orphanSubjectIds = subjectDao.getAllIds().filterNot { it in keepSubjectIds }
            if (orphanSubjectIds.isNotEmpty()) subjectDao.deleteByIds(orphanSubjectIds)
        }
    }

    override suspend fun forceReseedFromEncryptedAsset() {
        loadQuestionsFromEncryptedAsset()
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

    override fun observeWrongAnswers(profileId: String): Flow<List<WrongAnswerEntry>> =
        attemptDao.observeStillWrong(profileId).map { rows ->
            if (rows.isEmpty()) return@map emptyList()
            val ids = rows.map { it.questionId }
            val questionEntities = questionDao.getByIds(ids).associateBy { it.id }
            val bookmarks = bookmarkDao.getQuestionIds(profileId).toSet()
            rows.mapNotNull { row ->
                val q = questionEntities[row.questionId] ?: return@mapNotNull null
                WrongAnswerEntry(
                    question = q.toDomain(isBookmarked = row.questionId in bookmarks),
                    lastAttemptedAt = Instant.ofEpochSecond(row.lastAttemptedAt),
                    attemptCount = row.attemptCount,
                    lastSelectedOptionId = row.lastSelectedOptionId,
                )
            }
        }
}
