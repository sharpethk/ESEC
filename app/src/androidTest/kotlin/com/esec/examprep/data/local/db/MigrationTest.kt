package com.esec.examprep.data.local.db

import android.content.Context
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MigrationTest {

    private val dbName = "migration_test.db"
    private lateinit var context: Context

    @Before fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        context.deleteDatabase(dbName)
    }

    @After fun tearDown() {
        context.deleteDatabase(dbName)
    }

    @Test
    fun migrate4to5_preservesDataAndCreatesDefaultProfile() {
        seedV4Database()

        val db = Room.databaseBuilder(context, AppDatabase::class.java, dbName)
            .addMigrations(AppDatabase.MIGRATION_4_5)
            .allowMainThreadQueries()
            .build()

        try {
            val raw = db.openHelper.writableDatabase

            raw.query("SELECT id, name, examCategory FROM profiles").use { c ->
                assertTrue("default profile must exist", c.moveToFirst())
                assertEquals(DEFAULT_PROFILE_ID, c.getString(0))
                assertEquals("GRADE_8", c.getString(2))
                assertFalse("only one profile expected", c.moveToNext())
            }

            raw.query("SELECT profileId FROM exam_results WHERE sessionId = 's1'").use { c ->
                assertTrue(c.moveToFirst())
                assertEquals(DEFAULT_PROFILE_ID, c.getString(0))
            }

            raw.query("SELECT profileId FROM question_attempts WHERE questionId = 'q1'").use { c ->
                assertTrue(c.moveToFirst())
                assertEquals(DEFAULT_PROFILE_ID, c.getString(0))
            }

            raw.query("SELECT profileId, questionId FROM bookmarks").use { c ->
                assertTrue("bookmark should be carried over", c.moveToFirst())
                assertEquals(DEFAULT_PROFILE_ID, c.getString(0))
                assertEquals("q1", c.getString(1))
                assertFalse(c.moveToNext())
            }

            raw.query("PRAGMA table_info(questions)").use { c ->
                val nameIdx = c.getColumnIndexOrThrow("name")
                while (c.moveToNext()) {
                    assertFalse(
                        "isBookmarked column must be dropped",
                        c.getString(nameIdx) == "isBookmarked",
                    )
                }
            }

            raw.query("SELECT id FROM questions WHERE id = 'q1'").use { c ->
                assertTrue("question rows must survive migration", c.moveToFirst())
            }

            raw.query("SELECT category FROM subjects WHERE id = 'sub1'").use { c ->
                assertTrue(c.moveToFirst())
                assertEquals("GRADE_8", c.getString(0))
            }
        } finally {
            db.close()
        }
    }

    private fun seedV4Database() {
        val helper = FrameworkSQLiteOpenHelperFactory().create(
            SupportSQLiteOpenHelper.Configuration.builder(context)
                .name(dbName)
                .callback(object : SupportSQLiteOpenHelper.Callback(4) {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        db.execSQL(
                            """
                            CREATE TABLE subjects (
                              id TEXT NOT NULL PRIMARY KEY,
                              name TEXT NOT NULL,
                              description TEXT NOT NULL,
                              iconRes INTEGER NOT NULL,
                              totalQuestions INTEGER NOT NULL,
                              category TEXT NOT NULL
                            )
                            """.trimIndent(),
                        )
                        db.execSQL(
                            """
                            CREATE TABLE questions (
                              id TEXT NOT NULL PRIMARY KEY,
                              subjectId TEXT NOT NULL,
                              year INTEGER NOT NULL,
                              text TEXT NOT NULL,
                              optionsJson TEXT NOT NULL,
                              correctOptionId TEXT NOT NULL,
                              explanation TEXT,
                              difficultyLevel TEXT NOT NULL,
                              isBookmarked INTEGER NOT NULL DEFAULT 0,
                              FOREIGN KEY(subjectId) REFERENCES subjects(id) ON DELETE CASCADE
                            )
                            """.trimIndent(),
                        )
                        db.execSQL("CREATE INDEX index_questions_subjectId ON questions(subjectId)")
                        db.execSQL(
                            """
                            CREATE TABLE exam_results (
                              sessionId TEXT NOT NULL PRIMARY KEY,
                              subjectId TEXT NOT NULL,
                              subjectName TEXT NOT NULL,
                              totalQuestions INTEGER NOT NULL,
                              correctAnswers INTEGER NOT NULL,
                              incorrectAnswers INTEGER NOT NULL,
                              skippedAnswers INTEGER NOT NULL,
                              scorePercent REAL NOT NULL,
                              passed INTEGER NOT NULL,
                              durationSeconds INTEGER NOT NULL,
                              completedAt INTEGER NOT NULL,
                              answersJson TEXT NOT NULL,
                              year INTEGER
                            )
                            """.trimIndent(),
                        )
                        db.execSQL(
                            """
                            CREATE TABLE question_attempts (
                              id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                              sessionId TEXT NOT NULL,
                              questionId TEXT NOT NULL,
                              subjectId TEXT NOT NULL,
                              selectedOptionId TEXT,
                              isCorrect INTEGER NOT NULL,
                              attemptedAt INTEGER NOT NULL,
                              FOREIGN KEY(questionId) REFERENCES questions(id) ON DELETE CASCADE
                            )
                            """.trimIndent(),
                        )
                        db.execSQL("CREATE INDEX index_question_attempts_questionId ON question_attempts(questionId)")
                        db.execSQL("CREATE INDEX index_question_attempts_subjectId ON question_attempts(subjectId)")
                        db.execSQL("CREATE INDEX index_question_attempts_attemptedAt ON question_attempts(attemptedAt)")

                        db.execSQL(
                            "INSERT INTO subjects VALUES ('sub1', 'Math', 'desc', 0, 1, 'GRADE_8')",
                        )
                        db.execSQL(
                            "INSERT INTO questions VALUES ('q1', 'sub1', 2024, 'Q?', '[]', 'a', NULL, 'EASY', 1)",
                        )
                        db.execSQL(
                            """
                            INSERT INTO exam_results VALUES
                              ('s1', 'sub1', 'Math', 5, 4, 1, 0, 80.0, 1, 300, 1700000000000, '{}', 2024)
                            """.trimIndent(),
                        )
                        db.execSQL(
                            "INSERT INTO question_attempts (sessionId, questionId, subjectId, selectedOptionId, isCorrect, attemptedAt) " +
                                "VALUES ('s1', 'q1', 'sub1', 'a', 1, 1700000000000)",
                        )
                    }

                    override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) {
                        // no-op for seed
                    }
                })
                .build(),
        )
        helper.writableDatabase
        helper.close()
    }
}
