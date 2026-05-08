package com.esec.examprep.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.esec.examprep.data.local.entity.DailyChallengeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyChallengeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: DailyChallengeEntity): Long

    @Query("SELECT * FROM daily_challenges WHERE profileId = :profileId AND date = :date LIMIT 1")
    suspend fun getForDate(profileId: String, date: Long): DailyChallengeEntity?

    @Query("SELECT * FROM daily_challenges WHERE profileId = :profileId AND date = :date LIMIT 1")
    fun observeForDate(profileId: String, date: Long): Flow<DailyChallengeEntity?>

    @Query("""
        UPDATE daily_challenges
           SET completedAt = :completedAt,
               scorePercent = :scorePercent,
               durationSeconds = :durationSeconds
         WHERE profileId = :profileId AND date = :date
    """)
    suspend fun markCompleted(
        profileId: String,
        date: Long,
        completedAt: Long,
        scorePercent: Float,
        durationSeconds: Int,
    )

    @Query("""
        SELECT date FROM daily_challenges
         WHERE profileId = :profileId AND completedAt IS NOT NULL
         ORDER BY date DESC
    """)
    fun observeCompletedDates(profileId: String): Flow<List<Long>>

    @Query("DELETE FROM daily_challenges WHERE profileId = :profileId")
    suspend fun deleteAllForProfile(profileId: String)
}
