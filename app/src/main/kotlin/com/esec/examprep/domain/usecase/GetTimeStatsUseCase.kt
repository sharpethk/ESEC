package com.esec.examprep.domain.usecase

import com.esec.examprep.domain.repository.ExamSessionRepository
import javax.inject.Inject

class GetTimeStatsUseCase @Inject constructor(
    private val repository: ExamSessionRepository,
) {
    suspend operator fun invoke(profileId: String): Double =
        repository.getAvgSecondsPerQuestion(profileId)
}
