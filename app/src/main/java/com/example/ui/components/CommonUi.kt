package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Language
import com.example.ui.theme.*

@Composable
fun FitMarathiLogo(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    showText: Boolean = true,
    textColor: Color = FitPrimaryGreen
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(FitDeepGreen, Color(0xFF042017))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(size * 0.7f)) {
                val w = this.size.width
                val h = this.size.height
                // Draw decorative wellness emblem
                drawCircle(
                    color = FitGold,
                    radius = w * 0.18f,
                    center = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.35f)
                )
                drawArc(
                    color = FitEmerald,
                    startAngle = 135f,
                    sweepAngle = 90f,
                    useCenter = true,
                    topLeft = androidx.compose.ui.geometry.Offset(w * 0.1f, h * 0.35f),
                    size = androidx.compose.ui.geometry.Size(w * 0.45f, h * 0.45f)
                )
                drawArc(
                    color = FitTeal,
                    startAngle = 315f,
                    sweepAngle = 90f,
                    useCenter = true,
                    topLeft = androidx.compose.ui.geometry.Offset(w * 0.45f, h * 0.35f),
                    size = androidx.compose.ui.geometry.Size(w * 0.45f, h * 0.45f)
                )
            }
        }
        if (showText) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Fit",
                        fontSize = (size.value * 0.45f).sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = textColor
                    )
                    Text(
                        text = "Marathi",
                        fontSize = (size.value * 0.45f).sp,
                        fontWeight = FontWeight.Bold,
                        color = FitSaffron
                    )
                }
                Text(
                    text = "तंदुरुस्त शरीर, आनंदी जीवन",
                    fontSize = 11.sp,
                    color = FitTextSecondary
                )
            }
        }
    }
}

@Composable
fun FitCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    borderColor: Color = FitBorder.copy(alpha = 0.6f),
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, borderColor, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

@Composable
fun CircularProgressMeter(
    progress: Float,
    targetValueText: String,
    label: String,
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
    strokeWidth: Dp = 10.dp,
    progressColor: Color = FitEmerald,
    trackColor: Color = FitLightTeal
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        label = "progressAnim"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            // Track
            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = stroke
            )
            // Progress
            drawArc(
                color = progressColor,
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress,
                useCenter = false,
                style = stroke
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = targetValueText,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = FitTextPrimary
            )
            Text(
                text = label,
                fontSize = 11.sp,
                color = FitTextSecondary
            )
        }
    }
}

@Composable
fun PillTabRow(
    tabs: List<String>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    activeColor: Color = FitPrimaryGreen,
    inactiveColor: Color = FitSurfaceVariant
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(inactiveColor)
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        tabs.forEachIndexed { index, title ->
            val isSelected = index == selectedIndex
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isSelected) activeColor else Color.Transparent)
                    .clickable { onTabSelected(index) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) Color.White else FitTextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun LanguageSwitchButton(
    currentLanguage: Language,
    onToggle: (Language) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable {
                val next = if (currentLanguage == Language.MARATHI) Language.ENGLISH else Language.MARATHI
                onToggle(next)
            }
            .testTag("language_switch_btn"),
        color = FitLightTeal,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = if (currentLanguage == Language.MARATHI) "म" else "EN",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = FitPrimaryGreen
            )
            Text(
                text = if (currentLanguage == Language.MARATHI) "मराठी" else "English",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = FitDeepGreen
            )
        }
    }
}

@Composable
fun DisclaimerBox(
    textEn: String,
    textMr: String,
    isMarathi: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, FitWarning.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
        color = FitWarning.copy(alpha = 0.08f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Medical Disclaimer",
                tint = FitWarning,
                modifier = Modifier.size(20.dp)
            )
            Column {
                Text(
                    text = if (isMarathi) "आरोग्य व वैद्यकीय सूचना:" else "Health & Medical Disclaimer:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = FitWarning
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (isMarathi) textMr else textEn,
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    color = FitTextSecondary
                )
            }
        }
    }
}
