package com.esec.examprep.domain.model

data class Subject(
    val id: String,
    val name: String,
    val description: String,
    val iconRes: Int,
    val totalQuestions: Int,
    val category: String,
)
