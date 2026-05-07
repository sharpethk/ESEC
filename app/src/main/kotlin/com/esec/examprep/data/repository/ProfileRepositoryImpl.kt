package com.esec.examprep.data.repository

import com.esec.examprep.data.local.dao.ProfileDao
import com.esec.examprep.data.local.entity.ProfileEntity
import com.esec.examprep.data.preferences.UserPreferencesRepository
import com.esec.examprep.domain.model.ExamCategory
import com.esec.examprep.domain.model.Profile
import com.esec.examprep.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.security.MessageDigest
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepositoryImpl @Inject constructor(
    private val profileDao: ProfileDao,
    private val prefs: UserPreferencesRepository,
) : ProfileRepository {

    override fun observeProfiles(): Flow<List<Profile>> =
        profileDao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeActiveProfile(): Flow<Profile?> =
        combine(profileDao.observeAll(), prefs.activeProfileId) { profiles, activeId ->
            val target = activeId ?: profiles.firstOrNull()?.id
            profiles.firstOrNull { it.id == target }?.toDomain()
        }

    override suspend fun getActiveProfile(): Profile? {
        val id = prefs.getActiveProfileId() ?: return null
        return profileDao.getById(id)?.toDomain()
    }

    override suspend fun setActiveProfile(id: String) {
        prefs.setActiveProfileId(id)
        profileDao.updateLastActive(id, System.currentTimeMillis() / 1000)
    }

    override suspend fun addProfile(
        name: String,
        avatarKey: String,
        gradeLevel: Int,
        category: ExamCategory,
        pin: String?,
    ): String {
        val now = System.currentTimeMillis() / 1000
        val id = UUID.randomUUID().toString()
        profileDao.insert(
            ProfileEntity(
                id = id,
                name = name,
                avatarKey = avatarKey,
                gradeLevel = gradeLevel,
                examCategory = category.storageKey,
                pinHash = pin?.let { hashPin(it) },
                createdAt = now,
                lastActiveAt = now,
            )
        )
        return id
    }

    override suspend fun renameProfile(id: String, name: String) {
        val current = profileDao.getById(id) ?: return
        profileDao.update(current.copy(name = name))
    }

    override suspend fun updateAvatar(id: String, avatarKey: String) {
        val current = profileDao.getById(id) ?: return
        profileDao.update(current.copy(avatarKey = avatarKey))
    }

    override suspend fun updateCategory(id: String, category: ExamCategory) {
        profileDao.updateCategory(id, category.storageKey)
    }

    override suspend fun deleteProfile(id: String) {
        profileDao.deleteById(id)
        if (prefs.getActiveProfileId() == id) {
            prefs.setActiveProfileId(null)
        }
    }

    override suspend fun setPin(id: String, pin: String?) {
        profileDao.updatePin(id, pin?.let { hashPin(it) })
    }

    override suspend fun verifyPin(id: String, pin: String): Boolean {
        val p = profileDao.getById(id) ?: return false
        val stored = p.pinHash ?: return true
        return stored == hashPin(pin)
    }

    override suspend fun profileCount(): Int = profileDao.count()

    private fun hashPin(pin: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest(pin.toByteArray(Charsets.UTF_8))
        return bytes.joinToString(separator = "") { "%02x".format(it) }
    }

    private fun ProfileEntity.toDomain() = Profile(
        id = id,
        name = name,
        avatarKey = avatarKey,
        gradeLevel = gradeLevel,
        examCategory = ExamCategory.fromStorageKey(examCategory),
        hasPin = pinHash != null,
        createdAt = createdAt,
        lastActiveAt = lastActiveAt,
    )
}
