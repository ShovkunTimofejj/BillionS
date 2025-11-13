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
import org.app.billions.notifications.NotificationsManager
import org.koin.compose.koinInject

class SplashScreenViewModel(
    private val settings: Settings,
    private val themeRepository: ThemeRepository,
    private val notificationsManager: NotificationsManager
) : ViewModel() {

    private val FIRST_LAUNCH_KEY = "first_launch"
    private val KEY_CHALLENGE = "challenge_enabled"
    private val KEY_HOUR = "daily_hour"
    private val KEY_MINUTE = "daily_minute"

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = _uiState

    init {
        viewModelScope.launch {
            themeRepository.initializeThemes()

            val currentTheme = themeRepository.getCurrentTheme()
            val themes = themeRepository.getThemes()

            val challengeEnabled = settings.getBoolean(KEY_CHALLENGE, false)
            val hour = settings.getInt(KEY_HOUR, 9)
            val minute = settings.getInt(KEY_MINUTE, 0)

            _uiState.update { state ->
                state.copy(
                    isFirstLaunch = isFirstLaunch(),
                    currentTheme = currentTheme,
                    themes = themes,
                    challengeReminderEnabled = challengeEnabled,
                    dailyReminderHour = hour,
                    dailyReminderMinute = minute,
                    isReady = true
                )
            }
        }
    }

    private fun isFirstLaunch(): Boolean {
        return settings.getBoolean(FIRST_LAUNCH_KEY, true)
    }

    fun markLaunched() {
        settings.putBoolean(FIRST_LAUNCH_KEY, false)
        _uiState.update { it.copy(isFirstLaunch = false) }
    }

    fun splashDelay(seconds: Long = 1L): Flow<Unit> = flow {
        delay(seconds * 1000)
        emit(Unit)
    }

    fun updateTheme(themeId: String) {
        viewModelScope.launch {
            themeRepository.setCurrentTheme(themeId)

            val current = themeRepository.getCurrentTheme()
            val themes = themeRepository.getThemes()

            _uiState.update {
                it.copy(
                    currentTheme = current,
                    themes = themes
                )
            }
        }
    }

    fun toggleChallengeReminder() {
        val current = _uiState.value.challengeReminderEnabled
        val newValue = !current

        _uiState.update { it.copy(challengeReminderEnabled = newValue) }

        settings.putBoolean(KEY_CHALLENGE, newValue)

        notificationsManager.setChallengeReminder(newValue)

        if (!newValue) {
            notificationsManager.cancelDailyReminder()
        }
    }

    fun updateDailyReminder(hour: Int, minute: Int) {

        _uiState.update {
            it.copy(
                dailyReminderHour = hour,
                dailyReminderMinute = minute
            )
        }

        settings.putInt(KEY_HOUR, hour)
        settings.putInt(KEY_MINUTE, minute)

        notificationsManager.scheduleDailyReminder(hour, minute)
    }
}

data class SplashUiState(
    val isFirstLaunch: Boolean = true,
    val currentTheme: Theme? = null,
    val themes: List<Theme> = emptyList(),

    val challengeReminderEnabled: Boolean = false,
    val dailyReminderHour: Int = 9,
    val dailyReminderMinute: Int = 0,
    val isReady: Boolean = false
)

//_uiState.update { it.copy(isFirstLaunch = true) }