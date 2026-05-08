package com.esec.examprep.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.esec.examprep.data.local.dao.BookmarkDao
import com.esec.examprep.data.local.dao.DailyChallengeDao
import com.esec.examprep.data.local.dao.ExamResultDao
import com.esec.examprep.data.local.dao.ProfileAchievementDao
import com.esec.examprep.data.local.dao.ProfileDao
import com.esec.examprep.data.local.dao.QuestionAttemptDao
import com.esec.examprep.data.local.dao.QuestionDao
import com.esec.examprep.data.local.dao.SubjectDao
import com.esec.examprep.data.local.entity.BookmarkEntity
import com.esec.examprep.data.local.entity.DailyChallengeEntity
import com.esec.examprep.data.local.entity.ExamResultEntity
import com.esec.examprep.data.local.entity.ProfileAchievementEntity
import com.esec.examprep.data.local.entity.ProfileEntity
import com.esec.examprep.data.local.entity.QuestionAttemptEntity
import com.esec.examprep.data.local.entity.QuestionEntity
import com.esec.examprep.data.local.entity.SubjectEntity

const val DEFAULT_PROFILE_ID = "default"

@Database(
    entities = [
        SubjectEntity::class,
        QuestionEntity::class,
        ExamResultEntity::class,
        QuestionAttemptEntity::class,
        ProfileEntity::class,
        BookmarkEntity::class,
        DailyChallengeEntity::class,
        ProfileAchievementEntity::class,
    ],
    version = 7,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun subjectDao(): SubjectDao
    abstract fun questionDao(): QuestionDao
    abstract fun examResultDao(): ExamResultDao
    abstract fun questionAttemptDao(): QuestionAttemptDao
    abstract fun profileDao(): ProfileDao
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun dailyChallengeDao(): DailyChallengeDao
    abstract fun profileAchievementDao(): ProfileAchievementDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE questions ADD COLUMN isBookmarked INTEGER NOT NULL DEFAULT 0")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS question_attempts (
                      id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                      sessionId TEXT NOT NULL,
                      questionId TEXT NOT NULL,
                      subjectId TEXT NOT NULL,
                      selectedOptionId TEXT,
                      isCorrect INTEGER NOT NULL,
                      attemptedAt INTEGER NOT NULL,
                      FOREIGN KEY(questionId) REFERENCES questions(id) ON DELETE CASCADE
                    )
                """.trimIndent())
                db.execSQL("CREATE INDEX IF NOT EXISTS index_question_attempts_questionId ON question_attempts(questionId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_question_attempts_subjectId ON question_attempts(subjectId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_question_attempts_attemptedAt ON question_attempts(attemptedAt)")
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE exam_results ADD COLUMN year INTEGER")
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                val now = System.currentTimeMillis() / 1000

                // 1. Create profiles table.
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS profiles (
                      id TEXT NOT NULL PRIMARY KEY,
                      name TEXT NOT NULL,
                      avatarKey TEXT NOT NULL,
                      gradeLevel INTEGER NOT NULL,
                      examCategory TEXT NOT NULL,
                      pinHash TEXT,
                      createdAt INTEGER NOT NULL,
                      lastActiveAt INTEGER NOT NULL
                    )
                """.trimIndent())

                // 2. Insert the default profile that owns all pre-existing user data.
                db.execSQL(
                    "INSERT INTO profiles (id, name, avatarKey, gradeLevel, examCategory, pinHash, createdAt, lastActiveAt) " +
                        "VALUES ('$DEFAULT_PROFILE_ID', 'Default Student', 'avatar_owl', 8, 'GRADE_8', NULL, $now, $now)"
                )

                // 3. Recreate exam_results with profileId column + FK.
                db.execSQL("""
                    CREATE TABLE exam_results_new (
                      sessionId TEXT NOT NULL PRIMARY KEY,
                      profileId TEXT NOT NULL,
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
                      year INTEGER,
                      FOREIGN KEY(profileId) REFERENCES profiles(id) ON DELETE CASCADE
                    )
                """.trimIndent())
                db.execSQL("""
                    INSERT INTO exam_results_new
                      (sessionId, profileId, subjectId, subjectName, totalQuestions,
                       correctAnswers, incorrectAnswers, skippedAnswers, scorePercent,
                       passed, durationSeconds, completedAt, answersJson, year)
                    SELECT sessionId, '$DEFAULT_PROFILE_ID', subjectId, subjectName, totalQuestions,
                           correctAnswers, incorrectAnswers, skippedAnswers, scorePercent,
                           passed, durationSeconds, completedAt, answersJson, year
                    FROM exam_results
                """.trimIndent())
                db.execSQL("DROP TABLE exam_results")
                db.execSQL("ALTER TABLE exam_results_new RENAME TO exam_results")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_exam_results_profileId ON exam_results(profileId)")

                // 4. Recreate question_attempts with profileId column + FK.
                db.execSQL("""
                    CREATE TABLE question_attempts_new (
                      id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                      profileId TEXT NOT NULL,
                      sessionId TEXT NOT NULL,
                      questionId TEXT NOT NULL,
                      subjectId TEXT NOT NULL,
                      selectedOptionId TEXT,
                      isCorrect INTEGER NOT NULL,
                      attemptedAt INTEGER NOT NULL,
                      FOREIGN KEY(questionId) REFERENCES questions(id) ON DELETE CASCADE,
                      FOREIGN KEY(profileId) REFERENCES profiles(id) ON DELETE CASCADE
                    )
                """.trimIndent())
                db.execSQL("""
                    INSERT INTO question_attempts_new
                      (id, profileId, sessionId, questionId, subjectId, selectedOptionId, isCorrect, attemptedAt)
                    SELECT id, '$DEFAULT_PROFILE_ID', sessionId, questionId, subjectId, selectedOptionId, isCorrect, attemptedAt
                    FROM question_attempts
                """.trimIndent())
                db.execSQL("DROP TABLE question_attempts")
                db.execSQL("ALTER TABLE question_attempts_new RENAME TO question_attempts")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_question_attempts_questionId ON question_attempts(questionId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_question_attempts_subjectId ON question_attempts(subjectId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_question_attempts_attemptedAt ON question_attempts(attemptedAt)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_question_attempts_profileId ON question_attempts(profileId)")

                // 5. Create bookmarks table and copy existing bookmark flags into it.
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS bookmarks (
                      profileId TEXT NOT NULL,
                      questionId TEXT NOT NULL,
                      createdAt INTEGER NOT NULL,
                      PRIMARY KEY(profileId, questionId),
                      FOREIGN KEY(profileId) REFERENCES profiles(id) ON DELETE CASCADE,
                      FOREIGN KEY(questionId) REFERENCES questions(id) ON DELETE CASCADE
                    )
                """.trimIndent())
                db.execSQL("CREATE INDEX IF NOT EXISTS index_bookmarks_questionId ON bookmarks(questionId)")
                db.execSQL(
                    "INSERT OR IGNORE INTO bookmarks (profileId, questionId, createdAt) " +
                        "SELECT '$DEFAULT_PROFILE_ID', id, $now FROM questions WHERE isBookmarked = 1"
                )

                // 6. Drop isBookmarked column from questions (recreate without it).
                db.execSQL("""
                    CREATE TABLE questions_new (
                      id TEXT NOT NULL PRIMARY KEY,
                      subjectId TEXT NOT NULL,
                      year INTEGER NOT NULL,
                      text TEXT NOT NULL,
                      optionsJson TEXT NOT NULL,
                      correctOptionId TEXT NOT NULL,
                      explanation TEXT,
                      difficultyLevel TEXT NOT NULL,
                      FOREIGN KEY(subjectId) REFERENCES subjects(id) ON DELETE CASCADE
                    )
                """.trimIndent())
                db.execSQL("""
                    INSERT INTO questions_new
                      (id, subjectId, year, text, optionsJson, correctOptionId, explanation, difficultyLevel)
                    SELECT id, subjectId, year, text, optionsJson, correctOptionId, explanation, difficultyLevel
                    FROM questions
                """.trimIndent())
                db.execSQL("DROP TABLE questions")
                db.execSQL("ALTER TABLE questions_new RENAME TO questions")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_questions_subjectId ON questions(subjectId)")

                // 7. Normalize subjects.category for any pre-existing rows.
                db.execSQL("UPDATE subjects SET category = 'GRADE_8' WHERE category IS NULL OR category = '' OR category = 'grade8'")
            }
        }

        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS daily_challenges (
                      id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                      profileId TEXT NOT NULL,
                      date INTEGER NOT NULL,
                      questionIdsJson TEXT NOT NULL,
                      completedAt INTEGER,
                      scorePercent REAL,
                      durationSeconds INTEGER,
                      FOREIGN KEY(profileId) REFERENCES profiles(id) ON DELETE CASCADE
                    )
                """.trimIndent())
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_daily_challenges_profileId_date ON daily_challenges(profileId, date)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_daily_challenges_date ON daily_challenges(date)")
            }
        }

        val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS profile_achievements (
                      profileId TEXT NOT NULL,
                      code TEXT NOT NULL,
                      unlockedAt INTEGER NOT NULL,
                      PRIMARY KEY(profileId, code),
                      FOREIGN KEY(profileId) REFERENCES profiles(id) ON DELETE CASCADE
                    )
                """.trimIndent())
                db.execSQL("CREATE INDEX IF NOT EXISTS index_profile_achievements_profileId ON profile_achievements(profileId)")
            }
        }
    }
}
