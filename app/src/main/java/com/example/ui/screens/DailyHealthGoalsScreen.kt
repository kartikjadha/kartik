package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entity.HealthGoalLogEntity
import com.example.data.local.entity.HealthGoalType
import com.example.data.model.Language
import com.example.ui.components.CircularProgressMeter
import com.example.ui.components.FitCard
import com.example.ui.components.LanguageSwitchButton
import com.example.ui.theme.*
import com.example.ui.viewmodel.DailyHealthGoalViewModel
import com.example.ui.viewmodel.DailyHealthUiState
import com.example.ui.viewmodel.GoalMetricSummary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private data class GoalQuickAction(
    val delta: Int,
    val label: String,
    val testTag: String
)

@Composable
fun DailyHealthGoalsScreen(
    viewModel: DailyHealthGoalViewModel,
    onNavigateBack: () -> Unit,
    onLanguageChanged: (Language) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var showEditTargetsDialog by remember { mutableStateOf(false) }
    var customLogGoalType by remember { mutableStateOf<HealthGoalType?>(null) }
    var showResetConfirmDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(FitBackground)
    ) {
        when (val state = uiState) {
            is DailyHealthUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = FitPrimaryGreen)
                }
            }

            is DailyHealthUiState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.ErrorOutline,
                        contentDescription = "Error",
                        tint = FitDanger,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = state.messageMr,
                        fontWeight = FontWeight.Bold,
                        color = FitTextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = state.messageEn,
                        fontSize = 13.sp,
                        color = FitTextSecondary
                    )
                }
            }

            is DailyHealthUiState.Success -> {
                val isMr = state.language == Language.MARATHI
                val goalSummaries = remember(state) {
                    listOf(
                        state.stepsSummary,
                        state.waterSummary,
                        state.caloriesSummary
                    )
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .testTag("daily_health_goals_list"),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(top = 14.dp, bottom = 96.dp)
                ) {
                    // Header Bar
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                IconButton(
                                    onClick = onNavigateBack,
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                        .testTag("daily_goals_back_btn")
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = if (isMr) "मागे जा" else "Back",
                                        tint = FitTextPrimary
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = if (isMr) "दैनिक आरोग्य ध्येय" else "Daily Health Goals",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = FitTextPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = state.selectedDate,
                                        fontSize = 12.sp,
                                        color = FitTeal,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                LanguageSwitchButton(
                                    currentLanguage = state.language,
                                    onToggle = { newLang ->
                                        viewModel.setLanguage(newLang)
                                        onLanguageChanged(newLang)
                                    }
                                )
                                IconButton(
                                    onClick = { showEditTargetsDialog = true },
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(FitLightTeal)
                                        .testTag("daily_goals_edit_targets_btn")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Tune,
                                        contentDescription = if (isMr) "ध्येय बदला" else "Edit Goals",
                                        tint = FitPrimaryGreen
                                    )
                                }
                            }
                        }
                    }

                    // Hero Summary Banner
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(FitDeepGreen, FitPrimaryGreen, FitTeal)
                                    )
                                )
                                .padding(18.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Surface(
                                        color = Color.White.copy(alpha = 0.16f),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = if (isMr) "Room स्थानिक जतन सक्रिय" else "Room Local Sync Active",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = FitGold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = state.statusHeadline,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        lineHeight = 20.sp
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        FilledTonalButton(
                                            onClick = { showEditTargetsDialog = true },
                                            colors = ButtonDefaults.filledTonalButtonColors(
                                                containerColor = Color.White.copy(alpha = 0.2f),
                                                contentColor = Color.White
                                            ),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                            modifier = Modifier.heightIn(min = 40.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Edit,
                                                contentDescription = null,
                                                modifier = Modifier.size(15.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = if (isMr) "ध्येय सेट करा" else "Set Targets",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                        TextButton(
                                            onClick = { showResetConfirmDialog = true },
                                            colors = ButtonDefaults.textButtonColors(
                                                contentColor = Color(0xFFD1FAE5)
                                            ),
                                            modifier = Modifier
                                                .heightIn(min = 40.dp)
                                                .testTag("daily_goals_reset_btn")
                                        ) {
                                            Text(
                                                text = if (isMr) "रीसेट" else "Reset",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Box(
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(CircleShape)
                                        .background(Color.White),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressMeter(
                                        progress = state.goal.overallGoalProgress,
                                        targetValueText = "${state.overallProgressPercent}%",
                                        label = if (isMr) "पूर्ण" else "Done",
                                        size = 92.dp,
                                        strokeWidth = 8.dp,
                                        progressColor = FitEmerald,
                                        trackColor = FitLightTeal
                                    )
                                }
                            }
                        }
                    }

                    // Section Title: Daily Health Goals List
                    item {
                        Text(
                            text = if (isMr) "आजची ३ मुख्य आरोग्य ध्येये" else "Today's Health Goals",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = FitTextPrimary
                        )
                    }

                    // Interactive List of Goals (Steps, Water, Calories)
                    items(goalSummaries, key = { it.type.name }) { summary ->
                        DailyHealthGoalCard(
                            summary = summary,
                            isMr = isMr,
                            onQuickDelta = { delta ->
                                when (summary.type) {
                                    HealthGoalType.STEPS -> viewModel.addSteps(delta)
                                    HealthGoalType.WATER -> viewModel.addWaterMl(delta)
                                    HealthGoalType.CALORIES -> viewModel.addCalories(delta)
                                }
                            },
                            onOpenCustomAdd = { customLogGoalType = summary.type }
                        )
                    }

                    // Saved Activity Logs Header
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isMr) "आजच्या नोंदी (${state.logs.size})" else "Today's Saved Logs (${state.logs.size})",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = FitTextPrimary
                            )
                            Text(
                                text = if (isMr) "हटवण्यासाठी कचरा चिन्हावर टॅप करा" else "Tap trash to undo",
                                fontSize = 11.sp,
                                color = FitTextSecondary
                            )
                        }
                    }

                    if (state.logs.isEmpty()) {
                        item {
                            FitCard(backgroundColor = Color.White) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.History,
                                        contentDescription = null,
                                        tint = FitTeal,
                                        modifier = Modifier.size(34.dp)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = if (isMr) "अद्याप कोणत्याही नोंदी जोडलेल्या नाहीत" else "No activity updates logged yet",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp,
                                        color = FitTextPrimary
                                    )
                                    Text(
                                        text = if (isMr) "वरील बटणांवर टॅप करून पावले, पाणी किंवा कॅलरीज नोंदवा." else "Tap any quick-add button above to log steps, water, or calories.",
                                        fontSize = 12.sp,
                                        color = FitTextSecondary
                                    )
                                }
                            }
                        }
                    } else {
                        items(state.logs, key = { it.id }) { logEntry ->
                            HealthLogEntryRow(
                                log = logEntry,
                                language = state.language,
                                onDelete = { viewModel.deleteLogEntry(logEntry) }
                            )
                        }
                    }
                }

                // Edit Targets Dialog
                if (showEditTargetsDialog) {
                    EditHealthTargetsDialog(
                        isMr = isMr,
                        initialStepGoal = state.goal.stepGoal,
                        initialWaterGoalMl = state.goal.waterGoalMl,
                        initialCalorieGoal = state.goal.calorieGoal,
                        onDismiss = { showEditTargetsDialog = false },
                        onSave = { stepGoal, waterGoalMl, calorieGoal ->
                            viewModel.updateGoals(stepGoal, waterGoalMl, calorieGoal)
                            showEditTargetsDialog = false
                        }
                    )
                }

                // Custom Progress Entry Dialog
                customLogGoalType?.let { goalType ->
                    CustomGoalEntryDialog(
                        goalType = goalType,
                        isMr = isMr,
                        onDismiss = { customLogGoalType = null },
                        onConfirm = { amount, note ->
                            val cleanNote = note.trim().takeIf { it.isNotEmpty() }
                            when (goalType) {
                                HealthGoalType.STEPS -> viewModel.addSteps(amount, cleanNote, cleanNote)
                                HealthGoalType.WATER -> viewModel.addWaterMl(amount, cleanNote, cleanNote)
                                HealthGoalType.CALORIES -> viewModel.addCalories(amount, cleanNote, cleanNote)
                            }
                            customLogGoalType = null
                        }
                    )
                }

                // Reset Confirmation Dialog
                if (showResetConfirmDialog) {
                    AlertDialog(
                        onDismissRequest = { showResetConfirmDialog = false },
                        title = {
                            Text(
                                text = if (isMr) "आजची प्रगती रीसेट करायची का?" else "Reset Today's Progress?",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        },
                        text = {
                            Text(
                                text = if (isMr) "आजची सर्व पावले, पाणी आणि कॅलरीजची नोंद शून्यावर येईल." else "This clears today's steps, water, and calorie progress back to zero.",
                                fontSize = 13.sp,
                                color = FitTextSecondary
                            )
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    viewModel.resetTodayProgress()
                                    showResetConfirmDialog = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = FitDanger),
                                modifier = Modifier.testTag("confirm_reset_goals_btn")
                            ) {
                                Text(if (isMr) "रीसेट करा" else "Reset")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showResetConfirmDialog = false }) {
                                Text(if (isMr) "रद्द करा" else "Cancel")
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun DailyHealthGoalCard(
    summary: GoalMetricSummary,
    isMr: Boolean,
    onQuickDelta: (Int) -> Unit,
    onOpenCustomAdd: () -> Unit
) {
    val accentColor = when (summary.type) {
        HealthGoalType.STEPS -> FitEmerald
        HealthGoalType.WATER -> FitTeal
        HealthGoalType.CALORIES -> FitSaffron
    }
    val softContainerColor = when (summary.type) {
        HealthGoalType.STEPS -> Color(0xFFE8F5E9)
        HealthGoalType.WATER -> Color(0xFFE0F2F1)
        HealthGoalType.CALORIES -> Color(0xFFFFF3E0)
    }
    val icon: ImageVector = when (summary.type) {
        HealthGoalType.STEPS -> Icons.AutoMirrored.Filled.DirectionsWalk
        HealthGoalType.WATER -> Icons.Default.WaterDrop
        HealthGoalType.CALORIES -> Icons.Default.LocalFireDepartment
    }
    val quickActions = remember(summary.type) {
        when (summary.type) {
            HealthGoalType.STEPS -> listOf(
                GoalQuickAction(-500, "-500", "goal_steps_minus_500"),
                GoalQuickAction(500, "+500", "goal_steps_plus_500"),
                GoalQuickAction(1000, "+1,000", "goal_steps_plus_1000")
            )
            HealthGoalType.WATER -> listOf(
                GoalQuickAction(-250, "-250 ml", "goal_water_minus_250"),
                GoalQuickAction(250, "+250 ml", "goal_water_plus_250"),
                GoalQuickAction(500, "+500 ml", "goal_water_plus_500")
            )
            HealthGoalType.CALORIES -> listOf(
                GoalQuickAction(150, "+150", "goal_calories_plus_150"),
                GoalQuickAction(350, "+350", "goal_calories_plus_350"),
                GoalQuickAction(500, "+500", "goal_calories_plus_500")
            )
        }
    }

    val animatedProgress by animateFloatAsState(
        targetValue = summary.progressFraction.coerceIn(0f, 1f),
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "goal_progress_${summary.type.name}"
    )

    FitCard(
        backgroundColor = Color.White,
        modifier = Modifier.testTag("goal_card_${summary.type.name.lowercase()}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(softContainerColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = summary.title,
                        tint = accentColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Column {
                    Text(
                        text = summary.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = FitTextPrimary
                    )
                    Text(
                        text = "${summary.currentDisplay} • ${summary.targetDisplay}",
                        fontSize = 12.sp,
                        color = FitTextSecondary
                    )
                }
            }

            Surface(
                color = accentColor.copy(alpha = 0.14f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "${summary.progressPercent}%",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = accentColor,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(6.dp)),
            color = accentColor,
            trackColor = softContainerColor
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            quickActions.forEach { action ->
                val isNegative = action.delta < 0
                OutlinedButton(
                    onClick = { onQuickDelta(action.delta) },
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp)
                        .testTag(action.testTag),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 8.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isNegative) FitBorder else accentColor.copy(alpha = 0.5f)
                    )
                ) {
                    Text(
                        text = action.label,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isNegative) FitTextSecondary else accentColor,
                        maxLines = 1
                    )
                }
            }

            IconButton(
                onClick = onOpenCustomAdd,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accentColor)
                    .testTag("goal_custom_add_${summary.type.name.lowercase()}")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = if (isMr) "कस्टम जोडा" else "Custom Add",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
private fun HealthLogEntryRow(
    log: HealthGoalLogEntity,
    language: Language,
    onDelete: () -> Unit
) {
    val isMr = language == Language.MARATHI
    val accent = when (log.goalType) {
        HealthGoalType.STEPS -> FitEmerald
        HealthGoalType.WATER -> FitTeal
        HealthGoalType.CALORIES -> FitSaffron
    }
    val timeText = remember(log.timestamp) {
        SimpleDateFormat("hh:mm a", Locale.US).format(Date(log.timestamp))
    }

    AnimatedVisibility(visible = true) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, FitBorder.copy(alpha = 0.7f), RoundedCornerShape(14.dp))
                .testTag("health_log_item_${log.id}"),
            color = Color.White,
            shape = RoundedCornerShape(14.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(accent)
                    )
                    Column {
                        Text(
                            text = log.localizedLabel(language),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = FitTextPrimary
                        )
                        Text(
                            text = "${log.goalType.localizedTitle(language)} • $timeText",
                            fontSize = 11.sp,
                            color = FitTextSecondary
                        )
                    }
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("delete_log_${log.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = if (isMr) "नोंद हटवा" else "Delete Log",
                        tint = FitTextMuted
                    )
                }
            }
        }
    }
}

@Composable
private fun EditHealthTargetsDialog(
    isMr: Boolean,
    initialStepGoal: Int,
    initialWaterGoalMl: Int,
    initialCalorieGoal: Int,
    onDismiss: () -> Unit,
    onSave: (Int, Int, Int) -> Unit
) {
    var stepGoalText by remember { mutableStateOf(initialStepGoal.toString()) }
    var waterGoalText by remember { mutableStateOf(initialWaterGoalMl.toString()) }
    var calorieGoalText by remember { mutableStateOf(initialCalorieGoal.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isMr) "दैनिक ध्येय बदला" else "Edit Daily Targets",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = stepGoalText,
                    onValueChange = { stepGoalText = it.filter(Char::isDigit) },
                    label = { Text(if (isMr) "पावले ध्येय (Steps)" else "Steps Target") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("dialog_step_target_input")
                )
                OutlinedTextField(
                    value = waterGoalText,
                    onValueChange = { waterGoalText = it.filter(Char::isDigit) },
                    label = { Text(if (isMr) "पाणी ध्येय (मिली / ml)" else "Water Target (ml)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("dialog_water_target_input")
                )
                OutlinedTextField(
                    value = calorieGoalText,
                    onValueChange = { calorieGoalText = it.filter(Char::isDigit) },
                    label = { Text(if (isMr) "कॅलरीज ध्येय (kcal)" else "Calories Target (kcal)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("dialog_calorie_target_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        stepGoalText.toIntOrNull() ?: initialStepGoal,
                        waterGoalText.toIntOrNull() ?: initialWaterGoalMl,
                        calorieGoalText.toIntOrNull() ?: initialCalorieGoal
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = FitPrimaryGreen),
                modifier = Modifier.testTag("dialog_save_targets_btn")
            ) {
                Text(if (isMr) "जतन करा" else "Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isMr) "रद्द करा" else "Cancel")
            }
        }
    )
}

@Composable
private fun CustomGoalEntryDialog(
    goalType: HealthGoalType,
    isMr: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (Int, String) -> Unit
) {
    val defaultAmount = when (goalType) {
        HealthGoalType.STEPS -> "1000"
        HealthGoalType.WATER -> "250"
        HealthGoalType.CALORIES -> "300"
    }
    var amountText by remember { mutableStateOf(defaultAmount) }
    var noteText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isMr) "${goalType.titleMr} जोडा" else "Log ${goalType.titleEn}",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter(Char::isDigit) },
                    label = {
                        Text(
                            if (isMr) "प्रमाण (${goalType.unitMr})" else "Amount (${goalType.unitEn})"
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("custom_goal_amount_input")
                )
                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    label = {
                        Text(
                            if (isMr) "टीप (ऐच्छिक, उदा. पोहे / सकाळचे चालणे)" else "Note (optional, e.g. Poha / Morning walk)"
                        )
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("custom_goal_note_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val parsed = amountText.toIntOrNull() ?: 0
                    if (parsed > 0) {
                        onConfirm(parsed, noteText)
                    } else {
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = FitPrimaryGreen),
                modifier = Modifier.testTag("custom_goal_confirm_btn")
            ) {
                Text(if (isMr) "जोडा" else "Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isMr) "रद्द करा" else "Cancel")
            }
        }
    )
}
