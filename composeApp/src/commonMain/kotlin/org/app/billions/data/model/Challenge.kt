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
    val reward: RewardType,
    val startDate: Long
)

enum class RewardType { Bronze, Silver, Gold }
enum class ChallengeType(val displayName: String) {
    Marathon30("Marathon 30"),
    Sprint7("Sprint 7"),
    StreakBuilder14("StreakBuilder 14");

    override fun toString(): String = displayName
}
enum class ChallengeStatus { Active, Available, Completed }