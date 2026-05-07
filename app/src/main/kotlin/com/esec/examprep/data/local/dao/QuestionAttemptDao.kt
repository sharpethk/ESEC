package com.esec.examprep.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.esec.examprep.data.local.entity.QuestionAttemptEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionAttemptDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(attempts: List<QuestionAttemptEntity>)

    @Query("""
        SELECT subjectId,
               (SUM(CASE WHEN isCorrect = 0 THEN 1 ELSE 0 END) * 1.0 / COUNT(*)) AS errorRate,
               COUNT(*) AS attempts
        FROM question_attempts
        WHERE profileId = :profileId
        GROUP BY subjectId
        ORDER BY errorRate DESC
    """)
    fun observeWeakTopics(profileId: String): Flow<List<WeakTopicRow>>

    @Query("""
        SELECT questionId, COUNT(*) AS wrongCount
        FROM question_attempts
        WHERE profileId = :profileId AND isCorrect = 0
        GROUP BY questionId
        ORDER BY wrongCount DESC
        LIMIT :limit
    """)
    suspend fun getMostMissedQuestionIds(profileId: String, limit: Int): List<MissedQuestionRow>

    @Query("DELETE FROM question_attempts WHERE profileId = :profileId")
    suspend fun deleteAllForProfile(profileId: String)
}

data class WeakTopicRow(
    val subjectId: String,
    val errorRate: Float,
    val attempts: Int,
)

data class MissedQuestionRow(
    val questionId: String,
    val wrongCount: Int,
)
