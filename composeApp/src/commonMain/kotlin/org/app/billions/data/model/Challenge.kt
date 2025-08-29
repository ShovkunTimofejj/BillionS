package org.app.billions.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Challenge(
    val id: Long,
    val type: ChallengeType,
    val status: ChallengeStatus,
    val progress: Double,
    val goal: Double,
    val daysLeft: Long,
    val reward: RewardType
)

enum class RewardType { Bronze, Silver, Gold }
enum class ChallengeType { Marathon30, Sprint7, StreakBuilder14 }
enum class ChallengeStatus { Active, Available, Completed }