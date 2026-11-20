package com.example.data.model

enum class Language(val code: String, val displayName: String, val nativeName: String) {
    MARATHI("mr", "Marathi", "मराठी"),
    ENGLISH("en", "English", "English")
}

enum class FitnessGoal(val englishLabel: String, val marathiLabel: String) {
    FAT_LOSS("Fat Loss", "वजन कमी करणे"),
    MUSCLE_GAIN("Muscle Gain", "वजन व स्नायू वाढवणे"),
    MAINTAIN("Maintain Weight", "वजन नियंत्रणात ठेवणे"),
    GENERAL_FITNESS("General Fitness", "सामान्य तंदुरुस्ती व आरोग्य")
}

enum class FoodPreference(val englishLabel: String, val marathiLabel: String) {
    VEGETARIAN("Vegetarian", "शाकाहारी"),
    NON_VEGETARIAN("Non-Vegetarian", "मांसाहारी"),
    BOTH("Both (Veg & Non-Veg)", "दोन्ही (शाकाहारी व मांसाहारी)")
}

enum class ActivityLevel(val englishLabel: String, val marathiLabel: String) {
    SEDENTARY("Sedentary (Desk Job)", "बैठे काम (कमी हालचाल)"),
    LIGHTLY_ACTIVE("Lightly Active (1-3 days/wk)", "हलका व्यायाम (१-३ दिवस)"),
    MODERATELY_ACTIVE("Moderately Active (3-5 days/wk)", "मध्यम व्यायाम (३-५ दिवस)"),
    VERY_ACTIVE("Very Active (6-7 days/wk)", "अति उत्साही / कठीण व्यायाम")
}

data class HealthCondition(
    val id: String,
    val nameEn: String,
    val nameMr: String,
    val isSelected: Boolean = false
)

data class UserProfile(
    val name: String = "Rohit Patil",
    val email: String = "rohit@gmail.com",
    val phone: String = "+91 98765 43210",
    val age: Int = 25,
    val birthDate: String = "15/08/1999",
    val gender: String = "पुरुष (Male)",
    val heightCm: Int = 170,
    val weightKg: Float = 72.0f,
    val targetWeightKg: Float = 68.0f,
    val goal: FitnessGoal = FitnessGoal.FAT_LOSS,
    val foodPreference: FoodPreference = FoodPreference.BOTH,
    val activityLevel: ActivityLevel = ActivityLevel.MODERATELY_ACTIVE,
    val waterGoalLiters: Float = 3.0f,
    val stepGoal: Int = 10000,
    val calorieGoal: Int = 1800,
    val workoutGoalMinutes: Int = 30,
    val healthConditions: List<String> = listOf("None"),
    val allergies: String = "No known allergies",
    val sleepHoursPerNight: Float = 7.5f,
    val hasPhotoConsent: Boolean = true,
    val isDemoUser: Boolean = true,
    val isAuthenticated: Boolean = true,
    val language: Language = Language.MARATHI
)

data class DailyLog(
    val date: String = "Today",
    val steps: Int = 8462,
    val stepGoal: Int = 10000,
    val caloriesBurned: Int = 540,
    val caloriesConsumed: Int = 1420,
    val calorieGoal: Int = 1800,
    val waterDrankLiters: Float = 1.5f,
    val waterGoalLiters: Float = 3.0f,
    val workoutMinutes: Int = 30,
    val workoutGoalMinutes: Int = 30,
    val completedExercisesCount: Int = 4,
    val totalScheduledExercises: Int = 6,
    val sleepHours: Float = 7.5f,
    val weightKg: Float = 72.0f,
    val bodyFatPercentage: Float = 18.0f
)
