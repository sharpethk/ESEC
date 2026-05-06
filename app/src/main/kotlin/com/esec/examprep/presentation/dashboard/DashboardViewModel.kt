package com.esec.examprep.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esec.examprep.domain.model.ExamResult
import com.esec.examprep.domain.model.UserProgress
import com.esec.examprep.domain.repository.ExamSessionRepository
import com.esec.examprep.domain.usecase.GetProgressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    getProgress: GetProgressUseCase,
    private val repository: ExamSessionRepository,
) : ViewModel() {

    val progress = getProgress()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun clearAllProgress() {
        viewModelScope.launch { repository.clearAllProgress() }
    }
}
