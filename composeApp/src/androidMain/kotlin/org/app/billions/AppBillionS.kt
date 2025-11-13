package org.app.billions

import android.app.Application
import org.app.billions.di.initKoin
import org.app.billions.exportCsv.appContext
import org.app.billions.exportCsv.share
import org.koin.android.ext.koin.androidContext

class AppBillionS : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin { androidContext(this@AppBillionS) }

        appContext = this
    }
}
