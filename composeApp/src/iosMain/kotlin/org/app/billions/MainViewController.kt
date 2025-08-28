package org.app.billions

import androidx.compose.ui.window.ComposeUIViewController
import org.app.billions.di.initKoin

fun MainViewController() = ComposeUIViewController {
    initKoin {  }
    App()

}