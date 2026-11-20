package com.example.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.data.model.Language

/**
 * Categories of tracked daily health goals with localized Marathi and English labels.
 */
enum class HealthGoalType(
    val titleEn: String,
    val titleMr: String,
    val unitEn: String,
    val unitMr: String
) {
    STEPS(
        titleEn = "Steps",
        titleMr = "पावले",
        unitEn = "steps",
        unitMr = "पावले"
    ),
    WATER(
        titleEn = "Water Intake",
        titleMr = "पाणी पिणे",
        unitEn = "ml",
        unitMr = "मिली"
    ),
    CALORIES(
        titleEn = "Calories",
        titleMr = "कॅलरीज",
        unitEn = "kcal",
        unitMr = "कॅलरी"
    );

    fun localizedTitle(language: Language): String =
        if (language == Language.MARATHI) titleMr else titleEn

    fun localizedUnit(language: Language): String =
        if (language == Language.MARATHI) unitMr else unitEn
}

/**
 * Room entity storing a user's daily health targets and progress
 * (steps, water intake in milliliters, and calories) keyed by ISO date (yyyy-MM-dd).
 */
@Entity(tableName = "daily_health_goals")
data class DailyHealthGoalEntity(
    @PrimaryKey
    @ColumnInfo(name = "date")
    val date: String,

    @ColumnInfo(name = "steps_count")
    val stepsCount: Int = 0,

    @ColumnInfo(name = "step_goal")
    val stepGoal: Int = 10000,

    @ColumnInfo(name = "water_intake_ml")
    val waterIntakeMl: Int = 0,

    @ColumnInfo(name = "water_goal_ml")
    val waterGoalMl: Int = 3000,

    @ColumnInfo(name = "calories_consumed")
    val caloriesConsumed: Int = 0,

    @ColumnInfo(name = "calories_burned")
    val caloriesBurned: Int = 0,

    @ColumnInfo(name = "calorie_goal")
    val calorieGoal: Int = 1800,

    @ColumnInfo(name = "workout_minutes")
    val workoutMinutes: Int = 0,

    @ColumnInfo(name = "workout_goal_minutes")
    val workoutGoalMinutes: Int = 30,

    @ColumnInfo(name = "language_code")
    val languageCode: String = Language.MARATHI.code,

    @ColumnInfo(name = "note_mr")
    val noteMr: String = "",

    @ColumnInfo(name = "note_en")
    val noteEn: String = "",

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
) {
    val waterIntakeLiters: Float
        get() = (waterIntakeMl / 100f).toInt() / 10f

    val waterGoalLiters: Float
        get() = (waterGoalMl / 100f).toInt() / 10f

    val stepsProgress: Float
        get() = if (stepGoal > 0) (stepsCount.toFloat() / stepGoal).coerceIn(0f, 1f) else 0f

    val waterProgress: Float
        get() = if (waterGoalMl > 0) (waterIntakeMl.toFloat() / waterGoalMl).coerceIn(0f, 1f) else 0f

    val caloriesProgress: Float
        get() = if (calorieGoal > 0) (caloriesConsumed.toFloat() / calorieGoal).coerceIn(0f, 1f) else 0f

    val overallGoalProgress: Float
        get() = ((stepsProgress + waterProgress + caloriesProgress) / 3f).coerceIn(0f, 1f)

    fun resolvedLanguage(): Language =
        if (languageCode == Language.ENGLISH.code) Language.ENGLISH else Language.MARATHI
}

/**
 * Room entity storing individual timestamped log entries for steps, water, and calories
 * with both Marathi and English descriptions.
 */
@Entity(
    tableName = "health_goal_logs",
    foreignKeys = [
        ForeignKey(
            entity = DailyHealthGoalEntity::class,
            parentColumns = ["date"],
            childColumns = ["date"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["date"])]
)
data class HealthGoalLogEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "date")
    val date: String,

    @ColumnInfo(name = "goal_type")
    val goalType: HealthGoalType,

    @ColumnInfo(name = "amount")
    val amount: Int,

    @ColumnInfo(name = "label_mr")
    val labelMr: String,

    @ColumnInfo(name = "label_en")
    val labelEn: String,

    @ColumnInfo(name = "timestamp")
    val timestamp: Long = System.currentTimeMillis()
) {
    fun localizedLabel(language: Language): String =
        if (language == Language.MARATHI) labelMr else labelEn
}
