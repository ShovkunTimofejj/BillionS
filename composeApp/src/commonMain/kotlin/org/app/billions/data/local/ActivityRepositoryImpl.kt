package org.app.billions.data.local

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.app.billions.data.BillionS
import org.app.billions.data.model.ActivitySample
import org.app.billions.data.repository.ActivityRepository

class ActivityRepositoryImpl(private val db: BillionS) : ActivityRepository {

    val queries = db.billionSQueries

    override suspend fun addSample(sample: ActivitySample) = withContext(Dispatchers.Default) {
        queries.insertActivity(
            date = sample.date.toString(),
            steps = sample.steps,
            distanceMeters = sample.distanceMeters,
            activeEnergyKcal = sample.activeEnergyKcal,
            source = sample.source
        )
    }

    override suspend fun getSamplesByDate(date: String): List<ActivitySample> = withContext(Dispatchers.Default) {
        queries.getActivityByDate("%$date%").executeAsList().map {
            ActivitySample(
                id = it.id,
                date = LocalDateTime.parse(it.date),
                steps = it.steps,
                distanceMeters = it.distanceMeters,
                activeEnergyKcal = it.activeEnergyKcal,
                source = it.source
            )
        }
    }

    override suspend fun getDailySummary(date: String) = null

    override suspend fun getGlobalCounter() = withContext(Dispatchers.Default) {
        org.app.billions.data.model.GlobalCounter(0, 0.0, 0.0, Clock.System.now().toLocalDateTime(
            TimeZone.currentSystemDefault()))
    }
}