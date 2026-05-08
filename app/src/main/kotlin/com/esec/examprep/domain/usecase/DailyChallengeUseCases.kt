package com.esec.examprep.domain.usecase

import com.esec.examprep.domain.model.DailyChallenge
import com.esec.examprep.domain.repository.DailyChallengeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTodayChallengeUseCase @Inject constructor(
    private val repository: DailyChallengeRepository,
) {
    operator fun invoke(profileId: String): Flow<DailyChallenge?> =
        repository.observeTodayChallenge(profileId)
}

class ObserveStreakUseCase @Inject constructor(
    private val repository: DailyChallengeRepository,
) {
    operator fun invoke(profileId: String): Flow<Int> = repository.observeStreak(profileId)
}

class CompleteDailyChallengeUseCase @Inject constructor(
    private val repository: DailyChallengeRepository,
) {
    suspend operator fun invoke(
        profileId: String,
        date: java.time.LocalDate,
        scorePercent: Float,
        durationSeconds: Int,
    ) = repository.markCompleted(profileId, date, scorePercent, durationSeconds)
}
