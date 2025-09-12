package org.app.billions.ui.screens.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.russhwolf.settings.Settings
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.app.billions.data.model.AppThemeResources
import org.app.billions.data.model.Theme
import org.app.billions.data.repository.ThemeRepository

class SplashScreenViewModel(
    private val settings: Settings,
    private val themeRepository: ThemeRepository
) : ViewModel() {

    private val FIRST_LAUNCH_KEY = "first_launch"

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = _uiState

    init {
        viewModelScope.launch {
            themeRepository.initializeThemes()
            val currentTheme = themeRepository.getCurrentTheme()
            println("SplashScreenViewModel init - currentTheme: $currentTheme")
            _uiState.update { state ->
                state.copy(
                    isFirstLaunch = isFirstLaunch(),
                    currentTheme = currentTheme
                )
            }
        }
    }

    private fun isFirstLaunch(): Boolean {
        return settings.getBoolean(FIRST_LAUNCH_KEY, true)
    }

    fun markLaunched() {
        println("SplashScreenViewModel markLaunched called")
        settings.putBoolean(FIRST_LAUNCH_KEY, false)
        _uiState.update { it.copy(isFirstLaunch = false) }
    }

    fun splashDelay(seconds: Long = 1L): Flow<Unit> = flow {
        println("SplashScreenViewModel splashDelay started with $seconds seconds")
        delay(seconds * 1000)
        emit(Unit)
        println("SplashScreenViewModel splashDelay emitted Unit")
    }

    fun updateTheme(themeId: String) {
        viewModelScope.launch {
            themeRepository.purchaseTheme(themeId)
            themeRepository.setCurrentTheme(themeId)
            val theme = themeRepository.getCurrentTheme()
            _uiState.update { it.copy(currentTheme = theme) }
        }
    }
}
data class SplashUiState(
    val isFirstLaunch: Boolean = true,
    val currentTheme: Theme? = null
)


//_uiState.update { it.copy(isFirstLaunch = true) }