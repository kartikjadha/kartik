package com.example.data.repository

import android.content.Context
import com.example.data.local.FitMarathiDatabase
import com.example.data.model.*
import com.example.notifications.HealthReminderScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FitMarathiRepository(
    private val context: Context? = null,
    dailyHealthGoalRepository: DailyHealthGoalRepository? = null
) {

    private val repoScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val healthGoalRepository: DailyHealthGoalRepository? = dailyHealthGoalRepository
        ?: context?.let { DailyHealthGoalRepository(FitMarathiDatabase.getInstance(it).dailyHealthGoalDao()) }

    private val todayDate: String
        get() = DailyHealthGoalRepository.todayIsoDate()

    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _dailyLog = MutableStateFlow(DailyLog())
    val dailyLog: StateFlow<DailyLog> = _dailyLog.asStateFlow()

    private val _meals = MutableStateFlow(SampleData.sampleMeals)
    val meals: StateFlow<List<MealItem>> = _meals.asStateFlow()

    private val _exercises = MutableStateFlow(SampleData.allExercises)
    val exercises: StateFlow<List<ExerciseItem>> = _exercises.asStateFlow()

    private val _yogaRoutines = MutableStateFlow(SampleData.yogaRoutines)
    val yogaRoutines: StateFlow<List<YogaRoutine>> = _yogaRoutines.asStateFlow()

    private val _routineCatalog = MutableStateFlow(WorkoutRoutineCatalog.allRoutines)
    val routineCatalog: StateFlow<List<FitnessYogaRoutine>> = _routineCatalog.asStateFlow()

    private val _reminders = MutableStateFlow(SampleData.defaultReminders)
    val reminders: StateFlow<List<ReminderItem>> = _reminders.asStateFlow()

    private val _groceries = MutableStateFlow(SampleData.groceryList)
    val groceries: StateFlow<List<GroceryItem>> = _groceries.asStateFlow()

    private val _healthReport = MutableStateFlow(
        HealthReportData(
            overallScore = 78,
            bodyFatPercentage = 18.0f,
            bmi = 24.8f,
            risks = SampleData.healthRisks
        )
    )
    val healthReport: StateFlow<HealthReportData> = _healthReport.asStateFlow()

    init {
        recalculatePersonalizedTargets(_userProfile.value)
        healthGoalRepository?.let { roomRepo ->
            repoScope.launch {
                val initial = _dailyLog.value
                roomRepo.ensureGoalExists(
                    date = todayDate,
                    initialSteps = initial.steps,
                    stepGoal = initial.stepGoal,
                    initialWaterMl = (initial.waterDrankLiters * 1000).toInt(),
                    waterGoalMl = (initial.waterGoalLiters * 1000).toInt(),
                    initialCaloriesConsumed = initial.caloriesConsumed,
                    initialCaloriesBurned = initial.caloriesBurned,
                    calorieGoal = initial.calorieGoal,
                    language = _userProfile.value.language
                )
                roomRepo.observeGoalByDate(todayDate).collect { entity ->
                    if (entity != null) {
                        _dailyLog.update { current ->
                            current.copy(
                                date = entity.date,
                                steps = entity.stepsCount,
                                stepGoal = entity.stepGoal,
                                waterDrankLiters = entity.waterIntakeLiters,
                                waterGoalLiters = entity.waterGoalLiters,
                                caloriesConsumed = entity.caloriesConsumed,
                                caloriesBurned = entity.caloriesBurned,
                                calorieGoal = entity.calorieGoal
                            )
                        }
                        _userProfile.update { profile ->
                            profile.copy(
                                stepGoal = entity.stepGoal,
                                waterGoalLiters = entity.waterGoalLiters,
                                calorieGoal = entity.calorieGoal,
                                language = entity.resolvedLanguage()
                            )
                        }
                    }
                }
            }
        }
        context?.let { ctx ->
            HealthReminderScheduler.syncAllReminders(ctx, _reminders.value, _userProfile.value.language)
        }
    }

    fun updateLanguage(newLang: Language) {
        _userProfile.update { it.copy(language = newLang) }
        healthGoalRepository?.let { roomRepo ->
            repoScope.launch { roomRepo.updateLanguage(todayDate, newLang) }
        }
        context?.let { ctx ->
            HealthReminderScheduler.syncAllReminders(ctx, _reminders.value, newLang)
        }
    }

    /**
     * Personalization Engine:
     * Dynamically calculates metabolic rate (BMR), calorie deficit/surplus,
     * protein targets, and optimal hydration based on weight, height, age,
     * gender, activity level, and fitness goal.
     */
    fun recalculatePersonalizedTargets(profile: UserProfile) {
        val isMale = !profile.gender.contains("Female", ignoreCase = true) && !profile.gender.contains("महिला", ignoreCase = true)
        val bmr = if (isMale) {
            (10 * profile.weightKg) + (6.25f * profile.heightCm) - (5 * profile.age) + 5
        } else {
            (10 * profile.weightKg) + (6.25f * profile.heightCm) - (5 * profile.age) - 161
        }

        val activityMultiplier = when (profile.activityLevel) {
            ActivityLevel.SEDENTARY -> 1.2f
            ActivityLevel.LIGHTLY_ACTIVE -> 1.375f
            ActivityLevel.MODERATELY_ACTIVE -> 1.55f
            ActivityLevel.VERY_ACTIVE -> 1.725f
        }

        val maintenanceCalories = (bmr * activityMultiplier).toInt()

        val targetCalories = when (profile.goal) {
            FitnessGoal.FAT_LOSS -> (maintenanceCalories - 400).coerceAtLeast(1300)
            FitnessGoal.MUSCLE_GAIN -> maintenanceCalories + 300
            FitnessGoal.MAINTAIN, FitnessGoal.GENERAL_FITNESS -> maintenanceCalories
        }

        // Hydration based on weight: 35ml per kg of body weight
        val targetWater = ((profile.weightKg * 0.035f * 10).toInt() / 10f).coerceIn(2.0f, 4.5f)

        // Height in meters for BMI
        val heightM = profile.heightCm / 100f
        val calculatedBmi = if (heightM > 0) profile.weightKg / (heightM * heightM) else 24.8f
        val formattedBmi = (calculatedBmi * 10).toInt() / 10f

        val (bmiCatEn, bmiCatMr) = when {
            calculatedBmi < 18.5f -> "Underweight" to "कमी वजन (Underweight)"
            calculatedBmi < 25.0f -> "Normal" to "सामान्य (Normal)"
            calculatedBmi < 30.0f -> "Overweight" to "जास्त वजन (Overweight)"
            else -> "Obese" to "लठ्ठपणा (Obese)"
        }

        // Diabetes risk adjustment based on BMI and goal
        val diabetesRiskPct = when {
            calculatedBmi >= 28.0f -> 45
            calculatedBmi >= 25.0f -> 28
            else -> 14
        }

        _userProfile.update {
            it.copy(
                calorieGoal = targetCalories,
                waterGoalLiters = targetWater
            )
        }

        _dailyLog.update {
            it.copy(
                calorieGoal = targetCalories,
                waterGoalLiters = targetWater,
                weightKg = profile.weightKg
            )
        }

        _healthReport.update {
            it.copy(
                bmi = formattedBmi,
                bmiCategoryEn = bmiCatEn,
                bmiCategoryMr = bmiCatMr,
                diabetesRiskPercent = diabetesRiskPct
            )
        }
    }

    fun updateProfile(
        name: String,
        email: String,
        phone: String,
        age: Int,
        heightCm: Int,
        weightKg: Float,
        goal: FitnessGoal,
        foodPreference: FoodPreference,
        activityLevel: ActivityLevel = ActivityLevel.MODERATELY_ACTIVE
    ) {
        val updated = _userProfile.value.copy(
            name = name,
            email = email,
            phone = phone,
            age = age,
            heightCm = heightCm,
            weightKg = weightKg,
            goal = goal,
            foodPreference = foodPreference,
            activityLevel = activityLevel
        )
        _userProfile.value = updated
        recalculatePersonalizedTargets(updated)
    }

    fun addWater(amountLiters: Float) {
        val deltaMl = (amountLiters * 1000).toInt()
        _dailyLog.update { current ->
            val updated = (current.waterDrankLiters + amountLiters).coerceAtLeast(0f)
            current.copy(waterDrankLiters = (updated * 10).toInt() / 10f)
        }
        healthGoalRepository?.let { roomRepo ->
            repoScope.launch { roomRepo.logWater(todayDate, deltaMl) }
        }
    }

    fun resetWater() {
        val currentMl = (_dailyLog.value.waterDrankLiters * 1000).toInt()
        _dailyLog.update { it.copy(waterDrankLiters = 0f) }
        healthGoalRepository?.let { roomRepo ->
            if (currentMl > 0) {
                repoScope.launch {
                    roomRepo.logWater(
                        date = todayDate,
                        waterDeltaMl = -currentMl,
                        labelMr = "पाण्याची नोंद रीसेट केली",
                        labelEn = "Water intake reset"
                    )
                }
            }
        }
    }

    fun setWaterGoal(goalLiters: Float) {
        _userProfile.update { it.copy(waterGoalLiters = goalLiters) }
        _dailyLog.update { it.copy(waterGoalLiters = goalLiters) }
        healthGoalRepository?.let { roomRepo ->
            val current = _dailyLog.value
            repoScope.launch {
                roomRepo.updateDailyTargets(
                    date = todayDate,
                    stepGoal = current.stepGoal,
                    waterGoalMl = (goalLiters * 1000).toInt(),
                    calorieGoal = current.calorieGoal
                )
            }
        }
    }

    fun addSteps(stepsToAdd: Int) {
        _dailyLog.update { current ->
            val newSteps = (current.steps + stepsToAdd).coerceAtLeast(0)
            val addedCalories = (stepsToAdd * 0.04f).toInt()
            current.copy(
                steps = newSteps,
                caloriesBurned = current.caloriesBurned + addedCalories
            )
        }
        healthGoalRepository?.let { roomRepo ->
            repoScope.launch { roomRepo.logSteps(todayDate, stepsToAdd) }
        }
    }

    fun toggleMealLogged(mealId: String) {
        var loggedMeal: MealItem? = null
        var diff = 0
        _meals.update { list ->
            list.map { meal ->
                if (meal.id == mealId) {
                    val toggled = !meal.isLogged
                    diff = if (toggled) meal.calories else -meal.calories
                    loggedMeal = meal
                    _dailyLog.update { log ->
                        log.copy(caloriesConsumed = (log.caloriesConsumed + diff).coerceAtLeast(0))
                    }
                    meal.copy(isLogged = toggled)
                } else meal
            }
        }
        loggedMeal?.let { meal ->
            healthGoalRepository?.let { roomRepo ->
                repoScope.launch {
                    roomRepo.logCalories(
                        date = todayDate,
                        caloriesDelta = diff,
                        labelMr = if (diff > 0) "${meal.titleMr} (+${meal.calories} कॅलरी)" else "${meal.titleMr} काढले ($diff)",
                        labelEn = if (diff > 0) "${meal.titleEn} (+${meal.calories} kcal)" else "${meal.titleEn} removed ($diff)"
                    )
                }
            }
        }
    }

    /**
     * Swap a meal with an alternative Maharashtrian dish.
     */
    fun swapMeal(oldMealId: String, newMeal: MealItem) {
        var calorieDiff = 0
        _meals.update { list ->
            list.map { meal ->
                if (meal.id == oldMealId) {
                    // Retain logged status if already logged
                    val wasLogged = meal.isLogged
                    if (wasLogged) {
                        calorieDiff = newMeal.calories - meal.calories
                        _dailyLog.update { log ->
                            log.copy(caloriesConsumed = (log.caloriesConsumed + calorieDiff).coerceAtLeast(0))
                        }
                    }
                    newMeal.copy(id = oldMealId, isLogged = wasLogged)
                } else meal
            }
        }
        if (calorieDiff != 0) {
            healthGoalRepository?.let { roomRepo ->
                repoScope.launch {
                    roomRepo.logCalories(
                        date = todayDate,
                        caloriesDelta = calorieDiff,
                        labelMr = "आहार बदल: ${newMeal.titleMr}",
                        labelEn = "Meal swap: ${newMeal.titleEn}"
                    )
                }
            }
        }
    }

    fun addCustomMeal(meal: MealItem) {
        _meals.update { it + meal }
        if (meal.isLogged) {
            _dailyLog.update { it.copy(caloriesConsumed = it.caloriesConsumed + meal.calories) }
            healthGoalRepository?.let { roomRepo ->
                repoScope.launch {
                    roomRepo.logCalories(
                        date = todayDate,
                        caloriesDelta = meal.calories,
                        labelMr = "${meal.titleMr} (+${meal.calories} कॅलरी)",
                        labelEn = "${meal.titleEn} (+${meal.calories} kcal)"
                    )
                }
            }
        }
    }

    fun removeMeal(mealId: String) {
        var removedCalories = 0
        var removedTitleMr = ""
        var removedTitleEn = ""
        _meals.update { list ->
            val mealToRemove = list.find { it.id == mealId }
            if (mealToRemove != null && mealToRemove.isLogged) {
                removedCalories = mealToRemove.calories
                removedTitleMr = mealToRemove.titleMr
                removedTitleEn = mealToRemove.titleEn
                _dailyLog.update { it.copy(caloriesConsumed = (it.caloriesConsumed - mealToRemove.calories).coerceAtLeast(0)) }
            }
            list.filterNot { it.id == mealId }
        }
        if (removedCalories > 0) {
            healthGoalRepository?.let { roomRepo ->
                repoScope.launch {
                    roomRepo.logCalories(
                        date = todayDate,
                        caloriesDelta = -removedCalories,
                        labelMr = "$removedTitleMr काढले (-$removedCalories कॅलरी)",
                        labelEn = "$removedTitleEn removed (-$removedCalories kcal)"
                    )
                }
            }
        }
    }

    fun toggleExerciseCompleted(exerciseId: String) {
        _exercises.update { list ->
            list.map { ex ->
                if (ex.id == exerciseId) {
                    val toggled = !ex.isCompleted
                    val diff = if (toggled) 1 else -1
                    val calorieDiff = if (toggled) ex.caloriesBurned else -ex.caloriesBurned
                    _dailyLog.update { log ->
                        log.copy(
                            completedExercisesCount = (log.completedExercisesCount + diff).coerceAtLeast(0),
                            caloriesBurned = (log.caloriesBurned + calorieDiff).coerceAtLeast(0)
                        )
                    }
                    ex.copy(isCompleted = toggled)
                } else ex
            }
        }
    }

    fun recordCompletedWorkout(durationMinutes: Int, caloriesBurned: Int, exercisesDone: Int) {
        _dailyLog.update { current ->
            current.copy(
                workoutMinutes = current.workoutMinutes + durationMinutes,
                caloriesBurned = current.caloriesBurned + caloriesBurned,
                completedExercisesCount = current.completedExercisesCount + exercisesDone
            )
        }
    }

    fun toggleRoutineCompleted(routineId: String) {
        _routineCatalog.update { list ->
            list.map { routine ->
                if (routine.id == routineId) {
                    val toggled = !routine.isCompleted
                    val minDiff = if (toggled) routine.durationMinutes else -routine.durationMinutes
                    val calDiff = if (toggled) routine.estimatedCalories else -routine.estimatedCalories
                    val countDiff = if (toggled) 1 else -1
                    _dailyLog.update { log ->
                        log.copy(
                            workoutMinutes = (log.workoutMinutes + minDiff).coerceAtLeast(0),
                            caloriesBurned = (log.caloriesBurned + calDiff).coerceAtLeast(0),
                            completedExercisesCount = (log.completedExercisesCount + countDiff).coerceAtLeast(0)
                        )
                    }
                    routine.copy(isCompleted = toggled)
                } else routine
            }
        }
    }

    fun toggleRoutineBookmark(routineId: String) {
        _routineCatalog.update { list ->
            list.map { routine ->
                if (routine.id == routineId) routine.copy(isBookmarked = !routine.isBookmarked) else routine
            }
        }
    }

    fun toggleReminder(reminderId: String) {
        var updatedItem: ReminderItem? = null
        _reminders.update { list ->
            list.map { rem ->
                if (rem.id == reminderId) {
                    val toggled = rem.copy(isEnabled = !rem.isEnabled)
                    updatedItem = toggled
                    toggled
                } else rem
            }
        }
        context?.let { ctx ->
            updatedItem?.let { item ->
                HealthReminderScheduler.scheduleReminder(ctx, item, _userProfile.value.language)
            }
        }
    }

    fun addCustomReminder(type: NotificationType, hourOfDay: Int, minute: Int) {
        val safeHour = hourOfDay.coerceIn(0, 23)
        val safeMinute = minute.coerceIn(0, 59)
        val formattedTime = ReminderItem.formatTime12Hour(safeHour, safeMinute)
        val id = "rem_custom_${type.name.lowercase()}_${System.currentTimeMillis()}"
        val newReminder = when (type) {
            NotificationType.WATER -> ReminderItem(
                id = id,
                type = NotificationType.WATER,
                titleEn = "Water Intake Reminder",
                titleMr = "पाणी पिण्याची आठवण",
                messageEn = "Time to drink 250 ml water and log your hydration!",
                messageMr = "२५० मिली पाणी पिण्याची व नोंदवण्याची वेळ झाली आहे!",
                timeSlot = formattedTime,
                isEnabled = true,
                hourOfDay = safeHour,
                minute = safeMinute
            )
            NotificationType.STEPS -> ReminderItem(
                id = id,
                type = NotificationType.STEPS,
                titleEn = "Steps Check-In Reminder",
                titleMr = "पावले नोंदवण्याची आठवण",
                messageEn = "Stand up, take a brisk walk, and log your daily steps!",
                messageMr = "थोडं चालून घ्या आणि आजची पावले अॅपमध्ये नोंदवा!",
                timeSlot = formattedTime,
                isEnabled = true,
                hourOfDay = safeHour,
                minute = safeMinute
            )
            else -> ReminderItem(
                id = id,
                type = type,
                titleEn = "Wellness Reminder",
                titleMr = "आरोग्य स्मरणपत्र",
                messageEn = "Stay consistent with your FitMarathi daily plan!",
                messageMr = "तुमच्या दैनिक फिटनेस योजनेचे पालन करा!",
                timeSlot = formattedTime,
                isEnabled = true,
                hourOfDay = safeHour,
                minute = safeMinute
            )
        }
        _reminders.update { listOf(newReminder) + it }
        context?.let { ctx ->
            HealthReminderScheduler.scheduleReminder(ctx, newReminder, _userProfile.value.language)
        }
    }

    fun updateReminderTime(reminderId: String, hourOfDay: Int, minute: Int) {
        val safeHour = hourOfDay.coerceIn(0, 23)
        val safeMinute = minute.coerceIn(0, 59)
        val formatted = ReminderItem.formatTime12Hour(safeHour, safeMinute)
        var updatedItem: ReminderItem? = null
        _reminders.update { list ->
            list.map { rem ->
                if (rem.id == reminderId) {
                    val edited = rem.copy(
                        hourOfDay = safeHour,
                        minute = safeMinute,
                        timeSlot = formatted,
                        isEnabled = true
                    )
                    updatedItem = edited
                    edited
                } else rem
            }
        }
        context?.let { ctx ->
            updatedItem?.let { item ->
                HealthReminderScheduler.scheduleReminder(ctx, item, _userProfile.value.language)
            }
        }
    }

    fun deleteReminder(reminderId: String) {
        _reminders.update { list -> list.filterNot { it.id == reminderId } }
        context?.let { ctx ->
            HealthReminderScheduler.cancelReminder(ctx, reminderId)
        }
    }

    fun sendTestNotification(type: NotificationType): Boolean {
        val ctx = context ?: return false
        val isMr = _userProfile.value.language == Language.MARATHI
        return when (type) {
            NotificationType.WATER -> HealthReminderScheduler.showLocalNotification(
                context = ctx,
                reminderId = "test_water_notification",
                type = NotificationType.WATER,
                title = if (isMr) "पाणी पिण्याची आठवण 💧" else "Water Intake Reminder 💧",
                message = if (isMr) {
                    "आजची नोंद: ${_dailyLog.value.waterDrankLiters}L / ${_dailyLog.value.waterGoalLiters}L. १ ग्लास पाणी प्या!"
                } else {
                    "Today's log: ${_dailyLog.value.waterDrankLiters}L / ${_dailyLog.value.waterGoalLiters}L. Drink a glass now!"
                }
            )
            NotificationType.STEPS -> HealthReminderScheduler.showLocalNotification(
                context = ctx,
                reminderId = "test_steps_notification",
                type = NotificationType.STEPS,
                title = if (isMr) "पावले नोंदवा 👟" else "Daily Steps Reminder 👟",
                message = if (isMr) {
                    "आजची पावले: ${_dailyLog.value.steps} / ${_dailyLog.value.stepGoal}. थोडं चालून लक्ष्य गाठा!"
                } else {
                    "Today's steps: ${_dailyLog.value.steps} / ${_dailyLog.value.stepGoal}. Take a quick walk!"
                }
            )
            else -> false
        }
    }

    fun toggleGroceryItem(itemId: String) {
        _groceries.update { list ->
            list.map { item ->
                if (item.id == itemId) item.copy(isChecked = !item.isChecked) else item
            }
        }
    }

    fun setAuthentication(isAuthenticated: Boolean) {
        _userProfile.update { it.copy(isAuthenticated = isAuthenticated) }
    }

    fun deleteAccountAndLogs() {
        _userProfile.value = UserProfile(name = "User", email = "", isAuthenticated = false)
        _dailyLog.value = DailyLog(steps = 0, caloriesConsumed = 0, caloriesBurned = 0, waterDrankLiters = 0f)
        _meals.value = SampleData.sampleMeals.map { it.copy(isLogged = false) }
        _exercises.value = SampleData.allExercises.map { it.copy(isCompleted = false) }
        _routineCatalog.value = WorkoutRoutineCatalog.allRoutines
    }
}
