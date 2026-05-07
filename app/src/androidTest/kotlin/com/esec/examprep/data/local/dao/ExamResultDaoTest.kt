package com.esec.examprep.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.esec.examprep.data.local.db.AppDatabase
import com.esec.examprep.data.local.entity.ExamResultEntity
import com.esec.examprep.data.local.entity.ProfileEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExamResultDaoTest {

    private val profileId = "p1"

    private lateinit var db: AppDatabase
    private lateinit var dao: ExamResultDao

    @Before
    fun setUp() = runBlocking {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java,
        ).allowMainThreadQueries().build()
        dao = db.examResultDao()
        db.profileDao().insert(
            ProfileEntity(
                id = profileId,
                name = "Tester",
                avatarKey = "avatar_owl",
                gradeLevel = 8,
                examCategory = "GRADE_8",
                pinHash = null,
                createdAt = 0L,
                lastActiveAt = 0L,
            ),
        )
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun insertAndObserve_returnsResultsNewestFirst() = runBlocking {
        dao.insert(result("s1", subjectId = "social_studies", completedAt = 100L))
        dao.insert(result("s2", subjectId = "social_studies", completedAt = 200L))

        val all = dao.observeAll(profileId).first()

        assertEquals(2, all.size)
        assertEquals("s2", all[0].sessionId)
        assertEquals("s1", all[1].sessionId)
    }

    @Test
    fun observeProgressBySubject_aggregatesPerSubject() = runBlocking {
        dao.insert(result("s1", subjectId = "social_studies", scorePercent = 80f, correctAnswers = 8))
        dao.insert(result("s2", subjectId = "social_studies", scorePercent = 60f, correctAnswers = 6))

        val progress = dao.observeProgressBySubject(profileId).first()

        assertEquals(1, progress.size)
        val row = progress[0]
        assertEquals("social_studies", row.subjectId)
        assertEquals(2, row.totalAttempts)
        assertEquals(80f, row.bestScore, 0.01f)
        assertEquals(70f, row.averageScore, 0.01f)
        assertEquals(14, row.totalCorrect)
    }

    @Test
    fun deleteAllForProfile_clearsTable() = runBlocking {
        dao.insert(result("s1"))
        dao.deleteAllForProfile(profileId)
        assertTrue(dao.observeAll(profileId).first().isEmpty())
    }

    private fun result(
        sessionId: String,
        subjectId: String = "social_studies",
        subjectName: String = "Social Studies",
        scorePercent: Float = 75f,
        correctAnswers: Int = 7,
        completedAt: Long = 0L,
    ) = ExamResultEntity(
        sessionId = sessionId,
        profileId = profileId,
        subjectId = subjectId,
        subjectName = subjectName,
        totalQuestions = 10,
        correctAnswers = correctAnswers,
        incorrectAnswers = 10 - correctAnswers,
        skippedAnswers = 0,
        scorePercent = scorePercent,
        passed = scorePercent >= 50f,
        durationSeconds = 600L,
        completedAt = completedAt,
        answersJson = "{}",
    )
}
