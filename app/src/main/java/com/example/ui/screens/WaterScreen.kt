package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Language
import com.example.data.model.NotificationType
import com.example.data.repository.FitMarathiRepository
import com.example.ui.components.CircularProgressMeter
import com.example.ui.components.FitCard
import com.example.ui.components.FitLottieAnimation
import com.example.ui.theme.*
import java.util.Calendar

@Composable
fun WaterScreen(
    repository: FitMarathiRepository,
    onNavigateBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val profile by repository.userProfile.collectAsState()
    val dailyLog by repository.dailyLog.collectAsState()
    val isMr = profile.language == Language.MARATHI

    var selectedInterval by remember { mutableStateOf("1 तास") }
    var showGoalDialog by remember { mutableStateOf(false) }
    var showCustomIntervalDialog by remember { mutableStateOf(false) }
    var customMinutesInput by remember { mutableStateOf("45") }
    val intervals = listOf("30 मिनिटे", "1 तास", "2 तास", "कस्टम")

    val progressFraction = (dailyLog.waterDrankLiters / dailyLog.waterGoalLiters).coerceIn(0f, 1f)
    val remainingLiters = (dailyLog.waterGoalLiters - dailyLog.waterDrankLiters).coerceAtLeast(0f)

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
                        .testTag("water_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = FitTextPrimary
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isMr) "पाणी पिण्याची आठवण" else "Water Reminder",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = FitTextPrimary
                    )
                    Text(
                        text = if (isMr) "शरीर हायड्रेटेड ठेवा • चयापचय सुधारा" else "Stay hydrated • Boost metabolism",
                        fontSize = 12.sp,
                        color = FitTextSecondary
                    )
                }

                IconButton(
                    onClick = { showGoalDialog = true },
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(FitLightTeal)
                        .testTag("water_settings_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Adjust Goal",
                        tint = FitPrimaryGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Circular Water Meter Card matching Storyboard
        item {
            FitCard(backgroundColor = Color.White) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        FitLottieAnimation(
                            rawResId = R.raw.lottie_water_ripple,
                            contentDescriptionText = "Animated hydration water droplet",
                            size = 64.dp,
                            modifier = Modifier.testTag("water_lottie_anim")
                        )
                        CircularProgressMeter(
                            progress = progressFraction,
                            targetValueText = "${dailyLog.waterDrankLiters} L / ${dailyLog.waterGoalLiters} L",
                            label = "${(progressFraction * 100).toInt()}% Done",
                            size = 136.dp,
                            strokeWidth = 14.dp,
                            progressColor = FitTeal,
                            trackColor = FitLightTeal
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = if (isMr) "आज अजून ${(remainingLiters * 10).toInt() / 10f} लिटर पाणी प्यायचे आहे." else "You need ${(remainingLiters * 10).toInt() / 10f} L more water today.",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = FitDeepGreen,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Row of Droplet Icons
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (i in 1..8) {
                            val isFilled = (i.toFloat() / 8f) <= progressFraction
                            Icon(
                                imageVector = Icons.Default.WaterDrop,
                                contentDescription = null,
                                tint = if (isFilled) FitTeal else FitBorder,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }

        // Quick Log Buttons
        item {
            FitCard(backgroundColor = Color.White) {
                Text(
                    text = if (isMr) "पाणी नोंदवा (Quick Add Intake)" else "Quick Add Intake",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = FitTextPrimary
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { repository.addWater(0.25f) },
                        modifier = Modifier.weight(1f).testTag("add_250ml_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = FitLightTeal, contentColor = FitDeepGreen),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("+250 ml", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Button(
                        onClick = { repository.addWater(0.5f) },
                        modifier = Modifier.weight(1f).testTag("add_500ml_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = FitLightTeal, contentColor = FitDeepGreen),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("+500 ml", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Button(
                        onClick = { repository.addWater(1.0f) },
                        modifier = Modifier.weight(1f).testTag("add_1000ml_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = FitLightTeal, contentColor = FitDeepGreen),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("+1.0 L", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = { repository.resetWater() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(if (isMr) "आजची नोंद पुन्हा सुरू करा (Reset)" else "Reset Today's Log", fontSize = 12.sp)
                }
            }
        }

        // Reminder Interval Selection matching Storyboard
        item {
            FitCard(backgroundColor = Color.White) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = null,
                        tint = FitSaffron,
                        modifier = Modifier.size(24.dp)
                    )
                    Column {
                        Text(
                            text = if (isMr) "पुढील आठवण (Next Reminder): 11:00 AM" else "Next Reminder: 11:00 AM",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = FitTextPrimary
                        )
                        Text(
                            text = if (isMr) "पाणी म्हणजे जीवन! नियमित पाणी पिणे आवश्यक आहे." else "Water is life! Consistent hydration boosts health.",
                            fontSize = 11.sp,
                            color = FitTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = if (isMr) "रिमाइंडर वेळ निवडा:" else "Set Reminder Frequency:",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    intervals.forEach { interval ->
                        val isSelected = selectedInterval == interval
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { selectedInterval = interval }
                                .border(
                                    1.dp,
                                    if (isSelected) FitPrimaryGreen else FitBorder,
                                    RoundedCornerShape(12.dp)
                                ),
                            color = if (isSelected) FitPrimaryGreen else FitSurfaceVariant,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = interval,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else FitTextPrimary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val minutesAhead = when (selectedInterval) {
                            "30 मिनिटे" -> 30
                            "2 तास" -> 120
                            "कस्टम" -> customMinutesInput.toIntOrNull()?.coerceIn(5, 240) ?: 45
                            else -> 60
                        }
                        val cal = Calendar.getInstance().apply { add(Calendar.MINUTE, minutesAhead) }
                        repository.addCustomReminder(
                            type = NotificationType.WATER,
                            hourOfDay = cal.get(Calendar.HOUR_OF_DAY),
                            minute = cal.get(Calendar.MINUTE)
                        )
                        Toast.makeText(
                            context,
                            if (isMr) "पाणी रिमाइंडर $selectedInterval साठी सेट झाले!" else "Local water reminder scheduled for $selectedInterval!",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    modifier = Modifier.fillMaxWidth().testTag("save_water_reminder_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = FitPrimaryGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (isMr) "रिमाइंडर सेव्ह करा (Save)" else "Save Reminder Settings",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    // Goal Tuning Dialog
    if (showGoalDialog) {
        var tempGoal by remember { mutableFloatStateOf(dailyLog.waterGoalLiters) }
        AlertDialog(
            onDismissRequest = { showGoalDialog = false },
            title = {
                Text(
                    text = if (isMr) "दैनंदिन पाणी ध्येय ठरवा" else "Set Daily Water Target",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = if (isMr) "सध्याचे ध्येय: ${dailyLog.waterGoalLiters} L" else "Current: ${dailyLog.waterGoalLiters} Liters",
                        fontSize = 13.sp,
                        color = FitTextSecondary
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        listOf(2.0f, 2.5f, 3.0f, 3.5f, 4.0f).forEach { g ->
                            FilterChip(
                                selected = tempGoal == g,
                                onClick = { tempGoal = g },
                                label = { Text("${g}L", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = FitTeal,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        repository.setWaterGoal(tempGoal)
                        showGoalDialog = false
                        Toast.makeText(
                            context,
                            if (isMr) "पाणी ध्येय ${tempGoal}L वर सेट झाले!" else "Water goal updated to ${tempGoal}L!",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FitPrimaryGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(if (isMr) "सेव्ह करा" else "Save Target", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showGoalDialog = false }) {
                    Text(if (isMr) "रद्द करा" else "Cancel")
                }
            }
        )
    }

    // Custom Interval Dialog if "कस्टम" is clicked
    if (selectedInterval == "कस्टम" && showCustomIntervalDialog) {
        AlertDialog(
            onDismissRequest = { showCustomIntervalDialog = false },
            title = {
                Text(
                    text = if (isMr) "कस्टम आठवण वेळ" else "Custom Interval",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                OutlinedTextField(
                    value = customMinutesInput,
                    onValueChange = { customMinutesInput = it.filter { ch -> ch.isDigit() } },
                    label = { Text(if (isMr) "मिनिटे (उदा. ४५)" else "Minutes (e.g. 45)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showCustomIntervalDialog = false
                        Toast.makeText(
                            context,
                            if (isMr) "रिमाइंडर दर $customMinutesInput मिनिटांनी येईल." else "Reminder set every $customMinutesInput min.",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FitPrimaryGreen)
                ) {
                    Text(if (isMr) "नक्की करा" else "Set")
                }
            }
        )
    }
}

