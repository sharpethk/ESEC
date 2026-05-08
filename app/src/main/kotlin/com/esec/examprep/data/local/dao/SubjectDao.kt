package com.esec.examprep.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.esec.examprep.data.local.entity.SubjectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SubjectDao {
    @Query("SELECT * FROM subjects WHERE totalQuestions > 0 AND category = :category ORDER BY name ASC")
    fun observeByCategory(category: String): Flow<List<SubjectEntity>>

    @Query("SELECT * FROM subjects WHERE totalQuestions > 0 ORDER BY name ASC")
    fun observeAll(): Flow<List<SubjectEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(subjects: List<SubjectEntity>)

    @Query("SELECT COUNT(*) FROM subjects")
    suspend fun count(): Int

    @Query("SELECT COUNT(*) FROM subjects WHERE category = :category AND totalQuestions > 0")
    suspend fun countByCategory(category: String): Int

    @Query("SELECT id FROM subjects")
    suspend fun getAllIds(): List<String>

    @Query("DELETE FROM subjects WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<String>)
}
