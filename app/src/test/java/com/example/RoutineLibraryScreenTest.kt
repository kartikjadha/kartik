package com.example

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.data.model.RoutineCategory
import com.example.data.model.WorkoutRoutineCatalog
import com.example.data.repository.FitMarathiRepository
import com.example.ui.screens.RoutineLibraryScreen
import com.example.ui.theme.FitMarathiTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class RoutineLibraryScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `catalog contains 60 plus unique routines across all categories with bilingual Marathi and English content`() {
        val routines = WorkoutRoutineCatalog.allRoutines
        assertTrue("Expected at least 60 routines, found ${routines.size}", routines.size >= 60)
        assertEquals("Expected all routine IDs to be unique", routines.size, routines.map { it.id }.toSet().size)

        WorkoutRoutineCatalog.browsableCategories.forEach { category ->
            val count = routines.count { it.category == category }
            assertTrue("Category ${category.name} should have routines", count > 0)
        }

        routines.forEach { routine ->
            assertTrue("Missing English title for ${routine.id}", routine.titleEn.isNotBlank())
            assertTrue("Missing Marathi title for ${routine.id}", routine.titleMr.isNotBlank())
            assertTrue("Missing English description for ${routine.id}", routine.descriptionEn.isNotBlank())
            assertTrue("Missing Marathi description for ${routine.id}", routine.descriptionMr.isNotBlank())
            assertTrue("Missing English focus area for ${routine.id}", routine.focusAreaEn.isNotBlank())
            assertTrue("Missing Marathi focus area for ${routine.id}", routine.focusAreaMr.isNotBlank())
            assertTrue("Missing English steps for ${routine.id}", routine.stepsEn.isNotEmpty())
            assertTrue("Missing Marathi steps for ${routine.id}", routine.stepsMr.isNotEmpty())
        }
    }

    @Test
    fun `RoutineLibraryScreen renders categorized bilingual routines and filters by category and search`() {
        val repository = FitMarathiRepository()

        composeTestRule.setContent {
            FitMarathiTheme {
                RoutineLibraryScreen(
                    repository = repository,
                    onNavigateBack = {}
                )
            }
        }

        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("routine_library_hero").assertExists()

        // Scroll to the first routine card and confirm bilingual content renders
        composeTestRule.onNodeWithTag("routine_library_list")
            .performScrollToNode(hasTestTag("routine_card_rt_yoga_01"))
        composeTestRule.onNodeWithTag("routine_card_rt_yoga_01").assertExists()
        composeTestRule.onNodeWithText("Classic 12-Step Surya Namaskar").assertExists()

        // Filter to Core & Abs category (scroll vertical list to row, then horizontal row to chip)
        composeTestRule.onNodeWithTag("routine_library_list")
            .performScrollToNode(hasTestTag("routine_category_row"))
        composeTestRule.onNodeWithTag("routine_category_row")
            .performScrollToNode(hasTestTag("routine_cat_${RoutineCategory.CORE_ABS.name}"))
        composeTestRule.onNodeWithTag("routine_cat_${RoutineCategory.CORE_ABS.name}").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("routine_library_list")
            .performScrollToNode(hasTestTag("routine_card_rt_core_01"))
        composeTestRule.onNodeWithTag("routine_card_rt_core_01").assertExists()

        // Toggle completion and verify Lottie celebration dialog + repository update
        composeTestRule.onNodeWithTag("routine_complete_rt_core_01").performClick()
        composeTestRule.waitForIdle()
        assertTrue(repository.routineCatalog.value.first { it.id == "rt_core_01" }.isCompleted)
        composeTestRule.onNodeWithTag("exercise_completion_lottie").assertExists()
        composeTestRule.onNodeWithTag("dismiss_celebration_btn").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("routine_completed_lottie_rt_core_01", useUnmergedTree = true).assertExists()

        // Reset to All and search in English/Marathi
        composeTestRule.onNodeWithTag("routine_library_list")
            .performScrollToNode(hasTestTag("routine_category_row"))
        composeTestRule.onNodeWithTag("routine_category_row")
            .performScrollToNode(hasTestTag("routine_cat_${RoutineCategory.ALL.name}"))
        composeTestRule.onNodeWithTag("routine_cat_${RoutineCategory.ALL.name}").performClick()
        composeTestRule.onNodeWithTag("routine_library_list")
            .performScrollToNode(hasTestTag("routine_search_input"))
        composeTestRule.onNodeWithTag("routine_search_input").performTextInput("Anulom")
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("routine_library_list")
            .performScrollToNode(hasTestTag("routine_card_rt_prana_01"))
        composeTestRule.onNodeWithTag("routine_card_rt_prana_01").assertExists()
    }
}
