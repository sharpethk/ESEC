package com.esec.examprep.domain.repository

import com.esec.examprep.domain.model.ExamCategory
import com.esec.examprep.domain.model.Profile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun observeProfiles(): Flow<List<Profile>>
    fun observeActiveProfile(): Flow<Profile?>
    suspend fun getActiveProfile(): Profile?
    suspend fun setActiveProfile(id: String)
    suspend fun addProfile(name: String, avatarKey: String, gradeLevel: Int, category: ExamCategory, pin: String?): String
    suspend fun renameProfile(id: String, name: String)
    suspend fun updateAvatar(id: String, avatarKey: String)
    suspend fun updateCategory(id: String, category: ExamCategory)
    suspend fun deleteProfile(id: String)
    suspend fun setPin(id: String, pin: String?)
    suspend fun verifyPin(id: String, pin: String): Boolean
    suspend fun profileCount(): Int
}
