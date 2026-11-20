package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.FitMarathiDatabase
import com.example.data.local.entity.HealthGoalType
import com.example.data.model.Language
import com.example.data.repository.DailyHealthGoalRepository
import com.example.ui.viewmodel.DailyHealthGoalViewModel
import com.example.ui.viewmodel.DailyHealthUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class DailyHealthGoalRoomTest {

    private lateinit var database: FitMarathiDatabase
    private lateinit var repository: DailyHealthGoalRepository
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = FitMarathiDatabase.buildInMemory(context)
        repository = DailyHealthGoalRepository(database.dailyHealthGoalDao())
    }

    @After
    fun tearDown() {
        database.close()
        Dispatchers.resetMain()
    }

    @Test
    fun `repository persists steps water and calories with bilingual logs`() = runTest {
        val date = "2026-11-19"
        repository.ensureGoalExists(
            date = date,
            initialSteps = 2000,
            stepGoal = 10000,
            initialWaterMl = 500,
            waterGoalMl = 3000,
            initialCaloriesConsumed = 400,
            calorieGoal = 1800,
            language = Language.MARATHI
        )

        repository.logSteps(date = date, stepsDelta = 1500)
        repository.logWater(date = date, waterDeltaMl = 500)
        repository.logCalories(
            date = date,
            caloriesDelta = 600,
            labelMr = "भाजी + भाकरी (+600 कॅलरी)",
            labelEn = "Bhaji + Bhakri (+600 kcal)"
        )

        val goal = repository.observeGoalByDate(date).first()
        assertNotNull(goal)
        assertEquals(3500, goal!!.stepsCount)
        assertEquals(1000, goal.waterIntakeMl)
        assertEquals(1000, goal.caloriesConsumed)

        val logs = repository.observeLogsForDate(date).first()
        assertEquals(3, logs.size)

        val calorieLog = logs.first { it.goalType == HealthGoalType.CALORIES }
        assertEquals("भाजी + भाकरी (+600 कॅलरी)", calorieLog.localizedLabel(Language.MARATHI))
        assertEquals("Bhaji + Bhakri (+600 kcal)", calorieLog.localizedLabel(Language.ENGLISH))

        // Deleting a log entry reverses its delta on the daily total
        repository.deleteLogEntry(calorieLog)
        val updatedGoal = repository.observeGoalByDate(date).first()
        assertEquals(400, updatedGoal!!.caloriesConsumed)
    }

    @Test
    fun `viewModel exposes Marathi and English UI state and updates targets`() = runTest {
        val date = "2026-11-19"
        val viewModel = DailyHealthGoalViewModel(
            repository = repository,
            initialDate = date,
            initialLanguage = Language.MARATHI
        )

        val collectJob = launch { viewModel.uiState.collect {} }

        val mrState = kotlinx.coroutines.withContext(Dispatchers.Default) {
            kotlinx.coroutines.withTimeout(5000) {
                viewModel.uiState.first { state ->
                    state is DailyHealthUiState.Success && state.goal.stepsCount == 8462
                } as DailyHealthUiState.Success
            }
        }
        assertEquals(Language.MARATHI, mrState.language)
        assertEquals("पावले", mrState.stepsSummary.title)
        assertEquals("पाणी पिणे", mrState.waterSummary.title)
        assertEquals("कॅलरीज", mrState.caloriesSummary.title)

        // Add water, steps & update goals
        viewModel.addWaterMl(500)
        viewModel.addSteps(538)
        viewModel.updateGoals(stepGoal = 12000, waterGoalMl = 3500, calorieGoal = 2000)
        viewModel.setLanguage(Language.ENGLISH)
        advanceUntilIdle()

        val enState = kotlinx.coroutines.withContext(Dispatchers.Default) {
            kotlinx.coroutines.withTimeout(5000) {
                viewModel.uiState.first { state ->
                    state is DailyHealthUiState.Success &&
                        state.language == Language.ENGLISH &&
                        state.goal.stepGoal == 12000 &&
                        state.goal.stepsCount == 9000 &&
                        state.goal.waterIntakeMl == 2000 &&
                        state.logs.size >= 2
                } as DailyHealthUiState.Success
            }
        }

        assertEquals(Language.ENGLISH, enState.language)
        assertEquals("Steps", enState.stepsSummary.title)
        assertEquals("Water Intake", enState.waterSummary.title)
        assertEquals("Calories", enState.caloriesSummary.title)
        assertEquals(12000, enState.goal.stepGoal)
        assertEquals(3500, enState.goal.waterGoalMl)
        assertEquals(2000, enState.goal.calorieGoal)
        assertEquals(9000, enState.goal.stepsCount)
        assertEquals(2000, enState.goal.waterIntakeMl)
        assertTrue(enState.logs.size >= 2)

        collectJob.cancel()
    }
}
