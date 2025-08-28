package org.app.billions.di

import com.russhwolf.settings.Settings
import org.app.billions.ui.screens.viewModel.SplashScreenViewModel
import org.koin.dsl.module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf

val viewModule = module {
    single { Settings() }

    viewModelOf(::SplashScreenViewModel)
}

