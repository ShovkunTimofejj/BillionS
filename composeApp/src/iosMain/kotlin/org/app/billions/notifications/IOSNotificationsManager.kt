//package org.app.billions.notifications
//
//import platform.UserNotifications.*
//import platform.Foundation.NSDateComponents
//
//class IOSNotificationsManager : NotificationsManager {
//
//    private val center = UNUserNotificationCenter.currentNotificationCenter()
//
//    override fun scheduleDailyReminder(hour: Int, minute: Int) {
//        val content = UNMutableNotificationContent().apply {
//            title = "Daily Reminder"
//            body = "Time to check your activity!"
//            sound = UNNotificationSound.defaultSound()
//        }
//
//        val dateComponents = NSDateComponents().apply {
//            this.hour = hour.toLong()
//            this.minute = minute.toLong()
//        }
//
//        val trigger = UNCalendarNotificationTrigger.dateMatchingComponents(
//            dateComponents = dateComponents,
//            repeats = true
//        )
//
//        val request = UNNotificationRequest.requestWithIdentifier(
//            identifier = "daily_reminder",
//            content = content,
//            trigger = trigger
//        )
//
//        center.addNotificationRequest(request) { error ->
//            if (error != null) println("Error scheduling notification: $error")
//        }
//    }
//
//    override fun cancelDailyReminder() {
//        center.removePendingNotificationRequestsWithIdentifiers(listOf("daily_reminder"))
//    }
//
//    override fun setChallengeReminder(enabled: Boolean) {
//        if (enabled) scheduleDailyReminder(10, 0) else cancelDailyReminder()
//    }
//}