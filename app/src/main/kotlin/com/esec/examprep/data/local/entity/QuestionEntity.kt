package com.esec.examprep.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "questions",
    foreignKeys = [
        ForeignKey(
            entity = SubjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["subjectId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("subjectId")],
)
data class QuestionEntity(
    @PrimaryKey val id: String,
    val subjectId: String,
    val year: Int,
    val text: String,
    val optionsJson: String,        // serialized List<OptionEntity> — avoids extra table join
    val correctOptionId: String,
    val explanation: String?,
    val difficultyLevel: String,
    val isBookmarked: Boolean = false,
)
