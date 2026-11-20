package com.example.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.model.Language
import com.example.data.model.NotificationType
import com.example.data.model.ReminderItem

/**
 * BroadcastReceiver that posts scheduled local notifications for Water Intake
 * and Steps reminders and automatically schedules the next daily occurrence.
 */
class HealthReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getStringExtra(HealthReminderScheduler.EXTRA_REMINDER_ID) ?: return
        val typeName = intent.getStringExtra(HealthReminderScheduler.EXTRA_REMINDER_TYPE) ?: NotificationType.WATER.name
        val type = runCatching { NotificationType.valueOf(typeName) }.getOrDefault(NotificationType.WATER)

        val titleEn = intent.getStringExtra(HealthReminderScheduler.EXTRA_TITLE_EN) ?: "FitMarathi Reminder"
        val titleMr = intent.getStringExtra(HealthReminderScheduler.EXTRA_TITLE_MR) ?: "FitMarathi आठवण"
        val messageEn = intent.getStringExtra(HealthReminderScheduler.EXTRA_MESSAGE_EN) ?: "Time to log your daily health progress!"
        val messageMr = intent.getStringExtra(HealthReminderScheduler.EXTRA_MESSAGE_MR) ?: "आजची आरोग्य प्रगती नोंदवण्याची वेळ झाली!"

        val langName = intent.getStringExtra(HealthReminderScheduler.EXTRA_LANGUAGE) ?: Language.MARATHI.name
        val language = runCatching { Language.valueOf(langName) }.getOrDefault(Language.MARATHI)
        val hour = intent.getIntExtra(HealthReminderScheduler.EXTRA_HOUR, 10)
        val minute = intent.getIntExtra(HealthReminderScheduler.EXTRA_MINUTE, 0)

        val title = if (language == Language.MARATHI) titleMr else titleEn
        val message = if (language == Language.MARATHI) messageMr else messageEn

        HealthReminderScheduler.showLocalNotification(
            context = context,
            reminderId = reminderId,
            type = type,
            title = title,
            message = message
        )

        // Schedule the next day's reminder automatically
        val nextReminder = ReminderItem(
            id = reminderId,
            type = type,
            titleEn = titleEn,
            titleMr = titleMr,
            messageEn = messageEn,
            messageMr = messageMr,
            timeSlot = ReminderItem.formatTime12Hour(hour, minute),
            isEnabled = true,
            hourOfDay = hour,
            minute = minute
        )
        HealthReminderScheduler.scheduleReminder(context, nextReminder, language)
    }
}
