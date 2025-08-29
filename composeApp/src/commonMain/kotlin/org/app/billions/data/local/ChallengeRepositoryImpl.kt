package org.app.billions.data.local

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import org.app.billions.data.BillionS
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

    override suspend fun calculateProgress(challenge: Challenge): Double {
        return when (challenge.type) {
            ChallengeType.Marathon30 -> calculateMarathon30()
            ChallengeType.Sprint7 -> calculateSprint7()
            ChallengeType.StreakBuilder14 -> calculateStreakBuilder14()
        }
    }

    private suspend fun calculateMarathon30(): Double {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val from = now.minus(DatePeriod(days = 30))
        val samples = queries.getActivityBetween(from.toString(), now.toString()).executeAsList()
        val totalSteps = samples.sumOf { it.steps }
        val goal = 300_000 // например, 300к шагов за 30 дней
        return totalSteps.toDouble() / goal
    }

    private suspend fun calculateSprint7(): Double {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val from = now.minus(DatePeriod(days = 7))
        val samplesByDay = queries.getActivityBetween(from.toString(), now.toString()).executeAsList()
            .groupBy { it.date.substring(0, 10) }

        val goalPerDay = 10_000 // шагов в день
        var streak = 0
        for (day in 0..6) {
            val date = from.plus(DatePeriod(days = day))
            val steps = samplesByDay[date.toString()]?.sumOf { it.steps } ?: 0
            if (steps >= goalPerDay) streak++ else return streak / 7.0
        }
        return streak / 7.0
    }

    private suspend fun calculateStreakBuilder14(): Double {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val from = now.minus(DatePeriod(days = 14))
        val samplesByDay = queries.getActivityBetween(from.toString(), now.toString()).executeAsList()
            .groupBy { it.date.substring(0, 10) }

        val minPerDay = 5000 // шагов
        var successDays = 0
        var jokers = 2
        for (day in 0..13) {
            val date = from.plus(DatePeriod(days = day))
            val steps = samplesByDay[date.toString()]?.sumOf { it.steps } ?: 0
            if (steps >= minPerDay) successDays++ else if (jokers > 0) jokers-- else break
        }
        return successDays / 14.0
    }
}
