package com.esec.examprep.presentation.bookmarks

import com.esec.examprep.domain.model.Question

data class BookmarksState(
    val questions: List<Question> = emptyList(),
    val isLoading: Boolean = true,
)
