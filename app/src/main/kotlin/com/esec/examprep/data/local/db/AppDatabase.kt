package com.esec.examprep.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.esec.examprep.data.local.dao.ExamResultDao
import com.esec.examprep.data.local.dao.QuestionAttemptDao
import com.esec.examprep.data.local.dao.QuestionDao
import com.esec.examprep.data.local.dao.SubjectDao
import com.esec.examprep.data.local.entity.ExamResultEntity
import com.esec.examprep.data.local.entity.QuestionAttemptEntity
import com.esec.examprep.data.local.entity.QuestionEntity
import com.esec.examprep.data.local.entity.SubjectEntity

@Database(
    entities = [
        SubjectEntity::class,
        QuestionEntity::class,
        ExamResultEntity::class,
        QuestionAttemptEntity::class,
    ],
    version = 3,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun subjectDao(): SubjectDao
    abstract fun questionDao(): QuestionDao
    abstract fun examResultDao(): ExamResultDao
    abstract fun questionAttemptDao(): QuestionAttemptDao

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
    }
}
