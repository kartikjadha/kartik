package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.ExerciseCategory
import com.example.data.model.ExerciseItem
import com.example.data.model.Language
import com.example.data.model.WorkoutLevel
import com.example.data.repository.FitMarathiRepository
import com.example.ui.components.ExerciseCompletionCelebrationDialog
import com.example.ui.components.FitCard
import com.example.ui.components.FitLottieAnimation
import com.example.ui.theme.*

@Composable
fun WorkoutScreen(
    repository: FitMarathiRepository,
    onNavigateToYoga: () -> Unit = {},
    onNavigateToRoutineLibrary: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val profile by repository.userProfile.collectAsState()
    val exercises by repository.exercises.collectAsState()
    val isMr = profile.language == Language.MARATHI

    var selectedCategory by remember { mutableStateOf(ExerciseCategory.ALL) }
    var selectedLevel by remember { mutableStateOf<WorkoutLevel?>(null) }
    var selectedDurationMinutes by remember { mutableIntStateOf(30) }
    var searchQuery by remember { mutableStateOf("") }
    var showActiveWorkoutDialog by remember { mutableStateOf(false) }
    var celebrationSummary by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var expandedExerciseId by remember { mutableStateOf<String?>(null) }

    val filteredExercises = remember(exercises, selectedCategory, selectedLevel, searchQuery) {
        exercises.filter { ex ->
            val matchesCategory = selectedCategory == ExerciseCategory.ALL || ex.category == selectedCategory
            val matchesLevel = selectedLevel == null || ex.level == selectedLevel
            val matchesSearch = searchQuery.isBlank() ||
                    ex.nameEn.contains(searchQuery, ignoreCase = true) ||
                    ex.nameMr.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesLevel && matchesSearch
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(FitBackground)
            .padding(horizontal = 16.dp)
            .testTag("workout_screen_list"),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = if (isMr) "वर्कआउट प्लॅन (६० कसरती)" else "Workout Plan (60 Exercises)",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = FitTextPrimary
                )
                Text(
                    text = if (isMr) "तुमच्या क्षमतेनुसार डिझाइन केलेले वजन घटवणारे व स्नायू बळकट करणारे व्यायाम" else "Scientific progression library tailored to your fitness level",
                    fontSize = 13.sp,
                    color = FitTextSecondary
                )
            }
        }

        // Plan Summary Banner (Personalized by Weight & Duration)
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = FitDeepGreen
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isMr) "तुमचा वैयक्तिक प्लॅन (${profile.weightKg} kg)" else "Your Personalized Plan (${profile.weightKg} kg)",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            FitLottieAnimation(
                                rawResId = R.raw.lottie_fitness_spark,
                                contentDescriptionText = "Animated workout spark",
                                size = 34.dp,
                                modifier = Modifier.testTag("workout_hero_lottie")
                            )
                            Surface(
                                color = FitGold,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "६० Exercises",
                                    color = FitDeepGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        PlanMetric(
                            icon = Icons.Default.Timer,
                            label = "$selectedDurationMinutes Min",
                            sub = if (isMr) "वेळ" else "Duration"
                        )
                        PlanMetric(
                            icon = Icons.Default.CalendarMonth,
                            label = if (isMr) "६ दिवस / आठवडा" else "6 Days / Week",
                            sub = if (isMr) "वारंवारता" else "Frequency"
                        )
                        PlanMetric(
                            icon = Icons.Default.FitnessCenter,
                            label = if (isMr) profile.goal.marathiLabel.take(8) else profile.goal.englishLabel,
                            sub = if (isMr) "फोकस" else "Focus"
                        )
                    }
                }
            }
        }

        // Duration Options (10 Min, 30 Min, 60 Min)
        item {
            Column {
                Text(
                    text = if (isMr) "व्यायामाचा वेळ निवडा (Select Duration):" else "Select Available Workout Time:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = FitTextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DurationOptionCard(
                        title = "10 Min",
                        label = if (isMr) "जलद ऊर्जा" else "Quick Blast",
                        isSelected = selectedDurationMinutes == 10,
                        modifier = Modifier.weight(1f),
                        onClick = { selectedDurationMinutes = 10 }
                    )
                    DurationOptionCard(
                        title = "30 Min",
                        label = if (isMr) "फॅट लॉस" else "Fat Loss Core",
                        isSelected = selectedDurationMinutes == 30,
                        modifier = Modifier.weight(1f),
                        onClick = { selectedDurationMinutes = 30 }
                    )
                    DurationOptionCard(
                        title = "60 Min",
                        label = if (isMr) "स्नायू ताकद" else "Muscle Power",
                        isSelected = selectedDurationMinutes == 60,
                        modifier = Modifier.weight(1f),
                        onClick = { selectedDurationMinutes = 60 }
                    )
                }
            }
        }

        // Today's Workout Hero Card
        item {
            FitCard(backgroundColor = Color.White) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isMr) "आजचा वर्कआउट ($selectedDurationMinutes मिनिटे)" else "Today's Workout ($selectedDurationMinutes Min)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = FitDeepGreen
                        )
                        Text(
                            text = when (selectedDurationMinutes) {
                                10 -> if (isMr) "१० मिनिटे • क्विक एनर्जी सर्किट (~१२० kcal)" else "10 Min • Quick Energy Blast (~120 kcal)"
                                60 -> if (isMr) "६० मिनिटे • फुल बॉडी स्ट्रेंथ व कार्डिओ (~५०० kcal)" else "60 Min • Full Body Strength & Conditioning (~500 kcal)"
                                else -> if (isMr) "३० मिनिटे • फुल बॉडी फॅट लॉस सर्किट (~३०० kcal)" else "30 Min • Full Body Fat Loss Circuit (~300 kcal)"
                            },
                            fontSize = 12.sp,
                            color = FitTeal,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isMr) "वॉर्म अप (५ मि) + मुख्य कसरत + कूल डाऊन (५ मि)" else "Warm Up (5m) + Main Circuit + Cool Down (5m)",
                            fontSize = 11.sp,
                            color = FitTextSecondary
                        )
                    }

                    Button(
                        onClick = { showActiveWorkoutDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = FitPrimaryGreen),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("start_workout_btn")
                    ) {
                        Text(
                            text = if (isMr) "सुरू करा" else "Start",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Quick Link to Yoga & Meditation Banner
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onNavigateToYoga() }
                    .border(1.dp, FitTeal.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                color = FitLightTeal
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.SelfImprovement, contentDescription = null, tint = FitTeal)
                        Column {
                            Text(
                                text = if (isMr) "योगा व ध्यान सत्रे (Yoga & Meditation)" else "Yoga, Meditation & Pranayama",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = FitDeepGreen
                            )
                            Text(
                                text = if (isMr) "सूर्यनमस्कार, ताणमुक्ती व शांत झोप" else "Surya Namaskar, stress release & better sleep",
                                fontSize = 11.sp,
                                color = FitTeal
                            )
                        }
                    }
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = FitTeal, modifier = Modifier.size(18.dp))
                }
            }
        }

        // Direct Link to 66+ Categorized Yoga & Fitness Routine Library
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onNavigateToRoutineLibrary() }
                    .border(1.dp, FitPrimaryGreen.copy(alpha = 0.45f), RoundedCornerShape(12.dp))
                    .testTag("open_routine_library_workout_btn"),
                color = Color(0xFFE8F5E9)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, tint = FitPrimaryGreen)
                        Column {
                            Text(
                                text = if (isMr) "६६+ योगा व फिटनेस दिनचर्या लायब्ररी" else "66+ Yoga & Fitness Routine Library",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = FitDeepGreen
                            )
                            Text(
                                text = if (isMr) "७ श्रेणी • मराठी व इंग्रजी सविस्तर वर्णनांसह" else "7 categories with Marathi & English descriptions",
                                fontSize = 11.sp,
                                color = FitPrimaryGreen
                            )
                        }
                    }
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = FitPrimaryGreen, modifier = Modifier.size(18.dp))
                }
            }
        }

        // Category Filter Chips Row
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(ExerciseCategory.values()) { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = {
                            Text(
                                text = if (isMr) cat.mrLabel else cat.enLabel,
                                fontSize = 12.sp
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = FitPrimaryGreen,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        // Level Filter Chips Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedLevel == null,
                    onClick = { selectedLevel = null },
                    label = { Text(if (isMr) "सर्व स्तर" else "All Levels", fontSize = 11.sp) }
                )
                WorkoutLevel.values().forEach { lvl ->
                    FilterChip(
                        selected = selectedLevel == lvl,
                        onClick = { selectedLevel = lvl },
                        label = { Text(if (isMr) lvl.mrLabel else lvl.enLabel, fontSize = 11.sp) }
                    )
                }
            }
        }

        // Results count
        item {
            Text(
                text = if (isMr) "उपलब्ध कसरती: ${filteredExercises.size} / ६०" else "Exercises available: ${filteredExercises.size} / 60",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = FitTextSecondary
            )
        }

        // Exercise List
        items(filteredExercises, key = { it.id }) { ex ->
            val isExpanded = expandedExerciseId == ex.id
            ExerciseCard(
                exercise = ex,
                isMr = isMr,
                isExpanded = isExpanded,
                onToggleExpand = {
                    expandedExerciseId = if (isExpanded) null else ex.id
                },
                onToggleCompleted = { repository.toggleExerciseCompleted(ex.id) }
            )
        }
    }

    // Active Guided Workout Session Dialog with Rest & Timer
    if (showActiveWorkoutDialog) {
        ActiveWorkoutSessionDialog(
            durationMinutes = selectedDurationMinutes,
            isMr = isMr,
            onDismiss = { showActiveWorkoutDialog = false },
            onFinish = { burnedCals, exercisesDone ->
                repository.recordCompletedWorkout(selectedDurationMinutes, burnedCals, exercisesDone)
                repository.addSteps(selectedDurationMinutes * 90)
                showActiveWorkoutDialog = false
                celebrationSummary = burnedCals to exercisesDone
            }
        )
    }

    celebrationSummary?.let { (burnedCals, exercisesDone) ->
        ExerciseCompletionCelebrationDialog(
            titleMr = "वर्कआउट यशस्वीरित्या पूर्ण! 🎉",
            titleEn = "Workout Completed! 🎉",
            subtitleMr = "$exercisesDone कसरती पूर्ण केल्या व अंदाजे ~$burnedCals kcal ऊर्जा जाळली. अशीच सातत्यपूर्ण प्रगती ठेवा!",
            subtitleEn = "You finished $exercisesDone exercises and burned an estimated ~$burnedCals kcal. Great consistency!",
            isMarathi = isMr,
            onDismiss = { celebrationSummary = null }
        )
    }
}

@Composable
private fun PlanMetric(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, sub: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, tint = FitGold, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Text(text = sub, color = Color(0xFFA7F3D0), fontSize = 10.sp)
    }
}

@Composable
private fun DurationOptionCard(
    title: String,
    label: String,
    isSelected: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .border(
                1.5.dp,
                if (isSelected) FitPrimaryGreen else FitBorder,
                RoundedCornerShape(12.dp)
            ),
        color = if (isSelected) FitLightTeal else Color.White,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = if (isSelected) FitDeepGreen else FitTextPrimary
            )
            Text(
                text = label,
                fontSize = 11.sp,
                color = if (isSelected) FitPrimaryGreen else FitTextSecondary
            )
        }
    }
}

@Composable
private fun ExerciseCard(
    exercise: ExerciseItem,
    isMr: Boolean,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onToggleCompleted: () -> Unit
) {
    FitCard(backgroundColor = Color.White) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggleExpand() },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (exercise.isCompleted) {
                    FitLottieAnimation(
                        rawResId = R.raw.lottie_exercise_complete,
                        contentDescriptionText = if (isMr) "कसरत पूर्ण झाली" else "Exercise completed",
                        size = 44.dp,
                        modifier = Modifier.testTag("exercise_completed_lottie_${exercise.id}")
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(FitLightTeal),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FitnessCenter,
                            contentDescription = null,
                            tint = FitPrimaryGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Column {
                    Text(
                        text = if (isMr) exercise.nameMr else exercise.nameEn,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = FitTextPrimary
                    )
                    Text(
                        text = if (isMr) exercise.setsAndRepsMr else exercise.setsAndRepsEn,
                        fontSize = 12.sp,
                        color = FitTeal,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            IconButton(
                onClick = onToggleCompleted,
                modifier = Modifier.testTag("toggle_exercise_${exercise.id}")
            ) {
                Icon(
                    imageVector = if (exercise.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = "Mark done",
                    tint = if (exercise.isCompleted) FitEmerald else FitBorder
                )
            }
        }

        AnimatedVisibility(visible = isExpanded) {
            Column(modifier = Modifier.padding(top = 10.dp)) {
                HorizontalDivider(color = FitBorder)
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (isMr) "कसे करावे (Instructions):" else "Instructions:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = FitTextPrimary
                )
                Text(
                    text = if (isMr) exercise.instructionsMr else exercise.instructionsEn,
                    fontSize = 12.sp,
                    color = FitTextSecondary,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Surface(
                    color = FitSurfaceVariant,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.TipsAndUpdates,
                            contentDescription = null,
                            tint = FitSaffron,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = (if (isMr) "सोपा पर्याय: " else "Modification: ") + if (isMr) exercise.modificationMr else exercise.modificationEn,
                            fontSize = 11.sp,
                            color = FitTextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ActiveWorkoutSessionDialog(
    durationMinutes: Int,
    isMr: Boolean,
    onDismiss: () -> Unit,
    onFinish: (burnedCalories: Int, exercisesDone: Int) -> Unit
) {
    var secondsLeft by remember { mutableIntStateOf(45) }
    var isRestPeriod by remember { mutableStateOf(false) }
    var currentExerciseIndex by remember { mutableIntStateOf(0) }

    val workoutList = remember(durationMinutes) {
        when (durationMinutes) {
            10 -> listOf(
                "जम्पिंग जॅक्स (Jumping Jacks)" to "हृदयाचे ठोके वाढवण्यासाठी वॉर्म-अप",
                "स्क्वॉट्स (Bodyweight Squats)" to "मांड्या आणि पायांची ताकद",
                "प्लँक (Standard Plank)" to "पोटाची चरबी कमी करण्यासाठी"
            )
            60 -> listOf(
                "हाय नीज (High Knees)" to "फुल बॉडी कार्डिओ वॉर्म अप",
                "पुश-अप्स (Push Ups)" to "छाती, खांदे व ट्रायसेप्स",
                "स्क्वॉट्स (Squats)" to "पायांची मोठी स्नायू ताकद",
                "लंजेस (Walking Lunges)" to "तोल व मांड्यांची ताकद",
                "प्लँक (Standard Plank)" to "कोर मजबुती",
                "बायसिकल क्रंचेस (Bicycle Crunches)" to "पोटावरील चरबी घटवणे",
                "माउंटन क्लायम्बर्स (Mountain Climbers)" to "कॅलरी बर्निंग",
                "वॉल सिट (Wall Sit)" to "स्नायू स्टॅमिना"
            )
            else -> listOf(
                "पुश-अप्स (Push Ups)" to "छाती व हातांची ताकद",
                "स्क्वॉट्स (Bodyweight Squats)" to "मांड्या व पायांची मजबुती",
                "प्लँक (Standard Plank)" to "पोटाची चरबी कमी करण्यासाठी",
                "जम्पिंग जॅक्स (Jumping Jacks)" to "हृदयाचे आरोग्य व फॅट बर्निंग",
                "माउंटन क्लायम्बर्स (Mountain Climbers)" to "जलद गतीने कॅलरीज जाळणे"
            )
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isRestPeriod) (if (isMr) "विश्रांती (Rest) 💧" else "Rest Period 💧") else (if (isMr) "कसरत सुरू आहे 🔥" else "Active Workout 🔥"),
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = if (isRestPeriod) FitTeal else FitPrimaryGreen
                )
                Text(
                    text = "${currentExerciseIndex + 1}/${workoutList.size}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = FitTeal
                )
            }
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = workoutList[currentExerciseIndex].first,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = FitDeepGreen,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = workoutList[currentExerciseIndex].second,
                    fontSize = 12.sp,
                    color = FitTeal,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(if (isRestPeriod) FitLightTeal else Color(0xFFE8F5E9)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${secondsLeft}s",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isRestPeriod) FitTeal else FitPrimaryGreen
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = if (isRestPeriod) (if (isMr) "थोडं पाणी प्या व दीर्घ श्वास घ्या..." else "Breathe deeply, hydrate with a sip of water...") else (if (isMr) "३ सेट्स पूर्ण करा • पाठ सरळ ठेवा" else "Maintain proper posture & breathe rhythmically"),
                    fontSize = 12.sp,
                    color = FitTextSecondary,
                    textAlign = TextAlign.Center
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (!isRestPeriod) {
                        isRestPeriod = true
                        secondsLeft = 15
                    } else {
                        isRestPeriod = false
                        secondsLeft = 45
                        if (currentExerciseIndex < workoutList.size - 1) {
                            currentExerciseIndex++
                        } else {
                            val burned = (durationMinutes * 9.5f).toInt()
                            onFinish(burned, workoutList.size)
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = FitPrimaryGreen)
            ) {
                Text(
                    if (!isRestPeriod)
                        (if (isMr) "सेट्स पूर्ण झाले (Rest)" else "Done Set (Rest)")
                    else if (currentExerciseIndex < workoutList.size - 1)
                        (if (isMr) "पुढील कसरत" else "Next Exercise")
                    else
                        (if (isMr) "वर्कआउट पूर्ण करा! 🎉" else "Finish Workout! 🎉")
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isMr) "थांबवा (Exit)" else "Exit")
            }
        }
    )
}
