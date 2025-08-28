package org.app.billions.di

import org.app.billions.data.IOSDatabaseDriverFactory
import org.koin.dsl.module

actual val platformModule = module {
    single { IOSDatabaseDriverFactory() }
}
