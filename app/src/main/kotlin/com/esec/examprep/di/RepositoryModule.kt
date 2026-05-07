package com.esec.examprep.di

import com.esec.examprep.data.repository.ExamSessionRepositoryImpl
import com.esec.examprep.data.repository.ProfileRepositoryImpl
import com.esec.examprep.data.repository.QuestionRepositoryImpl
import com.esec.examprep.domain.repository.ExamSessionRepository
import com.esec.examprep.domain.repository.ProfileRepository
import com.esec.examprep.domain.repository.QuestionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds @Singleton
    abstract fun bindQuestionRepository(impl: QuestionRepositoryImpl): QuestionRepository

    @Binds @Singleton
    abstract fun bindExamSessionRepository(impl: ExamSessionRepositoryImpl): ExamSessionRepository

    @Binds @Singleton
    abstract fun bindProfileRepository(impl: ProfileRepositoryImpl): ProfileRepository
}
