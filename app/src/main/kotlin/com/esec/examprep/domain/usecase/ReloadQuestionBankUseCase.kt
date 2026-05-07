package com.esec.examprep.domain.usecase

import com.esec.examprep.data.preferences.UserPreferencesRepository
import com.esec.examprep.domain.repository.QuestionRepository
import javax.inject.Inject

/**
 * Force-reseeds the question bank from the bundled encrypted asset.
 * Bumps the stored bank version to the current target so [EnsureDataLoadedUseCase]
 * does not re-trigger on the next launch. Used by the debug "Reload question bank"
 * action in Settings.
 */
class ReloadQuestionBankUseCase @Inject constructor(
    private val repository: QuestionRepository,
    private val prefs: UserPreferencesRepository,
) {
    suspend operator fun invoke() {
        repository.forceReseedFromEncryptedAsset()
        prefs.setQuestionBankVersion(EnsureDataLoadedUseCase.CURRENT_BANK_VERSION)
    }
}
