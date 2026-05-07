package com.esec.examprep.domain.usecase

import com.esec.examprep.domain.repository.QuestionRepository
import javax.inject.Inject

class ToggleBookmarkUseCase @Inject constructor(
    private val repository: QuestionRepository,
) {
    suspend operator fun invoke(profileId: String, questionId: String, bookmarked: Boolean) =
        repository.setBookmark(profileId, questionId, bookmarked)
}
