package org.app.billions.data.local

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import org.app.billions.data.BillionS
import org.app.billions.data.model.ActivitySample
import org.app.billions.data.model.Challenge
import org.app.billions.data.model.ChallengeStatus
import org.app.billions.data.model.ChallengeType
import org.app.billions.data.model.RewardType
import org.app.billions.data.repository.ActivityRepository
import org.app.billions.data.repository.ChallengeRepository

class ChallengeRepositoryImpl(
    private val activityRepository: ActivityRepository,
    private val db: BillionS
) : ChallengeRepository {

    private val queries = db.billionSQueries

    override suspend fun getChallenges(): List<Challenge> = withContext(Dispatchers.Default) {
        queries.getChallenges().executeAsList().map {
            Challenge(
                id = it.id,
                type = ChallengeType.valueOf(it.type),
                status = ChallengeStatus.valueOf(it.status),
                progress = it.progress,
                goal = it.goal,
                daysLeft = it.daysLeft,
                reward = RewardType.valueOf(it.reward),
                startDate = it.startDate
            )
        }
    }

    override suspend fun updateChallenge(challenge: Challenge) = withContext(Dispatchers.Default) {
        queries.updateChallenge(
            type = challenge.type.name,
            status = challenge.status.name,
            progress = challenge.progress,
            goal = challenge.goal,
            daysLeft = challenge.daysLeft,
            reward = challenge.reward.name,
            startDate = challenge.startDate,
            id = challenge.id
        )
    }


    override suspend fun initializeDefaultChallenges() {
        val existing = queries.getChallenges().executeAsList()
        if (existing.isNotEmpty()) return

        val defaultChallenges = listOf(
            Challenge(
                1L,
                ChallengeType.Marathon30,
                ChallengeStatus.Available,
                0.0,
                300_000.0,
                30,
                RewardType.Bronze,
                startDate = 0L
            ),
            Challenge(
                2L,
                ChallengeType.Sprint7,
                ChallengeStatus.Available,
                0.0,
                70_000.0,
                7,
                RewardType.Silver,
                startDate = 0L
            ),
            Challenge(
                3L,
                ChallengeType.StreakBuilder14,
                ChallengeStatus.Available,
                0.0,
                70_000.0,
                14,
                RewardType.Gold,
                startDate = 0L
            )
        )

        defaultChallenges.forEach { c ->
            queries.insertChallenge(
                id = c.id,
                type = c.type.name,
                status = c.status.name,
                progress = c.progress,
                goal = c.goal,
                daysLeft = c.daysLeft,
                reward = c.reward.name,
                startDate = c.startDate
            )
        }
    }

    override suspend fun calculateProgress(challenge: Challenge): Double {
        return when (challenge.status) {
            ChallengeStatus.Available -> 0.0
            ChallengeStatus.Active -> when (challenge.type) {
                ChallengeType.Marathon30 -> calculateMarathon30(challenge)
                ChallengeType.Sprint7 -> calculateSprint7(challenge)
                ChallengeType.StreakBuilder14 -> calculateStreakBuilder14(challenge)
            }
            ChallengeStatus.Completed -> challenge.progress
        }
    }

    private suspend fun calculateMarathon30(challenge: Challenge): Double {
        if (challenge.startDate == 0L) return 0.0

        val fromStr = Instant.fromEpochMilliseconds(challenge.startDate).toString()
        val toStr = Clock.System.now().toString()

        val samples = activityRepository.getSamplesBetween(fromStr, toStr)
        val totalSteps = samples.sumOf { it.steps }

        return (totalSteps.toDouble() / challenge.goal).coerceAtMost(1.0)
    }

    private suspend fun calculateSprint7(challenge: Challenge): Double {
        if (challenge.startDate == 0L) return 0.0

        val fromStr = Instant.fromEpochMilliseconds(challenge.startDate).toString()
        val toStr = Clock.System.now().toString()

        val samples = activityRepository.getSamplesBetween(fromStr, toStr)
        val samplesByDay = samples.groupBy { it.date.date }

        val goalPerDay = 10_000
        val totalDays = 7
        var streak = 0

        val startDate = Instant.fromEpochMilliseconds(challenge.startDate)
            .toLocalDateTime(TimeZone.currentSystemDefault()).date

        for (i in 0 until totalDays) {
            val date = startDate.plus(DatePeriod(days = i))
            val steps = samplesByDay[date]?.sumOf { it.steps } ?: 0
            if (steps >= goalPerDay) streak++
        }

        return (streak.toDouble() / totalDays).coerceAtMost(1.0)
    }

    private suspend fun calculateStreakBuilder14(challenge: Challenge): Double {
        if (challenge.startDate == 0L) return 0.0

        val fromStr = Instant.fromEpochMilliseconds(challenge.startDate).toString()
        val toStr = Clock.System.now().toString()

        val samples = activityRepository.getSamplesBetween(fromStr, toStr)
        val samplesByDay = samples.groupBy { it.date.date }

        val minPerDay = 5000
        val totalDays = 14
        var successDays = 0
        var jokers = 2

        val startDate = Instant.fromEpochMilliseconds(challenge.startDate)
            .toLocalDateTime(TimeZone.currentSystemDefault()).date

        for (i in 0 until totalDays) {
            val date = startDate.plus(DatePeriod(days = i))
            val steps = samplesByDay[date]?.sumOf { it.steps } ?: 0

            if (steps >= minPerDay) {
                successDays++
            } else if (jokers > 0) {
                jokers--
                successDays++
            } else {
                break
            }
        }

        return (successDays.toDouble() / totalDays).coerceAtMost(1.0)
    }
}
