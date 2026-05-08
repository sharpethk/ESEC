package com.esec.examprep.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * One generated daily challenge per (profileId, date). [date] is the local-date epoch day.
 * [questionIdsJson] is a JSON array of question ids in display order.
 */
@Entity(
    tableName = "daily_challenges",
    foreignKeys = [
        ForeignKey(
            entity = ProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["profileId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["profileId", "date"], unique = true),
        Index("date"),
    ],
)
data class DailyChallengeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val profileId: String,
    val date: Long,
    val questionIdsJson: String,
    val completedAt: Long? = null,
    val scorePercent: Float? = null,
    val durationSeconds: Int? = null,
)
