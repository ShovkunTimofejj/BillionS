package org.app.billions.di

import org.app.billions.data.DatabaseDriverFactory
import org.app.billions.data.IOSDatabaseDriverFactory
import org.app.billions.ui.screens.viewModel.NoOpEffects
import org.app.billions.ui.screens.viewModel.NoOpShare
import org.app.billions.ui.screens.viewModel.PlatformEffects
import org.app.billions.ui.screens.viewModel.PlatformShare
import org.koin.dsl.module

actual val platformModule = module {
    single<DatabaseDriverFactory> { IOSDatabaseDriverFactory() }
    single<PlatformEffects> { NoOpEffects() }
    single<PlatformShare> { NoOpShare() }
}

