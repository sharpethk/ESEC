package com.esec.examprep.domain.usecase

import com.esec.examprep.domain.model.SubjectTrend
import com.esec.examprep.domain.repository.ExamSessionRepository
import javax.inject.Inject

class GetSubjectTrendUseCase @Inject constructor(
    private val repository: ExamSessionRepository,
) {
    suspend operator fun invoke(subjectId: String, limit: Int = 10): SubjectTrend =
        repository.getSubjectTrend(subjectId, limit)
}
