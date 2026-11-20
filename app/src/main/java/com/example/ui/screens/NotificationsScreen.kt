package com.example.ui.screens

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.Language
import com.example.data.model.NotificationType
import com.example.data.model.ReminderItem
import com.example.data.repository.FitMarathiRepository
import com.example.notifications.HealthReminderScheduler
import com.example.ui.components.FitCard
import com.example.ui.components.FitLottieAnimation
import com.example.ui.theme.*

@Composable
fun NotificationsScreen(
    repository: FitMarathiRepository,
    onNavigateBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val profile by repository.userProfile.collectAsStateWithLifecycle()
    val reminders by repository.reminders.collectAsStateWithLifecycle()
    val isMr = profile.language == Language.MARATHI

    var selectedFilter by remember { mutableStateOf("All") }
    var hasPermission by remember {
        mutableStateOf(HealthReminderScheduler.hasNotificationPermission(context))
    }
    var showAddReminderDialog by remember { mutableStateOf(false) }
    var editingReminder by remember { mutableStateOf<ReminderItem?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
        if (granted) {
            HealthReminderScheduler.syncAllReminders(context, reminders, profile.language)
            Toast.makeText(
                context,
                if (isMr) "सूचना सुरू केल्या आहेत!" else "Local reminders enabled!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    val filteredReminders = remember(reminders, selectedFilter) {
        when (selectedFilter) {
            "Water" -> reminders.filter { it.type == NotificationType.WATER }
            "Steps" -> reminders.filter { it.type == NotificationType.STEPS }
            "Wellness" -> reminders.filter {
                it.type == NotificationType.WORKOUT ||
                    it.type == NotificationType.MEAL ||
                    it.type == NotificationType.HEALTH ||
                    it.type == NotificationType.QUOTE
            }
            else -> reminders
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(FitBackground)
            .padding(horizontal = 16.dp)
            .testTag("notifications_list"),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
    ) {
        // Header
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
                            .testTag("notifications_back_btn")
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
                            text = if (isMr) "सूचना व स्मरणपत्रे" else "Local Reminders",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = FitDeepGreen
                        )
                        Text(
                            text = if (isMr) "पाणी व पावले नोंदवण्यासाठी ठराविक वेळेचे अलर्ट्स" else "Scheduled water intake & daily step alerts",
                            fontSize = 12.sp,
                            color = FitTextSecondary
                        )
                    }
                }
                FilledTonalIconButton(
                    onClick = { showAddReminderDialog = true },
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("add_reminder_btn"),
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = FitPrimaryGreen,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.AddAlarm,
                        contentDescription = if (isMr) "नवीन आठवण जोडा" else "Add Reminder"
                    )
                }
            }
        }

        // Runtime Permission Prompt Card on Android 13+
        if (!hasPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            item {
                FitCard(
                    backgroundColor = Color(0xFFFFF7ED),
                    modifier = Modifier.testTag("notification_permission_card")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isMr) "सूचना परवानगी आवश्यक आहे" else "Enable Notification Permission",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = FitDeepGreen
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isMr) "ठराविक वेळी पाणी व पावले आठवण मिळण्यासाठी परवानगी द्या." else "Allow local alerts so FitMarathi can remind you to log water and steps on time.",
                                fontSize = 12.sp,
                                color = FitTextSecondary
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = FitSaffron),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("grant_notification_permission_btn")
                        ) {
                            Text(if (isMr) "सुरू करा" else "Allow", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Interactive Lottie Quick-Test Banner for Water & Steps Local Notifications
        item {
            FitCard(
                backgroundColor = Color.White,
                modifier = Modifier.testTag("water_steps_test_reminders_card")
            ) {
                Text(
                    text = if (isMr) "त्वरित स्थानिक सूचना तपासा (Test Reminders)" else "Instant Local Notification Check",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = FitDeepGreen
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        color = FitLightTeal,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable {
                                if (!hasPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                } else {
                                    val sent = repository.sendTestNotification(NotificationType.WATER)
                                    Toast.makeText(
                                        context,
                                        if (sent) {
                                            if (isMr) "पाण्याची आठवण सूचना पाठवली!" else "Water reminder notification sent!"
                                        } else {
                                            if (isMr) "सूचना परवानगी द्या" else "Please allow notifications"
                                        },
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                            .testTag("test_water_notification_btn")
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FitLottieAnimation(
                                rawResId = R.raw.lottie_water_ripple,
                                contentDescriptionText = "Water ripple animation",
                                size = 40.dp
                            )
                            Column {
                                Text(
                                    text = if (isMr) "पाणी अलर्ट" else "Water Alert",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FitDeepGreen
                                )
                                Text(
                                    text = if (isMr) "आत्ता तपासा" else "Send Test",
                                    fontSize = 11.sp,
                                    color = FitTeal
                                )
                            }
                        }
                    }

                    Surface(
                        color = Color(0xFFE8F5E9),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable {
                                if (!hasPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                } else {
                                    val sent = repository.sendTestNotification(NotificationType.STEPS)
                                    Toast.makeText(
                                        context,
                                        if (sent) {
                                            if (isMr) "पावलांची आठवण सूचना पाठवली!" else "Steps reminder notification sent!"
                                        } else {
                                            if (isMr) "सूचना परवानगी द्या" else "Please allow notifications"
                                        },
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                            .testTag("test_steps_notification_btn")
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FitLottieAnimation(
                                rawResId = R.raw.lottie_steps_energy,
                                contentDescriptionText = "Steps energy animation",
                                size = 40.dp
                            )
                            Column {
                                Text(
                                    text = if (isMr) "पावले अलर्ट" else "Steps Alert",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FitDeepGreen
                                )
                                Text(
                                    text = if (isMr) "आत्ता तपासा" else "Send Test",
                                    fontSize = 11.sp,
                                    color = FitPrimaryGreen
                                )
                            }
                        }
                    }
                }
            }
        }

        // Filter chips: All, Water, Steps, Wellness
        item {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val filters = listOf(
                    "All" to if (isMr) "सर्व (${reminders.size})" else "All (${reminders.size})",
                    "Water" to if (isMr) "पाणी" else "Water",
                    "Steps" to if (isMr) "पावले" else "Steps",
                    "Wellness" to if (isMr) "व्यायाम व आरोग्य" else "Wellness"
                )
                items(filters) { (key, label) ->
                    FilterChip(
                        selected = selectedFilter == key,
                        onClick = { selectedFilter = key },
                        label = { Text(label, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = FitPrimaryGreen,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("reminder_filter_$key")
                    )
                }
            }
        }

        // Reminders List
        items(filteredReminders, key = { it.id }) { item ->
            ReminderCard(
                item = item,
                isMr = isMr,
                onToggle = { repository.toggleReminder(item.id) },
                onEditTime = { editingReminder = item },
                onDelete = { repository.deleteReminder(item.id) }
            )
        }
    }

    if (showAddReminderDialog) {
        ReminderScheduleDialog(
            initialType = NotificationType.WATER,
            initialHour = 10,
            initialMinute = 0,
            isEditing = false,
            isMr = isMr,
            onDismiss = { showAddReminderDialog = false },
            onSave = { type, hour, minute ->
                repository.addCustomReminder(type, hour, minute)
                showAddReminderDialog = false
                Toast.makeText(
                    context,
                    if (isMr) "नवीन स्मरणपत्र ${ReminderItem.formatTime12Hour(hour, minute)} साठी सेट झाले!" else "Reminder scheduled for ${ReminderItem.formatTime12Hour(hour, minute)}!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }

    editingReminder?.let { reminder ->
        ReminderScheduleDialog(
            initialType = reminder.type,
            initialHour = reminder.hourOfDay,
            initialMinute = reminder.minute,
            isEditing = true,
            isMr = isMr,
            onDismiss = { editingReminder = null },
            onSave = { _, hour, minute ->
                repository.updateReminderTime(reminder.id, hour, minute)
                editingReminder = null
                Toast.makeText(
                    context,
                    if (isMr) "वेळ ${ReminderItem.formatTime12Hour(hour, minute)} वर अद्यतनित केली!" else "Updated to ${ReminderItem.formatTime12Hour(hour, minute)}!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }
}

@Composable
private fun ReminderCard(
    item: ReminderItem,
    isMr: Boolean,
    onToggle: () -> Unit,
    onEditTime: () -> Unit,
    onDelete: () -> Unit
) {
    FitCard(
        backgroundColor = Color.White,
        modifier = Modifier.testTag("reminder_card_${item.id}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (item.type == NotificationType.QUOTE) {
                    FitLottieAnimation(
                        rawResId = R.raw.lottie_motivational_quote,
                        contentDescriptionText = if (isMr) "प्रेरणादायी सुविचार अ‍ॅनिमेशन" else "Motivational quote animation",
                        size = 44.dp,
                        modifier = Modifier.testTag("quote_reminder_lottie_${item.id}")
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                when (item.type) {
                                    NotificationType.WATER -> FitLightTeal
                                    NotificationType.STEPS -> Color(0xFFE8F5E9)
                                    NotificationType.WORKOUT -> Color(0xFFFFF3E0)
                                    NotificationType.MEAL -> Color(0xFFE8F5E9)
                                    NotificationType.HEALTH -> Color(0xFFFFEBEE)
                                    NotificationType.QUOTE -> Color(0xFFEDE7F6)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (item.type) {
                                NotificationType.WATER -> Icons.Default.WaterDrop
                                NotificationType.STEPS -> Icons.AutoMirrored.Filled.DirectionsWalk
                                NotificationType.WORKOUT -> Icons.Default.FitnessCenter
                                NotificationType.MEAL -> Icons.Default.Restaurant
                                NotificationType.HEALTH -> Icons.Default.HealthAndSafety
                                NotificationType.QUOTE -> Icons.Default.Favorite
                            },
                            contentDescription = null,
                            tint = when (item.type) {
                                NotificationType.WATER -> FitTeal
                                NotificationType.STEPS -> FitEmerald
                                NotificationType.WORKOUT -> FitSaffron
                                NotificationType.MEAL -> FitPrimaryGreen
                                NotificationType.HEALTH -> Color(0xFFE53935)
                                NotificationType.QUOTE -> Color(0xFF8E24AA)
                            },
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Column {
                    Text(
                        text = if (isMr) item.titleMr else item.titleEn,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = FitTextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isMr) item.messageMr else item.messageEn,
                        fontSize = 12.sp,
                        color = FitTextSecondary,
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = FitLightTeal,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onEditTime() }
                                .testTag("edit_time_${item.id}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = if (isMr) "वेळ बदला" else "Change time",
                                    tint = FitPrimaryGreen,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = item.timeSlot,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FitDeepGreen
                                )
                            }
                        }
                        if (item.id.startsWith("rem_custom_")) {
                            IconButton(
                                onClick = onDelete,
                                modifier = Modifier
                                    .size(32.dp)
                                    .testTag("delete_reminder_${item.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = if (isMr) "काढा" else "Delete reminder",
                                    tint = Color(0xFFDC2626),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }

            Switch(
                checked = item.isEnabled,
                onCheckedChange = { onToggle() },
                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = FitEmerald),
                modifier = Modifier.testTag("toggle_reminder_${item.id}")
            )
        }
    }
}

@Composable
private fun ReminderScheduleDialog(
    initialType: NotificationType,
    initialHour: Int,
    initialMinute: Int,
    isEditing: Boolean,
    isMr: Boolean,
    onDismiss: () -> Unit,
    onSave: (NotificationType, Int, Int) -> Unit
) {
    var selectedType by remember { mutableStateOf(initialType) }
    var hour by remember { mutableIntStateOf(initialHour) }
    var minute by remember { mutableIntStateOf(initialMinute) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isEditing) {
                    if (isMr) "आठवणीची वेळ बदला" else "Edit Reminder Time"
                } else {
                    if (isMr) "नवीन पाणी / पावले आठवण" else "Schedule Water / Steps Reminder"
                },
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = FitDeepGreen
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (!isEditing) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = selectedType == NotificationType.WATER,
                            onClick = { selectedType = NotificationType.WATER },
                            label = { Text(if (isMr) "पाणी (Water)" else "Water Intake", fontSize = 12.sp) },
                            leadingIcon = { Icon(Icons.Default.WaterDrop, null, Modifier.size(16.dp)) },
                            modifier = Modifier.weight(1f).testTag("dialog_type_water")
                        )
                        FilterChip(
                            selected = selectedType == NotificationType.STEPS,
                            onClick = { selectedType = NotificationType.STEPS },
                            label = { Text(if (isMr) "पावले (Steps)" else "Steps Log", fontSize = 12.sp) },
                            leadingIcon = { Icon(Icons.AutoMirrored.Filled.DirectionsWalk, null, Modifier.size(16.dp)) },
                            modifier = Modifier.weight(1f).testTag("dialog_type_steps")
                        )
                    }
                }

                // Selected Time Readout
                Surface(
                    color = FitLightTeal,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = ReminderItem.formatTime12Hour(hour, minute),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = FitDeepGreen
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                onClick = { hour = (hour + 23) % 24 },
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) { Text("-1h") }
                            OutlinedButton(
                                onClick = { hour = (hour + 1) % 24 },
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) { Text("+1h") }
                            OutlinedButton(
                                onClick = { minute = (minute + 15) % 60 },
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) { Text("+15m") }
                        }
                    }
                }

                Text(
                    text = if (isMr) "झटपट वेळा निवडा:" else "Quick Time Presets:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = FitTextSecondary
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        "8:00 AM" to (8 to 0),
                        "1:00 PM" to (13 to 0),
                        "5:00 PM" to (17 to 0),
                        "8:00 PM" to (20 to 0)
                    ).forEach { (label, hm) ->
                        FilterChip(
                            selected = hour == hm.first && minute == hm.second,
                            onClick = {
                                hour = hm.first
                                minute = hm.second
                            },
                            label = { Text(label, fontSize = 10.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(selectedType, hour, minute) },
                colors = ButtonDefaults.buttonColors(containerColor = FitPrimaryGreen),
                modifier = Modifier.testTag("save_reminder_schedule_btn")
            ) {
                Text(if (isMr) "वेळ जतन करा" else "Schedule")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isMr) "रद्द करा" else "Cancel")
            }
        }
    )
}
