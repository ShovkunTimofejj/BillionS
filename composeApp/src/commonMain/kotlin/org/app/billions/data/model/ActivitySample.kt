package org.app.billions.data.model

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class ActivitySample(
    val id: Long,
    val date: LocalDateTime,
    val steps: Long,
    val distanceMeters: Double,
    val activeEnergyKcal: Double,
    val source: String // manual, device, api
)