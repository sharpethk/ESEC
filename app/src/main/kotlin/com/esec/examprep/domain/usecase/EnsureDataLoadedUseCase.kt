package com.esec.examprep.domain.usecase

import com.esec.examprep.data.preferences.UserPreferencesRepository
import com.esec.examprep.domain.repository.QuestionRepository
import javax.inject.Inject

class EnsureDataLoadedUseCase @Inject constructor(
    private val repository: QuestionRepository,
    private val prefs: UserPreferencesRepository,
) {
    suspend operator fun invoke() {
        val storedVersion = prefs.getQuestionBankVersion()
        val isEmpty = !repository.isDataLoaded()
        if (isEmpty || storedVersion < CURRENT_BANK_VERSION) {
            repository.loadQuestionsFromEncryptedAsset()
            prefs.setQuestionBankVersion(CURRENT_BANK_VERSION)
        }
    }

    companion object {
        const val CURRENT_BANK_VERSION = 3
    }
}
