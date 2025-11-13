package org.app.billions.notifications

import kotlinx.cinterop.*
import platform.Foundation.*
import platform.UserNotifications.*

class IOSNotificationsManager : NotificationsManager {

    private val center: UNUserNotificationCenter
        get() = UNUserNotificationCenter.currentNotificationCenter()

    override fun scheduleDailyReminder(hour: Int, minute: Int) {

        println(" [iOS] scheduleDailyReminder called: hour=$hour minute=$minute")

        val content = UNMutableNotificationContent().apply {
            this.setValue("Daily Reminder", forKey = "title")
            this.setValue("Time to check your activity!", forKey = "body")
            this.setValue(UNNotificationSound.defaultSound(), forKey = "sound")
        }

        println(" [iOS] Notification content created")

        val components = NSDateComponents().apply {
            this.hour = hour.toLong()
            this.minute = minute.toLong()
            this.second = 0
            this.timeZone = NSTimeZone.localTimeZone
        }

        println(" [iOS] Date components set: $components")

        val trigger = UNCalendarNotificationTrigger.triggerWithDateMatchingComponents(
            dateComponents = components,
            repeats = false
        )

        println(" [iOS] Trigger created (repeats=false)")

        val request = UNNotificationRequest.requestWithIdentifier(
            identifier = "daily_reminder",
            content = content,
            trigger = trigger
        )

        println(" [iOS] Request created with ID 'daily_reminder'")

        center.addNotificationRequest(request, withCompletionHandler = { error ->
            if (error != null) {
                println(" [iOS] Failed to schedule notification: $error")
            } else {
                println(" [iOS] Notification scheduled successfully!")
            }
        })
    }

    override fun cancelDailyReminder() {
        println(" [iOS] cancelDailyReminder called")

        center.removePendingNotificationRequestsWithIdentifiers(listOf("daily_reminder"))

        println(" [iOS] Pending 'daily_reminder' removed")
    }

    override fun setChallengeReminder(enabled: Boolean) {
        println("️ [iOS] setChallengeReminder: enabled=$enabled")

        if (enabled) {
            println(" [iOS] Enabling challenge reminder")
            scheduleDailyReminder(10, 0)
        } else {
            println(" [iOS] Disabling challenge reminder")
            cancelDailyReminder()
        }
    }
}
