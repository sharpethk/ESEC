package com.esec.examprep.domain.usecase

import com.esec.examprep.domain.model.Question
import com.esec.examprep.domain.repository.QuestionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBookmarkedQuestionsUseCase @Inject constructor(
    private val repository: QuestionRepository,
) {
    operator fun invoke(profileId: String): Flow<List<Question>> =
        repository.observeBookmarkedQuestions(profileId)
}
