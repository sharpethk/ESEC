package com.esec.examprep.domain.repository

import com.esec.examprep.domain.model.Achievement
import com.esec.examprep.domain.model.AchievementCode
import com.esec.examprep.domain.model.ExamSession
import kotlinx.coroutines.flow.Flow

interface AchievementRepository {
    fun observeForProfile(profileId: String): Flow<List<Achievement>>

    /** Returns achievement codes newly unlocked by [evaluate]. Empty list if nothing changed. */
    suspend fun evaluate(profileId: String, lastSession: ExamSession? = null): List<AchievementCode>

    suspend fun deleteAllForProfile(profileId: String)
}
