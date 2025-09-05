package org.app.billions.data.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class AppThemeResources(
    val id: String,
    val name: String,
    val isPurchased: Boolean,
    val backgroundRes: String,
    val logoRes: String,
    val monocleRes: String,
    val accentColor: Color,
    val splashText: String
)

val LocalAppTheme = compositionLocalOf<AppThemeResources> {
    AppThemeResources(
        id = "default",
        name = "Default",
        isPurchased = true,
        backgroundRes = "bg_default",
        logoRes = "logo_default",
        monocleRes = "monocle_default",
        accentColor = Color.Magenta,
        splashText = "Welcome!"
    )
}

@Composable
fun AppThemeProvider(
    currentTheme: AppThemeResources,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalAppTheme provides currentTheme
    ) {
        content()
    }
}