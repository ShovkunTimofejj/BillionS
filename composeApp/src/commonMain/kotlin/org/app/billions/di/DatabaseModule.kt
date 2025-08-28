package org.app.billions.di

import org.app.billions.data.BillionS
import org.app.billions.data.DatabaseDriverFactory
import org.koin.dsl.module

val databaseModule = module {
    single { BillionS(get<DatabaseDriverFactory>().createDriver()) }
}