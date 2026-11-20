package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HealthConditionRisk
import com.example.data.model.Language
import com.example.data.model.RiskLevel
import com.example.data.repository.FitMarathiRepository
import com.example.ui.components.CircularProgressMeter
import com.example.ui.components.DisclaimerBox
import com.example.ui.components.FitCard
import com.example.ui.components.PillTabRow
import com.example.ui.theme.*

@Composable
fun HealthReportScreen(
    repository: FitMarathiRepository,
    onNavigateBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val profile by repository.userProfile.collectAsState()
    val healthReport by repository.healthReport.collectAsState()
    val isMr = profile.language == Language.MARATHI

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Overview, 1: Diabetes Risk, 2: All Conditions

    val screenTitle = when (selectedTab) {
        1 -> if (isMr) "डायबेटीस जोखीम विश्लेषण" else "Diabetes Risk Analysis"
        2 -> if (isMr) "संभाव्य आरोग्य समस्या" else "Possible Health Issues"
        else -> if (isMr) "आरोग्य अहवाल" else "Health Report"
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
                    onClick = {
                        if (selectedTab > 0) {
                            selectedTab = 0
                        } else {
                            onNavigateBack()
                        }
                    },
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .testTag("health_report_back_btn")
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
                        text = screenTitle,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = FitTextPrimary
                    )
                    Text(
                        text = if (isMr) "सर्वसमावेशक आरोग्य विश्लेषण व जीवनशैली जोखीम" else "Comprehensive lifestyle & metabolic wellness score",
                        fontSize = 12.sp,
                        color = FitTextSecondary
                    )
                }
            }
        }

        // Tabs: Overview, Diabetes Risk, Details
        item {
            PillTabRow(
                tabs = listOf(
                    if (isMr) "आढावा (Overview)" else "Overview",
                    if (isMr) "डायबेटीस जोखीम" else "Diabetes Risk",
                    if (isMr) "संभाव्य समस्या" else "Health Issues"
                ),
                selectedIndex = selectedTab,
                onTabSelected = { selectedTab = it },
                activeColor = FitPrimaryGreen
            )
        }

        if (selectedTab == 0) {
            // Overall Health Score Card matching Storyboard
            item {
                FitCard(backgroundColor = Color.White) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isMr) "एकूण हेल्थ स्कोअर (Health Score)" else "Overall Health Score",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = FitDeepGreen
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        CircularProgressMeter(
                            progress = healthReport.overallScore / 100f,
                            targetValueText = "${healthReport.overallScore}/100",
                            label = if (isMr) healthReport.scoreRatingMr else healthReport.scoreRatingEn,
                            size = 130.dp,
                            strokeWidth = 12.dp,
                            progressColor = FitEmerald,
                            trackColor = FitLightTeal
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = if (isMr) "तुमचं शरीर ठीक आहे, पण थोडे लक्ष द्या. तुम्ही आणखी सुधारणा करू शकता." else "Your body is in good shape, but attention to daily habits will yield even better vitality.",
                            fontSize = 12.sp,
                            color = FitTextSecondary,
                            lineHeight = 17.sp,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }
            }

            // Health Parameter List matching Storyboard
            item {
                FitCard(backgroundColor = Color.White) {
                    Text(
                        text = if (isMr) "महत्त्वाचे शारीरिक निर्देशांक (Vitals)" else "Key Body Parameters",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = FitTextPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Body Fat with gradient bar matching Storyboard 2 Panel 10
                    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isMr) "बॉडी फॅट (Body Fat)" else "Body Fat",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = FitTextPrimary
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "22%",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FitEmerald
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = FitEmerald.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = if (isMr) "सामान्य (Normal)" else "Normal",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = FitEmerald,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(FitEmerald, FitGold, FitWarning, Color(0xFFEF4444))
                                    )
                                )
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    HealthParameterRow(
                        icon = Icons.Default.Speed,
                        title = "BMI (Body Mass Index)",
                        value = "${healthReport.bmi}",
                        badge = if (isMr) healthReport.bmiCategoryMr else healthReport.bmiCategoryEn,
                        badgeColor = FitEmerald
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    HealthParameterRow(
                        icon = Icons.Default.Favorite,
                        title = if (isMr) "हृदय आरोग्य (Heart Health)" else "Heart Health",
                        value = if (isMr) healthReport.heartHealthMr else healthReport.heartHealthEn,
                        badge = if (isMr) "चांगले (Good)" else "Good",
                        badgeColor = FitEmerald
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    HealthParameterRow(
                        icon = Icons.Default.WaterDrop,
                        title = if (isMr) "डायबेटीस जोखीम (Diabetes)" else "Diabetes Risk",
                        value = "${healthReport.diabetesRiskPercent}%",
                        badge = if (isMr) "मध्यम (Moderate)" else "Moderate",
                        badgeColor = FitWarning
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    HealthParameterRow(
                        icon = Icons.Default.LocalHospital,
                        title = if (isMr) "यकृत आरोग्य (Liver Health)" else "Liver Health",
                        value = if (isMr) healthReport.liverHealthMr else healthReport.liverHealthEn,
                        badge = if (isMr) "सामान्य (Normal)" else "Normal",
                        badgeColor = FitEmerald
                    )
                }
            }

            item {
                Button(
                    onClick = { selectedTab = 1 },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("view_detailed_report_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = FitPrimaryGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (isMr) "सविस्तर अहवाल पहा (View Detailed Report)" else "View Detailed Report",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        } else if (selectedTab == 1) {
            // Diabetes Risk Analysis Deep Dive matching Storyboard 1 panel 6
            item {
                FitCard(backgroundColor = Color.White) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isMr) "डायबेटीस जोखीम विश्लेषण" else "Diabetes Risk Analysis",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = FitDeepGreen
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        CircularProgressMeter(
                            progress = 0.28f,
                            targetValueText = "28%",
                            label = if (isMr) "मध्यम जोखीम" else "Moderate Risk",
                            size = 120.dp,
                            strokeWidth = 12.dp,
                            progressColor = FitWarning,
                            trackColor = FitSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (isMr) "तुमच्या सध्याच्या बैठी जीवनशैली आणि आहाराच्या सवयींनुसार भविष्यात मधुमेहाची मध्यम जोखीम उद्भवू शकते." else "Based on current sedentary desk hours and refined carb frequency, lifestyle risk is moderate but fully manageable.",
                            fontSize = 13.sp,
                            color = FitTextSecondary,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // What to do? Action Checklist
            item {
                FitCard(backgroundColor = Color.White) {
                    Text(
                        text = if (isMr) "काय करावे? (What to do?)" else "Actionable Steps:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = FitTextPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    val actions = if (isMr) listOf(
                        "साखर व गूळ-गोड पदार्थांचे प्रमाण अत्यंत मर्यादित करा",
                        "दररोज न चुकता किमान ३० मिनिटे व्यायाम / चालणे",
                        "वजन व पोटाचा घेर नियंत्रणात ठेवा (बीएमआय < २५)",
                        "आहारात ज्वारी, बाजरी, मटकी व पालेभाज्यांचा वापर वाढवा",
                        "वर्षातून एकदा उपाशीपोटी रक्तातील साखरेची (Fasting Glucose) तपासणी करा"
                    ) else listOf(
                        "Eliminate refined sugar and sweet beverages",
                        "Exercise or walk at brisk pace 30 minutes daily",
                        "Maintain healthy waistline and BMI under 25",
                        "Eat whole native grains: Jowar, Bajra & Sprouted Matki",
                        "Undergo annual preventive blood glucose checkup"
                    )

                    actions.forEach { action ->
                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = FitEmerald,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = action,
                                fontSize = 13.sp,
                                color = FitTextSecondary,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            item {
                Button(
                    onClick = { selectedTab = 2 },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("view_full_report_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = FitPrimaryGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (isMr) "सर्व आजार विश्लेषण पहा (View Full Report)" else "View Full Report",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        } else {
            // All Health Conditions & Risk Predictions matching Storyboard 1 panel 7
            item {
                Text(
                    text = if (isMr) "संभाव्य आरोग्य समस्या अंदाज" else "Potential Health Risk Indicators",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            items(healthReport.risks) { risk ->
                RiskCard(risk = risk, isMr = isMr)
            }
        }

        // Mandatory Disclaimer Box
        item {
            DisclaimerBox(
                textEn = "Educational lifestyle assessment only. Do not rely on algorithms for medical diagnosis. Always consult a certified doctor or healthcare professional for clinical screenings and treatment.",
                textMr = "टीप: हे केवळ शैक्षणिक अंदाज आहेत. हे डॉक्टरांच्या तपासणीचा किंवा वैद्यकीय सल्ल्याचा पर्याय नाहीत. कोणत्याही समस्येसाठी योग्य डॉक्टरांचा सल्ला घ्या.",
                isMarathi = isMr
            )
        }
    }
}

@Composable
private fun HealthParameterRow(
    icon: ImageVector,
    title: String,
    value: String,
    badge: String,
    badgeColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(icon, contentDescription = null, tint = FitTeal, modifier = Modifier.size(20.dp))
            Text(text = title, fontSize = 13.sp, color = FitTextPrimary, fontWeight = FontWeight.Medium)
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = FitTextPrimary)
            Surface(
                color = badgeColor.copy(alpha = 0.15f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = badge,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = badgeColor,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun RiskCard(risk: HealthConditionRisk, isMr: Boolean) {
    FitCard(backgroundColor = Color.White) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isMr) risk.conditionNameMr else risk.conditionNameEn,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = FitTextPrimary
            )
            Surface(
                color = Color(risk.riskLevel.colorHex).copy(alpha = 0.15f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "${if (isMr) risk.riskLevel.mrLabel else risk.riskLevel.enLabel} (${risk.percentage}%)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(risk.riskLevel.colorHex),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = if (isMr) risk.explanationMr else risk.explanationEn,
            fontSize = 12.sp,
            color = FitTextSecondary,
            lineHeight = 16.sp
        )

        Spacer(modifier = Modifier.height(8.dp))
        val recs = if (isMr) risk.recommendationsMr else risk.recommendationsEn
        recs.take(2).forEach { rec ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(5.dp).clip(CircleShape).background(FitEmerald))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = rec, fontSize = 11.sp, color = FitDeepGreen)
            }
        }
    }
}
