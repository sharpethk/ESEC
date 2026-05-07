package com.esec.examprep.presentation.questiondetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esec.examprep.domain.usecase.GetQuestionByIdUseCase
import com.esec.examprep.domain.usecase.GetSubjectsUseCase
import com.esec.examprep.domain.usecase.ToggleBookmarkUseCase
import com.esec.examprep.presentation.common.ActiveProfileHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuestionDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getQuestionById: GetQuestionByIdUseCase,
    private val getSubjects: GetSubjectsUseCase,
    private val toggleBookmark: ToggleBookmarkUseCase,
    private val activeProfile: ActiveProfileHolder,
) : ViewModel() {

    private val questionId: String = checkNotNull(savedStateHandle["questionId"])
    private val _state = MutableStateFlow(QuestionDetailState())
    val state = _state.asStateFlow()

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            val profile = activeProfile.activeProfile.value
                ?: run {
                    _state.update { it.copy(isLoading = false, notFound = true) }
                    return@launch
                }
            val question = getQuestionById(profile.id, questionId)
            if (question == null) {
                _state.update { it.copy(isLoading = false, notFound = true) }
                return@launch
            }
            val subjects = runCatching { getSubjects(profile.examCategory).first() }.getOrDefault(emptyList())
            val subjectName = subjects.firstOrNull { it.id == question.subjectId }?.name
                ?: question.subjectId
            _state.update {
                it.copy(
                    question = question,
                    subjectName = subjectName,
                    isLoading = false,
                    notFound = false,
                )
            }
        }
    }

    fun toggleBookmark() {
        val current = _state.value.question ?: return
        val profileId = activeProfile.activeProfile.value?.id ?: return
        val newValue = !current.isBookmarked
        _state.update { it.copy(question = current.copy(isBookmarked = newValue)) }
        viewModelScope.launch { toggleBookmark.invoke(profileId, current.id, newValue) }
    }
}
