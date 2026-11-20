package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.DailyLog
import com.example.data.model.Language
import com.example.data.model.UserProfile
import com.example.data.repository.FitMarathiRepository
import com.example.ui.components.DailyGoalsRadialProgressCard
import com.example.ui.components.FitCard
import com.example.ui.components.FitLottieAnimation
import com.example.ui.components.FitMarathiLogo
import com.example.ui.components.LanguageSwitchButton
import com.example.ui.components.MotivationalQuoteCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.DailyHealthGoalViewModel
import com.example.ui.viewmodel.DailyHealthUiState

@Composable
fun HomeScreen(
    repository: FitMarathiRepository,
    dailyHealthViewModel: DailyHealthGoalViewModel? = null,
    onNavigateToDailyGoals: () -> Unit = {},
    onNavigateToRoutineLibrary: () -> Unit = {},
    onNavigateToWorkouts: () -> Unit,
    onNavigateToDiet: () -> Unit,
    onNavigateToAiCoach: () -> Unit,
    onNavigateToYoga: () -> Unit,
    onNavigateToWater: () -> Unit,
    onNavigateToProgress: () -> Unit,
    onNavigateToHealthReport: () -> Unit,
    onNavigateToPersonalCare: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    modifier: Modifier = Modifier
) {
    val profile by repository.userProfile.collectAsStateWithLifecycle()
    val dailyLog by repository.dailyLog.collectAsStateWithLifecycle()
    val roomHealthState = dailyHealthViewModel?.uiState?.collectAsStateWithLifecycle()?.value
    val roomGoal = (roomHealthState as? DailyHealthUiState.Success)?.goal
    val isMr = profile.language == Language.MARATHI

    val displayedSteps = roomGoal?.stepsCount ?: dailyLog.steps
    val displayedStepGoal = roomGoal?.stepGoal ?: dailyLog.stepGoal
    val displayedCalories = roomGoal?.caloriesConsumed ?: dailyLog.caloriesConsumed
    val displayedCalorieGoal = roomGoal?.calorieGoal ?: dailyLog.calorieGoal
    val displayedWaterLiters = roomGoal?.waterIntakeLiters ?: dailyLog.waterDrankLiters
    val displayedWaterGoalLiters = roomGoal?.waterGoalLiters ?: dailyLog.waterGoalLiters
    val displayedWaterGoalMl = roomGoal?.waterGoalMl ?: (dailyLog.waterGoalLiters * 1000).toInt()

    var showStepLogDialog by remember { mutableStateOf(false) }
    var showEditGoalsDialog by remember { mutableStateOf(false) }
    var showWorkoutTargetDialog by remember { mutableStateOf(false) }
    var targetWorkoutMinutes by remember { mutableIntStateOf(60) }
    var completedWorkoutMinutes by remember { mutableIntStateOf(30) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(FitBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp)
    ) {
        // Top Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FitMarathiLogo(size = 40.dp, showText = true)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LanguageSwitchButton(
                        currentLanguage = profile.language,
                        onToggle = { newLang ->
                            repository.updateLanguage(newLang)
                            dailyHealthViewModel?.setLanguage(newLang)
                        }
                    )
                    IconButton(
                        onClick = onNavigateToNotifications,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(FitLightTeal)
                            .testTag("home_notifications_btn")
                    ) {
                        BadgedBox(
                            badge = {
                                Badge(containerColor = FitSaffron) {
                                    Text("3", fontSize = 9.sp)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = FitPrimaryGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        // Greeting and Subtitle with Lottie Energy Badge (Storyboard panel 9)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isMr) "नमस्कार, ${profile.name}! 👋" else "Good Morning, ${profile.name}! 👋",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = FitTextPrimary
                    )
                    Text(
                        text = if (isMr) "तुमचा आजचा फिटनेस रिपोर्ट" else "Stay Fit • Stay Healthy • Be Happy",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = FitTeal
                    )
                }
                FitLottieAnimation(
                    rawResId = R.raw.lottie_fitness_spark,
                    contentDescriptionText = "Animated fitness motivation spark",
                    size = 48.dp,
                    modifier = Modifier.testTag("home_lottie_spark")
                )
            }
        }

        // Main Dashboard Multi-Ring Radial Chart & Progress Bars (Backed by Room DailyHealthGoalViewModel)
        item {
            DailyGoalsRadialProgressCard(
                steps = displayedSteps,
                stepGoal = displayedStepGoal,
                waterLiters = displayedWaterLiters,
                waterGoalLiters = displayedWaterGoalLiters,
                calories = displayedCalories,
                calorieGoal = displayedCalorieGoal,
                isMarathi = isMr,
                onStepsClick = { showStepLogDialog = true },
                onWaterClick = onNavigateToWater,
                onCaloriesClick = onNavigateToDiet,
                onChartClick = { showEditGoalsDialog = true },
                onViewAllGoalsClick = onNavigateToDailyGoals
            )
        }

        // Quick Action Grid (Workout, Diet, Yoga, AI Coach)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickActionItem(
                    title = if (isMr) "व्यायाम" else "Workout",
                    icon = Icons.Default.FitnessCenter,
                    containerColor = Color(0xFFE8F5E9),
                    iconColor = FitPrimaryGreen,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToWorkouts,
                    tag = "quick_workout"
                )
                QuickActionItem(
                    title = if (isMr) "आहार" else "Diet Plan",
                    icon = Icons.Default.Restaurant,
                    containerColor = Color(0xFFFFF3E0),
                    iconColor = FitSaffron,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToDiet,
                    tag = "quick_diet"
                )
                QuickActionItem(
                    title = if (isMr) "योगा" else "Yoga",
                    icon = Icons.Default.SelfImprovement,
                    containerColor = Color(0xFFE0F2F1),
                    iconColor = FitTeal,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToYoga,
                    tag = "quick_yoga"
                )
                QuickActionItem(
                    title = if (isMr) "एआय कोच" else "AI Coach",
                    icon = Icons.Default.Psychology,
                    containerColor = Color(0xFFEDE7F6),
                    iconColor = Color(0xFF673AB7),
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToAiCoach,
                    tag = "quick_coach"
                )
            }
        }

        // 66+ Yoga & Fitness Routines Library Card
        item {
            FitCard(
                backgroundColor = Color.White,
                modifier = Modifier
                    .clickable { onNavigateToRoutineLibrary() }
                    .testTag("open_routine_library_home_card")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    Brush.linearGradient(listOf(FitPrimaryGreen, FitTeal))
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                contentDescription = if (isMr) "योगा व फिटनेस लायब्ररी" else "Yoga & Fitness Library",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = if (isMr) "६६+ योगा व फिटनेस दिनचर्या" else "66+ Yoga & Fitness Routines",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FitDeepGreen
                                )
                                Surface(
                                    color = FitLightTeal,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "मराठी + EN",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = FitPrimaryGreen,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isMr) "योगासने, प्राणायाम, HIIT, कार्डिओ व कोर • ७ श्रेणी" else "Yoga, Pranayama, Strength, HIIT, Cardio & Core • 7 Categories",
                                fontSize = 11.sp,
                                color = FitTextSecondary
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = if (isMr) "लायब्ररी उघडा" else "Open Library",
                        tint = FitPrimaryGreen
                    )
                }
            }
        }

        // Daily Motivational Quote Card with Lottie Sunrise Glow
        item {
            MotivationalQuoteCard(isMarathi = isMr)
        }

        // Today's Target Card (Storyboard Panel 9)
        item {
            val targetLabel = if (targetWorkoutMinutes >= 60) {
                "${targetWorkoutMinutes / 60} तास"
            } else {
                "$targetWorkoutMinutes मिनिटे"
            }
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onNavigateToWorkouts() },
                color = FitLightTeal,
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isMr) "आजचा टार्गेट" else "Today's Target",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = FitTeal
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            IconButton(
                                onClick = { showWorkoutTargetDialog = true },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Target",
                                    tint = FitPrimaryGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isMr) "$targetLabel व्यायाम" else "$targetWorkoutMinutes Min Workout",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = FitDeepGreen
                        )
                        Text(
                            text = if (isMr) "$completedWorkoutMinutes/$targetWorkoutMinutes मिनिटे (${dailyLog.completedExercisesCount}/6 कसरती पूर्ण)" else "$completedWorkoutMinutes/$targetWorkoutMinutes Minutes (${dailyLog.completedExercisesCount}/6 completed)",
                            fontSize = 12.sp,
                            color = FitTextSecondary
                        )
                    }

                    Button(
                        onClick = onNavigateToWorkouts,
                        colors = ButtonDefaults.buttonColors(containerColor = FitPrimaryGreen),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (isMr) "सुरू करा" else "Start Now",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // AI Personal Coach Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToAiCoach() }
                    .testTag("ai_coach_home_banner"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = FitDeepGreen)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(FitEmerald),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SmartToy,
                            contentDescription = "AI Coach",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "FitMarathi AI Coach",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = FitGold.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "Gemini",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FitGold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isMr) "आज काय खायला पाहिजे? विचारा..." else "What should I eat today? Ask your coach...",
                            fontSize = 12.sp,
                            color = Color(0xFFD1FAE5)
                        )
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Open Coach",
                        tint = Color.White
                    )
                }
            }
        }

        // Water Quick Hydration Bar
        item {
            FitCard(backgroundColor = Color.White) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FitLottieAnimation(
                            rawResId = R.raw.lottie_water_ripple,
                            contentDescriptionText = "Animated hydration water droplet",
                            size = 46.dp,
                            modifier = Modifier.testTag("home_water_lottie")
                        )
                        Column {
                            Text(
                                text = if (isMr) "पाणी पिण्याची नोंद (Water)" else "Water Intake",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "$displayedWaterLiters L / $displayedWaterGoalLiters L",
                                fontSize = 12.sp,
                                color = FitTeal,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = {
                                if (dailyHealthViewModel != null) {
                                    dailyHealthViewModel.addWaterMl(250)
                                } else {
                                    repository.addWater(0.25f)
                                }
                            },
                            shape = RoundedCornerShape(16.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("quick_add_water_btn")
                        ) {
                            Text("+250 ml", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        IconButton(
                            onClick = onNavigateToWater,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Water Tracker",
                                tint = FitTextSecondary
                            )
                        }
                    }
                }
            }
        }

        // Health Report & Risk Analysis Teaser
        item {
            FitCard(
                backgroundColor = Color.White,
                modifier = Modifier.clickable { onNavigateToHealthReport() }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.HealthAndSafety,
                                contentDescription = null,
                                tint = FitPrimaryGreen,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isMr) "आरोग्य अहवाल (Health Report)" else "Health Report",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = FitTextPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isMr) "हेल्थ स्कोअर: 78/100 (उत्कृष्ट) • डायबिटीस जोखीम: 28% (मध्यम)" else "Health Score: 78/100 (Good) • Diabetes Risk: 28% (Moderate)",
                            fontSize = 12.sp,
                            color = FitTextSecondary
                        )
                    }

                    Surface(
                        color = FitEmerald.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "78/100",
                            fontWeight = FontWeight.Bold,
                            color = FitEmerald,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Grooming & Style Teaser
        item {
            FitCard(
                backgroundColor = Color.White,
                modifier = Modifier.clickable { onNavigateToPersonalCare() }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(FitLightTeal),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Face,
                                contentDescription = "Grooming",
                                tint = FitPrimaryGreen
                            )
                        }
                        Column {
                            Text(
                                text = if (isMr) "ग्रूमिंग, केस व स्टाइल" else "Grooming & Style",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = if (isMr) "अंडाकृती चेहरा • क्लासिक फेड • पारंपारिक कुर्ता" else "Oval Face • Classic Fade • Smart Kurtas",
                                fontSize = 12.sp,
                                color = FitTextSecondary
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Style suggestions",
                        tint = FitTextSecondary
                    )
                }
            }
        }
    }

    if (showStepLogDialog) {
        var customStepsText by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showStepLogDialog = false },
            title = {
                Text(
                    text = if (isMr) "आजची पावले नोंदवा (Log Steps)" else "Daily Steps Activity",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = if (isMr) "सध्याची नोंद: $displayedSteps / $displayedStepGoal पावले" else "Current: $displayedSteps / $displayedStepGoal steps",
                        fontSize = 13.sp,
                        color = FitTextSecondary
                    )

                    Text(
                        text = if (isMr) "झटपट जोडा:" else "Quick Add:",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                if (dailyHealthViewModel != null) {
                                    dailyHealthViewModel.addSteps(500)
                                } else {
                                    repository.addSteps(500)
                                }
                                showStepLogDialog = false
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("+500", fontSize = 11.sp)
                        }
                        OutlinedButton(
                            onClick = {
                                if (dailyHealthViewModel != null) {
                                    dailyHealthViewModel.addSteps(1000)
                                } else {
                                    repository.addSteps(1000)
                                }
                                showStepLogDialog = false
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("+1,000", fontSize = 11.sp)
                        }
                        OutlinedButton(
                            onClick = {
                                if (dailyHealthViewModel != null) {
                                    dailyHealthViewModel.addSteps(2500)
                                } else {
                                    repository.addSteps(2500)
                                }
                                showStepLogDialog = false
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("+2,500", fontSize = 11.sp)
                        }
                    }

                    OutlinedButton(
                        onClick = {
                            showStepLogDialog = false
                            onNavigateToProgress()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Timeline, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isMr) "सविस्तर प्रगती आलेख पहा" else "View Detailed Step Analytics", fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showStepLogDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = FitPrimaryGreen),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(if (isMr) "पूर्ण झाले" else "Done")
                }
            }
        )
    }

    if (showEditGoalsDialog) {
        var stepGoalInput by remember { mutableStateOf(displayedStepGoal.toString()) }
        var waterGoalMlInput by remember { mutableStateOf(displayedWaterGoalMl.toString()) }
        var calorieGoalInput by remember { mutableStateOf(displayedCalorieGoal.toString()) }

        AlertDialog(
            onDismissRequest = { showEditGoalsDialog = false },
            title = {
                Text(
                    text = if (isMr) "दैनिक आरोग्य ध्येय बदला" else "Edit Daily Health Goals",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = stepGoalInput,
                        onValueChange = { stepGoalInput = it.filter { ch -> ch.isDigit() } },
                        label = { Text(if (isMr) "पावले ध्येय (Steps Goal)" else "Steps Goal") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("edit_step_goal_input")
                    )
                    OutlinedTextField(
                        value = waterGoalMlInput,
                        onValueChange = { waterGoalMlInput = it.filter { ch -> ch.isDigit() } },
                        label = { Text(if (isMr) "पाणी ध्येय (मिली / ml)" else "Water Goal (ml)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("edit_water_goal_input")
                    )
                    OutlinedTextField(
                        value = calorieGoalInput,
                        onValueChange = { calorieGoalInput = it.filter { ch -> ch.isDigit() } },
                        label = { Text(if (isMr) "कॅलरीज ध्येय (kcal)" else "Calorie Goal (kcal)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("edit_calorie_goal_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newSteps = stepGoalInput.toIntOrNull() ?: displayedStepGoal
                        val newWaterMl = waterGoalMlInput.toIntOrNull() ?: displayedWaterGoalMl
                        val newCalories = calorieGoalInput.toIntOrNull() ?: displayedCalorieGoal
                        dailyHealthViewModel?.updateGoals(
                            stepGoal = newSteps,
                            waterGoalMl = newWaterMl,
                            calorieGoal = newCalories
                        )
                        showEditGoalsDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FitPrimaryGreen),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("save_daily_goals_btn")
                ) {
                    Text(if (isMr) "जतन करा" else "Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditGoalsDialog = false }) {
                    Text(if (isMr) "रद्द करा" else "Cancel")
                }
            }
        )
    }

    if (showWorkoutTargetDialog) {
        AlertDialog(
            onDismissRequest = { showWorkoutTargetDialog = false },
            title = {
                Text(
                    text = if (isMr) "आजचा टार्गेट बदला (Workout Target)" else "Adjust Today's Target",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(
                        text = if (isMr) "दररोजचा व्यायाम वेळ निवडा:" else "Select target duration:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(30, 45, 60, 90).forEach { mins ->
                            val isSel = targetWorkoutMinutes == mins
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { targetWorkoutMinutes = mins }
                                    .border(
                                        1.dp,
                                        if (isSel) FitPrimaryGreen else FitBorder,
                                        RoundedCornerShape(8.dp)
                                    ),
                                color = if (isSel) FitLightTeal else Color.White,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = if (mins >= 60) "${mins / 60}h" else "${mins}m",
                                    fontSize = 12.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSel) FitPrimaryGreen else FitTextPrimary,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (isMr) "व्यायाम मिनिटे नोंदवा (+Log Minutes):" else "Log completed minutes:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { completedWorkoutMinutes = (completedWorkoutMinutes + 15).coerceAtMost(180) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("+15m", fontSize = 11.sp)
                        }
                        OutlinedButton(
                            onClick = { completedWorkoutMinutes = (completedWorkoutMinutes + 30).coerceAtMost(180) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("+30m", fontSize = 11.sp)
                        }
                        OutlinedButton(
                            onClick = { completedWorkoutMinutes = targetWorkoutMinutes },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(if (isMr) "पूर्ण" else "Full", fontSize = 11.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showWorkoutTargetDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = FitPrimaryGreen),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(if (isMr) "जतन करा (Save)" else "Save")
                }
            }
        )
    }
}

@Composable
private fun QuickActionItem(
    title: String,
    icon: ImageVector,
    containerColor: Color,
    iconColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    tag: String
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 4.dp)
            .testTag(tag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(containerColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconColor,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = FitTextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
