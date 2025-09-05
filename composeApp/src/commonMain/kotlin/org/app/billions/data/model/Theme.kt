package org.app.billions.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Theme(
    val id: String,
    val name: String,
    val isPurchased: Boolean = false,
    val backgroundRes: String,
    val logoRes: String,
    val monocleRes: String,
    val primaryColor: Long,
    val splashText: String
)