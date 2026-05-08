package com.esec.examprep.domain.usecase

import com.esec.examprep.domain.model.Achievement
import com.esec.examprep.domain.model.AchievementCode
import com.esec.examprep.domain.model.ExamSession
import com.esec.examprep.domain.repository.AchievementRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveAchievementsUseCase @Inject constructor(
    private val repository: AchievementRepository,
) {
    operator fun invoke(profileId: String): Flow<List<Achievement>> =
        repository.observeForProfile(profileId)
}

class EvaluateAchievementsUseCase @Inject constructor(
    private val repository: AchievementRepository,
) {
    suspend operator fun invoke(profileId: String, lastSession: ExamSession? = null): List<AchievementCode> =
        repository.evaluate(profileId, lastSession)
}
