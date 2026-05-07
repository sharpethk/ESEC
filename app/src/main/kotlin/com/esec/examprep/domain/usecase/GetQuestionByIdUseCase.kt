package com.esec.examprep.domain.usecase

import com.esec.examprep.domain.model.Question
import com.esec.examprep.domain.repository.QuestionRepository
import javax.inject.Inject

class GetQuestionByIdUseCase @Inject constructor(
    private val repository: QuestionRepository,
) {
    suspend operator fun invoke(questionId: String): Question? =
        repository.getQuestionById(questionId)
}
