package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.entity.DailyHealthGoalEntity
import com.example.data.local.entity.HealthGoalLogEntity
import com.example.data.local.entity.HealthGoalType
import com.example.data.model.Language
import com.example.data.repository.DailyHealthGoalRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Localized Marathi/English presentation summary for a single tracked health metric.
 */
data class GoalMetricSummary(
    val type: HealthGoalType,
    val title: String,
    val currentDisplay: String,
    val targetDisplay: String,
    val progressFraction: Float,
    val progressPercent: Int
)

/**
 * Sealed UI state for daily health goal tracking following type-safe MVVM patterns.
 */
sealed interface DailyHealthUiState {
    data object Loading : DailyHealthUiState

    data class Success(
        val selectedDate: String,
        val language: Language,
        val goal: DailyHealthGoalEntity,
        val logs: List<HealthGoalLogEntity>,
        val recentHistory: List<DailyHealthGoalEntity>,
        val stepsSummary: GoalMetricSummary,
        val waterSummary: GoalMetricSummary,
        val caloriesSummary: GoalMetricSummary,
        val overallProgressPercent: Int,
        val statusHeadline: String
    ) : DailyHealthUiState

    data class Error(
        val messageMr: String,
        val messageEn: String
    ) : DailyHealthUiState
}

/**
 * ViewModel managing Room-backed daily health goals (steps, water intake, calories)
 * with reactive bilingual Marathi & English UI state.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class DailyHealthGoalViewModel(
    private val repository: DailyHealthGoalRepository,
    initialDate: String = DailyHealthGoalRepository.todayIsoDate(),
    initialLanguage: Language = Language.MARATHI
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(initialDate)
    val selectedDate: StateFlow<String> = _selectedDate

    private val _language = MutableStateFlow(initialLanguage)
    val language: StateFlow<Language> = _language

    private val goalFlow = _selectedDate.flatMapLatest { date ->
        repository.observeGoalByDate(date)
    }

    private val logsFlow = _selectedDate.flatMapLatest { date ->
        repository.observeLogsForDate(date)
    }

    private val historyFlow = repository.observeRecentGoals(limit = 7)

    val uiState: StateFlow<DailyHealthUiState> = combine(
        _selectedDate,
        _language,
        goalFlow,
        logsFlow,
        historyFlow
    ) { date, lang, entity, logs, history ->
        val resolvedGoal = entity ?: DailyHealthGoalEntity(
            date = date,
            languageCode = lang.code
        )
        buildSuccessState(
            date = date,
            language = lang,
            goal = resolvedGoal,
            logs = logs,
            recentHistory = history
        ) as DailyHealthUiState
    }
        .catch { error ->
            emit(
                DailyHealthUiState.Error(
                    messageMr = "आरोग्य माहिती लोड करताना त्रुटी आली: ${error.localizedMessage ?: "अज्ञात त्रुटी"}",
                    messageEn = "Failed to load health goals: ${error.localizedMessage ?: "Unknown error"}"
                )
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DailyHealthUiState.Loading
        )

    init {
        viewModelScope.launch {
            repository.ensureGoalExists(
                date = initialDate,
                initialSteps = 8462,
                stepGoal = 10000,
                initialWaterMl = 1500,
                waterGoalMl = 3000,
                initialCaloriesConsumed = 1420,
                initialCaloriesBurned = 540,
                calorieGoal = 1800,
                language = initialLanguage
            )
        }
    }

    fun selectDate(date: String) {
        _selectedDate.value = date
        viewModelScope.launch {
            repository.ensureGoalExists(date = date, language = _language.value)
        }
    }

    fun setLanguage(newLanguage: Language) {
        _language.value = newLanguage
        viewModelScope.launch {
            repository.updateLanguage(_selectedDate.value, newLanguage)
        }
    }

    fun addSteps(stepsDelta: Int, noteMr: String? = null, noteEn: String? = null) {
        viewModelScope.launch {
            repository.logSteps(
                date = _selectedDate.value,
                stepsDelta = stepsDelta,
                labelMr = noteMr,
                labelEn = noteEn
            )
        }
    }

    fun addWaterMl(waterDeltaMl: Int, noteMr: String? = null, noteEn: String? = null) {
        viewModelScope.launch {
            repository.logWater(
                date = _selectedDate.value,
                waterDeltaMl = waterDeltaMl,
                labelMr = noteMr,
                labelEn = noteEn
            )
        }
    }

    fun addCalories(caloriesDelta: Int, noteMr: String? = null, noteEn: String? = null) {
        viewModelScope.launch {
            repository.logCalories(
                date = _selectedDate.value,
                caloriesDelta = caloriesDelta,
                labelMr = noteMr,
                labelEn = noteEn
            )
        }
    }

    fun updateGoals(stepGoal: Int, waterGoalMl: Int, calorieGoal: Int) {
        viewModelScope.launch {
            repository.updateDailyTargets(
                date = _selectedDate.value,
                stepGoal = stepGoal,
                waterGoalMl = waterGoalMl,
                calorieGoal = calorieGoal
            )
        }
    }

    fun deleteLogEntry(log: HealthGoalLogEntity) {
        viewModelScope.launch {
            repository.deleteLogEntry(log)
        }
    }

    fun resetTodayProgress() {
        viewModelScope.launch {
            repository.resetDailyProgress(_selectedDate.value)
        }
    }

    private fun buildSuccessState(
        date: String,
        language: Language,
        goal: DailyHealthGoalEntity,
        logs: List<HealthGoalLogEntity>,
        recentHistory: List<DailyHealthGoalEntity>
    ): DailyHealthUiState.Success {
        val isMr = language == Language.MARATHI

        val stepsSummary = GoalMetricSummary(
            type = HealthGoalType.STEPS,
            title = HealthGoalType.STEPS.localizedTitle(language),
            currentDisplay = if (isMr) "${goal.stepsCount} पावले" else "${goal.stepsCount} steps",
            targetDisplay = if (isMr) "ध्येय: ${goal.stepGoal}" else "Goal: ${goal.stepGoal}",
            progressFraction = goal.stepsProgress,
            progressPercent = (goal.stepsProgress * 100).toInt()
        )

        val waterSummary = GoalMetricSummary(
            type = HealthGoalType.WATER,
            title = HealthGoalType.WATER.localizedTitle(language),
            currentDisplay = if (isMr) "${goal.waterIntakeLiters} लि. (${goal.waterIntakeMl} मिली)" else "${goal.waterIntakeLiters} L (${goal.waterIntakeMl} ml)",
            targetDisplay = if (isMr) "ध्येय: ${goal.waterGoalLiters} लि." else "Goal: ${goal.waterGoalLiters} L",
            progressFraction = goal.waterProgress,
            progressPercent = (goal.waterProgress * 100).toInt()
        )

        val caloriesSummary = GoalMetricSummary(
            type = HealthGoalType.CALORIES,
            title = HealthGoalType.CALORIES.localizedTitle(language),
            currentDisplay = if (isMr) "${goal.caloriesConsumed} कॅलरी" else "${goal.caloriesConsumed} kcal",
            targetDisplay = if (isMr) "ध्येय: ${goal.calorieGoal} कॅलरी" else "Goal: ${goal.calorieGoal} kcal",
            progressFraction = goal.caloriesProgress,
            progressPercent = (goal.caloriesProgress * 100).toInt()
        )

        val overallPct = (goal.overallGoalProgress * 100).toInt()
        val headline = when {
            overallPct >= 80 -> if (isMr) "अप्रतिम! तुम्ही आजचे आरोग्य ध्येय पूर्ण केले आहे." else "Excellent! You're crushing today's health goals."
            overallPct >= 50 -> if (isMr) "छान प्रगती! थोडे आणखी प्रयत्न करा." else "Great progress! Keep moving toward your daily targets."
            else -> if (isMr) "तुमच्या दिवसाची आरोग्यदायी सुरुवात करा!" else "Let's kick off today's health goals!"
        }

        return DailyHealthUiState.Success(
            selectedDate = date,
            language = language,
            goal = goal,
            logs = logs,
            recentHistory = recentHistory,
            stepsSummary = stepsSummary,
            waterSummary = waterSummary,
            caloriesSummary = caloriesSummary,
            overallProgressPercent = overallPct,
            statusHeadline = headline
        )
    }

    class Factory(
        private val repository: DailyHealthGoalRepository,
        private val initialDate: String = DailyHealthGoalRepository.todayIsoDate(),
        private val initialLanguage: Language = Language.MARATHI
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DailyHealthGoalViewModel::class.java)) {
                return DailyHealthGoalViewModel(
                    repository = repository,
                    initialDate = initialDate,
                    initialLanguage = initialLanguage
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
