package com.esec.examprep.domain.usecase

import com.esec.examprep.domain.model.Question
import com.esec.examprep.domain.repository.QuestionRepository
import javax.inject.Inject

class GetQuestionsForExamUseCase @Inject constructor(
    private val repository: QuestionRepository,
) {
    suspend operator fun invoke(subjectId: String, count: Int = 40): List<Question> =
        repository.getQuestionsBySubject(subjectId, limit = count).shuffled()
}
