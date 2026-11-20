package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.data.repository.FitMarathiRepository
import com.example.ui.components.DisclaimerBox
import com.example.ui.components.FitCard
import com.example.ui.components.FitMarathiLogo
import com.example.ui.theme.*

@Composable
fun OnboardingScreen(
    repository: FitMarathiRepository,
    onOnboardingComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 0: Splash, 1: Welcome/Intro, 2: Language, 3: Login, 4: Basic Info (Wizard 1), 5: Body Details (Wizard 2), 6: Photos (Wizard 3), 7: Food & Health (Wizard 4)
    var step by remember { mutableIntStateOf(0) }
    val profile by repository.userProfile.collectAsState()
    val isMr = profile.language == Language.MARATHI

    // Form inputs state
    var name by remember { mutableStateOf(profile.name) }
    var email by remember { mutableStateOf(profile.email) }
    var phone by remember { mutableStateOf(profile.phone) }
    var birthDate by remember { mutableStateOf(profile.birthDate) }
    var weightText by remember { mutableStateOf(profile.weightKg.toInt().toString()) }
    var heightText by remember { mutableStateOf(profile.heightCm.toString()) }
    var selectedGender by remember { mutableStateOf("पुरुष") }
    var selectedGoal by remember { mutableStateOf(profile.goal) }
    var selectedFoodPref by remember { mutableStateOf(profile.foodPreference) }
    var consentGranted by remember { mutableStateOf(true) }

    var frontPhotoUploaded by remember { mutableStateOf(false) }
    var sidePhotoUploaded by remember { mutableStateOf(false) }
    var facePhotoUploaded by remember { mutableStateOf(false) }

    val conditionsList = remember {
        mutableStateListOf(
            "डायबिटीस (Diabetes)" to false,
            "हाय ब्लड प्रेशर (High BP)" to false,
            "थायरॉईड (Thyroid)" to false,
            "केसांचा त्रास (Hair Issues)" to false,
            "इतर (Other)" to false
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(if (step == 0) Color(0xFF042017) else FitBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header for Wizard Steps (Steps 4 to 7: Panels 5 to 8)
            if (step in 4..7) {
                Column(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { step-- },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = FitTextPrimary)
                        }
                        Text(
                            text = if (isMr) "टप्पा ${step - 3} / ४" else "Step ${step - 3} of 4",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = FitPrimaryGreen
                        )
                        TextButton(onClick = {
                            repository.setAuthentication(true)
                            onOnboardingComplete()
                        }) {
                            Text(if (isMr) "वगळा" else "Skip", fontSize = 13.sp, color = FitTeal)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // 4-Step Stepper matching Storyboard 2 panels 5-8
                    WizardStepper(activeStep = step - 3, totalSteps = 4)
                }
            } else if (step in 1..3) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { if (step > 0) step-- },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = FitTextPrimary)
                    }
                    Text(
                        text = "FitMarathi",
                        fontWeight = FontWeight.Bold,
                        color = FitPrimaryGreen,
                        fontSize = 16.sp
                    )
                    TextButton(onClick = {
                        repository.setAuthentication(true)
                        onOnboardingComplete()
                    }) {
                        Text(if (isMr) "वगळा" else "Skip", fontSize = 13.sp, color = FitTeal)
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Main Step Content
            AnimatedContent(
                targetState = step,
                label = "onboarding_steps_content"
            ) { targetStep ->
                when (targetStep) {
                    // Panel 1: Splash Screen
                    0 -> {
                        SplashScreenContent(
                            onStart = { step = 1 },
                            isMr = isMr
                        )
                    }

                    // Panel 2: Welcome / Intro
                    1 -> {
                        WelcomeScreenContent(
                            isMr = isMr,
                            onGetStarted = { step = 2 },
                            onLogin = { step = 3 }
                        )
                    }

                    // Panel 3: Language Selection
                    2 -> {
                        LanguageSelectionContent(
                            selectedLanguage = profile.language,
                            onSelectLanguage = { repository.updateLanguage(it) },
                            onNext = { step = 3 }
                        )
                    }

                    // Panel 4: Login (Google)
                    3 -> {
                        LoginScreenContent(
                            isMr = isMr,
                            onGoogleLogin = {
                                repository.setAuthentication(true)
                                step = 4
                            },
                            onOtherLogin = {
                                repository.setAuthentication(true)
                                step = 4
                            },
                            onDemoLogin = {
                                repository.setAuthentication(true)
                                onOnboardingComplete()
                            },
                            onSignUp = { step = 4 }
                        )
                    }

                    // Panel 5: Create Account / Basic Info (Wizard 1/4)
                    4 -> {
                        BasicInfoContent(
                            isMr = isMr,
                            name = name,
                            onNameChange = { name = it },
                            email = email,
                            onEmailChange = { email = it },
                            phone = phone,
                            onPhoneChange = { phone = it },
                            birthDate = birthDate,
                            onBirthDateChange = { birthDate = it }
                        )
                    }

                    // Panel 6: Body Details (Wizard 2/4)
                    5 -> {
                        BodyDetailsContent(
                            isMr = isMr,
                            weightText = weightText,
                            onWeightChange = { weightText = it },
                            heightText = heightText,
                            onHeightChange = { heightText = it },
                            selectedGender = selectedGender,
                            onGenderChange = { selectedGender = it },
                            selectedGoal = selectedGoal,
                            onGoalChange = { selectedGoal = it }
                        )
                    }

                    // Panel 7: Photo Upload (Wizard 3/4)
                    6 -> {
                        PhotoUploadContent(
                            isMr = isMr,
                            frontUploaded = frontPhotoUploaded,
                            onToggleFront = { frontPhotoUploaded = !frontPhotoUploaded },
                            sideUploaded = sidePhotoUploaded,
                            onToggleSide = { sidePhotoUploaded = !sidePhotoUploaded },
                            faceUploaded = facePhotoUploaded,
                            onToggleFace = { facePhotoUploaded = !facePhotoUploaded },
                            consentGranted = consentGranted,
                            onConsentChange = { consentGranted = it }
                        )
                    }

                    // Panel 8: Food Habit & Health Info (Wizard 4/4)
                    7 -> {
                        FoodAndHealthContent(
                            isMr = isMr,
                            selectedFoodPref = selectedFoodPref,
                            onFoodPrefChange = { selectedFoodPref = it },
                            conditionsList = conditionsList
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Bottom Action Button (Steps 1 to 7)
            if (step > 0) {
                Button(
                    onClick = {
                        if (step < 7) {
                            step++
                        } else {
                            // Finish and save profile
                            val weight = weightText.toFloatOrNull() ?: 72.0f
                            val height = heightText.toIntOrNull() ?: 170
                            repository.updateProfile(
                                name = name.ifBlank { "Rohit Patil" },
                                email = email.ifBlank { "rohit@gmail.com" },
                                phone = phone.ifBlank { "+91 98765 43210" },
                                age = 25,
                                heightCm = height,
                                weightKg = weight,
                                goal = selectedGoal,
                                foodPreference = selectedFoodPref
                            )
                            repository.setAuthentication(true)
                            onOnboardingComplete()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("onboarding_primary_action_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = FitPrimaryGreen),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = when (step) {
                            1 -> if (isMr) "सुरू करा (Get Started)" else "Get Started"
                            2 -> if (isMr) "पुढे जा (Next)" else "Next"
                            3 -> if (isMr) "साइन इन करा (Sign In)" else "Sign In"
                            7 -> if (isMr) "डॅशबोर्ड सुरू करा (Launch Dashboard)" else "Launch Dashboard"
                            else -> if (isMr) "पुढे जा (Next)" else "Next"
                        },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// Panel 1: Splash Screen
// -------------------------------------------------------------
@Composable
private fun SplashScreenContent(
    onStart: () -> Unit,
    isMr: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        FitMarathiLogo(size = 90.dp, showText = true, textColor = Color.White)

        Spacer(modifier = Modifier.height(30.dp))

        // Silhouette athlete graphic on mountain peak
        Box(
            modifier = Modifier
                .size(220.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(FitSaffron.copy(alpha = 0.8f), FitGold.copy(alpha = 0.4f), Color(0xFF0F5A41).copy(alpha = 0.1f))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                // Mountain slope
                drawCircle(
                    color = Color(0xFF0A3C2B),
                    radius = w * 0.45f,
                    center = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.85f)
                )
            }
            Icon(
                imageVector = Icons.Default.DirectionsRun,
                contentDescription = "FitMarathi Runner",
                tint = Color.White,
                modifier = Modifier.size(96.dp)
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "FitMarathi",
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White
        )
        Text(
            text = "तंदुरुस्त शरीर, आनंदी जीवन",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = FitGold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Your Health Partner In Marathi & English",
            fontSize = 13.sp,
            color = Color(0xFFD1FAE5)
        )

        Spacer(modifier = Modifier.height(36.dp))

        Button(
            onClick = onStart,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("splash_start_btn"),
            colors = ButtonDefaults.buttonColors(containerColor = FitEmerald),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(
                text = if (isMr) "सुरू करा (Get Started)" else "Get Started",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.White)
        }
    }
}

// -------------------------------------------------------------
// Panel 2: Welcome / Intro Screen
// -------------------------------------------------------------
@Composable
private fun WelcomeScreenContent(
    isMr: Boolean,
    onGetStarted: () -> Unit,
    onLogin: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FitMarathiLogo(size = 54.dp, showText = true)

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "आजपासून तुमच्या फिटनेसचा नवा प्रवास!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = FitDeepGreen,
            textAlign = TextAlign.Center,
            lineHeight = 32.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Healthy Body • Better Mind • Longer Life",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = FitTeal,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Center visual avatar card
        Box(
            modifier = Modifier
                .size(190.dp)
                .clip(CircleShape)
                .background(
                    Brush.verticalGradient(
                        listOf(FitLightTeal, Color(0xFFC8E6C9))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.FitnessCenter,
                contentDescription = "Fitness",
                tint = FitPrimaryGreen,
                modifier = Modifier.size(80.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 3 carousel dots
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Box(modifier = Modifier.size(20.dp, 6.dp).clip(CircleShape).background(FitPrimaryGreen))
            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(FitBorder))
            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(FitBorder))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = FitLightTeal,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "🌱 अस्सल महाराष्ट्रीयन आहार योजना",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = FitDeepGreen
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "💪 ६०+ वजन व क्षमतेनुसार कसरती व योगा",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = FitDeepGreen
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "🤖 २४/७ मराठी व इंग्रजी Gemini AI कोच",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = FitDeepGreen
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isMr) "आधीच खाते आहे का?" else "Already have an account? ",
                fontSize = 13.sp,
                color = FitTextSecondary
            )
            TextButton(onClick = onLogin) {
                Text(
                    text = if (isMr) "लॉगिन करा (Login)" else "Login",
                    fontWeight = FontWeight.Bold,
                    color = FitPrimaryGreen,
                    fontSize = 13.sp
                )
            }
        }
    }
}

// -------------------------------------------------------------
// Panel 3: Language Selection Screen
// -------------------------------------------------------------
@Composable
private fun LanguageSelectionContent(
    selectedLanguage: Language,
    onSelectLanguage: (Language) -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "भाषा निवडा",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = FitTextPrimary
        )
        Text(
            text = "Choose Language",
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = FitTeal
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "तुम्हाला कोणती भाषा वापरायची आहे?\nWhich language would you like to use?",
            fontSize = 13.sp,
            color = FitTextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(28.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Marathi Card (Saffron 'म' circle)
            LanguageCardItem(
                title = "मराठी",
                subtitle = "Maharashtra Native",
                symbol = "म",
                badgeColor = FitSaffron,
                isSelected = selectedLanguage == Language.MARATHI,
                modifier = Modifier.weight(1f),
                onClick = { onSelectLanguage(Language.MARATHI) }
            )

            // English Card (Blue 'A' circle)
            LanguageCardItem(
                title = "English",
                subtitle = "Global Standard",
                symbol = "A",
                badgeColor = Color(0xFF1976D2),
                isSelected = selectedLanguage == Language.ENGLISH,
                modifier = Modifier.weight(1f),
                onClick = { onSelectLanguage(Language.ENGLISH) }
            )
        }
    }
}

@Composable
private fun LanguageCardItem(
    title: String,
    subtitle: String,
    symbol: String,
    badgeColor: Color,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .border(
                width = if (isSelected) 2.5.dp else 1.dp,
                color = if (isSelected) FitPrimaryGreen else FitBorder,
                shape = RoundedCornerShape(16.dp)
            ),
        color = if (isSelected) FitLightTeal else Color.White,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 24.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(badgeColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = symbol,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = FitTextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = FitTextSecondary
            )
        }
    }
}

// -------------------------------------------------------------
// Panel 4: Login Screen
// -------------------------------------------------------------
@Composable
private fun LoginScreenContent(
    isMr: Boolean,
    onGoogleLogin: () -> Unit,
    onOtherLogin: () -> Unit,
    onDemoLogin: () -> Unit,
    onSignUp: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FitMarathiLogo(size = 64.dp, showText = true)

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = if (isMr) "लॉगिन करा" else "Login to Continue",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = FitTextPrimary
        )
        Text(
            text = if (isMr) "Sign in with your Google account" else "Your Health, Our Priority",
            fontSize = 13.sp,
            color = FitTextSecondary
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Continue with Google Button
        OutlinedButton(
            onClick = onGoogleLogin,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("google_login_btn"),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.2.dp, FitBorder)
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEA4335)),
                contentAlignment = Alignment.Center
            ) {
                Text("G", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = if (isMr) "Continue with Google" else "Continue with Google",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = FitTextPrimary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "or Other Login Options",
            fontSize = 12.sp,
            color = FitTextSecondary
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Row with Phone, Email, SMS
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onOtherLogin,
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .border(1.dp, FitBorder, CircleShape)
                    .background(Color.White)
            ) {
                Icon(Icons.Default.Phone, contentDescription = "Phone", tint = FitTeal)
            }

            IconButton(
                onClick = onOtherLogin,
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .border(1.dp, FitBorder, CircleShape)
                    .background(Color.White)
            ) {
                Icon(Icons.Default.Email, contentDescription = "Email", tint = FitPrimaryGreen)
            }

            IconButton(
                onClick = onOtherLogin,
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .border(1.dp, FitBorder, CircleShape)
                    .background(Color.White)
            ) {
                Icon(Icons.Default.Chat, contentDescription = "Message", tint = FitSaffron)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Demo Quick Access
        OutlinedButton(
            onClick = onDemoLogin,
            modifier = Modifier.fillMaxWidth().height(46.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = FitPrimaryGreen)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isMr) "डेमो मोड (Explore as Demo User)" else "Explore as Demo User",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = FitDeepGreen
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = if (isMr) "खाते नाही का? " else "Don't have an account? ",
                fontSize = 13.sp,
                color = FitTextSecondary
            )
            Text(
                text = if (isMr) "Sign Up" else "Sign Up",
                fontWeight = FontWeight.Bold,
                color = FitPrimaryGreen,
                fontSize = 13.sp,
                modifier = Modifier.clickable { onSignUp() }
            )
        }
    }
}

// -------------------------------------------------------------
// Panel 5: Basic Info (Wizard 1/4)
// -------------------------------------------------------------
@Composable
private fun BasicInfoContent(
    isMr: Boolean,
    name: String,
    onNameChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    phone: String,
    onPhoneChange: (String) -> Unit,
    birthDate: String,
    onBirthDateChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "तुमची मूलभूत माहिती",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = FitTextPrimary
        )
        Text(
            text = "(Your Basic Information)",
            fontSize = 13.sp,
            color = FitTeal,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(18.dp))

        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text(if (isMr) "पूर्ण नाव (Full Name)" else "Full Name") },
            placeholder = { Text("उदा. रोहित पाटील") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = FitPrimaryGreen) },
            modifier = Modifier.fillMaxWidth().testTag("name_input"),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text(if (isMr) "ईमेल (Email)" else "Email Address") },
            placeholder = { Text("example@gmail.com") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = FitPrimaryGreen) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = onPhoneChange,
            label = { Text(if (isMr) "मोबाईल नंबर (Mobile Number)" else "Mobile Number") },
            placeholder = { Text("+91 98765 43210") },
            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = FitPrimaryGreen) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = birthDate,
            onValueChange = onBirthDateChange,
            label = { Text(if (isMr) "जन्मतारीख (Date of Birth)" else "Date of Birth") },
            placeholder = { Text("DD/MM/YYYY") },
            leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null, tint = FitPrimaryGreen) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )
    }
}

// -------------------------------------------------------------
// Panel 6: Body Details (Wizard 2/4)
// -------------------------------------------------------------
@Composable
private fun BodyDetailsContent(
    isMr: Boolean,
    weightText: String,
    onWeightChange: (String) -> Unit,
    heightText: String,
    onHeightChange: (String) -> Unit,
    selectedGender: String,
    onGenderChange: (String) -> Unit,
    selectedGoal: FitnessGoal,
    onGoalChange: (FitnessGoal) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "शारीरिक माहिती",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = FitTextPrimary
        )
        Text(
            text = "(Body Details)",
            fontSize = 13.sp,
            color = FitTeal,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = weightText,
                onValueChange = onWeightChange,
                label = { Text(if (isMr) "वजन (Weight)" else "Weight") },
                placeholder = { Text("उदा. 70 kg") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f).testTag("weight_input"),
                shape = RoundedCornerShape(12.dp)
            )
            OutlinedTextField(
                value = heightText,
                onValueChange = onHeightChange,
                label = { Text(if (isMr) "उंची (Height)" else "Height") },
                placeholder = { Text("उदा. 170 cm") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f).testTag("height_input"),
                shape = RoundedCornerShape(12.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (isMr) "लिंग (Gender):" else "Gender:",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("पुरुष", "महिला", "इतर").forEach { g ->
                val isSelected = selectedGender.startsWith(g)
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onGenderChange(g) }
                        .border(
                            1.dp,
                            if (isSelected) FitPrimaryGreen else FitBorder,
                            RoundedCornerShape(10.dp)
                        ),
                    color = if (isSelected) FitLightTeal else Color.White,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = when (g) {
                            "पुरुष" -> "पुरुष (Male)"
                            "महिला" -> "महिला (Female)"
                            else -> "इतर (Other)"
                        },
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) FitPrimaryGreen else FitTextPrimary,
                        modifier = Modifier.padding(vertical = 10.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = if (isMr) "ध्येय (Goal):" else "Fitness Goal:",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        FitnessGoal.values().forEach { goal ->
            val isSelected = goal == selectedGoal
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onGoalChange(goal) }
                    .border(
                        1.dp,
                        if (isSelected) FitPrimaryGreen else FitBorder,
                        RoundedCornerShape(12.dp)
                    ),
                color = if (isSelected) FitLightTeal else Color.White,
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = { onGoalChange(goal) },
                        colors = RadioButtonDefaults.colors(selectedColor = FitPrimaryGreen)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isMr) goal.marathiLabel else goal.englishLabel,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// Panel 7: Photo Upload (Wizard 3/4)
// -------------------------------------------------------------
@Composable
private fun PhotoUploadContent(
    isMr: Boolean,
    frontUploaded: Boolean,
    onToggleFront: () -> Unit,
    sideUploaded: Boolean,
    onToggleSide: () -> Unit,
    faceUploaded: Boolean,
    onToggleFace: () -> Unit,
    consentGranted: Boolean,
    onConsentChange: (Boolean) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "तुमचे फोटो अपलोड करा",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = FitTextPrimary
        )
        Text(
            text = "(Upload Photos)",
            fontSize = 13.sp,
            color = FitTeal,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "AI साठी तुमचे फोटो आवश्यक आहेत",
            fontSize = 12.sp,
            color = FitTextSecondary
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SilhouetteUploadBox(
                title = if (isMr) "समोरील फोटो" else "Front Photo",
                subtitle = "(समोरून फोटो)",
                isUploaded = frontUploaded,
                icon = Icons.Default.Person,
                modifier = Modifier.weight(1f),
                onClick = onToggleFront
            )
            SilhouetteUploadBox(
                title = if (isMr) "बाजूचा फोटो" else "Side Photo",
                subtitle = "(बाजूचा फोटो)",
                isUploaded = sideUploaded,
                icon = Icons.Default.Accessibility,
                modifier = Modifier.weight(1f),
                onClick = onToggleSide
            )
            SilhouetteUploadBox(
                title = if (isMr) "चेहरा फोटो" else "Face Photo",
                subtitle = "(चेहरा जवळचा)",
                isUploaded = faceUploaded,
                icon = Icons.Default.Face,
                modifier = Modifier.weight(1f),
                onClick = onToggleFace
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = consentGranted,
                onCheckedChange = onConsentChange,
                colors = CheckboxDefaults.colors(checkedColor = FitPrimaryGreen)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isMr) "माहिती व फोटोंचा गोपनीय वापर करण्यास माझी संमती आहे." else "I consent to private on-device processing of my fitness photos.",
                fontSize = 12.sp,
                color = FitTextSecondary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        DisclaimerBox(
            textEn = "Photos cannot reliably diagnose medical conditions. They are exclusively used for progress tracking and hairstyle recommendations.",
            textMr = "फोटोंवरून आजार किंवा डायबिटीसचे निदान करता येत नाही. तुमची माहिती पूर्णतः सुरक्षित आहे.",
            isMarathi = isMr
        )
    }
}

@Composable
private fun SilhouetteUploadBox(
    title: String,
    subtitle: String,
    isUploaded: Boolean,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .border(
                1.5.dp,
                if (isUploaded) FitEmerald else FitBorder,
                RoundedCornerShape(14.dp)
            ),
        color = if (isUploaded) Color(0xFFE8F5E9) else Color.White,
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(if (isUploaded) FitEmerald.copy(alpha = 0.2f) else FitSurfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isUploaded) Icons.Default.CheckCircle else icon,
                    contentDescription = title,
                    tint = if (isUploaded) FitEmerald else FitTextSecondary,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = FitTextSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isUploaded) "Uploaded ✓" else "+ Upload",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isUploaded) FitEmerald else FitPrimaryGreen
            )
        }
    }
}

// -------------------------------------------------------------
// Panel 8: Food Habit & Health Info (Wizard 4/4)
// -------------------------------------------------------------
@Composable
private fun FoodAndHealthContent(
    isMr: Boolean,
    selectedFoodPref: FoodPreference,
    onFoodPrefChange: (FoodPreference) -> Unit,
    conditionsList: MutableList<Pair<String, Boolean>>
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "आहार व आरोग्य माहिती",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = FitTextPrimary
        )
        Text(
            text = "(Food & Health Info)",
            fontSize = 13.sp,
            color = FitTeal,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = if (isMr) "तुम्ही शाकाहारी, मांसाहारी की दोन्ही?" else "Food Preference (Veg, Non-Veg, Both):",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        listOf(
            FoodPreference.VEGETARIAN to "🌱 शाकाहारी (Vegetarian)",
            FoodPreference.NON_VEGETARIAN to "🍗 मांसाहारी (Non-Vegetarian)",
            FoodPreference.BOTH to "🥗 दोन्ही (Both)"
        ).forEach { (pref, label) ->
            val isSelected = pref == selectedFoodPref
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onFoodPrefChange(pref) }
                    .border(
                        1.dp,
                        if (isSelected) FitPrimaryGreen else FitBorder,
                        RoundedCornerShape(12.dp)
                    ),
                color = if (isSelected) FitLightTeal else Color.White,
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = { onFoodPrefChange(pref) },
                        colors = RadioButtonDefaults.colors(selectedColor = FitPrimaryGreen)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = label,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 14.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (isMr) "तुम्हाला काही आजार आहेत का?" else "Any existing health conditions?",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(6.dp))

        conditionsList.forEachIndexed { index, (cond, checked) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        conditionsList[index] = cond to !checked
                    }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = checked,
                    onCheckedChange = { conditionsList[index] = cond to it },
                    colors = CheckboxDefaults.colors(checkedColor = FitPrimaryGreen)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = cond, fontSize = 13.sp)
            }
        }
    }
}

// -------------------------------------------------------------
// Wizard Stepper Bar (Panels 5 to 8)
// -------------------------------------------------------------
@Composable
private fun WizardStepper(
    activeStep: Int, // 1 to 4
    totalSteps: Int = 4,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        for (i in 1..totalSteps) {
            val isCurrent = i == activeStep
            val isCompleted = i < activeStep

            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isCompleted || isCurrent -> FitPrimaryGreen
                            else -> FitBorder
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                } else {
                    Text(
                        text = "$i",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isCurrent) Color.White else FitTextSecondary
                    )
                }
            }

            if (i < totalSteps) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(3.dp)
                        .background(
                            if (i < activeStep) FitPrimaryGreen else FitBorder
                        )
                )
            }
        }
    }
}
