package org.app.billions

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import org.app.billions.billing.IOSBillingRepository
import org.app.billions.data.repository.ThemeRepository
import org.app.billions.di.initKoin
import org.app.billions.notifications.initNotifications
import org.koin.compose.getKoin

fun MainViewController() = ComposeUIViewController {
    LaunchedEffect(Unit) {
        initKoin { }
        initNotifications()
    }

    val themeRepository: ThemeRepository = getKoin().get()

    val billingRepository = remember { IOSBillingRepository(themeRepository) }

    App(billingRepository = billingRepository)
}

