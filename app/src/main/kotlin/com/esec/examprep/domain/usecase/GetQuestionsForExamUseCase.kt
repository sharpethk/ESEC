package com.esec.examprep.domain.usecase

import com.esec.examprep.domain.model.Question
import com.esec.examprep.domain.repository.QuestionRepository
import javax.inject.Inject

class GetQuestionsForExamUseCase @Inject constructor(
    private val repository: QuestionRepository,
) {
    /**
     * If [year] is non-null and > 0, returns the full year paper in original order (no shuffle).
     * Otherwise returns up to [count] random questions ([count] <= 0 means "all available").
     */
    suspend operator fun invoke(
        subjectId: String,
        count: Int = 40,
        year: Int? = null,
    ): List<Question> {
        if (year != null && year > 0) {
            return repository.getQuestionsByYear(subjectId, year)
        }
        val limit = count.takeIf { it > 0 }
        return repository.getQuestionsBySubject(subjectId, limit = limit).shuffled()
    }
}
