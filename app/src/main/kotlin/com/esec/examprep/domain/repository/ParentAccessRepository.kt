package com.esec.examprep.domain.repository

interface ParentAccessRepository {
    suspend fun hasPin(): Boolean
    suspend fun setPin(pin: String)
    suspend fun verifyPin(pin: String): Boolean
    suspend fun clearPin()
}
