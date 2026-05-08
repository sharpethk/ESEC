package com.esec.examprep.domain.usecase

import com.esec.examprep.domain.model.Grade
import com.esec.examprep.domain.model.ParentProfileSummary
import com.esec.examprep.domain.repository.DailyChallengeRepository
import com.esec.examprep.domain.repository.ExamSessionRepository
import com.esec.examprep.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetParentSummariesUseCase @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val examSessionRepository: ExamSessionRepository,
    private val dailyChallengeRepository: DailyChallengeRepository,
) {
    suspend operator fun invoke(): List<ParentProfileSummary> {
        val profiles = profileRepository.observeProfiles().first()
        return profiles.map { profile ->
            val pid = profile.id
            val progress = examSessionRepository.getProgressBySubject(pid).first()
            val recent = examSessionRepository.getRecentResults(pid, limit = 5)
            val weak = examSessionRepository.observeWeakTopics(pid).first().take(3)
            val streak = runCatching { dailyChallengeRepository.observeStreak(pid).first() }.getOrDefault(0)
            val avgPerQ = runCatching { examSessionRepository.getAvgSecondsPerQuestion(pid) }.getOrDefault(0.0)

            val totalExams = progress.sumOf { it.totalAttempts }
            val avgScore = if (progress.isEmpty()) 0f
                else progress.sumOf { (it.averageScore * it.totalAttempts).toDouble() }.toFloat() /
                    progress.sumOf { it.totalAttempts }.coerceAtLeast(1)
            val gpa = Grade.weightedGpa(progress.map { it.bestScore to it.totalAttempts.coerceAtLeast(1) })

            ParentProfileSummary(
                profile = profile,
                totalExams = totalExams,
                avgScorePercent = avgScore,
                weightedGpa = gpa,
                streakDays = streak,
                recentExams = recent,
                weakSubjects = weak,
                avgSecondsPerQuestion = avgPerQ,
            )
        }
    }
}
