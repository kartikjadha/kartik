package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FitnessGoal
import com.example.data.model.FoodPreference
import com.example.data.model.Language
import com.example.data.repository.FitMarathiRepository
import com.example.ui.components.FitCard
import com.example.ui.theme.*

@Composable
fun ProfileSettingsScreen(
    repository: FitMarathiRepository,
    onNavigateToOnboarding: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val profile by repository.userProfile.collectAsState()
    val isMr = profile.language == Language.MARATHI

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showGoalDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(FitBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp)
    ) {
        item {
            Text(
                text = if (isMr) "माझा प्रोफाइल व सेटिंग्ज" else "My Profile & Settings",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = FitTextPrimary
            )
        }

        // Profile Header Card matching Storyboard
        item {
            FitCard(backgroundColor = Color.White) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(FitPrimaryGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = profile.name.take(1),
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = profile.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = FitTextPrimary
                    )
                    Text(
                        text = profile.email,
                        fontSize = 12.sp,
                        color = FitTextSecondary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 3 Metric Pills: Weight, Height, Age
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(FitLightTeal)
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        ProfileMetricColumn(
                            value = "${profile.weightKg} kg",
                            label = if (isMr) "वजन" else "Weight"
                        )
                        ProfileMetricColumn(
                            value = "${profile.heightCm} cm",
                            label = if (isMr) "उंची" else "Height"
                        )
                        ProfileMetricColumn(
                            value = "${profile.age}",
                            label = if (isMr) "वय" else "Age"
                        )
                    }
                }
            }
        }

        // Settings Navigation Options
        item {
            FitCard(backgroundColor = Color.White) {
                SettingsRow(
                    icon = Icons.Default.Person,
                    title = if (isMr) "प्रोफाइल संपादन (Edit Profile)" else "Edit Profile",
                    onClick = { showEditProfileDialog = true }
                )
                HorizontalDivider(color = FitBorder)
                SettingsRow(
                    icon = Icons.Default.TrackChanges,
                    title = if (isMr) "ध्येय व आहार पसंती (Goals & Diet)" else "Goals & Food Preferences",
                    onClick = { showGoalDialog = true }
                )
                HorizontalDivider(color = FitBorder)
                SettingsRow(
                    icon = Icons.Default.Language,
                    title = if (isMr) "भाषा बदला (Change Language)" else "Change Language",
                    trailingText = if (isMr) "मराठी" else "English",
                    onClick = {
                        val next = if (isMr) Language.ENGLISH else Language.MARATHI
                        repository.updateLanguage(next)
                    }
                )
                HorizontalDivider(color = FitBorder)
                SettingsRow(
                    icon = Icons.Default.Security,
                    title = if (isMr) "गोपनीयता व सुरक्षा (Privacy & Data)" else "Privacy & Security",
                    onClick = { showPrivacyDialog = true }
                )
                HorizontalDivider(color = FitBorder)
                SettingsRow(
                    icon = Icons.Default.Logout,
                    title = if (isMr) "लॉगआउट करा (Log Out)" else "Log Out",
                    textColor = FitDanger,
                    onClick = {
                        repository.setAuthentication(false)
                        onNavigateToOnboarding()
                    }
                )
            }
        }
    }

    // Edit Profile Dialog
    if (showEditProfileDialog) {
        var editName by remember { mutableStateOf(profile.name) }
        var editWeight by remember { mutableStateOf(profile.weightKg.toString()) }
        var editHeight by remember { mutableStateOf(profile.heightCm.toString()) }

        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = {
                Text(text = if (isMr) "प्रोफाइल अपडेट करा" else "Update Profile", fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text(if (isMr) "नाव" else "Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editWeight,
                        onValueChange = { editWeight = it },
                        label = { Text(if (isMr) "वजन (kg)" else "Weight (kg)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editHeight,
                        onValueChange = { editHeight = it },
                        label = { Text(if (isMr) "उंची (cm)" else "Height (cm)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val w = editWeight.toFloatOrNull() ?: profile.weightKg
                        val h = editHeight.toIntOrNull() ?: profile.heightCm
                        repository.updateProfile(
                            name = editName,
                            email = profile.email,
                            phone = profile.phone,
                            age = profile.age,
                            heightCm = h,
                            weightKg = w,
                            goal = profile.goal,
                            foodPreference = profile.foodPreference
                        )
                        showEditProfileDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FitPrimaryGreen)
                ) {
                    Text(if (isMr) "जतन करा" else "Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text(if (isMr) "रद्द करा" else "Cancel")
                }
            }
        )
    }

    // Goals & Diet Dialog
    if (showGoalDialog) {
        var tempGoal by remember { mutableStateOf(profile.goal) }
        var tempFood by remember { mutableStateOf(profile.foodPreference) }

        AlertDialog(
            onDismissRequest = { showGoalDialog = false },
            title = {
                Text(text = if (isMr) "ध्येय व आहार" else "Goals & Food", fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(text = if (isMr) "ध्येय निवडा:" else "Select Goal:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    FitnessGoal.values().forEach { g ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { tempGoal = g }
                        ) {
                            RadioButton(selected = tempGoal == g, onClick = { tempGoal = g })
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isMr) g.marathiLabel else g.englishLabel, fontSize = 13.sp)
                        }
                    }

                    HorizontalDivider()

                    Text(text = if (isMr) "आहार पसंती:" else "Food Preference:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    FoodPreference.values().forEach { f ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { tempFood = f }
                        ) {
                            RadioButton(selected = tempFood == f, onClick = { tempFood = f })
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isMr) f.marathiLabel else f.englishLabel, fontSize = 13.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        repository.updateProfile(
                            name = profile.name,
                            email = profile.email,
                            phone = profile.phone,
                            age = profile.age,
                            heightCm = profile.heightCm,
                            weightKg = profile.weightKg,
                            goal = tempGoal,
                            foodPreference = tempFood
                        )
                        showGoalDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FitPrimaryGreen)
                ) {
                    Text(if (isMr) "अपडेट करा" else "Update")
                }
            },
            dismissButton = {
                TextButton(onClick = { showGoalDialog = false }) {
                    Text(if (isMr) "रद्द करा" else "Cancel")
                }
            }
        )
    }

    // Privacy & Security Dialog
    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = {
                Text(text = if (isMr) "गोपनीयता व सुरक्षा" else "Privacy & Data Rights", fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = if (isMr) "FitMarathi तुमची आरोग्य माहिती आणि फोटो पूर्णपणे सुरक्षित ठेवते. आम्ही कोणतीही संवेदनशील माहिती विकत नाही किंवा अनधिकृतपणे सामायिक करत नाही." else "FitMarathi ensures complete privacy of your logged data and photos. Data is processed responsibly.",
                        fontSize = 13.sp,
                        color = FitTextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedButton(
                        onClick = {
                            Toast.makeText(context, if (isMr) "डेटा सुरक्षितपणे एक्सपोर्ट झाला." else "Health data exported successfully.", Toast.LENGTH_SHORT).show()
                            showPrivacyDialog = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isMr) "माझा डेटा एक्सपोर्ट करा (Export)" else "Export My Data")
                    }
                    Button(
                        onClick = {
                            Toast.makeText(context, if (isMr) "सर्व फोटो व माहिती हटवण्यात आली." else "All stored photos deleted.", Toast.LENGTH_SHORT).show()
                            showPrivacyDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = FitDanger),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isMr) "फोटो व डेटा हटवा (Delete Data)" else "Delete Photos & Health Logs")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyDialog = false }) {
                    Text(if (isMr) "बंद करा" else "Close")
                }
            }
        )
    }
}

@Composable
private fun ProfileMetricColumn(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = FitDeepGreen)
        Text(text = label, fontSize = 11.sp, color = FitTeal)
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    trailingText: String? = null,
    textColor: Color = FitTextPrimary,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(icon, contentDescription = null, tint = textColor, modifier = Modifier.size(22.dp))
            Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = textColor)
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (trailingText != null) {
                Text(text = trailingText, fontSize = 12.sp, color = FitTeal, fontWeight = FontWeight.Bold)
            }
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = FitTextSecondary, modifier = Modifier.size(18.dp))
        }
    }
}
