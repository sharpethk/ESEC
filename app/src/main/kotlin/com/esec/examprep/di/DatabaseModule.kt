package com.esec.examprep.di

import android.content.Context
import androidx.room.Room
import com.esec.examprep.data.local.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "esec_db")
            .addMigrations(AppDatabase.MIGRATION_1_2)
            .build()

    @Provides fun provideSubjectDao(db: AppDatabase) = db.subjectDao()
    @Provides fun provideQuestionDao(db: AppDatabase) = db.questionDao()
    @Provides fun provideExamResultDao(db: AppDatabase) = db.examResultDao()
}
