package com.example.data.model

enum class ExerciseCategory(val enLabel: String, val mrLabel: String) {
    ALL("All", "सर्व"),
    STRENGTH("Strength", "शक्ती"),
    FAT_LOSS("Fat Loss", "चरबी घटवणे"),
    BODYWEIGHT("Bodyweight", "बॉडीवेट"),
    CARDIO("Cardio", "कार्डिओ"),
    CORE("Core", "कोर"),
    FLEXIBILITY("Flexibility", "लवचिकता")
}

enum class WorkoutLevel(val enLabel: String, val mrLabel: String) {
    BEGINNER("Beginner", "नवशिक्या"),
    INTERMEDIATE("Intermediate", "मध्यम"),
    ADVANCED("Advanced", "प्रगत")
}

data class ExerciseItem(
    val id: String,
    val nameEn: String,
    val nameMr: String,
    val category: ExerciseCategory,
    val level: WorkoutLevel,
    val setsAndRepsEn: String,
    val setsAndRepsMr: String,
    val durationSeconds: Int = 45,
    val restSeconds: Int = 20,
    val caloriesBurned: Int = 40,
    val instructionsEn: String,
    val instructionsMr: String,
    val modificationEn: String,
    val modificationMr: String,
    val isCompleted: Boolean = false
)

enum class YogaType(val enLabel: String, val mrLabel: String) {
    YOGA("Yoga", "योगासने"),
    MEDITATION("Meditation", "ध्यान"),
    BREATHING("Breathing (Pranayama)", "प्राणायाम")
}

data class YogaRoutine(
    val id: String,
    val titleEn: String,
    val titleMr: String,
    val type: YogaType,
    val durationMinutes: Int,
    val level: WorkoutLevel,
    val benefitEn: String,
    val benefitMr: String,
    val stepsEn: List<String>,
    val stepsMr: List<String>
)

data class WorkoutSessionPlan(
    val id: String,
    val titleEn: String,
    val titleMr: String,
    val durationMinutes: Int,
    val exercises: List<ExerciseItem>,
    val warmUpMinutes: Int = 5,
    val coolDownMinutes: Int = 5,
    val targetLevel: WorkoutLevel = WorkoutLevel.BEGINNER
)
