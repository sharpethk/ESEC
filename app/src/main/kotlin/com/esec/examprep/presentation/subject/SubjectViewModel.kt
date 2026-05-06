package com.esec.examprep.presentation.subject

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esec.examprep.domain.model.Subject
import com.esec.examprep.domain.usecase.GetSubjectsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SubjectViewModel @Inject constructor(
    getSubjects: GetSubjectsUseCase,
) : ViewModel() {

    val subjects = getSubjects()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}
