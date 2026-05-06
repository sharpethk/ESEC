package com.esec.examprep.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.esec.examprep.data.local.db.AppDatabase
import com.esec.examprep.data.local.entity.QuestionAttemptEntity
import com.esec.examprep.data.local.entity.QuestionEntity
import com.esec.examprep.data.local.entity.SubjectEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuestionAttemptDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: QuestionAttemptDao
    private lateinit var subjectDao: SubjectDao
    private lateinit var questionDao: QuestionDao

    @Before
    fun setUp() = runBlocking {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java,
        ).allowMainThreadQueries().build()
        db.openHelper.writableDatabase.execSQL("PRAGMA foreign_keys = ON")
        dao = db.questionAttemptDao()
        subjectDao = db.subjectDao()
        questionDao = db.questionDao()

        subjectDao.insertAll(listOf(subject("sub1"), subject("sub2")))
        questionDao.insertAll(
            listOf(question("q1", "sub1"), question("q2", "sub1"), question("q3", "sub2"))
        )
    }

    @After fun tearDown() { db.close() }

    @Test
    fun insertAll_andWeakTopicsAggregatesByErrorRate() = runBlocking {
        dao.insertAll(listOf(
            attempt("q1", "sub1", isCorrect = false),
            attempt("q1", "sub1", isCorrect = false),
            attempt("q2", "sub1", isCorrect = true),
            attempt("q3", "sub2", isCorrect = true),
            attempt("q3", "sub2", isCorrect = true),
        ))

        val rows = dao.observeWeakTopics().first().associateBy { it.subjectId }

        assertEquals(2, rows.size)
        assertEquals(2f / 3f, rows.getValue("sub1").errorRate, 0.01f)
        assertEquals(3, rows.getValue("sub1").attempts)
        assertEquals(0f, rows.getValue("sub2").errorRate, 0.01f)
        // sub1 (higher error rate) should appear first per ORDER BY
        assertEquals("sub1", dao.observeWeakTopics().first().first().subjectId)
    }

    @Test
    fun mostMissedQuestionIds_ordersByWrongCount() = runBlocking {
        dao.insertAll(listOf(
            attempt("q1", "sub1", isCorrect = false),
            attempt("q1", "sub1", isCorrect = false),
            attempt("q1", "sub1", isCorrect = false),
            attempt("q2", "sub1", isCorrect = false),
            attempt("q3", "sub2", isCorrect = true),
        ))

        val missed = dao.getMostMissedQuestionIds(limit = 5)

        assertEquals(2, missed.size)
        assertEquals("q1", missed[0].questionId)
        assertEquals(3, missed[0].wrongCount)
        assertEquals("q2", missed[1].questionId)
    }

    @Test
    fun deleteAll_clearsTable() = runBlocking {
        dao.insertAll(listOf(attempt("q1", "sub1", isCorrect = true)))
        dao.deleteAll()
        assertTrue(dao.observeWeakTopics().first().isEmpty())
    }

    @Test
    fun deletingQuestion_cascadesAttempts() = runBlocking {
        dao.insertAll(listOf(
            attempt("q1", "sub1", isCorrect = false),
            attempt("q2", "sub1", isCorrect = true),
        ))
        db.openHelper.writableDatabase.execSQL("DELETE FROM questions")

        assertTrue(dao.observeWeakTopics().first().isEmpty())
    }

    private fun subject(id: String) = SubjectEntity(
        id = id, name = id, description = "", iconRes = 0,
        totalQuestions = 0, category = "test",
    )

    private fun question(id: String, subjectId: String) = QuestionEntity(
        id = id, subjectId = subjectId, year = 2024, text = "Q $id",
        optionsJson = "[]", correctOptionId = "a",
        explanation = null, difficultyLevel = "EASY",
    )

    private fun attempt(
        questionId: String,
        subjectId: String,
        isCorrect: Boolean,
        sessionId: String = "s1",
    ) = QuestionAttemptEntity(
        sessionId = sessionId, questionId = questionId, subjectId = subjectId,
        selectedOptionId = if (isCorrect) "a" else "b",
        isCorrect = isCorrect, attemptedAt = 0L,
    )
}
