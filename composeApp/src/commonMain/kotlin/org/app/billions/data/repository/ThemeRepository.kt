package org.app.billions.data.repository

import app.cash.sqldelight.db.QueryResult
import org.app.billions.data.model.Theme

interface ThemeRepository {
    suspend fun getThemes(): List<Theme>
    suspend fun getThemeById(id: String): Theme?
    suspend fun purchaseTheme(id: String): Boolean
    suspend fun setCurrentTheme(id: String): QueryResult<Long>
    suspend fun getCurrentTheme(): Theme
    suspend fun initializeThemes()
}
