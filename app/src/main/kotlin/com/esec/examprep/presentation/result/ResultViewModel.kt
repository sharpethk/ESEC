package com.esec.examprep.presentation.result

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esec.examprep.domain.model.ExamResult
import com.esec.examprep.domain.repository.ExamSessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: ExamSessionRepository,
) : ViewModel() {

    private val sessionId: String = checkNotNull(savedStateHandle["sessionId"])

    private val _result = MutableStateFlow<ExamResult?>(null)
    val result = _result.asStateFlow()

    init {
        viewModelScope.launch {
            _result.value = repository.getRecentResults(50)
                .firstOrNull { it.sessionId == sessionId }
        }
    }
}
