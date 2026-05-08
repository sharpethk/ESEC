package com.esec.examprep.presentation.common

import com.esec.examprep.domain.usecase.BuildPracticeExamUseCase
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Hands a [BuildPracticeExamUseCase.Config] from the Practice Builder screen to the
 * Exam screen across the navigation boundary. The config is consumed once.
 */
@Singleton
class PracticeConfigHolder @Inject constructor() {
    private var pending: BuildPracticeExamUseCase.Config? = null

    fun set(config: BuildPracticeExamUseCase.Config) {
        pending = config
    }

    fun take(): BuildPracticeExamUseCase.Config? {
        val c = pending
        pending = null
        return c
    }
}
