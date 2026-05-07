package com.esec.examprep.presentation.bookmarks

import com.esec.examprep.domain.model.Question

data class YearGroup(
    val year: Int,
    val questions: List<Question>,
)

data class SubjectGroup(
    val subjectId: String,
    val subjectName: String,
    val years: List<YearGroup>,
) {
    val totalCount: Int get() = years.sumOf { it.questions.size }
}

data class BookmarksState(
    val groups: List<SubjectGroup> = emptyList(),
    val isLoading: Boolean = true,
) {
    val isEmpty: Boolean get() = groups.isEmpty()
    val totalCount: Int get() = groups.sumOf { it.totalCount }
}
