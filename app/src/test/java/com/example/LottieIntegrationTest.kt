package com.example

import android.content.Context
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import com.airbnb.lottie.LottieCompositionFactory
import com.example.data.repository.FitMarathiRepository
import com.example.ui.components.MotivationalQuoteCard
import com.example.ui.screens.WaterScreen
import com.example.ui.screens.WorkoutScreen
import com.example.ui.screens.YogaScreen
import com.example.ui.theme.FitMarathiTheme
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class LottieIntegrationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `all bundled Lottie raw JSON resources parse cleanly with zero errors`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val rawResources = listOf(
            R.raw.lottie_water_ripple,
            R.raw.lottie_exercise_complete,
            R.raw.lottie_motivational_quote,
            R.raw.lottie_steps_energy,
            R.raw.lottie_yoga_lotus,
            R.raw.lottie_fitness_spark
        )

        rawResources.forEach { resId ->
            val result = LottieCompositionFactory.fromRawResSync(context, resId)
            assertNull("Expected no parse exception for resId=$resId: ${result.exception}", result.exception)
            assertNotNull("Expected non-null LottieComposition for resId=$resId", result.value)
            assertTrue("Expected at least one layer for resId=$resId", result.value!!.layers.isNotEmpty())
        }
    }

    @Test
    fun `MotivationalQuoteCard displays Lottie animation and cycles bilingual quotes`() {
        composeTestRule.setContent {
            FitMarathiTheme {
                MotivationalQuoteCard(isMarathi = false)
            }
        }

        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("home_motivational_quote_card").assertExists()
        composeTestRule.onNodeWithTag("motivational_quote_lottie").assertExists()
        composeTestRule.onNodeWithText("FitMarathi Daily Motivation").assertExists()

        composeTestRule.onNodeWithTag("next_motivational_quote_btn").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Morning Fitness Mantra").assertExists()
    }

    @Test
    fun `WaterScreen renders water ripple Lottie animation alongside quick hydration buttons`() {
        val repository = FitMarathiRepository()

        composeTestRule.setContent {
            FitMarathiTheme {
                WaterScreen(repository = repository)
            }
        }

        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("water_lottie_anim").assertExists()
        composeTestRule.onNodeWithTag("add_250ml_btn").assertExists()
    }

    @Test
    fun `WorkoutScreen renders hero Lottie and shows completion Lottie when exercise marked done`() {
        val repository = FitMarathiRepository()
        val firstExerciseId = repository.exercises.value.first().id

        composeTestRule.setContent {
            FitMarathiTheme {
                WorkoutScreen(repository = repository)
            }
        }

        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("workout_hero_lottie").assertExists()

        composeTestRule.onNodeWithTag("workout_screen_list")
            .performScrollToNode(hasTestTag("toggle_exercise_$firstExerciseId"))
        composeTestRule.onNodeWithTag("toggle_exercise_$firstExerciseId").performClick()
        composeTestRule.waitForIdle()

        assertTrue(repository.exercises.value.first { it.id == firstExerciseId }.isCompleted)
        composeTestRule.onNodeWithTag("exercise_completed_lottie_$firstExerciseId", useUnmergedTree = true).assertExists()
    }

    @Test
    fun `YogaScreen renders lotus Lottie animation`() {
        val repository = FitMarathiRepository()

        composeTestRule.setContent {
            FitMarathiTheme {
                YogaScreen(repository = repository)
            }
        }

        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("yoga_lotus_lottie").assertExists()
    }
}
