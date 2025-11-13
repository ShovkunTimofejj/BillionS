package org.app.billions.di

import org.app.billions.billing.IOSBillingRepository
import org.app.billions.data.DatabaseDriverFactory
import org.app.billions.data.IOSDatabaseDriverFactory
import org.app.billions.exportCsv.IOSShareManager
import org.app.billions.exportCsv.ShareManager
import org.app.billions.notifications.IOSNotificationsManager
import org.app.billions.notifications.NotificationsManager
import org.app.billions.ui.screens.inAppPurchase.BillingRepository
import org.app.billions.ui.screens.viewModel.NoOpEffects
import org.app.billions.ui.screens.viewModel.NoOpShare
import org.app.billions.ui.screens.viewModel.PlatformEffects
import org.app.billions.ui.screens.viewModel.PlatformShare
import org.koin.dsl.module

actual val platformModule = module {
    single<DatabaseDriverFactory> { IOSDatabaseDriverFactory() }
    single<PlatformEffects> { NoOpEffects() }
    single<PlatformShare> { NoOpShare() }
    single<ShareManager> { IOSShareManager() }
    single<NotificationsManager> { IOSNotificationsManager() }
    single<BillingRepository> { IOSBillingRepository(get()) }
}

object KoinStarter {
    fun start() = initKoin()
}
