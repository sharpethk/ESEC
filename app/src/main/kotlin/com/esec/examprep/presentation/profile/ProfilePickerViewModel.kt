package com.esec.examprep.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esec.examprep.domain.model.Profile
import com.esec.examprep.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfilePickerState(
    val pinDialogProfileId: String? = null,
    val pinError: Boolean = false,
)

@HiltViewModel
class ProfilePickerViewModel @Inject constructor(
    private val repo: ProfileRepository,
) : ViewModel() {

    val profiles = repo.observeProfiles()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _state = MutableStateFlow(ProfilePickerState())
    val state = _state.asStateFlow()

    fun selectProfile(profile: Profile, onPicked: () -> Unit) {
        if (profile.hasPin) {
            _state.update { it.copy(pinDialogProfileId = profile.id, pinError = false) }
        } else {
            viewModelScope.launch {
                repo.setActiveProfile(profile.id)
                onPicked()
            }
        }
    }

    fun submitPin(pin: String, onPicked: () -> Unit) {
        val id = _state.value.pinDialogProfileId ?: return
        viewModelScope.launch {
            val ok = repo.verifyPin(id, pin)
            if (ok) {
                repo.setActiveProfile(id)
                _state.update { it.copy(pinDialogProfileId = null, pinError = false) }
                onPicked()
            } else {
                _state.update { it.copy(pinError = true) }
            }
        }
    }

    fun dismissPinDialog() {
        _state.update { it.copy(pinDialogProfileId = null, pinError = false) }
    }
}
