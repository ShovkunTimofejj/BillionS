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

    override suspend fun purchaseTheme(themeId: String): Boolean = withContext(Dispatchers.Default) {
        val theme = queries.getThemes().executeAsList().find { it.id == themeId }
        if (theme != null) {
            queries.insertTheme(
                id = theme.id,
                name = theme.name,
                isPurchased = 1,
                backgroundRes = theme.backgroundRes,
                logoRes = theme.logoRes,
                monocleRes = theme.monocleRes,
                primaryColor = theme.primaryColor,
                splashText = theme.splashText
            )
            true
        } else {
            false
        }
    }
}