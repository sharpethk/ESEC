package com.esec.examprep.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "exam_results",
    foreignKeys = [
        ForeignKey(
            entity = ProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["profileId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("profileId")],
)
data class ExamResultEntity(
    @PrimaryKey val sessionId: String,
    val profileId: String,
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
    val year: Int? = null,           // null for random/timed runs; >0 for past-paper runs
)
