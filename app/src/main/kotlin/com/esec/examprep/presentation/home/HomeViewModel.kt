package com.esec.examprep.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esec.examprep.domain.usecase.EnsureDataLoadedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val ensureDataLoaded: EnsureDataLoadedUseCase,
) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            runCatching { ensureDataLoaded() }
                .onFailure { _error.value = it.message }
                .also { _isLoading.value = false }
        }
    }

    fun retryLoad() {
        _error.value = null
        _isLoading.value = true
        loadData()
    }
}
