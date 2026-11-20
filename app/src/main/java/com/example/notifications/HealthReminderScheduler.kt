package com.example.notifications

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R
import com.example.data.model.Language
import com.example.data.model.NotificationType
import com.example.data.model.ReminderItem
import java.util.Calendar

/**
 * Play-policy compliant local notification scheduler for FitMarathi.
 * Schedules daily reminders for water intake, step logging, and wellness habits
 * at user-selected times using AlarmManager.setAndAllowWhileIdle (no restricted
 * exact-alarm permission required) and NotificationCompat channels.
 */
object HealthReminderScheduler {

    const val CHANNEL_WATER = "fitmarathi_water_reminders"
    const val CHANNEL_STEPS = "fitmarathi_steps_reminders"
    const val CHANNEL_GENERAL = "fitmarathi_wellness_reminders"

    const val EXTRA_REMINDER_ID = "extra_reminder_id"
    const val EXTRA_REMINDER_TYPE = "extra_reminder_type"
    const val EXTRA_TITLE_EN = "extra_title_en"
    const val EXTRA_TITLE_MR = "extra_title_mr"
    const val EXTRA_MESSAGE_EN = "extra_message_en"
    const val EXTRA_MESSAGE_MR = "extra_message_mr"
    const val EXTRA_LANGUAGE = "extra_language"
    const val EXTRA_HOUR = "extra_hour"
    const val EXTRA_MINUTE = "extra_minute"

    fun ensureChannelsCreated(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return

        val waterChannel = NotificationChannel(
            CHANNEL_WATER,
            "Water Intake Reminders (पाणी पिण्याच्या सूचना)",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Scheduled reminders to drink and log water intake throughout the day."
        }

        val stepsChannel = NotificationChannel(
            CHANNEL_STEPS,
            "Daily Steps Reminders (पावले चालण्याच्या सूचना)",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Scheduled reminders to walk and log daily steps toward your goal."
        }

        val generalChannel = NotificationChannel(
            CHANNEL_GENERAL,
            "Wellness & Routine Reminders",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Workout, meal, and daily health review reminders."
        }

        manager.createNotificationChannel(waterChannel)
        manager.createNotificationChannel(stepsChannel)
        manager.createNotificationChannel(generalChannel)
    }

    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun computeNextTriggerMillis(
        hourOfDay: Int,
        minute: Int,
        nowMillis: Long = System.currentTimeMillis()
    ): Long {
        val cal = Calendar.getInstance().apply {
            timeInMillis = nowMillis
            set(Calendar.HOUR_OF_DAY, hourOfDay.coerceIn(0, 23))
            set(Calendar.MINUTE, minute.coerceIn(0, 59))
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        if (cal.timeInMillis <= nowMillis) {
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        return cal.timeInMillis
    }

    fun scheduleReminder(
        context: Context,
        reminder: ReminderItem,
        language: Language
    ) {
        ensureChannelsCreated(context)
        if (!reminder.isEnabled) {
            cancelReminder(context, reminder.id)
            return
        }
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val triggerAtMillis = computeNextTriggerMillis(reminder.hourOfDay, reminder.minute)
        val pendingIntent = buildReminderPendingIntent(context, reminder, language)

        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            pendingIntent
        )
    }

    fun cancelReminder(context: Context, reminderId: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, HealthReminderReceiver::class.java).apply {
            action = "com.example.notifications.ACTION_REMINDER_$reminderId"
        }
        val flags = PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        val existing = PendingIntent.getBroadcast(context, reminderId.hashCode(), intent, flags)
        if (existing != null) {
            alarmManager.cancel(existing)
            existing.cancel()
        }
    }

    fun syncAllReminders(
        context: Context,
        reminders: List<ReminderItem>,
        language: Language
    ) {
        ensureChannelsCreated(context)
        reminders.forEach { reminder ->
            if (reminder.isEnabled) {
                scheduleReminder(context, reminder, language)
            } else {
                cancelReminder(context, reminder.id)
            }
        }
    }

    fun showLocalNotification(
        context: Context,
        reminderId: String,
        type: NotificationType,
        title: String,
        message: String
    ): Boolean {
        ensureChannelsCreated(context)
        if (!hasNotificationPermission(context)) {
            return false
        }

        val channelId = when (type) {
            NotificationType.WATER -> CHANNEL_WATER
            NotificationType.STEPS -> CHANNEL_STEPS
            else -> CHANNEL_GENERAL
        }

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val contentIntent = PendingIntent.getActivity(
            context,
            reminderId.hashCode(),
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(contentIntent)
            .build()

        return try {
            NotificationManagerCompat.from(context).notify(reminderId.hashCode(), notification)
            true
        } catch (_: SecurityException) {
            false
        }
    }

    private fun buildReminderPendingIntent(
        context: Context,
        reminder: ReminderItem,
        language: Language
    ): PendingIntent {
        val intent = Intent(context, HealthReminderReceiver::class.java).apply {
            action = "com.example.notifications.ACTION_REMINDER_${reminder.id}"
            putExtra(EXTRA_REMINDER_ID, reminder.id)
            putExtra(EXTRA_REMINDER_TYPE, reminder.type.name)
            putExtra(EXTRA_TITLE_EN, reminder.titleEn)
            putExtra(EXTRA_TITLE_MR, reminder.titleMr)
            putExtra(EXTRA_MESSAGE_EN, reminder.messageEn)
            putExtra(EXTRA_MESSAGE_MR, reminder.messageMr)
            putExtra(EXTRA_LANGUAGE, language.name)
            putExtra(EXTRA_HOUR, reminder.hourOfDay)
            putExtra(EXTRA_MINUTE, reminder.minute)
        }
        return PendingIntent.getBroadcast(
            context,
            reminder.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
