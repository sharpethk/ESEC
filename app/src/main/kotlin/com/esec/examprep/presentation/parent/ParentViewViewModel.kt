package com.esec.examprep.presentation.parent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esec.examprep.domain.model.ParentProfileSummary
import com.esec.examprep.domain.usecase.GetParentSummariesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ParentViewState(
    val isLoading: Boolean = true,
    val summaries: List<ParentProfileSummary> = emptyList(),
)

@HiltViewModel
class ParentViewViewModel @Inject constructor(
    private val getParentSummaries: GetParentSummariesUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(ParentViewState())
    val state: StateFlow<ParentViewState> = _state.asStateFlow()

    init { refresh() }

    fun refresh() {
        viewModelScope.launch {
            _state.value = ParentViewState(isLoading = true)
            val list = runCatching { getParentSummaries() }.getOrDefault(emptyList())
            _state.value = ParentViewState(isLoading = false, summaries = list)
        }
    }
}
