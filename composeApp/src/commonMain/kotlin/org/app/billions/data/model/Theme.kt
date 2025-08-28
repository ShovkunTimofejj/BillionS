package org.app.billions.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Theme(
    val id: String,
    val name: String,
    val isPurchased: Boolean = false
)