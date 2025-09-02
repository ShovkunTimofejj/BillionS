package org.app.billions.data.local

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
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
                reward = RewardType.valueOf(it.reward)
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
                RewardType.Bronze
            ),
            Challenge(
                2L,
                ChallengeType.Sprint7,
                ChallengeStatus.Available,
                0.0,
                70_000.0,
                7,
                RewardType.Silver
            ),
            Challenge(
                3L,
                ChallengeType.StreakBuilder14,
                ChallengeStatus.Available,
                0.0,
                70_000.0,
                14,
                RewardType.Gold
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
                reward = c.reward.name
            )
        }
    }

    override suspend fun calculateProgress(challenge: Challenge): Double = when (challenge.type) {
        ChallengeType.Marathon30 -> calculateMarathon30()
        ChallengeType.Sprint7 -> calculateSprint7()
        ChallengeType.StreakBuilder14 -> calculateStreakBuilder14()
    }

    private suspend fun calculateMarathon30(): Double {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val from = now.minus(DatePeriod(days = 30))

        val fromStr = "${from}T00:00:00"
        val toStr = "${now}T23:59:59"

        val samples = activityRepository.getSamplesBetween(fromStr, toStr)
        val totalSteps = samples.sumOf { it.steps }
        val goal = 300_000

        return (totalSteps.toDouble() / goal).coerceAtMost(1.0)
    }

    private suspend fun calculateSprint7(): Double {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val totalDays = 7
        val from = now.minus(DatePeriod(days = totalDays - 1))

        val fromStr = "${from}T00:00:00"
        val toStr = "${now}T23:59:59"

        val samples = activityRepository.getSamplesBetween(fromStr, toStr)
        val samplesByDay = samples.groupBy { it.date.date }

        val goalPerDay = 10_000
        var streak = 0

        for (i in 0 until totalDays) {
            val date = from.plus(DatePeriod(days = i))
            val steps = samplesByDay[date]?.sumOf { it.steps } ?: 0
            if (steps >= goalPerDay) streak++
        }

        return (streak.toDouble() / totalDays).coerceAtMost(1.0)
    }

    private suspend fun calculateStreakBuilder14(): Double {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val totalDays = 14
        val from = now.minus(DatePeriod(days = totalDays - 1))

        val fromStr = "${from}T00:00:00"
        val toStr = "${now}T23:59:59"

        val samples = activityRepository.getSamplesBetween(fromStr, toStr)
        val samplesByDay = samples.groupBy { it.date.date }

        val minPerDay = 5000
        var successDays = 0
        var jokers = 2

        for (i in 0 until totalDays) {
            val date = from.plus(DatePeriod(days = i))
            val steps = samplesByDay[date]?.sumOf { it.steps } ?: 0

            if (steps >= minPerDay) {
                successDays++
            } else if (jokers > 0) {
                jokers--
            } else {
                break
            }
        }

        return (successDays.toDouble() / totalDays).coerceAtMost(1.0)
    }
}
