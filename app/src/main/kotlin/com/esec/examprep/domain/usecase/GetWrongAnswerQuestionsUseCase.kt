package com.esec.examprep.domain.usecase

import com.esec.examprep.domain.model.Question
import com.esec.examprep.domain.repository.QuestionRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetWrongAnswerQuestionsUseCase @Inject constructor(
    private val repository: QuestionRepository,
) {
    suspend operator fun invoke(profileId: String, subjectId: String? = null): List<Question> {
        val all = repository.observeWrongAnswers(profileId).first()
        val filtered = if (subjectId.isNullOrBlank() || subjectId == ALL_SUBJECTS) {
            all
        } else {
            all.filter { it.question.subjectId == subjectId }
        }
        return filtered.map { it.question }
    }

    companion object {
        const val ALL_SUBJECTS = "all"
    }
}
