package com.esec.examprep.presentation.wronganswers

import com.esec.examprep.domain.model.WrongAnswerEntry

data class WrongSubjectGroup(
    val subjectId: String,
    val subjectName: String,
    val entries: List<WrongAnswerEntry>,
)

data class WrongAnswersState(
    val groups: List<WrongSubjectGroup> = emptyList(),
    val isLoading: Boolean = true,
) {
    val isEmpty: Boolean get() = groups.isEmpty()
    val totalCount: Int get() = groups.sumOf { it.entries.size }
}
