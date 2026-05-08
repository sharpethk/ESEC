package com.esec.examprep.data.repository

import com.esec.examprep.data.preferences.UserPreferencesRepository
import com.esec.examprep.domain.repository.ParentAccessRepository
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ParentAccessRepositoryImpl @Inject constructor(
    private val prefs: UserPreferencesRepository,
) : ParentAccessRepository {

    override suspend fun hasPin(): Boolean = prefs.getParentPinHash() != null

    override suspend fun setPin(pin: String) {
        prefs.setParentPinHash(hash(pin))
    }

    override suspend fun verifyPin(pin: String): Boolean {
        val stored = prefs.getParentPinHash() ?: return false
        return stored == hash(pin)
    }

    override suspend fun clearPin() {
        prefs.setParentPinHash(null)
    }

    private fun hash(pin: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest(pin.toByteArray(Charsets.UTF_8))
        return bytes.joinToString(separator = "") { "%02x".format(it) }
    }
}
