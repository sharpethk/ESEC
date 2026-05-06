package com.esec.examprep.domain.usecase

import com.esec.examprep.domain.repository.QuestionRepository
import javax.inject.Inject

class EnsureDataLoadedUseCase @Inject constructor(
    private val repository: QuestionRepository,
) {
    suspend operator fun invoke() {
        if (!repository.isDataLoaded()) {
            repository.loadQuestionsFromEncryptedAsset()
        }
    }
}
