package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*

data class MotivationalQuote(
    val quoteMr: String,
    val quoteEn: String,
    val authorMr: String,
    val authorEn: String
)

private val dailyMotivationalQuotes = listOf(
    MotivationalQuote(
        quoteMr = "“शरीर हेच आपले पहिले मंदिर आहे — रोज ३० मिनिटे व्यायाम व पुरेसे पाणी हीच खरी संपत्ती!”",
        quoteEn = "“Your body is your first home — 30 minutes of movement and steady hydration are your greatest wealth.”",
        authorMr = "FitMarathi दैनिक प्रेरणा",
        authorEn = "FitMarathi Daily Motivation"
    ),
    MotivationalQuote(
        quoteMr = "“आजचे छोटे पाऊल उद्याचे मोठे यश घडवते. सातत्य ठेवा, तंदुरुस्त राहा!”",
        quoteEn = "“Small daily steps create extraordinary results. Stay consistent, stay strong!”",
        authorMr = "सकाळचा सुविचार",
        authorEn = "Morning Fitness Mantra"
    ),
    MotivationalQuote(
        quoteMr = "“ज्वारीची भाकरी, सकस आहार आणि दररोजचे सूर्यनमस्कार — निरोगी आयुष्याचा खरा मंत्र!”",
        quoteEn = "“Wholesome traditional meals and daily Surya Namaskar — the timeless formula for lifelong vitality.”",
        authorMr = "आरोग्य विचार",
        authorEn = "Wellness Wisdom"
    ),
    MotivationalQuote(
        quoteMr = "“थकवा तात्पुरता असतो, पण व्यायामातून मिळणारी ताकद कायमची असते.”",
        quoteEn = "“Fatigue fades in minutes, but the strength you build today stays with you for life.”",
        authorMr = "प्रेरणा मंत्र",
        authorEn = "Strength Mindset"
    )
)

/**
 * Dashboard card displaying a daily Marathi & English motivational quote paired with
 * a warm glowing Lottie sunrise animation and tap-to-refresh quote cycling.
 */
@Composable
fun MotivationalQuoteCard(
    isMarathi: Boolean,
    modifier: Modifier = Modifier
) {
    var quoteIndex by remember { mutableIntStateOf(0) }
    val activeQuote = dailyMotivationalQuotes[quoteIndex]

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(Color(0xFFFFF7ED), Color(0xFFFEF3C7))
                )
            )
            .padding(16.dp)
            .testTag("home_motivational_quote_card")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FitLottieAnimation(
                rawResId = R.raw.lottie_motivational_quote,
                contentDescriptionText = "Animated motivational quote glow",
                size = 56.dp,
                modifier = Modifier.testTag("motivational_quote_lottie")
            )

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isMarathi) activeQuote.authorMr else activeQuote.authorEn,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = FitSaffron
                    )
                    IconButton(
                        onClick = { quoteIndex = (quoteIndex + 1) % dailyMotivationalQuotes.size },
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("next_motivational_quote_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = if (isMarathi) "पुढील सुविचार" else "Next Quote",
                            tint = FitSaffron,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Text(
                    text = if (isMarathi) activeQuote.quoteMr else activeQuote.quoteEn,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontStyle = FontStyle.Italic,
                    color = FitDeepGreen,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

/**
 * Celebratory dialog with animated Lottie check & sparks when an exercise or routine is completed.
 */
@Composable
fun ExerciseCompletionCelebrationDialog(
    titleMr: String,
    titleEn: String,
    subtitleMr: String,
    subtitleEn: String,
    isMarathi: Boolean,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                FitLottieAnimation(
                    rawResId = R.raw.lottie_exercise_complete,
                    contentDescriptionText = "Exercise completed celebration animation",
                    size = 96.dp,
                    modifier = Modifier.testTag("exercise_completion_lottie")
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (isMarathi) titleMr else titleEn,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = FitDeepGreen,
                    textAlign = TextAlign.Center
                )
            }
        },
        text = {
            Text(
                text = if (isMarathi) subtitleMr else subtitleEn,
                fontSize = 13.sp,
                color = FitTextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = FitPrimaryGreen),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dismiss_celebration_btn")
            ) {
                Text(
                    text = if (isMarathi) "शाब्बास! पुढे चला" else "Awesome! Keep Going",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    )
}
