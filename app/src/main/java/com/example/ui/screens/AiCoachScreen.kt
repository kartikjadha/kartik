package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Language
import com.example.data.repository.FitMarathiRepository
import com.example.data.service.AiCoachService
import com.example.data.service.ChatMessage
import com.example.data.service.MessageSender
import com.example.ui.components.DisclaimerBox
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun AiCoachScreen(
    repository: FitMarathiRepository,
    modifier: Modifier = Modifier
) {
    val profile by repository.userProfile.collectAsState()
    val isMr = profile.language == Language.MARATHI
    val scope = rememberCoroutineScope()
    val aiCoachService = remember { AiCoachService() }
    val listState = rememberLazyListState()

    var inputPrompt by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var showApiDialog by remember { mutableStateOf(false) }

    val initialCoachGreeting = remember(isMr, profile.name) {
        if (isMr) {
            """
            नमस्कार ${profile.name}! मी तुमचा FitMarathi AI Coach आहे. 🌿
            
            तुमच्या आरोग्याबद्दल किंवा आहाराबद्दल काहीही विचारा:
            • आज काय खायला पाहिजे?
            • भाकरी खावी की पोळी?
            • वजन कमी करण्यासाठी कोणता व्यायाम करू?
            • शरीरात पाणी आणि ऊर्जा कशी टिकवून ठेवावी?
            """.trimIndent()
        } else {
            """
            Hello ${profile.name}! I am your personal FitMarathi AI Coach. 🌿
            
            Feel free to ask me anything about your wellness journey:
            • What should I eat today for my goals?
            • Can I eat jowar bhakri, poli, rice, or eggs?
            • Which 30-minute home workout is best for me?
            • How can I build healthy daily habits?
            """.trimIndent()
        }
    }

    val messages = remember {
        mutableStateListOf(
            ChatMessage(
                sender = MessageSender.COACH,
                text = initialCoachGreeting,
                isDemoResponse = !aiCoachService.hasValidApiKey()
            )
        )
    }

    val quickQuestions = remember(isMr) {
        if (isMr) {
            listOf(
                "आज काय खायला पाहिजे?",
                "भाकरी खावी की पोळी?",
                "३० मिनिटे कसरत प्लॅन",
                "वजन कसे कमी करावे?",
                "उसळ व डाळीचे फायदे"
            )
        } else {
            listOf(
                "What should I eat today?",
                "Can I eat bhakri & rice?",
                "Today's 30 Min Workout",
                "Tips for safe fat loss",
                "Sprouted Matki benefits"
            )
        }
    }

    fun sendMessage(query: String) {
        if (query.isBlank() || isLoading) return
        val userMsg = ChatMessage(sender = MessageSender.USER, text = query.trim())
        messages.add(userMsg)
        inputPrompt = ""
        isLoading = true

        scope.launch {
            listState.animateScrollToItem(messages.size - 1)
            val reply = aiCoachService.getCoachResponse(query, profile, messages)
            messages.add(reply)
            isLoading = false
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Surface(
                color = FitPrimaryGreen,
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(FitEmerald),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SmartToy,
                                contentDescription = "AI Coach",
                                tint = Color.White
                            )
                        }
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "FitMarathi AI Coach",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF34D399))
                                )
                            }
                            Text(
                                text = if (aiCoachService.hasValidApiKey()) "Gemini 2.5 Live" else "Intelligent Coach (Offline/Demo Mode)",
                                fontSize = 11.sp,
                                color = Color(0xFFD1FAE5)
                            )
                        }
                    }

                    IconButton(onClick = { showApiDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "API Status",
                            tint = Color.White
                        )
                    }
                }
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .padding(bottom = 75.dp) // space for bottom nav bar
            ) {
                // Quick Questions Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                ) {
                    items(quickQuestions) { question ->
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { sendMessage(question) }
                                .border(1.dp, FitPrimaryGreen.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
                            color = FitLightTeal,
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = question,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = FitDeepGreen,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                // Chat Input Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = inputPrompt,
                        onValueChange = { inputPrompt = it },
                        placeholder = {
                            Text(
                                text = if (isMr) "काहीही विचारा (मराठी किंवा English)..." else "Ask anything in Marathi or English...",
                                fontSize = 13.sp
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("ai_coach_input_field"),
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FitPrimaryGreen,
                            unfocusedBorderColor = FitBorder
                        ),
                        maxLines = 3
                    )

                    IconButton(
                        onClick = { sendMessage(inputPrompt) },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(if (inputPrompt.isNotBlank() && !isLoading) FitPrimaryGreen else FitBorder)
                            .testTag("ai_coach_send_btn"),
                        enabled = inputPrompt.isNotBlank() && !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Send",
                                tint = if (inputPrompt.isNotBlank()) Color.White else FitTextMuted
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .background(FitBackground)
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 12.dp)
        ) {
            // Optional Demo Mode Banner
            if (!aiCoachService.hasValidApiKey()) {
                item {
                    Surface(
                        color = FitLightTeal,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = FitTeal,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = if (isMr) "एआय कोच सक्रिय आहे (डेमो/ऑफलाइन मोड). जेमिनी की जोडण्यासाठी वरील सेटिंग चिन्हास स्पर्श करा." else "AI Coach active in Smart Demo Mode. Tap the gear icon to configure Gemini API Key.",
                                fontSize = 11.sp,
                                color = FitDeepGreen,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }

            items(messages, key = { it.id }) { msg ->
                ChatBubble(message = msg, isMr = isMr)
            }

            if (isLoading) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = FitPrimaryGreen,
                            strokeWidth = 2.dp
                        )
                        Text(
                            text = if (isMr) "FitMarathi एआय कोच उत्तर तयार करत आहे..." else "FitMarathi Coach is thinking...",
                            fontSize = 12.sp,
                            color = FitTextSecondary
                        )
                    }
                }
            }
        }
    }

    if (showApiDialog) {
        AlertDialog(
            onDismissRequest = { showApiDialog = false },
            title = {
                Text(text = "Gemini API Configuration", fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "FitMarathi uses the Google Gemini API to power personalized Maharashtrian nutrition and fitness coaching.",
                        fontSize = 13.sp
                    )
                    Surface(
                        color = FitSurfaceVariant,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "Status: " + if (aiCoachService.hasValidApiKey()) "Connected to Gemini 2.5" else "Running in Smart Offline Demo Mode",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if (aiCoachService.hasValidApiKey()) FitEmerald else FitSaffron
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "To use your live Gemini API key, add GEMINI_API_KEY into the Secrets panel in AI Studio. The app automatically reads it through BuildConfig securely without exposing it in source code.",
                                fontSize = 11.sp,
                                color = FitTextSecondary
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showApiDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = FitPrimaryGreen)
                ) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
private fun ChatBubble(message: ChatMessage, isMr: Boolean) {
    val isUser = message.sender == MessageSender.USER

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(FitPrimaryGreen),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SmartToy,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Surface(
            modifier = Modifier.widthIn(max = 300.dp),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            color = if (isUser) FitPrimaryGreen else Color.White,
            shadowElevation = 1.dp
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = message.text,
                    fontSize = 13.sp,
                    color = if (isUser) Color.White else FitTextPrimary,
                    lineHeight = 18.sp
                )
                if (!isUser && message.isDemoResponse) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "FitMarathi Coach • Maharashtrian Nutrition Engine",
                        fontSize = 10.sp,
                        color = FitTeal,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
