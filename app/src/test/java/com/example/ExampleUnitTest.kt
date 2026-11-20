package com.example

import com.example.data.model.FitnessGoal
import com.example.data.model.FoodPreference
import com.example.data.model.Language
import com.example.data.repository.FitMarathiRepository
import com.example.data.repository.SampleData
import com.example.data.service.AiCoachService
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testExerciseLibraryHasSixtyExercises() {
        assertEquals("FitMarathi must have 60 curated exercises", 60, SampleData.allExercises.size)
    }

    @Test
    fun testRepositoryWaterAndStepsTracking() {
        val repo = FitMarathiRepository()
        val initialWater = repo.dailyLog.value.waterDrankLiters
        repo.addWater(0.5f)
        assertEquals(initialWater + 0.5f, repo.dailyLog.value.waterDrankLiters, 0.01f)

        val initialSteps = repo.dailyLog.value.steps
        repo.addSteps(1000)
        assertEquals(initialSteps + 1000, repo.dailyLog.value.steps)
    }

    @Test
    fun testLanguageToggle() {
        val repo = FitMarathiRepository()
        assertEquals(Language.MARATHI, repo.userProfile.value.language)
        repo.updateLanguage(Language.ENGLISH)
        assertEquals(Language.ENGLISH, repo.userProfile.value.language)
    }

    @Test
    fun testMealLoggingCalorieSync() {
        val repo = FitMarathiRepository()
        val initialConsumed = repo.dailyLog.value.caloriesConsumed
        val firstMeal = repo.meals.value.first()

        repo.toggleMealLogged(firstMeal.id)
        assertTrue(repo.meals.value.first().isLogged)
        assertEquals(initialConsumed + firstMeal.calories, repo.dailyLog.value.caloriesConsumed)

        // Untoggle
        repo.toggleMealLogged(firstMeal.id)
        assertFalse(repo.meals.value.first().isLogged)
        assertEquals(initialConsumed, repo.dailyLog.value.caloriesConsumed)
    }

    @Test
    fun testAiCoachIntelligentMaharashtrianReply() {
        val service = AiCoachService()
        val profile = com.example.data.model.UserProfile(
            name = "Rohit",
            language = Language.MARATHI,
            goal = FitnessGoal.FAT_LOSS,
            foodPreference = FoodPreference.BOTH
        )

        val dietReply = service.generateIntelligentMaharashtrianReply("आज काय खायला पाहिजे?", profile)
        assertTrue("Reply should contain Maharashtrian foods", dietReply.contains("भाकरी") || dietReply.contains("नाश्ता"))

        val englishProfile = profile.copy(language = Language.ENGLISH)
        val workoutReply = service.generateIntelligentMaharashtrianReply("What workout today?", englishProfile)
        assertTrue("Reply should provide structured workout advice", workoutReply.contains("Workout") || workoutReply.contains("Push-Ups"))
    }
}
