package org.app.billions

import android.app.Application
import org.app.billions.di.initKoin
import org.koin.android.ext.koin.androidContext

class AppBillionS : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin { androidContext(this@AppBillionS) }
    }
}