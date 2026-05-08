package com.esec.examprep.data.repository

import com.esec.examprep.data.local.dao.DailyChallengeDao
import com.esec.examprep.data.local.dao.QuestionDao
import com.esec.examprep.data.local.entity.DailyChallengeEntity
import com.esec.examprep.data.mapper.toDomain
import com.esec.examprep.domain.model.DailyChallenge
import com.esec.examprep.domain.repository.DailyChallengeRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

private const val DAILY_CHALLENGE_SIZE = 10
private val LIST_TYPE = object : TypeToken<List<String>>() {}.type

@Singleton
class DailyChallengeRepositoryImpl @Inject constructor(
    private val dailyChallengeDao: DailyChallengeDao,
    private val questionDao: QuestionDao,
    private val gson: Gson,
) : DailyChallengeRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeTodayChallenge(profileId: String): Flow<DailyChallenge?> = flow {
        val today = LocalDate.now()
        ensureTodayChallenge(profileId, today)
        emitAll(
            dailyChallengeDao.observeForDate(profileId, today.toEpochDay())
                .map { entity -> entity?.let { hydrate(it) } }
        )
    }

    override fun observeStreak(profileId: String): Flow<Int> =
        dailyChallengeDao.observeCompletedDates(profileId)
            .map { dates -> computeStreak(dates, LocalDate.now()) }
            .distinctUntilChanged()

    override suspend fun ensureTodayChallenge(profileId: String, date: LocalDate): DailyChallenge? {
        val epochDay = date.toEpochDay()
        val existing = dailyChallengeDao.getForDate(profileId, epochDay)
        if (existing != null) return hydrate(existing)

        val allIds = questionDao.getAllIds()
        if (allIds.isEmpty()) return null

        val seed = profileId.hashCode().toLong() * 31L + epochDay
        val rng = Random(seed)
        val pickedIds = allIds.shuffled(rng).take(DAILY_CHALLENGE_SIZE)

        val entity = DailyChallengeEntity(
            profileId = profileId,
            date = epochDay,
            questionIdsJson = gson.toJson(pickedIds),
        )
        dailyChallengeDao.upsert(entity)
        val saved = dailyChallengeDao.getForDate(profileId, epochDay) ?: return null
        return hydrate(saved)
    }

    override suspend fun markCompleted(
        profileId: String,
        date: LocalDate,
        scorePercent: Float,
        durationSeconds: Int,
    ) {
        dailyChallengeDao.markCompleted(
            profileId = profileId,
            date = date.toEpochDay(),
            completedAt = Instant.now().toEpochMilli(),
            scorePercent = scorePercent,
            durationSeconds = durationSeconds,
        )
    }

    override suspend fun deleteAllForProfile(profileId: String) {
        dailyChallengeDao.deleteAllForProfile(profileId)
    }

    private suspend fun hydrate(entity: DailyChallengeEntity): DailyChallenge {
        val ids: List<String> = gson.fromJson(entity.questionIdsJson, LIST_TYPE) ?: emptyList()
        val byId = questionDao.getByIds(ids).associateBy { it.id }
        val ordered = ids.mapNotNull { byId[it] }.map { it.toDomain(isBookmarked = false) }
        return DailyChallenge(
            date = LocalDate.ofEpochDay(entity.date),
            questions = ordered,
            completedAt = entity.completedAt?.let { Instant.ofEpochMilli(it) },
            scorePercent = entity.scorePercent,
        )
    }

    private fun computeStreak(completedEpochDays: List<Long>, today: LocalDate): Int {
        if (completedEpochDays.isEmpty()) return 0
        val set = completedEpochDays.toHashSet()
        // Anchor: today if completed, else yesterday (today still in-progress shouldn't break streak).
        var anchor = today
        if (anchor.toEpochDay() !in set) anchor = today.minusDays(1)
        if (anchor.toEpochDay() !in set) return 0
        var count = 0
        var cursor = anchor
        while (cursor.toEpochDay() in set) {
            count++
            cursor = cursor.minusDays(1)
        }
        return count
    }
}
