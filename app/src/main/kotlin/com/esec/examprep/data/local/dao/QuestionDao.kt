package com.esec.examprep.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.esec.examprep.data.local.entity.QuestionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(questions: List<QuestionEntity>)

    @Query("SELECT * FROM questions WHERE subjectId = :subjectId ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandomBySubject(subjectId: String, limit: Int): List<QuestionEntity>

    @Query("SELECT * FROM questions WHERE subjectId = :subjectId")
    suspend fun getAllBySubject(subjectId: String): List<QuestionEntity>

    @Query("SELECT COUNT(*) FROM questions WHERE subjectId = :subjectId")
    suspend fun countBySubject(subjectId: String): Int

    @Query("SELECT DISTINCT year FROM questions WHERE subjectId = :subjectId AND year > 0 ORDER BY year DESC")
    suspend fun getYearsForSubject(subjectId: String): List<Int>

    @Query(
        "SELECT year, COUNT(*) AS questionCount FROM questions " +
            "WHERE subjectId = :subjectId AND year > 0 GROUP BY year ORDER BY year DESC"
    )
    suspend fun getYearCountsForSubject(subjectId: String): List<YearCountRow>

    @Query("SELECT * FROM questions WHERE subjectId = :subjectId AND year = :year ORDER BY id ASC")
    suspend fun getBySubjectAndYear(subjectId: String, year: Int): List<QuestionEntity>

    @Query("SELECT COUNT(*) FROM questions")
    suspend fun totalCount(): Int

    @Query("SELECT * FROM questions WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): QuestionEntity?

    @Query("UPDATE questions SET isBookmarked = :bookmarked WHERE id = :id")
    suspend fun setBookmark(id: String, bookmarked: Boolean)

    @Query("SELECT * FROM questions WHERE isBookmarked = 1 ORDER BY subjectId, year")
    fun observeBookmarked(): Flow<List<QuestionEntity>>

    @Query("SELECT COUNT(*) FROM questions WHERE isBookmarked = 1")
    fun observeBookmarkedCount(): Flow<Int>
}

data class YearCountRow(
    val year: Int,
    val questionCount: Int,
)
