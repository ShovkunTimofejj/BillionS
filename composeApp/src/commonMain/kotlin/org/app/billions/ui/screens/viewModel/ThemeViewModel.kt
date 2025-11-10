package org.app.billions.ui.screens.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.Color
import org.app.billions.data.model.AppThemeResources
import org.app.billions.data.repository.ThemeRepository

class ThemeViewModel(
    private val themeRepository: ThemeRepository
) : ViewModel() {

    private val _currentTheme = MutableStateFlow<AppThemeResources?>(null)
    val currentTheme: StateFlow<AppThemeResources?> = _currentTheme

    var themes: List<AppThemeResources> = emptyList()

    fun loadThemes() {
        viewModelScope.launch {
            themes = themeRepository.getThemes().map {
                AppThemeResources(
                    id = it.id,
                    name = it.name,
                    isPurchased = it.isPurchased,
                    backgroundRes = it.backgroundRes,
                    logoRes = it.logoRes,
                    monocleRes = it.monocleRes,
                    accentColor = Color(it.primaryColor),
                    splashText = it.splashText
                )
            }
            if (_currentTheme.value == null) {
                _currentTheme.value = themes.firstOrNull { it.isPurchased } ?: themes.first()
            }
        }
    }

    fun applyTheme(theme: AppThemeResources) {
        _currentTheme.value = theme
    }

    fun purchaseTheme(themeId: String) {
        viewModelScope.launch {
            themeRepository.purchaseTheme(themeId)
            val updated = themes.find { it.id == themeId }?.copy(isPurchased = true)
            updated?.let { _currentTheme.value = it }
        }
    }
}