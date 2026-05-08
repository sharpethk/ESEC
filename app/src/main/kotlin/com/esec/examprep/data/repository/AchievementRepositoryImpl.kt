package com.esec.examprep.data.repository

import com.esec.examprep.data.local.dao.ExamResultDao
import com.esec.examprep.data.local.dao.ProfileAchievementDao
import com.esec.examprep.data.local.dao.QuestionAttemptDao
import com.esec.examprep.data.local.dao.SubjectDao
import com.esec.examprep.data.local.entity.ProfileAchievementEntity
import com.esec.examprep.domain.model.Achievement
import com.esec.examprep.domain.model.AchievementCode
import com.esec.examprep.domain.model.ExamSession
import com.esec.examprep.domain.model.Grade
import com.esec.examprep.domain.repository.AchievementRepository
import com.esec.examprep.domain.repository.DailyChallengeRepository
import com.esec.examprep.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

private const val NOTEBOOK_HAS_WRONG_THRESHOLD = 1

@Singleton
class AchievementRepositoryImpl @Inject constructor(
    private val achievementDao: ProfileAchievementDao,
    private val examResultDao: ExamResultDao,
    private val attemptDao: QuestionAttemptDao,
    private val subjectDao: SubjectDao,
    private val profileRepository: ProfileRepository,
    private val dailyChallengeRepository: DailyChallengeRepository,
) : AchievementRepository {

    override fun observeForProfile(profileId: String): Flow<List<Achievement>> =
        achievementDao.observeForProfile(profileId).map { unlocked ->
            val unlockedByCode = unlocked.associateBy { it.code }
            AchievementCode.entries.map { code ->
                Achievement(
                    code = code,
                    unlockedAt = unlockedByCode[code.name]?.unlockedAt?.let(Instant::ofEpochMilli),
                )
            }
        }

    override suspend fun evaluate(profileId: String, lastSession: ExamSession?): List<AchievementCode> {
        val already = achievementDao.getUnlockedCodes(profileId).toHashSet()
        val newlyUnlocked = mutableListOf<AchievementCode>()
        val now = Instant.now().toEpochMilli()

        suspend fun unlock(code: AchievementCode) {
            if (code.name in already) return
            achievementDao.insert(ProfileAchievementEntity(profileId, code.name, now))
            already.add(code.name)
            newlyUnlocked.add(code)
        }

        val examCount = examResultDao.countForProfile(profileId)
        if (examCount > 0) unlock(AchievementCode.FIRST_EXAM)

        val attemptCount = attemptDao.countForProfile(profileId)
        if (attemptCount >= 100) unlock(AchievementCode.HUNDRED_QUESTIONS)

        if (lastSession != null && hasTenCorrectInARow(lastSession)) {
            unlock(AchievementCode.TEN_IN_A_ROW)
        }

        val gpa = computeWeightedGpa(profileId)
        if (gpa >= 3.5f) unlock(AchievementCode.GPA_3_5)

        val activeProfile = profileRepository.getActiveProfile()
        if (activeProfile?.id == profileId) {
            val totalSubjects = subjectDao.countByCategory(activeProfile.examCategory.storageKey)
            val attemptedSubjects = examResultDao.countDistinctSubjects(profileId)
            if (totalSubjects > 0 && attemptedSubjects >= totalSubjects) {
                unlock(AchievementCode.ALL_SUBJECTS)
            }
        }

        val streak = runCatching { dailyChallengeRepository.observeStreak(profileId).first() }.getOrDefault(0)
        if (streak >= 7) unlock(AchievementCode.STREAK_7)

        val totalWrongAttempts = attemptDao.countWrongForProfile(profileId)
        if (totalWrongAttempts >= NOTEBOOK_HAS_WRONG_THRESHOLD) {
            val stillWrong = attemptDao.observeStillWrong(profileId).first()
            if (stillWrong.isEmpty()) unlock(AchievementCode.NOTEBOOK_CLEARED)
        }

        return newlyUnlocked
    }

    override suspend fun deleteAllForProfile(profileId: String) {
        achievementDao.deleteAllForProfile(profileId)
    }

    private fun hasTenCorrectInARow(session: ExamSession): Boolean {
        var streak = 0
        for (q in session.questions) {
            val selected = session.answers[q.id]
            if (selected != null && selected == q.correctOptionId) {
                streak++
                if (streak >= 10) return true
            } else {
                streak = 0
            }
        }
        return false
    }

    private suspend fun computeWeightedGpa(profileId: String): Float {
        val rows = examResultDao.observeProgressBySubject(profileId).first()
        if (rows.isEmpty()) return 0f
        val items = rows.map { it.bestScore to it.totalAttempts.coerceAtLeast(1) }
        return Grade.weightedGpa(items)
    }
}
