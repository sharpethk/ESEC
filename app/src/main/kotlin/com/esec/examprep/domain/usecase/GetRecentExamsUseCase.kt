package com.esec.examprep.domain.usecase

import com.esec.examprep.domain.model.ExamResult
import com.esec.examprep.domain.repository.ExamSessionRepository
import javax.inject.Inject

class GetRecentExamsUseCase @Inject constructor(
    private val repository: ExamSessionRepository,
) {
    suspend operator fun invoke(limit: Int = 10): List<ExamResult> = repository.getRecentResults(limit)
}
