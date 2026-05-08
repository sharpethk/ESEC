package com.esec.examprep.presentation.common

import com.esec.examprep.domain.model.Question
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/** Holds the questions + date for a Daily Challenge run, consumed once by ExamScreen. */
@Singleton
class DailyChallengeRunHolder @Inject constructor() {
    data class Run(val date: LocalDate, val questions: List<Question>)

    private var pending: Run? = null

    fun set(run: Run) {
        pending = run
    }

    fun take(): Run? {
        val r = pending
        pending = null
        return r
    }
}
