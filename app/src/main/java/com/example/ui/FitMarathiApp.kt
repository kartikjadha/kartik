package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Language
import com.example.data.repository.FitMarathiRepository
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.DailyHealthGoalViewModel

enum class AppScreen {
    ONBOARDING,
    HOME,
    WORKOUTS,
    DIET,
    AI_COACH,
    PROFILE,
    YOGA,
    WATER,
    PROGRESS,
    HEALTH_REPORT,
    PERSONAL_CARE,
    NOTIFICATIONS,
    DAILY_GOALS,
    ROUTINE_LIBRARY
}

data class NavigationTab(
    val screen: AppScreen,
    val titleMr: String,
    val titleEn: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val tag: String
)

@Composable
fun FitMarathiApp(
    repository: FitMarathiRepository,
    dailyHealthViewModel: DailyHealthGoalViewModel? = null,
    modifier: Modifier = Modifier
) {
    val profile by repository.userProfile.collectAsState()
    val isMr = profile.language == Language.MARATHI

    var currentScreen by remember {
        mutableStateOf(if (profile.isAuthenticated) AppScreen.HOME else AppScreen.ONBOARDING)
    }

    val tabs = remember {
        listOf(
            NavigationTab(AppScreen.HOME, "मुख्य (Home)", "Home", Icons.Filled.Home, Icons.Outlined.Home, "tab_home"),
            NavigationTab(AppScreen.WORKOUTS, "व्यायाम", "Workouts", Icons.Filled.FitnessCenter, Icons.Outlined.FitnessCenter, "tab_workouts"),
            NavigationTab(AppScreen.DIET, "आहार", "Diet", Icons.Filled.Restaurant, Icons.Outlined.Restaurant, "tab_diet"),
            NavigationTab(AppScreen.AI_COACH, "एआय कोच", "AI Coach", Icons.Filled.SmartToy, Icons.Outlined.SmartToy, "tab_ai"),
            NavigationTab(AppScreen.PROFILE, "प्रोफाइल", "Profile", Icons.Filled.Person, Icons.Outlined.Person, "tab_profile")
        )
    }

    // System Back Gesture handling
    BackHandler(enabled = currentScreen != AppScreen.HOME && currentScreen != AppScreen.ONBOARDING) {
        currentScreen = AppScreen.HOME
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            if (currentScreen != AppScreen.ONBOARDING) {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 8.dp,
                    modifier = Modifier.testTag("fitmarathi_bottom_nav")
                ) {
                    val activeTabScreen = when (currentScreen) {
                        AppScreen.HOME, AppScreen.WATER, AppScreen.PROGRESS, AppScreen.HEALTH_REPORT, AppScreen.NOTIFICATIONS, AppScreen.DAILY_GOALS -> AppScreen.HOME
                        AppScreen.WORKOUTS, AppScreen.YOGA, AppScreen.ROUTINE_LIBRARY -> AppScreen.WORKOUTS
                        AppScreen.DIET -> AppScreen.DIET
                        AppScreen.AI_COACH -> AppScreen.AI_COACH
                        AppScreen.PROFILE, AppScreen.PERSONAL_CARE -> AppScreen.PROFILE
                        else -> AppScreen.HOME
                    }

                    tabs.forEach { tab ->
                        val isSelected = activeTabScreen == tab.screen
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { currentScreen = tab.screen },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                    contentDescription = if (isMr) tab.titleMr else tab.titleEn
                                )
                            },
                            label = {
                                Text(
                                    text = if (isMr) tab.titleMr else tab.titleEn,
                                    fontSize = 11.sp
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = FitPrimaryGreen,
                                selectedTextColor = FitPrimaryGreen,
                                indicatorColor = FitLightTeal,
                                unselectedIconColor = FitTextSecondary,
                                unselectedTextColor = FitTextSecondary
                            ),
                            modifier = Modifier.testTag(tab.tag)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                AppScreen.ONBOARDING -> {
                    OnboardingScreen(
                        repository = repository,
                        onOnboardingComplete = { currentScreen = AppScreen.HOME }
                    )
                }

                AppScreen.HOME -> {
                    HomeScreen(
                        repository = repository,
                        dailyHealthViewModel = dailyHealthViewModel,
                        onNavigateToDailyGoals = { currentScreen = AppScreen.DAILY_GOALS },
                        onNavigateToRoutineLibrary = { currentScreen = AppScreen.ROUTINE_LIBRARY },
                        onNavigateToWorkouts = { currentScreen = AppScreen.WORKOUTS },
                        onNavigateToDiet = { currentScreen = AppScreen.DIET },
                        onNavigateToAiCoach = { currentScreen = AppScreen.AI_COACH },
                        onNavigateToYoga = { currentScreen = AppScreen.YOGA },
                        onNavigateToWater = { currentScreen = AppScreen.WATER },
                        onNavigateToProgress = { currentScreen = AppScreen.PROGRESS },
                        onNavigateToHealthReport = { currentScreen = AppScreen.HEALTH_REPORT },
                        onNavigateToPersonalCare = { currentScreen = AppScreen.PERSONAL_CARE },
                        onNavigateToNotifications = { currentScreen = AppScreen.NOTIFICATIONS }
                    )
                }

                AppScreen.WORKOUTS -> {
                    WorkoutScreen(
                        repository = repository,
                        onNavigateToYoga = { currentScreen = AppScreen.YOGA },
                        onNavigateToRoutineLibrary = { currentScreen = AppScreen.ROUTINE_LIBRARY }
                    )
                }

                AppScreen.DIET -> {
                    DietScreen(repository = repository)
                }

                AppScreen.AI_COACH -> {
                    AiCoachScreen(repository = repository)
                }

                AppScreen.PROFILE -> {
                    ProfileSettingsScreen(
                        repository = repository,
                        onNavigateToOnboarding = { currentScreen = AppScreen.ONBOARDING }
                    )
                }

                AppScreen.YOGA -> {
                    YogaScreen(
                        repository = repository,
                        onNavigateBack = { currentScreen = AppScreen.HOME },
                        onNavigateToRoutineLibrary = { currentScreen = AppScreen.ROUTINE_LIBRARY }
                    )
                }

                AppScreen.WATER -> {
                    WaterScreen(
                        repository = repository,
                        onNavigateBack = { currentScreen = AppScreen.HOME }
                    )
                }

                AppScreen.PROGRESS -> {
                    ProgressScreen(
                        repository = repository,
                        onNavigateToHealthReport = { currentScreen = AppScreen.HEALTH_REPORT },
                        onNavigateBack = { currentScreen = AppScreen.HOME }
                    )
                }

                AppScreen.HEALTH_REPORT -> {
                    HealthReportScreen(
                        repository = repository,
                        onNavigateBack = { currentScreen = AppScreen.HOME }
                    )
                }

                AppScreen.PERSONAL_CARE -> {
                    PersonalCareScreen(
                        repository = repository,
                        onNavigateBack = { currentScreen = AppScreen.HOME }
                    )
                }

                AppScreen.NOTIFICATIONS -> {
                    NotificationsScreen(
                        repository = repository,
                        onNavigateBack = { currentScreen = AppScreen.HOME }
                    )
                }

                AppScreen.DAILY_GOALS -> {
                    val vm = dailyHealthViewModel
                    if (vm != null) {
                        DailyHealthGoalsScreen(
                            viewModel = vm,
                            onNavigateBack = { currentScreen = AppScreen.HOME },
                            onLanguageChanged = { repository.updateLanguage(it) }
                        )
                    } else {
                        currentScreen = AppScreen.HOME
                    }
                }

                AppScreen.ROUTINE_LIBRARY -> {
                    RoutineLibraryScreen(
                        repository = repository,
                        onNavigateBack = { currentScreen = AppScreen.HOME }
                    )
                }
            }
        }
    }
}
