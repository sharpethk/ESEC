package com.esec.examprep.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subjects")
data class SubjectEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val iconRes: Int,
    val totalQuestions: Int,
    val category: String,
)
