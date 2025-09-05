//package org.app.billions.notifications
//
//import com.mmk.kmpnotifier.notification.NotifierManager
//import com.mmk.kmpnotifier.notification.RepeatInterval
//import kotlinx.datetime.*
//import kotlinx.datetime.Clock
//import kotlinx.datetime.LocalDateTime
//import kotlinx.datetime.TimeZone
//import kotlinx.datetime.toLocalDateTime
//
//class IOSNotificationsManager : NotificationsManager {
//
//    override fun scheduleDailyReminder(hour: Int, minute: Int) {
//        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
//
//
//        val todayTrigger = LocalDateTime(
//            year = now.year,
//            month = now.month,
//            dayOfMonth = now.dayOfMonth,
//            hour = hour,
//            minute = minute,
//            second = 0
//        )
//
//
//        val triggerDateTime = if (todayTrigger < now) {
//            todayTrigger.date.plus(1, kotlinx.datetime.DateTimeUnit.DAY)
//                .atTime(hour, minute)
//        } else {
//            todayTrigger
//        }
//
//        NotifierManager.scheduleNotification(
//            id = "daily_reminder",
//            title = "Daily Reminder",
//            body = "Time to check your activity!",
//            deliveryDateTime = triggerDateTime,
//            repeatInterval = RepeatInterval.Daily
//        )
//    }
//
//    override fun cancelDailyReminder() {
//        NotifierManager.cancelNotification("daily_reminder")
//    }
//
//    override fun setChallengeReminder(enabled: Boolean) {
//        if (enabled) {
//            scheduleDailyReminder(10, 0)
//        } else {
//            cancelDailyReminder()
//        }
//    }
//}
