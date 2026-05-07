package com.esec.examprep.domain.usecase

import com.esec.examprep.domain.model.YearStat
import com.esec.examprep.domain.repository.QuestionRepository
import javax.inject.Inject

class GetYearStatsForSubjectUseCase @Inject constructor(
    private val repository: QuestionRepository,
) {
    suspend operator fun invoke(subjectId: String): List<YearStat> =
        repository.getYearStatsForSubject(subjectId)
}
