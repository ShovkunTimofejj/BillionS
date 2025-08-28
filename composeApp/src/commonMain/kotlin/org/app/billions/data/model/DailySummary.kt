package org.app.billions.data.model

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class DailySummary(
    val date: LocalDateTime,
    val steps: Int,
    val distanceMeters: Double,
    val kcal: Double,
    val moveMinutes: Int,
    val rings: Rings,
    val streak: Int
)