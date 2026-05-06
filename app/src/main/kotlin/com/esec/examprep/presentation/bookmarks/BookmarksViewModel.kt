package com.esec.examprep.presentation.bookmarks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esec.examprep.domain.usecase.GetBookmarkedQuestionsUseCase
import com.esec.examprep.domain.usecase.ToggleBookmarkUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarksViewModel @Inject constructor(
    private val getBookmarked: GetBookmarkedQuestionsUseCase,
    private val toggleBookmark: ToggleBookmarkUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(BookmarksState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            getBookmarked().collect { list ->
                _state.update { it.copy(questions = list, isLoading = false) }
            }
        }
    }

    fun removeBookmark(questionId: String) {
        viewModelScope.launch { toggleBookmark(questionId, false) }
    }
}
