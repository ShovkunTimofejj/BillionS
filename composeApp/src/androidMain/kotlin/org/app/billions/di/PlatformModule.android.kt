package org.app.billions.di

import org.app.billions.data.AndroidDatabaseDriverFactory
import org.app.billions.data.DatabaseDriverFactory
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val platformModule = module {
    single<DatabaseDriverFactory> { AndroidDatabaseDriverFactory(androidContext()) }
}