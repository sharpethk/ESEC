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

    /**
     * Returns one row per question whose most-recent attempt for [profileId] was incorrect.
     * Most recently missed first. Drives the Wrong Answer Notebook.
     */
    @Query("""
        WITH latest AS (
          SELECT questionId, MAX(attemptedAt) AS maxAt
          FROM question_attempts
          WHERE profileId = :profileId
          GROUP BY questionId
        )
        SELECT a.questionId, a.subjectId, a.attemptedAt AS lastAttemptedAt,
               a.selectedOptionId AS lastSelectedOptionId,
               (SELECT COUNT(*) FROM question_attempts a2
                WHERE a2.profileId = :profileId AND a2.questionId = a.questionId) AS attemptCount
        FROM question_attempts a
        JOIN latest ON a.questionId = latest.questionId AND a.attemptedAt = latest.maxAt
        WHERE a.profileId = :profileId AND a.isCorrect = 0
        ORDER BY a.attemptedAt DESC
    """)
    fun observeStillWrong(profileId: String): Flow<List<WrongAnswerRow>>
}

data class WrongAnswerRow(
    val questionId: String,
    val subjectId: String,
    val lastAttemptedAt: Long,
    val lastSelectedOptionId: String?,
    val attemptCount: Int,
)

data class WeakTopicRow(
    val subjectId: String,
    val errorRate: Float,
    val attempts: Int,
)

data class MissedQuestionRow(
    val questionId: String,
    val wrongCount: Int,
)
