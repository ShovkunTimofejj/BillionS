package org.app.billions.data.local

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.app.billions.data.BillionS
import org.app.billions.data.model.Theme
import org.app.billions.data.repository.ThemeRepository

class ThemeRepositoryImpl(private val db: BillionS) : ThemeRepository {

    private val queries = db.billionSQueries

    override suspend fun getThemes(): List<Theme> = withContext(Dispatchers.Default) {
        queries.getThemes().executeAsList().map { t ->
            Theme(
                id = t.id,
                name = t.name,
                isPurchased = t.isPurchased != 0L,
                backgroundRes = t.backgroundRes ?: "default_background",
                logoRes = t.logoRes ?: "default_logo",
                monocleRes = t.monocleRes ?: "default_monocle",
                primaryColor = t.primaryColor ?: 0xFF001F3F,
                splashText = t.splashText ?: ""
            )
        }
    }

    override suspend fun getThemeById(id: String): Theme? = withContext(Dispatchers.Default) {
        queries.getThemeById(id).executeAsOneOrNull()?.let {
            Theme(
                id = it.id,
                name = it.name,
                isPurchased = it.isPurchased != 0L,
                backgroundRes = it.backgroundRes,
                logoRes = it.logoRes,
                monocleRes = it.monocleRes,
                primaryColor = it.primaryColor,
                splashText = it.splashText
            )
        }
    }

    override suspend fun purchaseTheme(themeId: String): Boolean = withContext(Dispatchers.Default) {
        val theme = queries.getThemes().executeAsList().find { it.id == themeId }
        theme?.let {
            queries.insertTheme(
                id = it.id,
                name = it.name,
                isPurchased = 1,
                backgroundRes = it.backgroundRes,
                logoRes = it.logoRes,
                monocleRes = it.monocleRes,
                primaryColor = it.primaryColor,
                splashText = it.splashText
            )
            true
        } ?: false
    }

    override suspend fun setCurrentTheme(id: String) = withContext(Dispatchers.Default) {
        queries.clearCurrentTheme()
        queries.insertCurrentTheme(id)
    }

    override suspend fun getCurrentTheme(): Theme = withContext(Dispatchers.Default) {
        val themeId = queries.getCurrentThemeId()?.executeAsOneOrNull()

        if (themeId != null) {
            getThemeById(themeId) ?: getFallbackTheme()
        } else {
            getFallbackTheme()
        }
    }

    private suspend fun getFallbackTheme(): Theme {
        return getThemeById("dark_lime")!!
    }

    override suspend fun initializeThemes() {
        val existing = queries.getThemes().executeAsList()
        if (existing.isEmpty()) {
            queries.insertTheme(
                "dark_lime", "Dark Lime", 1,
                "bg_dark_lime", "logo_dark_lime", "monocle_dark_lime",
                0xFF001F3F, "Let’s go!"
            )
            queries.insertTheme(
                "neon_coral", "Neon Coral", 0,
                "bg_neon_coral", "logo_neon_coral", "monocle_neon_coral",
                0xFFFF4081, "Burn bright!"
            )
            queries.insertTheme(
                "royal_blue", "Royal Blue", 0,
                "bg_royal_blue", "logo_royal_blue", "monocle_royal_blue",
                0xFF3F51B5, "Stay sharp!"
            )
            queries.insertTheme(
                "graphite_gold", "Graphite Gold", 0,
                "bg_graphite_gold", "logo_graphite_gold", "monocle_graphite_gold",
                0xFFFFD700, "Shine on!"
            )
        }
    }
}