package com.esec.examprep.presentation.questiondetail

import com.esec.examprep.domain.model.Question

data class QuestionDetailState(
    val question: Question? = null,
    val subjectName: String = "",
    val isLoading: Boolean = true,
    val notFound: Boolean = false,
)
