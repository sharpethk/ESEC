package com.esec.examprep.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
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
    version = 1,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun subjectDao(): SubjectDao
    abstract fun questionDao(): QuestionDao
    abstract fun examResultDao(): ExamResultDao
}
