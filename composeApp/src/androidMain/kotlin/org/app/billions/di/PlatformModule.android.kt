package org.app.billions.di

import org.app.billions.data.AndroidDatabaseDriverFactory
import org.app.billions.data.DatabaseDriverFactory
import org.app.billions.notifications.AndroidNotificationsManager
import org.app.billions.notifications.NotificationsManager
import org.app.billions.ui.screens.viewModel.NoOpEffects
import org.app.billions.ui.screens.viewModel.NoOpShare
import org.app.billions.ui.screens.viewModel.PlatformEffects
import org.app.billions.ui.screens.viewModel.PlatformShare
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val platformModule = module {
    single<DatabaseDriverFactory> { AndroidDatabaseDriverFactory(androidContext()) }
    single<PlatformEffects> { NoOpEffects() }
    single<PlatformShare> { NoOpShare() }

    single<NotificationsManager> { AndroidNotificationsManager(androidContext()) }
}

