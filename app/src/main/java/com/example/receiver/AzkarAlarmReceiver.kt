package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.datasource.AzkarDataSource
import com.example.util.NotificationHelper

class AzkarAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == Intent.ACTION_BOOT_COMPLETED) {
            // When device restarts, default active reminders can be rescheduled
            NotificationHelper.scheduleDailyReminder(
                context, "morning", 7, 0,
                "أذكار الصباح ☀️",
                "أَصْبَحْنَا وَأَصْبَحَ الْمُلْكُ لِلَّهِ... ابدأ يومك بذكر الله وحصنه الحصين"
            )
            NotificationHelper.scheduleDailyReminder(
                context, "evening", 18, 0,
                "أذكار المساء 🌙",
                "أَمْسَيْنَا وَأَمْسَى الْمُلْكُ لِلَّهِ... اختم يومك بالسكينة والاستغفار"
            )
            return
        }

        val reminderId = intent.getStringExtra(NotificationHelper.EXTRA_REMINDER_ID) ?: "daily"
        val title = intent.getStringExtra(NotificationHelper.EXTRA_TITLE) ?: "ذكر اليوم 🌿"
        val customText = intent.getStringExtra(NotificationHelper.EXTRA_TEXT)

        val message = customText ?: run {
            val dailyDhikr = AzkarDataSource.getDailyDhikr()
            dailyDhikr.text
        }

        val notificationId = when (reminderId) {
            "morning" -> 1001
            "evening" -> 1002
            "sleep" -> 1003
            "random" -> 1004
            else -> 1000
        }

        NotificationHelper.showAzkarNotification(context, notificationId, title, message)
    }
}
