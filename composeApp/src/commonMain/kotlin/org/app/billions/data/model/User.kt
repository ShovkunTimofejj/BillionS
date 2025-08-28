package org.app.billions.data.model

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Long,
    val nickname: String,
    val avatar: String? = null,
    val units: Units = Units.Metric,
    val timezone: String,
    val subscription: Subscription = Subscription.Free
)

enum class Units { Metric, Imperial }
enum class Subscription { Free, Premium }