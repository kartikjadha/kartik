package com.example

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.FitMarathiDatabase
import com.example.data.model.Language
import com.example.data.repository.DailyHealthGoalRepository
import com.example.ui.screens.DailyHealthGoalsScreen
import com.example.ui.theme.FitMarathiTheme
import com.example.ui.viewmodel.DailyHealthGoalViewModel
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class DailyHealthGoalsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var database: FitMarathiDatabase
    private lateinit var repository: DailyHealthGoalRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = FitMarathiDatabase.buildInMemory(context)
        repository = DailyHealthGoalRepository(database.dailyHealthGoalDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `DailyHealthGoalsScreen displays all three goals and updates Room progress`() {
        val viewModel = DailyHealthGoalViewModel(
            repository = repository,
            initialDate = "2026-11-19",
            initialLanguage = Language.ENGLISH
        )

        composeTestRule.setContent {
            FitMarathiTheme {
                DailyHealthGoalsScreen(
                    viewModel = viewModel,
                    onNavigateBack = {}
                )
            }
        }

        // Wait until Room emits the seeded goal state
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule
                .onAllNodes(androidx.compose.ui.test.hasTestTag("goal_card_steps"))
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Scroll to Steps card and add +1,000 steps
        composeTestRule.onNodeWithTag("daily_health_goals_list")
            .performScrollToNode(androidx.compose.ui.test.hasTestTag("goal_card_steps"))
        composeTestRule.onNodeWithTag("goal_card_steps").assertIsDisplayed()
        composeTestRule.onNodeWithTag("goal_steps_plus_1000").performClick()

        // Scroll to Water card and add +250 ml
        composeTestRule.onNodeWithTag("daily_health_goals_list")
            .performScrollToNode(androidx.compose.ui.test.hasTestTag("goal_card_water"))
        composeTestRule.onNodeWithTag("goal_card_water").assertIsDisplayed()
        composeTestRule.onNodeWithTag("goal_water_plus_250").performClick()

        // Scroll to Calories card and add +350 kcal
        composeTestRule.onNodeWithTag("daily_health_goals_list")
            .performScrollToNode(androidx.compose.ui.test.hasTestTag("goal_card_calories"))
        composeTestRule.onNodeWithTag("goal_card_calories").assertIsDisplayed()
        composeTestRule.onNodeWithTag("goal_calories_plus_350").performClick()

        // Verify Room-backed UI reflects updated totals (1420 + 350 = 1770 kcal and 8462 + 1000 = 9462 steps)
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule
                .onAllNodes(androidx.compose.ui.test.hasText("1770 kcal", substring = true))
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("1770 kcal", substring = true).assertIsDisplayed()

        composeTestRule.onNodeWithTag("daily_health_goals_list")
            .performScrollToNode(androidx.compose.ui.test.hasTestTag("goal_card_steps"))
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule
                .onAllNodes(androidx.compose.ui.test.hasText("9462 steps", substring = true))
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("9462 steps", substring = true).assertIsDisplayed()
    }
}
