package com.esec.examprep.domain.usecase

import com.esec.examprep.domain.model.Question
import com.esec.examprep.domain.repository.QuestionRepository
import javax.inject.Inject

class GetQuestionsForExamUseCase @Inject constructor(
    private val repository: QuestionRepository,
) {
    /** [count] <= 0 means "all available questions for the subject". */
    suspend operator fun invoke(subjectId: String, count: Int = 40): List<Question> {
        val limit = count.takeIf { it > 0 }
        return repository.getQuestionsBySubject(subjectId, limit = limit).shuffled()
    }
}
