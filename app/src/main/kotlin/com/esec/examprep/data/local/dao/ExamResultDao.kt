package com.esec.examprep.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.esec.examprep.data.local.entity.ExamResultEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExamResultDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(result: ExamResultEntity)

    @Query("SELECT * FROM exam_results WHERE profileId = :profileId ORDER BY completedAt DESC")
    fun observeAll(profileId: String): Flow<List<ExamResultEntity>>

    @Query("SELECT * FROM exam_results WHERE profileId = :profileId ORDER BY completedAt DESC LIMIT :limit")
    suspend fun getRecent(profileId: String, limit: Int): List<ExamResultEntity>

    @Query("""
        SELECT subjectId, subjectName,
               COUNT(*) AS totalAttempts,
               MAX(scorePercent) AS bestScore,
               AVG(scorePercent) AS averageScore,
               SUM(totalQuestions) AS totalQuestionsAttempted,
               SUM(correctAnswers) AS totalCorrect
        FROM exam_results
        WHERE profileId = :profileId
        GROUP BY subjectId
    """)
    fun observeProgressBySubject(profileId: String): Flow<List<SubjectProgressRow>>

    @Query("DELETE FROM exam_results WHERE profileId = :profileId")
    suspend fun deleteAllForProfile(profileId: String)

    @Query("SELECT scorePercent FROM exam_results WHERE profileId = :profileId AND subjectId = :subjectId ORDER BY completedAt DESC LIMIT :limit")
    suspend fun getRecentScoresForSubject(profileId: String, subjectId: String, limit: Int): List<Float>

    @Query("SELECT COALESCE(SUM(durationSeconds) * 1.0 / NULLIF(SUM(totalQuestions), 0), 0.0) FROM exam_results WHERE profileId = :profileId")
    suspend fun getAvgSecondsPerQuestion(profileId: String): Double
}

data class SubjectProgressRow(
    val subjectId: String,
    val subjectName: String,
    val totalAttempts: Int,
    val bestScore: Float,
    val averageScore: Float,
    val totalQuestionsAttempted: Int,
    val totalCorrect: Int,
)
