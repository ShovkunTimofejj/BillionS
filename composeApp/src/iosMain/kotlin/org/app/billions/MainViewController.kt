package org.app.billions

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import org.app.billions.billing.IOSBillingRepository
import org.app.billions.data.repository.ThemeRepository
import org.app.billions.di.initKoin
import org.koin.compose.getKoin

fun MainViewController() = ComposeUIViewController { App() }

