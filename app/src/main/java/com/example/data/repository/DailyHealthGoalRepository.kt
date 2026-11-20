package com.example.data.repository

import com.example.data.local.dao.DailyHealthGoalDao
import com.example.data.local.entity.DailyHealthGoalEntity
import com.example.data.local.entity.HealthGoalLogEntity
import com.example.data.local.entity.HealthGoalType
import com.example.data.model.Language
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Repository abstracting Room database access for daily health goals (steps, water intake, calories)
 * and bilingual Marathi/English tracking logs.
 */
class DailyHealthGoalRepository(
    private val dao: DailyHealthGoalDao
) {
    fun observeGoalByDate(date: String): Flow<DailyHealthGoalEntity?> =
        dao.observeGoalByDate(date)

    fun observeRecentGoals(limit: Int = 7): Flow<List<DailyHealthGoalEntity>> =
        dao.observeRecentGoals(limit)

    fun observeAllGoals(): Flow<List<DailyHealthGoalEntity>> =
        dao.observeAllGoals()

    fun observeLogsForDate(date: String): Flow<List<HealthGoalLogEntity>> =
        dao.observeLogsForDate(date)

    fun observeLogsByType(date: String, goalType: HealthGoalType): Flow<List<HealthGoalLogEntity>> =
        dao.observeLogsByDateAndType(date, goalType)

    /**
     * Ensures a daily health goal row exists for [date] before logging progress.
     * Seeds sensible initial defaults when creating a fresh record.
     */
    suspend fun ensureGoalExists(
        date: String = todayIsoDate(),
        initialSteps: Int = 0,
        stepGoal: Int = 10000,
        initialWaterMl: Int = 0,
        waterGoalMl: Int = 3000,
        initialCaloriesConsumed: Int = 0,
        initialCaloriesBurned: Int = 0,
        calorieGoal: Int = 1800,
        language: Language = Language.MARATHI
    ): DailyHealthGoalEntity {
        val existing = dao.getGoalByDate(date)
        if (existing != null) return existing

        val fresh = DailyHealthGoalEntity(
            date = date,
            stepsCount = initialSteps.coerceAtLeast(0),
            stepGoal = stepGoal.coerceAtLeast(1000),
            waterIntakeMl = initialWaterMl.coerceAtLeast(0),
            waterGoalMl = waterGoalMl.coerceAtLeast(500),
            caloriesConsumed = initialCaloriesConsumed.coerceAtLeast(0),
            caloriesBurned = initialCaloriesBurned.coerceAtLeast(0),
            calorieGoal = calorieGoal.coerceAtLeast(1000),
            languageCode = language.code,
            noteMr = "आजचे आरोग्य ध्येय ट्रॅक सुरू आहे",
            noteEn = "Tracking today's health goals"
        )
        dao.insertGoalIfAbsent(fresh)
        return dao.getGoalByDate(date) ?: fresh
    }

    suspend fun upsertGoal(goal: DailyHealthGoalEntity) {
        dao.upsertGoal(goal.copy(updatedAt = System.currentTimeMillis()))
    }

    /**
     * Logs incremental steps and updates estimated burned calories (~0.04 kcal per step).
     */
    suspend fun logSteps(
        date: String = todayIsoDate(),
        stepsDelta: Int,
        labelMr: String? = null,
        labelEn: String? = null
    ) {
        if (stepsDelta == 0) return
        ensureGoalExists(date)
        val burnedDelta = (stepsDelta * 0.04f).toInt()
        dao.addSteps(
            date = date,
            deltaSteps = stepsDelta,
            deltaCaloriesBurned = burnedDelta
        )
        dao.insertLog(
            HealthGoalLogEntity(
                date = date,
                goalType = HealthGoalType.STEPS,
                amount = stepsDelta,
                labelMr = labelMr ?: if (stepsDelta > 0) "चालणे (+$stepsDelta पावले)" else "पावले बदल ($stepsDelta)",
                labelEn = labelEn ?: if (stepsDelta > 0) "Walking (+$stepsDelta steps)" else "Steps adjusted ($stepsDelta)"
            )
        )
    }

    /**
     * Logs water intake in milliliters (e.g., 250 ml = 1 glass).
     */
    suspend fun logWater(
        date: String = todayIsoDate(),
        waterDeltaMl: Int,
        labelMr: String? = null,
        labelEn: String? = null
    ) {
        if (waterDeltaMl == 0) return
        ensureGoalExists(date)
        dao.addWaterMl(date = date, deltaMl = waterDeltaMl)
        dao.insertLog(
            HealthGoalLogEntity(
                date = date,
                goalType = HealthGoalType.WATER,
                amount = waterDeltaMl,
                labelMr = labelMr ?: if (waterDeltaMl > 0) "पाणी (+$waterDeltaMl मिली)" else "पाणी बदल ($waterDeltaMl मिली)",
                labelEn = labelEn ?: if (waterDeltaMl > 0) "Water (+$waterDeltaMl ml)" else "Water adjusted ($waterDeltaMl ml)"
            )
        )
    }

    /**
     * Logs consumed calories (e.g., Maharashtrian meal or snack).
     */
    suspend fun logCalories(
        date: String = todayIsoDate(),
        caloriesDelta: Int,
        labelMr: String? = null,
        labelEn: String? = null
    ) {
        if (caloriesDelta == 0) return
        ensureGoalExists(date)
        dao.addCaloriesConsumed(date = date, deltaCalories = caloriesDelta)
        dao.insertLog(
            HealthGoalLogEntity(
                date = date,
                goalType = HealthGoalType.CALORIES,
                amount = caloriesDelta,
                labelMr = labelMr ?: if (caloriesDelta > 0) "आहार (+$caloriesDelta कॅलरी)" else "कॅलरी बदल ($caloriesDelta)",
                labelEn = labelEn ?: if (caloriesDelta > 0) "Meal (+$caloriesDelta kcal)" else "Calories adjusted ($caloriesDelta kcal)"
            )
        )
    }

    suspend fun updateDailyTargets(
        date: String = todayIsoDate(),
        stepGoal: Int,
        waterGoalMl: Int,
        calorieGoal: Int
    ) {
        ensureGoalExists(date)
        dao.updateTargets(
            date = date,
            stepGoal = stepGoal.coerceIn(1000, 50000),
            waterGoalMl = waterGoalMl.coerceIn(500, 8000),
            calorieGoal = calorieGoal.coerceIn(1000, 6000)
        )
    }

    suspend fun updateLanguage(date: String = todayIsoDate(), language: Language) {
        ensureGoalExists(date, language = language)
        dao.updateLanguage(date = date, languageCode = language.code)
    }

    /**
     * Removes a single log entry and reverses its contribution to the daily totals.
     */
    suspend fun deleteLogEntry(log: HealthGoalLogEntity) {
        dao.deleteLogById(log.id)
        when (log.goalType) {
            HealthGoalType.STEPS -> {
                val burnedDelta = -(log.amount * 0.04f).toInt()
                dao.addSteps(log.date, -log.amount, burnedDelta)
            }
            HealthGoalType.WATER -> {
                dao.addWaterMl(log.date, -log.amount)
            }
            HealthGoalType.CALORIES -> {
                dao.addCaloriesConsumed(log.date, -log.amount)
            }
        }
    }

    suspend fun resetDailyProgress(date: String = todayIsoDate()) {
        ensureGoalExists(date)
        dao.resetProgressForDate(date)
        dao.deleteLogsForDate(date)
    }

    companion object {
        fun todayIsoDate(): String {
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            return formatter.format(Date())
        }
    }
}
