package com.esec.examprep.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exam_results")
data class ExamResultEntity(
    @PrimaryKey val sessionId: String,
    val subjectId: String,
    val subjectName: String,
    val totalQuestions: Int,
    val correctAnswers: Int,
    val incorrectAnswers: Int,
    val skippedAnswers: Int,
    val scorePercent: Float,
    val passed: Boolean,
    val durationSeconds: Long,
    val completedAt: Long,           // epoch seconds
    val answersJson: String,         // Map<questionId, selectedOptionId> serialized
)
