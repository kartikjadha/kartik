package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.FitnessYogaRoutine
import com.example.data.model.Language
import com.example.data.model.RoutineCategory
import com.example.data.model.WorkoutLevel
import com.example.data.model.WorkoutRoutineCatalog
import com.example.data.repository.FitMarathiRepository
import com.example.ui.components.ExerciseCompletionCelebrationDialog
import com.example.ui.components.FitCard
import com.example.ui.components.FitLottieAnimation
import com.example.ui.components.LanguageSwitchButton
import com.example.ui.theme.*

@Composable
fun RoutineLibraryScreen(
    repository: FitMarathiRepository,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val profile by repository.userProfile.collectAsStateWithLifecycle()
    val allRoutines by repository.routineCatalog.collectAsStateWithLifecycle()
    val isMr = profile.language == Language.MARATHI

    var selectedCategory by remember { mutableStateOf(RoutineCategory.ALL) }
    var selectedLevel by remember { mutableStateOf<WorkoutLevel?>(null) }
    var showBookmarkedOnly by remember { mutableStateOf(false) }
    var showBilingualDescriptions by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") }
    var activeDetailRoutineId by remember { mutableStateOf<String?>(null) }
    var celebratedRoutine by remember { mutableStateOf<FitnessYogaRoutine?>(null) }

    val categoryCounts = remember(allRoutines) {
        buildMap {
            put(RoutineCategory.ALL, allRoutines.size)
            WorkoutRoutineCatalog.browsableCategories.forEach { category ->
                put(category, allRoutines.count { it.category == category })
            }
        }
    }

    val filteredRoutines = remember(allRoutines, selectedCategory, selectedLevel, showBookmarkedOnly, searchQuery) {
        val query = searchQuery.trim()
        allRoutines.filter { routine ->
            val matchesCategory = selectedCategory == RoutineCategory.ALL || routine.category == selectedCategory
            val matchesLevel = selectedLevel == null || routine.level == selectedLevel
            val matchesBookmark = !showBookmarkedOnly || routine.isBookmarked
            val matchesSearch = query.isEmpty() ||
                routine.titleEn.contains(query, ignoreCase = true) ||
                routine.titleMr.contains(query, ignoreCase = true) ||
                routine.descriptionEn.contains(query, ignoreCase = true) ||
                routine.descriptionMr.contains(query, ignoreCase = true) ||
                routine.focusAreaEn.contains(query, ignoreCase = true) ||
                routine.focusAreaMr.contains(query, ignoreCase = true)
            matchesCategory && matchesLevel && matchesBookmark && matchesSearch
        }
    }

    val groupedByCategory = remember(filteredRoutines) {
        WorkoutRoutineCatalog.browsableCategories.mapNotNull { cat ->
            val itemsInCategory = filteredRoutines.filter { it.category == cat }
            if (itemsInCategory.isNotEmpty()) cat to itemsInCategory else null
        }
    }

    val completedCount = remember(allRoutines) { allRoutines.count { it.isCompleted } }
    val activeDetailRoutine = remember(allRoutines, activeDetailRoutineId) {
        activeDetailRoutineId?.let { id -> allRoutines.find { it.id == id } }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(FitBackground)
            .padding(horizontal = 16.dp)
            .testTag("routine_library_list"),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
    ) {
        // Header with Back & Language Toggle
        item(key = "header") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
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
                            .testTag("routine_library_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = if (isMr) "मागे जा" else "Navigate back",
                            tint = FitTextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (isMr) "योगा व फिटनेस लायब्ररी" else "Yoga & Fitness Library",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = FitDeepGreen
                        )
                        Text(
                            text = if (isMr) "मराठी व इंग्रजी वर्णनांसह ${allRoutines.size} दिनचर्या" else "${allRoutines.size} categorized routines in Marathi & English",
                            fontSize = 12.sp,
                            color = FitTextSecondary
                        )
                    }
                }
                LanguageSwitchButton(
                    currentLanguage = profile.language,
                    onToggle = { repository.updateLanguage(it) }
                )
            }
        }

        // Hero Summary Card
        item(key = "hero_banner") {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(FitDeepGreen, FitPrimaryGreen, FitTeal)
                        )
                    )
                    .padding(18.dp)
                    .testTag("routine_library_hero")
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isMr) "६६+ योगासने व फिटनेस दिनचर्या" else "66+ Yoga & Fitness Routines",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isMr) "७ श्रेणी • मराठी व इंग्रजी सविस्तर माहिती" else "7 Categories • Bilingual Marathi & English Guides",
                                color = Color(0xFFD1FAE5),
                                fontSize = 12.sp
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FitLottieAnimation(
                                rawResId = R.raw.lottie_yoga_lotus,
                                contentDescriptionText = "Animated yoga and fitness lotus",
                                size = 46.dp,
                                modifier = Modifier.testTag("routine_library_lottie")
                            )
                            Surface(
                                color = FitGold,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = if (isMr) "$completedCount पूर्ण" else "$completedCount Done",
                                    color = FitDeepGreen,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        HeroPillStat(
                            label = if (isMr) "योगा व ध्यान: २२" else "Yoga & Breath: 22",
                            modifier = Modifier.weight(1f)
                        )
                        HeroPillStat(
                            label = if (isMr) "व्यायाम व HIIT: ४४" else "Fitness & HIIT: 44",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Search Field
        item(key = "search_bar") {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("routine_search_input"),
                placeholder = {
                    Text(
                        text = if (isMr) "योगा, सूर्यनमस्कार, HIIT किंवा कसरत शोधा..." else "Search yoga, pranayama, HIIT, abs, back...",
                        fontSize = 13.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = if (isMr) "शोधा" else "Search",
                        tint = FitPrimaryGreen
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = if (isMr) "शोध पुसा" else "Clear search"
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = FitPrimaryGreen,
                    unfocusedBorderColor = FitBorder
                )
            )
        }

        // Category Filter Chips
        item(key = "category_chips") {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("routine_category_row")
            ) {
                items(RoutineCategory.entries, key = { it.name }) { category ->
                    val count = categoryCounts[category] ?: 0
                    val isSelected = selectedCategory == category
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = category },
                        label = {
                            Text(
                                text = "${if (isMr) category.mrLabel else category.enLabel} ($count)",
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = categoryIcon(category),
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = FitPrimaryGreen,
                            selectedLabelColor = Color.White,
                            selectedLeadingIconColor = Color.White,
                            containerColor = Color.White
                        ),
                        modifier = Modifier.testTag("routine_cat_${category.name}")
                    )
                }
            }
        }

        // Level + Bilingual + Saved Filters Row
        item(key = "secondary_filters") {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    FilterChip(
                        selected = showBilingualDescriptions,
                        onClick = { showBilingualDescriptions = !showBilingualDescriptions },
                        label = {
                            Text(
                                text = if (showBilingualDescriptions) "मराठी + EN" else (if (isMr) "फक्त मराठी" else "English Only"),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Translate,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = FitLightTeal,
                            selectedLabelColor = FitDeepGreen,
                            selectedLeadingIconColor = FitPrimaryGreen
                        ),
                        modifier = Modifier.testTag("routine_bilingual_toggle")
                    )
                }
                item {
                    FilterChip(
                        selected = showBookmarkedOnly,
                        onClick = { showBookmarkedOnly = !showBookmarkedOnly },
                        label = {
                            Text(
                                text = if (isMr) "जतन केलेले" else "Saved",
                                fontSize = 11.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = if (showBookmarkedOnly) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp)
                            )
                        },
                        modifier = Modifier.testTag("routine_saved_filter")
                    )
                }
                item {
                    FilterChip(
                        selected = selectedLevel == null,
                        onClick = { selectedLevel = null },
                        label = { Text(if (isMr) "सर्व स्तर" else "All Levels", fontSize = 11.sp) }
                    )
                }
                items(WorkoutLevel.entries, key = { it.name }) { lvl ->
                    FilterChip(
                        selected = selectedLevel == lvl,
                        onClick = { selectedLevel = if (selectedLevel == lvl) null else lvl },
                        label = { Text(if (isMr) lvl.mrLabel else lvl.enLabel, fontSize = 11.sp) },
                        modifier = Modifier.testTag("routine_level_${lvl.name}")
                    )
                }
            }
        }

        // Results Summary Line
        item(key = "results_count") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isMr) {
                        "दाखवलेल्या दिनचर्या: ${filteredRoutines.size} / ${allRoutines.size}"
                    } else {
                        "Showing ${filteredRoutines.size} of ${allRoutines.size} routines"
                    },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = FitTextSecondary,
                    modifier = Modifier.testTag("routine_results_count")
                )
                if (selectedCategory != RoutineCategory.ALL || selectedLevel != null || showBookmarkedOnly || searchQuery.isNotBlank()) {
                    TextButton(
                        onClick = {
                            selectedCategory = RoutineCategory.ALL
                            selectedLevel = null
                            showBookmarkedOnly = false
                            searchQuery = ""
                        },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                    ) {
                        Text(
                            text = if (isMr) "फिल्टर काढा" else "Reset",
                            fontSize = 12.sp,
                            color = FitPrimaryGreen
                        )
                    }
                }
            }
        }

        // Empty State or Categorized Routines List
        if (filteredRoutines.isEmpty()) {
            item(key = "empty_state") {
                FitCard(backgroundColor = Color.White) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = null,
                            tint = FitTextSecondary,
                            modifier = Modifier.size(36.dp)
                        )
                        Text(
                            text = if (isMr) "कोणतीही दिनचर्या सापडली नाही" else "No matching routines found",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = FitTextPrimary
                        )
                        Text(
                            text = if (isMr) "कृपया दुसरा शब्द शोधा किंवा फिल्टर बदला." else "Try a different keyword or reset your filters.",
                            fontSize = 12.sp,
                            color = FitTextSecondary
                        )
                        Button(
                            onClick = {
                                selectedCategory = RoutineCategory.ALL
                                selectedLevel = null
                                showBookmarkedOnly = false
                                searchQuery = ""
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = FitPrimaryGreen)
                        ) {
                            Text(if (isMr) "सर्व दिनचर्या पहा" else "Show All Routines")
                        }
                    }
                }
            }
        } else {
            groupedByCategory.forEach { (category, routinesInCategory) ->
                item(key = "section_header_${category.name}") {
                    CategorySectionHeader(
                        category = category,
                        count = routinesInCategory.size,
                        isMr = isMr
                    )
                }

                items(routinesInCategory, key = { it.id }) { routine ->
                    RoutineLibraryCard(
                        routine = routine,
                        isMr = isMr,
                        showBilingualDescriptions = showBilingualDescriptions,
                        onOpenDetail = { activeDetailRoutineId = routine.id },
                        onToggleBookmark = { repository.toggleRoutineBookmark(routine.id) },
                        onToggleCompleted = {
                            val becomingCompleted = !routine.isCompleted
                            repository.toggleRoutineCompleted(routine.id)
                            if (becomingCompleted) {
                                celebratedRoutine = routine
                            }
                        }
                    )
                }
            }
        }
    }

    // Detail Modal showing full Marathi & English steps and descriptions
    activeDetailRoutine?.let { routine ->
        RoutineDetailDialog(
            routine = routine,
            isMr = isMr,
            onDismiss = { activeDetailRoutineId = null },
            onToggleCompleted = {
                val becomingCompleted = !routine.isCompleted
                repository.toggleRoutineCompleted(routine.id)
                if (becomingCompleted) {
                    celebratedRoutine = routine
                }
            },
            onToggleBookmark = { repository.toggleRoutineBookmark(routine.id) }
        )
    }

    celebratedRoutine?.let { completed ->
        ExerciseCompletionCelebrationDialog(
            titleMr = "दिनचर्या पूर्ण झाली! 🌿",
            titleEn = "Routine Completed! 🌿",
            subtitleMr = "${completed.titleMr} (${completed.durationMinutes} मि • ~${completed.estimatedCalories} kcal) यशस्वीरित्या नोंदवले!",
            subtitleEn = "${completed.titleEn} (${completed.durationMinutes} min • ~${completed.estimatedCalories} kcal) logged to today's progress!",
            isMarathi = isMr,
            onDismiss = { celebratedRoutine = null }
        )
    }
}

@Composable
private fun HeroPillStat(label: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = Color.White.copy(alpha = 0.16f),
        shape = RoundedCornerShape(10.dp)
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun CategorySectionHeader(
    category: RoutineCategory,
    count: Int,
    isMr: Boolean
) {
    val accent = categoryAccentColor(category)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp, bottom = 2.dp)
            .testTag("routine_section_${category.name}"),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accent.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = categoryIcon(category),
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(19.dp)
                )
            }
            Column {
                Text(
                    text = if (isMr) "${category.mrLabel} • ${category.enLabel}" else "${category.enLabel} • ${category.mrLabel}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = FitDeepGreen
                )
                Text(
                    text = if (isMr) category.subtitleMr else category.subtitleEn,
                    fontSize = 11.sp,
                    color = FitTextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Surface(
            color = accent.copy(alpha = 0.12f),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(
                text = "$count",
                color = accent,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp)
            )
        }
    }
}

@Composable
private fun RoutineLibraryCard(
    routine: FitnessYogaRoutine,
    isMr: Boolean,
    showBilingualDescriptions: Boolean,
    onOpenDetail: () -> Unit,
    onToggleBookmark: () -> Unit,
    onToggleCompleted: () -> Unit
) {
    val accent = categoryAccentColor(routine.category)

    FitCard(
        backgroundColor = Color.White,
        modifier = Modifier
            .clickable { onOpenDetail() }
            .testTag("routine_card_${routine.id}")
    ) {
        // Top Row: Titles + Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isMr) routine.titleMr else routine.titleEn,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = FitDeepGreen
                )
                Text(
                    text = if (isMr) routine.titleEn else routine.titleMr,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = FitTeal
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onToggleBookmark,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("routine_bookmark_${routine.id}")
                ) {
                    Icon(
                        imageVector = if (routine.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = if (isMr) "जतन करा" else "Bookmark routine",
                        tint = if (routine.isBookmarked) FitSaffron else FitTextSecondary
                    )
                }
                IconButton(
                    onClick = onToggleCompleted,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("routine_complete_${routine.id}")
                ) {
                    if (routine.isCompleted) {
                        FitLottieAnimation(
                            rawResId = R.raw.lottie_exercise_complete,
                            contentDescriptionText = if (isMr) "पूर्ण झाले" else "Completed",
                            size = 38.dp,
                            modifier = Modifier.testTag("routine_completed_lottie_${routine.id}")
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.RadioButtonUnchecked,
                            contentDescription = if (isMr) "पूर्ण म्हणून चिन्हांकित करा" else "Mark routine complete",
                            tint = FitBorder
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Metadata Pills (Duration, Calories, Level)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MetaBadge(
                text = "${routine.durationMinutes} ${if (isMr) "मि" else "min"}",
                bg = FitLightTeal,
                fg = FitDeepGreen
            )
            MetaBadge(
                text = "~${routine.estimatedCalories} kcal",
                bg = Color(0xFFFFF3E0),
                fg = FitSaffron
            )
            MetaBadge(
                text = if (isMr) routine.level.mrLabel else routine.level.enLabel,
                bg = accent.copy(alpha = 0.12f),
                fg = accent
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Bilingual Marathi & English Descriptions
        if (showBilingualDescriptions) {
            BilingualDescriptionBlock(
                badge = "मराठी",
                text = routine.descriptionMr,
                badgeBg = Color(0xFFE8F5E9),
                badgeFg = FitPrimaryGreen
            )
            Spacer(modifier = Modifier.height(6.dp))
            BilingualDescriptionBlock(
                badge = "EN",
                text = routine.descriptionEn,
                badgeBg = FitLightTeal,
                badgeFg = FitTeal
            )
        } else {
            Text(
                text = if (isMr) routine.descriptionMr else routine.descriptionEn,
                fontSize = 12.sp,
                color = FitTextSecondary,
                lineHeight = 17.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${if (isMr) "लक्ष्य:" else "Focus:"} ${if (isMr) routine.focusAreaMr else routine.focusAreaEn}",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = FitTextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = if (isMr) "पायऱ्या पहा ›" else "View Steps ›",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = FitPrimaryGreen
            )
        }
    }
}

@Composable
private fun BilingualDescriptionBlock(
    badge: String,
    text: String,
    badgeBg: Color,
    badgeFg: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Surface(
            color = badgeBg,
            shape = RoundedCornerShape(6.dp)
        ) {
            Text(
                text = badge,
                color = badgeFg,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
        Text(
            text = text,
            fontSize = 12.sp,
            color = FitTextSecondary,
            lineHeight = 17.sp,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun MetaBadge(text: String, bg: Color, fg: Color) {
    Surface(color = bg, shape = RoundedCornerShape(8.dp)) {
        Text(
            text = text,
            color = fg,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@Composable
private fun RoutineDetailDialog(
    routine: FitnessYogaRoutine,
    isMr: Boolean,
    onDismiss: () -> Unit,
    onToggleCompleted: () -> Unit,
    onToggleBookmark: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = if (isMr) routine.titleMr else routine.titleEn,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = FitDeepGreen
                )
                Text(
                    text = if (isMr) routine.titleEn else routine.titleMr,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = FitTeal
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MetaBadge("${routine.durationMinutes} min", FitLightTeal, FitDeepGreen)
                    MetaBadge("~${routine.estimatedCalories} kcal", Color(0xFFFFF3E0), FitSaffron)
                    MetaBadge(
                        if (isMr) routine.level.mrLabel else routine.level.enLabel,
                        Color(0xFFE8F5E9),
                        FitPrimaryGreen
                    )
                }

                BilingualDescriptionBlock(
                    badge = "मराठी",
                    text = routine.descriptionMr,
                    badgeBg = Color(0xFFE8F5E9),
                    badgeFg = FitPrimaryGreen
                )
                BilingualDescriptionBlock(
                    badge = "EN",
                    text = routine.descriptionEn,
                    badgeBg = FitLightTeal,
                    badgeFg = FitTeal
                )

                HorizontalDivider(color = FitBorder)

                Text(
                    text = if (isMr) "सराव पायऱ्या (Step-by-Step Guide)" else "Step-by-Step Guide (सराव पायऱ्या)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = FitDeepGreen
                )

                routine.stepsMr.indices.forEach { index ->
                    Surface(
                        color = FitSurfaceVariant,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Text(
                                text = "${index + 1}. ${routine.stepsMr[index]}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = FitTextPrimary
                            )
                            val enStep = routine.stepsEn.getOrNull(index)
                            if (!enStep.isNullOrBlank()) {
                                Text(
                                    text = enStep,
                                    fontSize = 11.sp,
                                    color = FitTextSecondary
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onToggleCompleted()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = FitPrimaryGreen),
                modifier = Modifier.testTag("routine_detail_complete_btn")
            ) {
                Text(
                    text = if (routine.isCompleted) {
                        if (isMr) "पूर्ण झाले ✓" else "Completed ✓"
                    } else {
                        if (isMr) "पूर्ण करा" else "Mark Complete"
                    }
                )
            }
        },
        dismissButton = {
            Row {
                IconButton(onClick = onToggleBookmark) {
                    Icon(
                        imageVector = if (routine.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = if (isMr) "जतन करा" else "Bookmark",
                        tint = if (routine.isBookmarked) FitSaffron else FitTextSecondary
                    )
                }
                TextButton(onClick = onDismiss) {
                    Text(if (isMr) "बंद करा" else "Close")
                }
            }
        }
    )
}

private fun categoryIcon(category: RoutineCategory): ImageVector = when (category) {
    RoutineCategory.ALL -> Icons.Default.GridView
    RoutineCategory.YOGA_ASANAS -> Icons.Default.SelfImprovement
    RoutineCategory.PRANAYAMA_MEDITATION -> Icons.Default.Spa
    RoutineCategory.STRENGTH_BODYWEIGHT -> Icons.Default.FitnessCenter
    RoutineCategory.FAT_LOSS_HIIT -> Icons.Default.LocalFireDepartment
    RoutineCategory.CARDIO_STAMINA -> Icons.AutoMirrored.Filled.DirectionsRun
    RoutineCategory.CORE_ABS -> Icons.Default.AccessibilityNew
    RoutineCategory.MOBILITY_RECOVERY -> Icons.Default.AirlineSeatReclineNormal
}

private fun categoryAccentColor(category: RoutineCategory): Color = when (category) {
    RoutineCategory.ALL -> FitPrimaryGreen
    RoutineCategory.YOGA_ASANAS -> FitEmerald
    RoutineCategory.PRANAYAMA_MEDITATION -> FitTeal
    RoutineCategory.STRENGTH_BODYWEIGHT -> FitDeepGreen
    RoutineCategory.FAT_LOSS_HIIT -> FitSaffron
    RoutineCategory.CARDIO_STAMINA -> Color(0xFF0284C7)
    RoutineCategory.CORE_ABS -> Color(0xFF7C3AED)
    RoutineCategory.MOBILITY_RECOVERY -> Color(0xFF0D9488)
}
