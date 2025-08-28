package org.app.billions.data.repository

import org.app.billions.data.model.Theme

interface ThemeRepository {
    suspend fun getThemes(): List<Theme>
    suspend fun purchaseTheme(themeId: String): Boolean
}