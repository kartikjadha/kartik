package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Language
import com.example.data.model.YogaRoutine
import com.example.data.model.YogaType
import com.example.data.repository.FitMarathiRepository
import com.example.ui.components.ExerciseCompletionCelebrationDialog
import com.example.ui.components.FitCard
import com.example.ui.components.FitLottieAnimation
import com.example.ui.components.PillTabRow
import com.example.ui.theme.*

@Composable
fun YogaScreen(
    repository: FitMarathiRepository,
    onNavigateBack: () -> Unit = {},
    onNavigateToRoutineLibrary: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val profile by repository.userProfile.collectAsState()
    val routines by repository.yogaRoutines.collectAsState()
    val isMr = profile.language == Language.MARATHI

    var selectedTab by remember { mutableIntStateOf(0) }
    var activeGuidedRoutine by remember { mutableStateOf<YogaRoutine?>(null) }
    var completedYogaRoutine by remember { mutableStateOf<YogaRoutine?>(null) }

    val filteredRoutines = remember(routines, selectedTab) {
        when (selectedTab) {
            0 -> routines.filter { it.type == YogaType.YOGA }
            1 -> routines.filter { it.type == YogaType.MEDITATION }
            else -> routines.filter { it.type == YogaType.BREATHING }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(FitBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .testTag("yoga_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = FitTextPrimary
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = if (isMr) "योगा आणि ध्यान" else "Yoga & Meditation",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = FitTextPrimary
                    )
                    Text(
                        text = if (isMr) "प्राणायाम, मनःशांती व शरीराची लवचिकता" else "Pranayama, mindfulness & inner balance",
                        fontSize = 12.sp,
                        color = FitTextSecondary
                    )
                }
            }
        }

        // Hero Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(FitDeepGreen, Color(0xFF0F766E), FitTeal)
                        )
                    )
                    .padding(20.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isMr) "दररोज १५ मिनिटे ध्यान करा" else "Daily 15 Minutes Mindfulness",
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isMr) "मन शांत होते, रक्तदाब नियंत्रणात राहतो" else "Lowers stress hormones & restores natural focus",
                            color = Color(0xFFD1FAE5),
                            fontSize = 12.sp
                        )
                    }
                    FitLottieAnimation(
                        rawResId = R.raw.lottie_yoga_lotus,
                        contentDescriptionText = "Breathing yoga lotus animation",
                        size = 66.dp,
                        modifier = Modifier.testTag("yoga_lotus_lottie")
                    )
                }
            }
        }

        // Quick link to full 66+ Categorized Routine Library
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { onNavigateToRoutineLibrary() }
                    .border(1.dp, FitPrimaryGreen.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                    .testTag("open_routine_library_yoga_btn"),
                color = Color.White
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
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
                                text = if (isMr) "संपूर्ण ६६+ योगा व व्यायाम लायब्ररी पहा" else "Browse All 66+ Yoga & Fitness Routines",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = FitDeepGreen
                            )
                            Text(
                                text = if (isMr) "मराठी व इंग्रजी वर्णने • ७ श्रेणींमध्ये विभागलेले" else "Categorized across 7 themes in Marathi & English",
                                fontSize = 11.sp,
                                color = FitTextSecondary
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = FitPrimaryGreen
                    )
                }
            }
        }

        // Pill Tabs (Yoga, Meditation, Breathing)
        item {
            PillTabRow(
                tabs = listOf(
                    if (isMr) "योगासने" else "Yoga",
                    if (isMr) "ध्यान (Meditation)" else "Meditation",
                    if (isMr) "प्राणायाम (Breathing)" else "Breathing"
                ),
                selectedIndex = selectedTab,
                onTabSelected = { selectedTab = it },
                activeColor = FitPrimaryGreen
            )
        }

        // Routine List
        items(filteredRoutines, key = { it.id }) { routine ->
            FitCard(
                backgroundColor = Color.White,
                modifier = Modifier.clickable { activeGuidedRoutine = routine }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(FitLightTeal),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when (routine.type) {
                                    YogaType.YOGA -> Icons.Default.SelfImprovement
                                    YogaType.MEDITATION -> Icons.Default.Spa
                                    YogaType.BREATHING -> Icons.Default.Air
                                },
                                contentDescription = null,
                                tint = FitPrimaryGreen,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Column {
                            Text(
                                text = if (isMr) routine.titleMr else routine.titleEn,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = FitTextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isMr) routine.benefitMr else routine.benefitEn,
                                fontSize = 12.sp,
                                color = FitTextSecondary
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Open Routine",
                        tint = FitTextSecondary
                    )
                }
            }
        }
    }

    // Interactive Guided Yoga & Breathing Session Dialog
    activeGuidedRoutine?.let { routine ->
        GuidedYogaSessionDialog(
            routine = routine,
            isMr = isMr,
            onDismiss = { activeGuidedRoutine = null },
            onComplete = {
                repository.recordCompletedWorkout(
                    durationMinutes = routine.durationMinutes,
                    caloriesBurned = routine.durationMinutes * 4,
                    exercisesDone = 3
                )
                activeGuidedRoutine = null
                completedYogaRoutine = routine
            }
        )
    }

    completedYogaRoutine?.let { done ->
        ExerciseCompletionCelebrationDialog(
            titleMr = "योगा सत्र पूर्ण झाले! 🌿",
            titleEn = "Yoga Session Completed! 🌿",
            subtitleMr = "${done.titleMr} पूर्ण केल्याबद्दल अभिनंदन! तुमचे मन व शरीर अधिक शांत व ऊर्जावान झाले आहे.",
            subtitleEn = "Great job completing ${done.titleEn}! Your daily mindfulness and activity minutes have been updated.",
            isMarathi = isMr,
            onDismiss = { completedYogaRoutine = null }
        )
    }
}

@Composable
private fun GuidedYogaSessionDialog(
    routine: YogaRoutine,
    isMr: Boolean,
    onDismiss: () -> Unit,
    onComplete: () -> Unit
) {
    var stepIndex by remember { mutableIntStateOf(0) }
    var secondsRemaining by remember { mutableIntStateOf(60) }
    val steps = if (isMr) routine.stepsMr else routine.stepsEn

    // Breathing pulse animation for meditation / breathing
    val infiniteTransition = rememberInfiniteTransition(label = "breathAnim")
    val breathScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathScale"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isMr) routine.titleMr else routine.titleEn,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Surface(
                    color = FitLightTeal,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "${stepIndex + 1}/${steps.size}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = FitTeal,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Interactive Breathing / Yoga Visualization
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(FitLightTeal),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawCircle(
                            color = FitPrimaryGreen.copy(alpha = 0.25f),
                            radius = (size.minDimension / 2) * breathScale
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (routine.type == YogaType.BREATHING) {
                                if (breathScale > 1.0f) (if (isMr) "श्वास घ्या" else "Inhale") else (if (isMr) "श्वास सोडा" else "Exhale")
                            } else {
                                "${secondsRemaining}s"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = FitDeepGreen
                        )
                    }
                }

                Text(
                    text = steps[stepIndex],
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = FitDeepGreen,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = if (isMr) routine.benefitMr else routine.benefitEn,
                    fontSize = 12.sp,
                    color = FitTextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (stepIndex < steps.size - 1) {
                        stepIndex++
                        secondsRemaining = 60
                    } else {
                        onComplete()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = FitPrimaryGreen)
            ) {
                Text(
                    if (stepIndex < steps.size - 1)
                        (if (isMr) "पुढील पायरी" else "Next Step")
                    else
                        (if (isMr) "सत्र पूर्ण झाले! 🌿" else "Complete Session! 🌿")
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isMr) "थांबवा" else "Exit")
            }
        }
    )
}
