package com.esec.examprep.domain.repository

import com.esec.examprep.domain.model.DailyChallenge
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface DailyChallengeRepository {
    /**
     * Returns today's challenge for [profileId], generating and persisting one if absent.
     * Emits null only when the question pool is empty.
     */
    fun observeTodayChallenge(profileId: String): Flow<DailyChallenge?>

    /** Current consecutive-day streak ending today (or yesterday if today not yet completed). */
    fun observeStreak(profileId: String): Flow<Int>

    suspend fun ensureTodayChallenge(profileId: String, date: LocalDate = LocalDate.now()): DailyChallenge?

    suspend fun markCompleted(
        profileId: String,
        date: LocalDate,
        scorePercent: Float,
        durationSeconds: Int,
    )

    suspend fun deleteAllForProfile(profileId: String)
}
