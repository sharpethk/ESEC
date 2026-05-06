package com.esec.examprep.presentation.dashboard

import com.esec.examprep.domain.model.ExamResult
import com.esec.examprep.domain.model.UserProgress
import com.esec.examprep.domain.model.WeakTopic

data class DashboardState(
    val progress: List<UserProgress> = emptyList(),
    val recent: List<ExamResult> = emptyList(),
    val weakTopics: List<WeakTopic> = emptyList(),
    val avgTimePerQuestion: Double = 0.0,
    val isLoading: Boolean = true,
)
