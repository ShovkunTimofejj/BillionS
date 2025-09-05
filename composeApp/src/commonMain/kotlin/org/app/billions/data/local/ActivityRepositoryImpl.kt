package org.app.billions.data.local

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.app.billions.data.ActivitySampleEntity
import org.app.billions.data.BillionS
import org.app.billions.data.model.ActivitySample
import org.app.billions.data.model.DailyGoals
import org.app.billions.data.repository.ActivityRepository

class ActivityRepositoryImpl(private val db: BillionS) : ActivityRepository {

    private val q = db.billionSQueries


    override suspend fun addSample(sample: ActivitySample) = withContext(Dispatchers.Default) {
        q.insertActivity(
            date = sample.date.toString(),
            steps = sample.steps,
            distanceMeters = sample.distanceMeters,
            activeEnergyKcal = sample.activeEnergyKcal,
            source = sample.source
        )
    }

    override suspend fun getDailyGoals(): DailyGoals? = withContext(Dispatchers.IO) {
        q.selectDailyGoals().executeAsOneOrNull()?.let {
            DailyGoals(
                stepGoal = it.stepGoal,
                distanceGoal = it.distanceGoal,
                calorieGoal = it.calorieGoal
            )
        }
    }

    override suspend fun saveDailyGoals(goals: DailyGoals) = withContext(Dispatchers.IO) {
        q.clearDailyGoals()
        q.insertDailyGoals(
            stepGoal = goals.stepGoal,
            distanceGoal = goals.distanceGoal,
            calorieGoal = goals.calorieGoal
        )
    }

    override suspend fun getSamplesByDate(date: String): List<ActivitySample> = withContext(Dispatchers.Default) {
        q.getActivityByDate("%$date%").executeAsList().map { it.toModel() }
    }

    override suspend fun getDailySummary(date: String) = null

    override suspend fun getGlobalCounter() = withContext(Dispatchers.Default) {
        org.app.billions.data.model.GlobalCounter(0, 0.0, 0.0, Clock.System.now().toLocalDateTime(
            TimeZone.currentSystemDefault()))
    }

    override suspend fun getSamplesBetween(from: String, to: String): List<ActivitySample> = withContext(Dispatchers.Default) {
        q.getActivityBetween(from, to).executeAsList().map { it.toModel() }
    }

    override suspend fun getAllSamples(): List<ActivitySample> = withContext(Dispatchers.Default) {
        q.getAllActivity().executeAsList().map { it.toModel() }
    }


    override suspend fun deleteSample(id: Long) = withContext(Dispatchers.Default) {
        q.deleteActivity(id)
    }

    override suspend fun updateSample(sample: ActivitySample) = withContext(Dispatchers.Default) {
        q.updateActivity(
            date = sample.date.toString(),
            steps = sample.steps,
            distanceMeters = sample.distanceMeters,
            activeEnergyKcal = sample.activeEnergyKcal,
            source = sample.source,
            id = sample.id
        )
    }
    override suspend fun getSampleById(id: Long): ActivitySample? = withContext(Dispatchers.Default) {
        q.getActivityById(id).executeAsOneOrNull()?.toModel()
    }
}

public fun ActivitySampleEntity.toModel() = ActivitySample(
    id = id,
    date = LocalDateTime.parse(date),
    steps = steps,
    distanceMeters = distanceMeters,
    activeEnergyKcal = activeEnergyKcal,
    source = source
)