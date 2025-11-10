package org.app.billions.data.model

import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Serializable
import org.app.billions.data.ThemeEntity

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

fun ThemeEntity.toResources(): AppThemeResources = AppThemeResources(
    id = id,
    name = name,
    isPurchased = isPurchased == 1L,
    backgroundRes = backgroundRes,
    logoRes = logoRes,
    monocleRes = monocleRes,
    accentColor = Color(primaryColor),
    splashText = splashText
)

fun Theme.toResources(): AppThemeResources = AppThemeResources(
    id = id,
    name = name,
    isPurchased = isPurchased,
    backgroundRes = backgroundRes,
    logoRes = logoRes,
    monocleRes = monocleRes,
    accentColor = Color(primaryColor),
    splashText = splashText
)
