package com.example

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class MainActivityLaunchTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun `MainActivity launches and renders home dashboard without crashing`() {
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("fitmarathi_bottom_nav").assertExists()
        composeTestRule.onNodeWithTag("health_dashboard_radial_card").assertExists()
        composeTestRule.onNodeWithTag("home_daily_goal_ring").assertExists()
        composeTestRule.onNodeWithTag("radial_progress_steps").assertExists()
        composeTestRule.onNodeWithTag("radial_progress_water").assertExists()
        composeTestRule.onNodeWithTag("radial_progress_calories").assertExists()
        composeTestRule.onNodeWithTag("open_daily_goals_screen_btn").assertExists()
    }
}
