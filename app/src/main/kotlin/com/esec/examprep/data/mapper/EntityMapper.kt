package com.esec.examprep.data.mapper

import com.esec.examprep.data.json.OptionDto
import com.esec.examprep.data.json.QuestionBankDto
import com.esec.examprep.data.json.SubjectDto
import com.esec.examprep.data.local.dao.SubjectProgressRow
import com.esec.examprep.data.local.entity.ExamResultEntity
import com.esec.examprep.data.local.entity.QuestionEntity
import com.esec.examprep.data.local.entity.SubjectEntity
import com.esec.examprep.domain.model.DifficultyLevel
import com.esec.examprep.domain.model.ExamCategory
import com.esec.examprep.domain.model.ExamResult
import com.esec.examprep.domain.model.Option
import com.esec.examprep.domain.model.Question
import com.esec.examprep.domain.model.QuestionResult
import com.esec.examprep.domain.model.Subject
import com.esec.examprep.domain.model.UserProgress
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.Instant

private val gson = Gson()

/**
 * Normalize the raw category string from the question-bank JSON to the
 * canonical [ExamCategory.storageKey] used everywhere else in the app
 * (profile records, DAO queries, etc.). Without this, JSON values like
 * "Grade 8" never match the "GRADE_8" key the SubjectDao filters on, and
 * the subject list comes up empty.
 */
private fun normalizeCategory(raw: String): String {
    val trimmed = raw.trim()
    if (trimmed.isEmpty()) return ExamCategory.GRADE_8.storageKey
    // Already canonical?
    ExamCategory.entries.firstOrNull { it.storageKey.equals(trimmed, ignoreCase = true) }
        ?.let { return it.storageKey }
    // Map human-readable forms ("Grade 8", "grade-8", "matric", ...).
    val collapsed = trimmed.uppercase().replace(Regex("[\\s\\-]+"), "_")
    ExamCategory.entries.firstOrNull { it.storageKey == collapsed }
        ?.let { return it.storageKey }
    return when {
        collapsed.contains("GRADE") && collapsed.contains("8") -> ExamCategory.GRADE_8.storageKey
        collapsed.startsWith("MATRIC") -> ExamCategory.MATRICULATION.storageKey
        else -> ExamCategory.GRADE_8.storageKey
    }
}

// ---- DTO → Entity ----

fun SubjectDto.toEntity(totalQuestions: Int) = SubjectEntity(
    id = id, name = name, description = description,
    iconRes = 0, totalQuestions = totalQuestions,
    category = normalizeCategory(category),
)

fun com.esec.examprep.data.json.QuestionDto.toEntity() = QuestionEntity(
    id = id, subjectId = subjectId, year = year, text = text,
    optionsJson = gson.toJson(options),
    correctOptionId = correctOptionId,
    explanation = explanation,
    difficultyLevel = difficulty,
)

// ---- Entity → Domain ----

fun SubjectEntity.toDomain() = Subject(
    id = id, name = name, description = description,
    iconRes = iconRes, totalQuestions = totalQuestions, category = category,
)

fun QuestionEntity.toDomain(isBookmarked: Boolean = false): Question {
    val optionType = object : TypeToken<List<OptionDto>>() {}.type
    val options: List<OptionDto> = gson.fromJson(optionsJson, optionType)
    return Question(
        id = id, subjectId = subjectId, year = year, text = text,
        options = options.map { Option(it.id, it.text) },
        correctOptionId = correctOptionId,
        explanation = explanation,
        difficultyLevel = when (difficultyLevel.uppercase()) {
            "EASY"   -> DifficultyLevel.EASY
            "HARD"   -> DifficultyLevel.HARD
            else     -> DifficultyLevel.MEDIUM
        },
        isBookmarked = isBookmarked,
    )
}

fun ExamResultEntity.toDomain(breakdown: List<QuestionResult> = emptyList()) = ExamResult(
    sessionId = sessionId, subjectId = subjectId, subjectName = subjectName,
    totalQuestions = totalQuestions, correctAnswers = correctAnswers,
    incorrectAnswers = incorrectAnswers, skippedAnswers = skippedAnswers,
    scorePercent = scorePercent, passed = passed, durationSeconds = durationSeconds,
    completedAt = Instant.ofEpochSecond(completedAt),
    questionBreakdown = breakdown,
    year = year,
)

fun ExamResult.toEntity(profileId: String) = ExamResultEntity(
    sessionId = sessionId, profileId = profileId,
    subjectId = subjectId, subjectName = subjectName,
    totalQuestions = totalQuestions, correctAnswers = correctAnswers,
    incorrectAnswers = incorrectAnswers, skippedAnswers = skippedAnswers,
    scorePercent = scorePercent, passed = passed, durationSeconds = durationSeconds,
    completedAt = completedAt.epochSecond,
    answersJson = gson.toJson(questionBreakdown.associate { it.question.id to it.selectedOptionId }),
    year = year,
)

fun SubjectProgressRow.toDomain() = UserProgress(
    subjectId = subjectId, subjectName = subjectName,
    totalAttempts = totalAttempts, bestScore = bestScore,
    averageScore = averageScore, totalQuestionsAttempted = totalQuestionsAttempted,
    totalCorrect = totalCorrect,
)
