package org.app.billions.notifications

interface NotificationsManager {
    fun scheduleDailyReminder(hour: Int, minute: Int)
    fun cancelDailyReminder()
    fun setChallengeReminder(enabled: Boolean)
}
