package com.esec.examprep.domain.model

data class Profile(
    val id: String,
    val name: String,
    val avatarKey: String,
    val gradeLevel: Int,
    val examCategory: ExamCategory,
    val hasPin: Boolean,
    val createdAt: Long,
    val lastActiveAt: Long,
)
