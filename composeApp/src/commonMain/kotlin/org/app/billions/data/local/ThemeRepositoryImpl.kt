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
                isPurchased = t.isPurchased != 0L
            )
        }
    }

    override suspend fun purchaseTheme(themeId: String): Boolean = withContext(Dispatchers.Default) {
        val theme = queries.getThemes().executeAsList().find { it.id == themeId }
        val themeName = theme?.name ?: "Unknown"
        queries.insertTheme(id = themeId, name = themeName, isPurchased = 1)
        true
    }
}