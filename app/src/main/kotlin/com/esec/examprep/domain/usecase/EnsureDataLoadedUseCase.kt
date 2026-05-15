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
        // Bump when the bundled questions_bank.enc changes so existing
        // installs trigger a re-seed via [invoke]. v4: normalize subject
        // categories to ExamCategory.storageKey (was "Grade 8", now "GRADE_8").
        // v5: re-encrypt after 2026-05 question-bank edits that were
        // previously blocked by an over-strict validator (duplicate option
        // ids are now a warning, matching runtime drop behavior).
        const val CURRENT_BANK_VERSION = 5
    }
}
