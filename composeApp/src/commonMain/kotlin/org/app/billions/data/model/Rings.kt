package org.app.billions.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Rings(
    val stepsGoal: Int,
    val stepsValue: Int,
    val distanceGoal: Double,
    val distanceValue: Double,
    val kcalGoal: Double,
    val kcalValue: Double
)