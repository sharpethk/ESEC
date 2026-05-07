package com.esec.examprep.presentation.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esec.examprep.domain.model.ExamCategory
import com.esec.examprep.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileEditState(
    val isNew: Boolean = true,
    val profileId: String? = null,
    val name: String = "",
    val avatarKey: String = ProfileAvatars.keys.first(),
    val category: ExamCategory = ExamCategory.GRADE_8,
    val pinEnabled: Boolean = false,
    val pin: String = "",
    val isLoading: Boolean = true,
    val saved: Boolean = false,
    val canDelete: Boolean = false,
)

@HiltViewModel
class ProfileEditViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repo: ProfileRepository,
) : ViewModel() {

    private val argId: String? = savedStateHandle["profileId"]

    private val _state = MutableStateFlow(ProfileEditState(isNew = argId.isNullOrEmpty()))
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            if (argId.isNullOrEmpty()) {
                _state.update { it.copy(isLoading = false, canDelete = false) }
            } else {
                val profileCount = repo.profileCount()
                val list = repo.observeProfiles().first()
                val profile = list.firstOrNull { it.id == argId }
                if (profile == null) {
                    _state.update { it.copy(isLoading = false) }
                } else {
                    _state.update {
                        it.copy(
                            isNew = false,
                            profileId = profile.id,
                            name = profile.name,
                            avatarKey = profile.avatarKey,
                            category = profile.examCategory,
                            pinEnabled = profile.hasPin,
                            isLoading = false,
                            canDelete = profileCount > 1,
                        )
                    }
                }
            }
        }
    }

    fun setName(v: String) = _state.update { it.copy(name = v) }
    fun setAvatar(v: String) = _state.update { it.copy(avatarKey = v) }
    fun setCategory(v: ExamCategory) = _state.update { it.copy(category = v) }
    fun setPinEnabled(v: Boolean) = _state.update { it.copy(pinEnabled = v, pin = if (!v) "" else it.pin) }
    fun setPin(v: String) {
        if (v.length <= 4 && v.all(Char::isDigit)) _state.update { it.copy(pin = v) }
    }

    fun save() {
        val s = _state.value
        if (s.name.isBlank()) return
        if (s.pinEnabled && s.pin.length != 4) return
        viewModelScope.launch {
            val gradeLevel = if (s.category == ExamCategory.GRADE_8) 8 else 12
            if (s.isNew) {
                repo.addProfile(
                    name = s.name.trim(),
                    avatarKey = s.avatarKey,
                    gradeLevel = gradeLevel,
                    category = s.category,
                    pin = if (s.pinEnabled) s.pin else null,
                )
            } else {
                val id = s.profileId ?: return@launch
                repo.renameProfile(id, s.name.trim())
                repo.updateAvatar(id, s.avatarKey)
                repo.updateCategory(id, s.category)
                repo.setPin(id, if (s.pinEnabled) s.pin else null)
            }
            _state.update { it.copy(saved = true) }
        }
    }

    fun delete() {
        val id = _state.value.profileId ?: return
        viewModelScope.launch {
            repo.deleteProfile(id)
            _state.update { it.copy(saved = true) }
        }
    }
}
