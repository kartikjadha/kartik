package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Language
import com.example.data.repository.FitMarathiRepository
import com.example.data.repository.SampleData
import com.example.ui.components.DisclaimerBox
import com.example.ui.components.FitCard
import com.example.ui.components.PillTabRow
import com.example.ui.theme.*

@Composable
fun ProgressScreen(
    repository: FitMarathiRepository,
    onNavigateToHealthReport: () -> Unit,
    onNavigateBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val profile by repository.userProfile.collectAsState()
    val dailyLog by repository.dailyLog.collectAsState()
    val isMr = profile.language == Language.MARATHI

    var timeRangeTab by remember { mutableIntStateOf(0) }
    var showManualLogDialog by remember { mutableStateOf(false) }

    val weeklyData = SampleData.weeklyProgress

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
                        .testTag("progress_back_btn")
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
                        text = if (isMr) "माझी प्रगती (My Progress)" else "My Progress & Analytics",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = FitTextPrimary
                    )
                    Text(
                        text = if (isMr) "वजन, पावले आणि कॅलरीजचा सविस्तर आलेख" else "Track weight loss, steps, and activity trends",
                        fontSize = 12.sp,
                        color = FitTextSecondary
                    )
                }
            }
        }

        // Time Range Tabs: Weekly, Monthly, Yearly
        item {
            PillTabRow(
                tabs = listOf(
                    if (isMr) "साप्ताहिक (Weekly)" else "Weekly",
                    if (isMr) "मासिक (Monthly)" else "Monthly",
                    if (isMr) "वार्षिक (Yearly)" else "Yearly"
                ),
                selectedIndex = timeRangeTab,
                onTabSelected = { timeRangeTab = it },
                activeColor = FitPrimaryGreen
            )
        }

        // 4 Key Metric Cards (2x2 Grid) matching Storyboard
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ProgressStatCard(
                        title = if (isMr) "वजन (Weight)" else "Weight",
                        value = "${profile.weightKg} kg",
                        change = "-2.0 kg",
                        isPositive = true,
                        modifier = Modifier.weight(1f)
                    )
                    ProgressStatCard(
                        title = if (isMr) "बॉडी फॅट" else "Body Fat",
                        value = "18%",
                        change = "-1.5%",
                        isPositive = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ProgressStatCard(
                        title = if (isMr) "एकूण पावले" else "Weekly Steps",
                        value = "42,650",
                        change = "+12%",
                        isPositive = true,
                        modifier = Modifier.weight(1f)
                    )
                    ProgressStatCard(
                        title = if (isMr) "कॅलरीज खर्च" else "Burned Cals",
                        value = "12,850 kcal",
                        change = "+8%",
                        isPositive = true,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Weight Progress Graph Card
        item {
            FitCard(backgroundColor = Color.White) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isMr) "वजनाचा आलेख (Weight Trend)" else "Weight Trend Graph",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = FitTextPrimary
                        )
                        Text(
                            text = "74.0 kg ➔ 72.0 kg",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = FitEmerald
                        )
                    }

                    OutlinedButton(
                        onClick = { showManualLogDialog = true },
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("manual_log_btn")
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isMr) "नोंद करा" else "Log", fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Custom Line Chart for Weight (Mon to Sun)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height
                        val minWeight = 71.0f
                        val maxWeight = 75.0f

                        val points = weeklyData.mapIndexed { index, record ->
                            val x = (index.toFloat() / (weeklyData.size - 1)) * w
                            val y = h - ((record.weightKg - minWeight) / (maxWeight - minWeight)) * h
                            Offset(x, y)
                        }

                        // Draw connecting path
                        val path = Path().apply {
                            points.forEachIndexed { i, pt ->
                                if (i == 0) moveTo(pt.x, pt.y) else lineTo(pt.x, pt.y)
                            }
                        }

                        drawPath(
                            path = path,
                            color = FitEmerald,
                            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                        )

                        // Draw points
                        points.forEach { pt ->
                            drawCircle(color = FitSurface, radius = 5.dp.toPx(), center = pt)
                            drawCircle(color = FitPrimaryGreen, radius = 3.dp.toPx(), center = pt)
                        }
                    }
                }

                // X-Axis Day Labels
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    weeklyData.forEach {
                        Text(
                            text = it.dayLabel.take(3),
                            fontSize = 10.sp,
                            color = FitTextSecondary
                        )
                    }
                }
            }
        }

        // Daily Step & Sleep Breakdown
        item {
            FitCard(backgroundColor = Color.White) {
                Text(
                    text = if (isMr) "आरोग्य सवयी (Daily Habits)" else "Daily Health Habits",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                HabitRow(
                    icon = Icons.Default.DirectionsWalk,
                    title = if (isMr) "सरासरी दैनिक पावले" else "Average Daily Steps",
                    value = "8,462 / 10,000",
                    progress = 0.84f,
                    color = FitEmerald
                )
                Spacer(modifier = Modifier.height(10.dp))
                HabitRow(
                    icon = Icons.Default.Bedtime,
                    title = if (isMr) "सरासरी झोप" else "Average Sleep",
                    value = "7.5 तास (Hours)",
                    progress = 0.93f,
                    color = FitTeal
                )
            }
        }

        // Health Report Navigation Banner
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onNavigateToHealthReport() },
                color = FitLightTeal,
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isMr) "सविस्तर आरोग्य अहवाल पहा" else "View Detailed Health Report",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = FitDeepGreen
                        )
                        Text(
                            text = if (isMr) "डायबेटीस जोखीम विश्लेषण, बीएमआय व हृदय स्थिती" else "Diabetes risk assessment, BMI & cardio overview",
                            fontSize = 12.sp,
                            color = FitTeal
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = FitDeepGreen
                    )
                }
            }
        }

        item {
            DisclaimerBox(
                textEn = "Progress charts represent user-logged and estimated metrics. They do not constitute official clinical diagnostic evaluations.",
                textMr = "प्रगती आलेख हे वापरकर्त्याने नोंदवलेल्या माहितीवर आधारित आहेत. हे कोणत्याही वैद्यकीय तपासणीचा पर्याय नाहीत.",
                isMarathi = isMr
            )
        }
    }

    if (showManualLogDialog) {
        var inputWeight by remember { mutableStateOf(profile.weightKg.toString()) }
        var inputSteps by remember { mutableStateOf("1000") }

        AlertDialog(
            onDismissRequest = { showManualLogDialog = false },
            title = {
                Text(text = if (isMr) "माहिती नोंदवा (Log Progress)" else "Log Today's Data", fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = inputWeight,
                        onValueChange = { inputWeight = it },
                        label = { Text(if (isMr) "आजचे वजन (Weight kg)" else "Weight (kg)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = inputSteps,
                        onValueChange = { inputSteps = it },
                        label = { Text(if (isMr) "अतिरिक्त पावले (Add Steps)" else "Add Steps") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val w = inputWeight.toFloatOrNull() ?: profile.weightKg
                        val s = inputSteps.toIntOrNull() ?: 0
                        repository.updateProfile(
                            name = profile.name,
                            email = profile.email,
                            phone = profile.phone,
                            age = profile.age,
                            heightCm = profile.heightCm,
                            weightKg = w,
                            goal = profile.goal,
                            foodPreference = profile.foodPreference
                        )
                        repository.addSteps(s)
                        showManualLogDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FitPrimaryGreen)
                ) {
                    Text(if (isMr) "जतन करा" else "Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showManualLogDialog = false }) {
                    Text(if (isMr) "रद्द करा" else "Cancel")
                }
            }
        )
    }
}

@Composable
private fun ProgressStatCard(
    title: String,
    value: String,
    change: String,
    isPositive: Boolean,
    modifier: Modifier = Modifier
) {
    FitCard(
        modifier = modifier,
        backgroundColor = Color.White
    ) {
        Text(text = title, fontSize = 12.sp, color = FitTextSecondary)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = FitTextPrimary)
        Spacer(modifier = Modifier.height(2.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (isPositive) Icons.Default.TrendingDown else Icons.Default.TrendingUp,
                contentDescription = null,
                tint = FitEmerald,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = change, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = FitEmerald)
        }
    }
}

@Composable
private fun HabitRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    progress: Float,
    color: Color
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
                Text(text = title, fontSize = 13.sp, color = FitTextPrimary, fontWeight = FontWeight.Medium)
            }
            Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = FitTextPrimary)
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
            color = color,
            trackColor = FitSurfaceVariant
        )
    }
}
