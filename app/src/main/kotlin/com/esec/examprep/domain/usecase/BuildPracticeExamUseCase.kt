package com.esec.examprep.domain.usecase

import com.esec.examprep.domain.model.DifficultyLevel
import com.esec.examprep.domain.model.Question
import com.esec.examprep.domain.repository.QuestionRepository
import javax.inject.Inject

/**
 * Builds a custom practice exam from a profile's question pool.
 *
 * Picks up to [count] questions from the requested subjects, filtered by year range
 * (when provided) and balanced by the requested difficulty mix percentages. Falls back
 * to whatever questions are available when a difficulty bucket runs out.
 */
class BuildPracticeExamUseCase @Inject constructor(
    private val repository: QuestionRepository,
) {
    data class Config(
        val subjectIds: List<String>,
        val yearRange: IntRange? = null,
        val easyPercent: Int = 33,
        val mediumPercent: Int = 34,
        val hardPercent: Int = 33,
        val count: Int = 20,
    )

    suspend operator fun invoke(profileId: String, config: Config): List<Question> {
        if (config.subjectIds.isEmpty() || config.count <= 0) return emptyList()

        val pool = mutableListOf<Question>()
        for (subjectId in config.subjectIds) {
            pool += repository.getQuestionsBySubject(profileId, subjectId, limit = null)
        }

        val filtered = pool.filter { q ->
            config.yearRange?.let { range -> q.year in range || q.year == 0 } ?: true
        }
        if (filtered.isEmpty()) return emptyList()

        val byDifficulty = filtered.groupBy { it.difficultyLevel }
        val easy = byDifficulty[DifficultyLevel.EASY].orEmpty().shuffled()
        val med  = byDifficulty[DifficultyLevel.MEDIUM].orEmpty().shuffled()
        val hard = byDifficulty[DifficultyLevel.HARD].orEmpty().shuffled()

        val totalPct = (config.easyPercent + config.mediumPercent + config.hardPercent).coerceAtLeast(1)
        val easyTarget = config.count * config.easyPercent / totalPct
        val medTarget  = config.count * config.mediumPercent / totalPct
        val hardTarget = config.count - easyTarget - medTarget

        val picked = mutableListOf<Question>()
        picked += easy.take(easyTarget)
        picked += med.take(medTarget)
        picked += hard.take(hardTarget)

        if (picked.size < config.count) {
            val takenIds = picked.map { it.id }.toHashSet()
            val remainder = filtered.filter { it.id !in takenIds }.shuffled()
            picked += remainder.take(config.count - picked.size)
        }

        return picked.shuffled()
    }
}
