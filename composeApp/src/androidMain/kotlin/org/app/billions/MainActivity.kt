package org.app.billions

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import org.app.billions.billing.AndroidBillingRepository
import org.app.billions.data.AndroidDatabaseDriverFactory
import org.app.billions.data.BillionS
import org.app.billions.data.local.ThemeRepositoryImpl
import org.app.billions.ui.screens.inAppPurchase.BillingRepository

class MainActivity : ComponentActivity() {

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var billingRepository: BillingRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        createNotificationChannel()

        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                Toast.makeText(this, "Permission for notifications has been received ✅", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Notification permission denied ❌", Toast.LENGTH_SHORT).show()
            }
        }
        requestNotificationPermissionIfNeeded()

        val driverFactory = AndroidDatabaseDriverFactory(this)
        val database = BillionS(driverFactory.createDriver())
        val themeRepository = ThemeRepositoryImpl(database)

        billingRepository = AndroidBillingRepository(this, themeRepository)

        setContent {
            App(billingRepository = billingRepository)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Daily Reminder"
            val descriptionText = "Channel for daily activity reminders"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(
                "daily_reminder_channel",
                name,
                importance
            ).apply {
                description = descriptionText
            }

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}