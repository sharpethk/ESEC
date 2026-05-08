package com.esec.examprep.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.esec.examprep.data.local.entity.ProfileAchievementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileAchievementDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: ProfileAchievementEntity): Long

    @Query("SELECT * FROM profile_achievements WHERE profileId = :profileId ORDER BY unlockedAt DESC")
    fun observeForProfile(profileId: String): Flow<List<ProfileAchievementEntity>>

    @Query("SELECT code FROM profile_achievements WHERE profileId = :profileId")
    suspend fun getUnlockedCodes(profileId: String): List<String>

    @Query("DELETE FROM profile_achievements WHERE profileId = :profileId")
    suspend fun deleteAllForProfile(profileId: String)
}
