package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.DailyHealthGoalEntity
import com.example.data.local.entity.HealthGoalLogEntity
import com.example.data.local.entity.HealthGoalType
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for daily health goal records and bilingual activity logs.
 * All read queries return reactive Flows and all write operations are suspend functions.
 */
@Dao
interface DailyHealthGoalDao {

    @Query("SELECT * FROM daily_health_goals WHERE date = :date LIMIT 1")
    fun observeGoalByDate(date: String): Flow<DailyHealthGoalEntity?>

    @Query("SELECT * FROM daily_health_goals WHERE date = :date LIMIT 1")
    suspend fun getGoalByDate(date: String): DailyHealthGoalEntity?

    @Query("SELECT * FROM daily_health_goals ORDER BY date DESC LIMIT :limit")
    fun observeRecentGoals(limit: Int = 7): Flow<List<DailyHealthGoalEntity>>

    @Query("SELECT * FROM daily_health_goals ORDER BY date DESC")
    fun observeAllGoals(): Flow<List<DailyHealthGoalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertGoal(goal: DailyHealthGoalEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertGoalIfAbsent(goal: DailyHealthGoalEntity): Long

    @Update
    suspend fun updateGoal(goal: DailyHealthGoalEntity)

    @Query(
        """
        UPDATE daily_health_goals
        SET steps_count = MAX(0, steps_count + :deltaSteps),
            calories_burned = MAX(0, calories_burned + :deltaCaloriesBurned),
            updated_at = :updatedAt
        WHERE date = :date
        """
    )
    suspend fun addSteps(
        date: String,
        deltaSteps: Int,
        deltaCaloriesBurned: Int,
        updatedAt: Long = System.currentTimeMillis()
    )

    @Query(
        """
        UPDATE daily_health_goals
        SET water_intake_ml = MAX(0, water_intake_ml + :deltaMl),
            updated_at = :updatedAt
        WHERE date = :date
        """
    )
    suspend fun addWaterMl(
        date: String,
        deltaMl: Int,
        updatedAt: Long = System.currentTimeMillis()
    )

    @Query(
        """
        UPDATE daily_health_goals
        SET calories_consumed = MAX(0, calories_consumed + :deltaCalories),
            updated_at = :updatedAt
        WHERE date = :date
        """
    )
    suspend fun addCaloriesConsumed(
        date: String,
        deltaCalories: Int,
        updatedAt: Long = System.currentTimeMillis()
    )

    @Query(
        """
        UPDATE daily_health_goals
        SET step_goal = :stepGoal,
            water_goal_ml = :waterGoalMl,
            calorie_goal = :calorieGoal,
            updated_at = :updatedAt
        WHERE date = :date
        """
    )
    suspend fun updateTargets(
        date: String,
        stepGoal: Int,
        waterGoalMl: Int,
        calorieGoal: Int,
        updatedAt: Long = System.currentTimeMillis()
    )

    @Query(
        """
        UPDATE daily_health_goals
        SET language_code = :languageCode,
            updated_at = :updatedAt
        WHERE date = :date
        """
    )
    suspend fun updateLanguage(
        date: String,
        languageCode: String,
        updatedAt: Long = System.currentTimeMillis()
    )

    @Query(
        """
        UPDATE daily_health_goals
        SET steps_count = 0,
            water_intake_ml = 0,
            calories_consumed = 0,
            calories_burned = 0,
            workout_minutes = 0,
            updated_at = :updatedAt
        WHERE date = :date
        """
    )
    suspend fun resetProgressForDate(
        date: String,
        updatedAt: Long = System.currentTimeMillis()
    )

    @Query("DELETE FROM daily_health_goals WHERE date = :date")
    suspend fun deleteGoalByDate(date: String)

    @Query("DELETE FROM daily_health_goals")
    suspend fun clearAllGoals()

    // --- Granular Bilingual Log Entries ---

    @Query("SELECT * FROM health_goal_logs WHERE date = :date ORDER BY timestamp DESC")
    fun observeLogsForDate(date: String): Flow<List<HealthGoalLogEntity>>

    @Query(
        """
        SELECT * FROM health_goal_logs
        WHERE date = :date AND goal_type = :goalType
        ORDER BY timestamp DESC
        """
    )
    fun observeLogsByDateAndType(
        date: String,
        goalType: HealthGoalType
    ): Flow<List<HealthGoalLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: HealthGoalLogEntity): Long

    @Query("DELETE FROM health_goal_logs WHERE id = :logId")
    suspend fun deleteLogById(logId: Long)

    @Query("DELETE FROM health_goal_logs WHERE date = :date")
    suspend fun deleteLogsForDate(date: String)
}
