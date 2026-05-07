package com.esec.examprep.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.esec.examprep.data.local.entity.BookmarkEntity
import com.esec.examprep.data.local.entity.QuestionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE profileId = :profileId AND questionId = :questionId")
    suspend fun delete(profileId: String, questionId: String)

    @Query(
        "SELECT q.* FROM questions q INNER JOIN bookmarks b " +
            "ON q.id = b.questionId WHERE b.profileId = :profileId " +
            "ORDER BY q.subjectId, q.year"
    )
    fun observeForProfile(profileId: String): Flow<List<QuestionEntity>>

    @Query("SELECT COUNT(*) FROM bookmarks WHERE profileId = :profileId")
    fun observeCount(profileId: String): Flow<Int>

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE profileId = :profileId AND questionId = :questionId)")
    suspend fun isBookmarked(profileId: String, questionId: String): Boolean

    @Query("SELECT questionId FROM bookmarks WHERE profileId = :profileId")
    suspend fun getQuestionIds(profileId: String): List<String>
}
