package com.esec.examprep.presentation.practice

import com.esec.examprep.domain.model.Subject

data class PracticeBuilderState(
    val subjects: List<Subject> = emptyList(),
    val selectedSubjectIds: Set<String> = emptySet(),
    val easyPercent: Int = 33,
    val mediumPercent: Int = 34,
    val hardPercent: Int = 33,
    val count: Int = 20,
    val isLoading: Boolean = true,
) {
    val canStart: Boolean get() = selectedSubjectIds.isNotEmpty() && count > 0
}
