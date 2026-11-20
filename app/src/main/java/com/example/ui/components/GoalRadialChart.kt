package com.example.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

data class RadialGoalRingSpec(
    val labelMr: String,
    val labelEn: String,
    val valueText: String,
    val progress: Float,
    val color: Color,
    val trackColor: Color,
    val icon: ImageVector,
    val testTag: String,
    val onClick: () -> Unit
)

/**
 * Native Jetpack Compose concentric multi-ring radial chart displaying
 * completion percentages for Steps (outer), Water (middle), and Calories (inner).
 */
@Composable
fun MultiRingRadialGoalChart(
    stepsProgress: Float,
    waterProgress: Float,
    caloriesProgress: Float,
    centerPercentText: String,
    centerLabel: String,
    modifier: Modifier = Modifier,
    size: Dp = 132.dp,
    ringWidth: Dp = 9.dp,
    ringGap: Dp = 5.dp
) {
    val animSteps by animateFloatAsState(
        targetValue = stepsProgress.coerceIn(0f, 1f),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "radial_steps_anim"
    )
    val animWater by animateFloatAsState(
        targetValue = waterProgress.coerceIn(0f, 1f),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "radial_water_anim"
    )
    val animCalories by animateFloatAsState(
        targetValue = caloriesProgress.coerceIn(0f, 1f),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "radial_calories_anim"
    )

    val stepsPct = (stepsProgress.coerceIn(0f, 1f) * 100).toInt()
    val waterPct = (waterProgress.coerceIn(0f, 1f) * 100).toInt()
    val caloriesPct = (caloriesProgress.coerceIn(0f, 1f) * 100).toInt()

    Box(
        modifier = modifier
            .size(size)
            .semantics {
                contentDescription =
                    "Daily goals radial chart: Steps $stepsPct%, Water $waterPct%, Calories $caloriesPct%, Overall $centerPercentText"
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokePx = ringWidth.toPx()
            val gapPx = ringGap.toPx()
            val canvasMin = this.size.minDimension
            val centerX = this.size.width / 2f
            val centerY = this.size.height / 2f

            val rings = listOf(
                Triple(animSteps, FitEmerald, Color(0xFFD1FAE5)),
                Triple(animWater, FitTeal, Color(0xFFCCFBF1)),
                Triple(animCalories, FitSaffron, Color(0xFFFFEDD5))
            )

            rings.forEachIndexed { index, (progress, activeColor, trackColor) ->
                val inset = (strokePx / 2f) + index * (strokePx + gapPx)
                val diameter = (canvasMin - inset * 2f).coerceAtLeast(strokePx)
                val topLeft = Offset(
                    x = centerX - diameter / 2f,
                    y = centerY - diameter / 2f
                )
                val arcSize = Size(diameter, diameter)
                val stroke = Stroke(width = strokePx, cap = StrokeCap.Round)

                // Background track ring
                drawArc(
                    color = trackColor,
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = stroke
                )

                // Active progress arc
                if (progress > 0f) {
                    drawArc(
                        color = activeColor,
                        startAngle = -90f,
                        sweepAngle = 360f * progress,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = stroke
                    )
                }
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = centerPercentText,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = FitDeepGreen
            )
            Text(
                text = centerLabel,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = FitTextSecondary
            )
        }
    }
}

/**
 * Health Dashboard card combining the concentric 3-ring radial chart with
 * animated visual progress bars for Steps, Water, and Calories completion percentages.
 */
@Composable
fun DailyGoalsRadialProgressCard(
    steps: Int,
    stepGoal: Int,
    waterLiters: Float,
    waterGoalLiters: Float,
    calories: Int,
    calorieGoal: Int,
    isMarathi: Boolean,
    onStepsClick: () -> Unit,
    onWaterClick: () -> Unit,
    onCaloriesClick: () -> Unit,
    onChartClick: () -> Unit,
    onViewAllGoalsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val stepsProgress = if (stepGoal > 0) (steps.toFloat() / stepGoal).coerceIn(0f, 1f) else 0f
    val waterProgress = if (waterGoalLiters > 0f) (waterLiters / waterGoalLiters).coerceIn(0f, 1f) else 0f
    val caloriesProgress = if (calorieGoal > 0) (calories.toFloat() / calorieGoal).coerceIn(0f, 1f) else 0f
    val overallProgress = ((stepsProgress + waterProgress + caloriesProgress) / 3f).coerceIn(0f, 1f)
    val overallPercent = (overallProgress * 100).toInt()

    val goalSpecs = listOf(
        RadialGoalRingSpec(
            labelMr = "पावले",
            labelEn = "Steps",
            valueText = "$steps / $stepGoal",
            progress = stepsProgress,
            color = FitEmerald,
            trackColor = Color(0xFFD1FAE5),
            icon = Icons.AutoMirrored.Filled.DirectionsWalk,
            testTag = "radial_progress_steps",
            onClick = onStepsClick
        ),
        RadialGoalRingSpec(
            labelMr = "पाणी",
            labelEn = "Water",
            valueText = "$waterLiters L / $waterGoalLiters L",
            progress = waterProgress,
            color = FitTeal,
            trackColor = Color(0xFFCCFBF1),
            icon = Icons.Default.WaterDrop,
            testTag = "radial_progress_water",
            onClick = onWaterClick
        ),
        RadialGoalRingSpec(
            labelMr = "कॅलरीज",
            labelEn = "Calories",
            valueText = "$calories / $calorieGoal kcal",
            progress = caloriesProgress,
            color = FitSaffron,
            trackColor = Color(0xFFFFEDD5),
            icon = Icons.Default.LocalFireDepartment,
            testTag = "radial_progress_calories",
            onClick = onCaloriesClick
        )
    )

    FitCard(
        backgroundColor = Color.White,
        modifier = modifier.testTag("health_dashboard_radial_card")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (isMarathi) "आजची ध्येय प्रगती" else "Daily Goals Progress",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = FitDeepGreen
                )
                Text(
                    text = if (isMarathi) "पावले • पाणी • कॅलरीज" else "Steps • Water • Calories",
                    fontSize = 11.sp,
                    color = FitTextSecondary
                )
            }
            Surface(
                color = FitLightTeal,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = if (isMarathi) "$overallPercent% पूर्ण" else "$overallPercent% Complete",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = FitPrimaryGreen,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            MultiRingRadialGoalChart(
                stepsProgress = stepsProgress,
                waterProgress = waterProgress,
                caloriesProgress = caloriesProgress,
                centerPercentText = "$overallPercent%",
                centerLabel = if (isMarathi) "दैनिक ध्येय" else "Daily Goal",
                size = 128.dp,
                ringWidth = 9.dp,
                ringGap = 5.dp,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { onChartClick() }
                    .testTag("home_daily_goal_ring")
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                goalSpecs.forEach { spec ->
                    GoalProgressBarRow(
                        spec = spec,
                        isMarathi = isMarathi
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
        HorizontalDivider(color = FitBorder.copy(alpha = 0.6f))
        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
                .clip(RoundedCornerShape(10.dp))
                .clickable { onViewAllGoalsClick() }
                .padding(horizontal = 4.dp)
                .testTag("open_daily_goals_screen_btn"),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isMarathi) "दैनिक आरोग्य ध्येय यादी व प्रगती अद्यतनित करा" else "View & Update Daily Health Goals",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = FitPrimaryGreen
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = if (isMarathi) "दैनिक ध्येये उघडा" else "Open Daily Goals",
                tint = FitPrimaryGreen,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun GoalProgressBarRow(
    spec: RadialGoalRingSpec,
    isMarathi: Boolean
) {
    val percent = (spec.progress.coerceIn(0f, 1f) * 100).toInt()
    val animatedProgress by animateFloatAsState(
        targetValue = spec.progress.coerceIn(0f, 1f),
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "bar_${spec.testTag}"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable { spec.onClick() }
            .padding(vertical = 4.dp, horizontal = 2.dp)
            .testTag(spec.testTag),
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Icon(
                    imageVector = spec.icon,
                    contentDescription = if (isMarathi) spec.labelMr else spec.labelEn,
                    tint = spec.color,
                    modifier = Modifier.size(15.dp)
                )
                Text(
                    text = if (isMarathi) spec.labelMr else spec.labelEn,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = FitTextPrimary
                )
            }
            Text(
                text = "$percent%",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = spec.color
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = spec.color,
            trackColor = spec.trackColor
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = spec.valueText,
            fontSize = 10.sp,
            color = FitTextSecondary
        )
    }
}
