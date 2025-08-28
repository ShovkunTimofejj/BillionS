package org.app.billions.data.model

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class GlobalCounter(
    val totalSteps: Long,
    val totalDistance: Double,
    val totalKcal: Double,
    val lastUpdate: LocalDateTime
)