package org.app.billions.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Challenge(
    val id: Long,
    val type: ChallengeType,
    val status: ChallengeStatus,
    val progress: Double
)

enum class ChallengeType { Marathon30, Sprint7, StreakBuilder14 }
enum class ChallengeStatus { Active, Available, Completed }