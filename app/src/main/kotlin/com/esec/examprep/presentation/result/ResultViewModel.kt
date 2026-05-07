package com.esec.examprep.presentation.result

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esec.examprep.domain.model.ExamResult
import com.esec.examprep.domain.repository.ExamSessionRepository
import com.esec.examprep.presentation.common.ActiveProfileHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: ExamSessionRepository,
    private val activeProfile: ActiveProfileHolder,
) : ViewModel() {

    private val sessionId: String = checkNotNull(savedStateHandle["sessionId"])

    private val _result = MutableStateFlow<ExamResult?>(null)
    val result = _result.asStateFlow()

    init {
        viewModelScope.launch {
            val profileId = activeProfile.activeProfile.value?.id ?: return@launch
            _result.value = repository.getRecentResults(profileId, 50)
                .firstOrNull { it.sessionId == sessionId }
        }
    }
}
