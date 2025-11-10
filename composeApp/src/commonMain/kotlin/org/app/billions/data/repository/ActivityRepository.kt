package org.app.billions.data.repository

import app.cash.sqldelight.db.QueryResult
import org.app.billions.data.model.ActivitySample
import org.app.billions.data.model.DailyGoals
import org.app.billions.data.model.DailySummary
import org.app.billions.data.model.GlobalCounter

interface ActivityRepository {
    suspend fun addSample(sample: ActivitySample): QueryResult<Long>
    suspend fun getSamplesByDate(date: String): List<ActivitySample>
    suspend fun getDailySummary(date: String): DailySummary?
    suspend fun getGlobalCounter(): GlobalCounter
    suspend fun getSamplesBetween(from: String, to: String): List<ActivitySample>
    suspend fun getAllSamples(): List<ActivitySample>
    suspend fun deleteSample(id: Long): QueryResult<Long>
    suspend fun updateSample(sample: ActivitySample): QueryResult<Long>
    suspend fun getSampleById(id: Long): ActivitySample?
    suspend fun getDailyGoals(): DailyGoals?
    suspend fun saveDailyGoals(goals: DailyGoals): QueryResult<Long>
}