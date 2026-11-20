package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.local.entity.HealthGoalType

/**
 * Room TypeConverters for custom enum types stored in the FitMarathi local database.
 */
class HealthGoalConverters {

    @TypeConverter
    fun fromHealthGoalType(type: HealthGoalType): String = type.name

    @TypeConverter
    fun toHealthGoalType(value: String): HealthGoalType =
        runCatching { HealthGoalType.valueOf(value) }.getOrDefault(HealthGoalType.STEPS)
}
