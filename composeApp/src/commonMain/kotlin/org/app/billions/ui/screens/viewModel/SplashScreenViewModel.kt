package org.app.billions.ui.screens.viewModel

import androidx.lifecycle.ViewModel
import com.russhwolf.settings.Settings
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SplashScreenViewModel(private val settings: Settings): ViewModel() {

    private val FIRST_LAUNCH_KEY = "first_launch"

    fun isFirstLaunch(): Boolean {
        return settings.getBoolean(FIRST_LAUNCH_KEY, true)
    }

    fun markLaunched() {
        settings.putBoolean(FIRST_LAUNCH_KEY, false)
    }

    fun splashDelay(seconds: Long = 1L): Flow<Unit> = flow {
        delay(seconds * 1000)
        emit(Unit)
    }
}