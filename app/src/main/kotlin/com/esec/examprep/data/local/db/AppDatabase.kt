package com.esec.examprep.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.esec.examprep.data.local.dao.ExamResultDao
import com.esec.examprep.data.local.dao.QuestionDao
import com.esec.examprep.data.local.dao.SubjectDao
import com.esec.examprep.data.local.entity.ExamResultEntity
import com.esec.examprep.data.local.entity.QuestionEntity
import com.esec.examprep.data.local.entity.SubjectEntity

@Database(
    entities = [
        SubjectEntity::class,
        QuestionEntity::class,
        ExamResultEntity::class,
    ],
    version = 2,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun subjectDao(): SubjectDao
    abstract fun questionDao(): QuestionDao
    abstract fun examResultDao(): ExamResultDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE questions ADD COLUMN isBookmarked INTEGER NOT NULL DEFAULT 0")
            }
        }
    }
}
