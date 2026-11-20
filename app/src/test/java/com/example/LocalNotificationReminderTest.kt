package com.example

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.data.model.NotificationType
import com.example.data.model.ReminderItem
import com.example.data.repository.FitMarathiRepository
import com.example.notifications.HealthReminderScheduler
import com.example.ui.screens.NotificationsScreen
import com.example.ui.theme.FitMarathiTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.Calendar

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class LocalNotificationReminderTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `formatTime12Hour formats morning noon afternoon and midnight correctly`() {
        assertEquals("12:05 AM", ReminderItem.formatTime12Hour(0, 5))
        assertEquals("7:30 AM", ReminderItem.formatTime12Hour(7, 30))
        assertEquals("12:00 PM", ReminderItem.formatTime12Hour(12, 0))
        assertEquals("6:45 PM", ReminderItem.formatTime12Hour(18, 45))
    }

    @Test
    fun `computeNextTriggerMillis schedules today when future and tomorrow when past`() {
        val baseCal = Calendar.getInstance().apply {
            set(2026, Calendar.NOVEMBER, 19, 10, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val now = baseCal.timeInMillis

        val laterToday = HealthReminderScheduler.computeNextTriggerMillis(15, 30, now)
        assertTrue("Later today should be after now", laterToday > now)
        val laterCal = Calendar.getInstance().apply { timeInMillis = laterToday }
        assertEquals(19, laterCal.get(Calendar.DAY_OF_MONTH))
        assertEquals(15, laterCal.get(Calendar.HOUR_OF_DAY))
        assertEquals(30, laterCal.get(Calendar.MINUTE))

        val tomorrowMorning = HealthReminderScheduler.computeNextTriggerMillis(8, 0, now)
        assertTrue("Earlier time should roll over to tomorrow", tomorrowMorning > now)
        val tomorrowCal = Calendar.getInstance().apply { timeInMillis = tomorrowMorning }
        assertEquals(20, tomorrowCal.get(Calendar.DAY_OF_MONTH))
        assertEquals(8, tomorrowCal.get(Calendar.HOUR_OF_DAY))
    }

    @Test
    fun `NotificationsScreen renders water and steps reminders and adds custom scheduled time`() {
        val repository = FitMarathiRepository()
        val initialCount = repository.reminders.value.size

        composeTestRule.setContent {
            FitMarathiTheme {
                NotificationsScreen(
                    repository = repository,
                    onNavigateBack = {}
                )
            }
        }

        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("water_steps_test_reminders_card").assertExists()
        composeTestRule.onNodeWithTag("test_water_notification_btn").assertExists()
        composeTestRule.onNodeWithTag("test_steps_notification_btn").assertExists()

        // Open add reminder dialog and schedule a Steps reminder
        composeTestRule.onNodeWithTag("add_reminder_btn").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("dialog_type_steps").performClick()
        composeTestRule.onNodeWithTag("save_reminder_schedule_btn").performClick()
        composeTestRule.waitForIdle()

        assertEquals(initialCount + 1, repository.reminders.value.size)
        assertEquals(NotificationType.STEPS, repository.reminders.value.first().type)
    }
}
