package com.esec.examprep.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esec.examprep.data.preferences.ThemeMode
import com.esec.examprep.data.preferences.UserPreferencesRepository
import com.esec.examprep.domain.repository.ExamSessionRepository
import com.esec.examprep.domain.usecase.ReloadQuestionBankUseCase
import com.esec.examprep.presentation.common.ActiveProfileHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val prefs: UserPreferencesRepository,
    private val examRepo: ExamSessionRepository,
    private val activeProfile: ActiveProfileHolder,
    private val reloadQuestionBank: ReloadQuestionBankUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            prefs.preferences.collect { p ->
                _state.update { it.copy(preferences = p, isLoading = false) }
            }
        }
    }

    fun onThemeModeChanged(mode: ThemeMode) {
        viewModelScope.launch { prefs.setThemeMode(mode) }
    }

    fun onExamLengthChanged(length: Int) {
        viewModelScope.launch { prefs.setExamLength(length) }
    }

    fun onTimerMinutesChanged(minutes: Int) {
        viewModelScope.launch { prefs.setTimerMinutes(minutes) }
    }

    fun showClearHistoryDialog(show: Boolean) {
        _state.update { it.copy(showClearHistoryDialog = show) }
    }

    fun clearHistoryConfirmed() {
        val profileId = activeProfile.activeProfile.value?.id ?: return
        viewModelScope.launch {
            examRepo.clearAllProgress(profileId)
            _state.update { it.copy(showClearHistoryDialog = false) }
        }
    }

    fun reloadQuestionBank() {
        if (_state.value.isReloadingBank) return
        _state.update { it.copy(isReloadingBank = true, reloadBankMessage = null) }
        viewModelScope.launch {
            val message = runCatching { reloadQuestionBank.invoke() }
                .fold(
                    onSuccess = { "Question bank reloaded." },
                    onFailure = { "Reload failed: ${it.message ?: "unknown error"}" },
                )
            _state.update { it.copy(isReloadingBank = false, reloadBankMessage = message) }
        }
    }

    fun consumeReloadBankMessage() {
        _state.update { it.copy(reloadBankMessage = null) }
    }
}
