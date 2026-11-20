package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FoodPreference
import com.example.data.model.Language
import com.example.data.model.MealItem
import com.example.data.model.MealType
import com.example.data.repository.FitMarathiRepository
import com.example.data.repository.SampleData
import com.example.ui.components.DisclaimerBox
import com.example.ui.components.FitCard
import com.example.ui.components.PillTabRow
import com.example.ui.theme.*

@Composable
fun DietScreen(
    repository: FitMarathiRepository,
    modifier: Modifier = Modifier
) {
    val profile by repository.userProfile.collectAsState()
    val meals by repository.meals.collectAsState()
    val groceries by repository.groceries.collectAsState()
    val isMr = profile.language == Language.MARATHI

    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedFilterPref by remember { mutableStateOf(profile.foodPreference) }

    // Dialog states
    var selectedMealForDetail by remember { mutableStateOf<MealItem?>(null) }
    var mealToSwap by remember { mutableStateOf<MealItem?>(null) }
    var showAddMealDialog by remember { mutableStateOf(false) }

    val filteredMeals = remember(meals, selectedFilterPref) {
        if (selectedFilterPref == FoodPreference.BOTH) {
            meals
        } else {
            meals.filter { it.foodPreference == selectedFilterPref || it.foodPreference == FoodPreference.BOTH }
        }
    }

    // Daily Macro Sums
    val loggedMeals = meals.filter { it.isLogged }
    val consumedCalories = loggedMeals.sumOf { it.calories }
    val consumedProtein = loggedMeals.sumOf { it.proteinGrams }
    val consumedCarbs = loggedMeals.sumOf { it.carbsGrams }
    val consumedFat = loggedMeals.sumOf { it.fatGrams }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(FitBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = if (isMr) "आहार योजना (Daily Nutrition Plan)" else "Daily Diet & Nutrition Plan",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = FitTextPrimary
                )
                Text(
                    text = if (isMr) "पौष्टिक महाराष्ट्रीयन घरगुती आहार • कॅलरीज व प्रथिनांचे संतुलन" else "Wholesome Maharashtrian cuisine with balanced macros",
                    fontSize = 13.sp,
                    color = FitTextSecondary
                )
            }
        }

        // Sub Navigation Tabs: Daily Plan, Groceries, Recipes
        item {
            PillTabRow(
                tabs = listOf(
                    if (isMr) "आजचा आहार" else "Daily Plan",
                    if (isMr) "किराणा यादी" else "Grocery List",
                    if (isMr) "पाककृती" else "Recipes"
                ),
                selectedIndex = selectedTab,
                onTabSelected = { selectedTab = it },
                activeColor = FitPrimaryGreen
            )
        }

        if (selectedTab == 0) {
            // Dietary Preference Chips (Veg, Non-Veg, Both)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        FilterChip(
                            selected = selectedFilterPref == FoodPreference.VEGETARIAN,
                            onClick = { selectedFilterPref = FoodPreference.VEGETARIAN },
                            label = { Text(if (isMr) "शाकाहारी (Veg)" else "Vegetarian", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = FitEmerald.copy(alpha = 0.2f),
                                selectedLabelColor = FitDeepGreen
                            )
                        )
                        FilterChip(
                            selected = selectedFilterPref == FoodPreference.NON_VEGETARIAN,
                            onClick = { selectedFilterPref = FoodPreference.NON_VEGETARIAN },
                            label = { Text(if (isMr) "मांसाहारी" else "Non-Veg", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = FitSaffron.copy(alpha = 0.2f),
                                selectedLabelColor = FitSaffron
                            )
                        )
                        FilterChip(
                            selected = selectedFilterPref == FoodPreference.BOTH,
                            onClick = { selectedFilterPref = FoodPreference.BOTH },
                            label = { Text(if (isMr) "दोन्ही (Both)" else "Both", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = FitTeal.copy(alpha = 0.2f),
                                selectedLabelColor = FitTeal
                            )
                        )
                    }

                    OutlinedButton(
                        onClick = { showAddMealDialog = true },
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("add_custom_meal_btn")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isMr) "आहार जोडा" else "Add", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Daily Macro Breakdown Summary Card
            item {
                FitCard(backgroundColor = Color.White) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (isMr) "दैनिक पोषण स्थिती (Daily Macros)" else "Daily Nutritional Intake",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = FitDeepGreen
                            )
                            Text(
                                text = if (isMr) "उद्दिष्ट: ${profile.calorieGoal} kcal" else "Target: ${profile.calorieGoal} kcal",
                                fontSize = 12.sp,
                                color = FitTeal,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Surface(
                            color = if (consumedCalories > profile.calorieGoal) FitWarning.copy(alpha = 0.15f) else FitEmerald.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = "$consumedCalories / ${profile.calorieGoal} kcal",
                                color = if (consumedCalories > profile.calorieGoal) FitWarning else FitEmerald,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 4 Macro Badges
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        MacroBadge(
                            title = if (isMr) "प्रथिने (Protein)" else "Protein",
                            amount = "${consumedProtein}g",
                            color = FitPrimaryGreen
                        )
                        MacroBadge(
                            title = if (isMr) "कर्बोदके (Carbs)" else "Carbs",
                            amount = "${consumedCarbs}g",
                            color = FitTeal
                        )
                        MacroBadge(
                            title = if (isMr) "चरबी (Fats)" else "Fats",
                            amount = "${consumedFat}g",
                            color = FitSaffron
                        )
                    }
                }
            }

            // Meal Items List
            items(filteredMeals, key = { it.id }) { meal ->
                MealCard(
                    meal = meal,
                    isMr = isMr,
                    onOpenDetail = { selectedMealForDetail = meal },
                    onStartSwap = { mealToSwap = meal },
                    onToggleLogged = { repository.toggleMealLogged(meal.id) }
                )
            }

            item {
                DisclaimerBox(
                    textEn = "All nutritional values are reasonable estimates based on traditional Maharashtrian preparation styles. Adjust quantities according to personal satiety and digestive comfort.",
                    textMr = "सर्व कॅलरी व प्रथिनांची आकडेवारी पारंपारिक पाककृतींनुसार अंदाजित आहे. वैयक्तिक क्षमतेनुसार प्रमाण बदलावे.",
                    isMarathi = isMr
                )
            }
        } else if (selectedTab == 1) {
            // Grocery List & Budget Tips
            item {
                FitCard(backgroundColor = Color.White) {
                    Text(
                        text = if (isMr) "महाराष्ट्रीयन सुदृढ किराणा यादी (Grocery List)" else "Maharashtrian Healthy Grocery List",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isMr) "किफायतशीर व देशी सुपरफूड्स: ज्वारी, बाजरी, मटकी, हरभरा, ताजे ताक" else "Budget-friendly native superfoods that power longevity",
                        fontSize = 12.sp,
                        color = FitTextSecondary
                    )
                }
            }

            items(groceries, key = { it.id }) { item ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { repository.toggleGroceryItem(item.id) }
                        .border(1.dp, FitBorder, RoundedCornerShape(12.dp)),
                    color = if (item.isChecked) FitSurfaceVariant else Color.White
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Checkbox(
                                checked = item.isChecked,
                                onCheckedChange = { repository.toggleGroceryItem(item.id) },
                                colors = CheckboxDefaults.colors(checkedColor = FitEmerald)
                            )
                            Column {
                                Text(
                                    text = if (isMr) item.nameMr else item.nameEn,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (item.isChecked) FitTextMuted else FitTextPrimary
                                )
                                Text(
                                    text = if (isMr) item.categoryMr else item.categoryEn,
                                    fontSize = 11.sp,
                                    color = FitTextSecondary
                                )
                            }
                        }
                        Text(
                            text = item.quantity,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = FitTeal
                        )
                    }
                }
            }
        } else {
            // Maharashtrian Healthy Recipes
            item {
                RecipeCard(
                    title = if (isMr) "१. मोड आलेल्या मटकीची उसळ" else "1. Sprouted Matki Usal",
                    time = "15 Min",
                    calories = "220 kcal • 14g Protein",
                    isVeg = true,
                    description = if (isMr) "मोड आलेली मटकी, आले-लसूण पेस्ट, मोहरी, हळद, कढीपत्ता आणि लिंबू. अत्यंत कमी तेलात परतून वाफ काढावी. प्रथिनांचा खजिना!" else "Sprouted dew beans lightly sautéed with mustard seeds, curry leaves, and lemon. High protein and gluten-free.",
                    isMr = isMr
                )
            }
            item {
                RecipeCard(
                    title = if (isMr) "२. ज्वारीची गरमागरम भाकरी" else "2. Traditional Jowar Bhakri",
                    time = "10 Min",
                    calories = "180 kcal • 4g Fiber",
                    isVeg = true,
                    description = if (isMr) "ज्वारीच्या पिठात कोमट पाणी घालून हाताने थापलेली पारंपरिक भाकरी. रक्तातील साखर वाढत नाही व पचन सुलभ होते." else "Hand-flattened whole sorghum flatbread. Zero gluten, high fiber, prevents insulin spikes.",
                    isMr = isMr
                )
            }
            item {
                RecipeCard(
                    title = if (isMr) "३. काकडी-गाजराची खमंग कोशिंबीर" else "3. Crunchy Peanut Koshimbir",
                    time = "5 Min",
                    calories = "90 kcal • Probiotics & Minerals",
                    isVeg = true,
                    description = if (isMr) "बारीक चिरलेली काकडी, गाजर, २ चमचे ताजे दही, थोडे भाजलेले शेंगदाणे कूट आणि कोथिंबीर. जेवणासोबत पचनासाठी सर्वोत्तम." else "Diced cucumber and carrots tossed with fresh dahi, a hint of crushed peanuts, and fresh cilantro. Enhances gut microbiome.",
                    isMr = isMr
                )
            }
        }
    }

    // Meal Detail Dialog
    selectedMealForDetail?.let { meal ->
        AlertDialog(
            onDismissRequest = { selectedMealForDetail = null },
            title = {
                Text(
                    text = if (isMr) meal.titleMr else meal.titleEn,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "${meal.mealType.timeSlot} • ~ ${meal.calories} kcal",
                        fontWeight = FontWeight.Bold,
                        color = FitTeal,
                        fontSize = 13.sp
                    )

                    HorizontalDivider()

                    Text(
                        text = if (isMr) "घटक व पदार्थ (Ingredients & Items):" else "Ingredients & Items:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    val itemsList = if (isMr) meal.itemsMr else meal.itemsEn
                    itemsList.forEach { item ->
                        Text(text = "• $item", fontSize = 12.sp, color = FitTextSecondary)
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (isMr) "प्रमाण व पोषण सल्ला (Portion Guidance):" else "Portion Guidance:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = if (isMr) meal.portionInfoMr else meal.portionInfoEn,
                        fontSize = 12.sp,
                        color = FitTextSecondary,
                        lineHeight = 16.sp
                    )

                    Surface(
                        color = FitLightTeal,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Text("Protein: ${meal.proteinGrams}g", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = FitDeepGreen)
                            Text("Carbs: ${meal.carbsGrams}g", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = FitTeal)
                            Text("Fat: ${meal.fatGrams}g", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = FitSaffron)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        repository.toggleMealLogged(meal.id)
                        selectedMealForDetail = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (meal.isLogged) FitEmerald else FitPrimaryGreen
                    )
                ) {
                    Text(if (meal.isLogged) (if (isMr) "नोंद काढून टाका" else "Unlog Meal") else (if (isMr) "आहार नोंदवा" else "Log Meal"))
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedMealForDetail = null }) {
                    Text(if (isMr) "बंद करा" else "Close")
                }
            }
        )
    }

    // Meal Swap Selection Dialog
    mealToSwap?.let { currentMeal ->
        val alternatives = SampleData.mealSwaps[currentMeal.mealType].orEmpty()

        AlertDialog(
            onDismissRequest = { mealToSwap = null },
            title = {
                Text(
                    text = if (isMr) "पर्यायी महाराष्ट्रीयन आहार निवडा" else "Swap with Maharashtrian Alternative",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = if (isMr) "सध्याचा आहार: ${currentMeal.titleMr}" else "Current: ${currentMeal.titleEn}",
                        fontSize = 12.sp,
                        color = FitTeal
                    )

                    alternatives.forEach { alt ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    repository.swapMeal(currentMeal.id, alt)
                                    mealToSwap = null
                                }
                                .border(1.dp, FitPrimaryGreen.copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
                            color = FitLightTeal
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = if (isMr) alt.titleMr else alt.titleEn,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = FitDeepGreen
                                )
                                Text(
                                    text = "~ ${alt.calories} kcal • ${alt.proteinGrams}g Protein",
                                    fontSize = 11.sp,
                                    color = FitEmerald,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { mealToSwap = null }) {
                    Text(if (isMr) "रद्द करा" else "Cancel")
                }
            }
        )
    }

    // Add Custom Meal Dialog
    if (showAddMealDialog) {
        var mealName by remember { mutableStateOf("") }
        var mealCalories by remember { mutableStateOf("300") }
        var mealProtein by remember { mutableStateOf("15") }
        var mealType by remember { mutableStateOf(MealType.BREAKFAST) }

        AlertDialog(
            onDismissRequest = { showAddMealDialog = false },
            title = {
                Text(text = if (isMr) "नवीन आहार नोंदवा" else "Add Custom Meal", fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = mealName,
                        onValueChange = { mealName = it },
                        label = { Text(if (isMr) "आहाराचे नाव (उदा. पोहे, खिचडी)" else "Meal Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = mealCalories,
                            onValueChange = { mealCalories = it },
                            label = { Text("Calories (kcal)") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = mealProtein,
                            onValueChange = { mealProtein = it },
                            label = { Text("Protein (g)") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (mealName.isNotBlank()) {
                            val cals = mealCalories.toIntOrNull() ?: 300
                            val prot = mealProtein.toIntOrNull() ?: 15
                            val newMeal = MealItem(
                                id = "custom_${System.currentTimeMillis()}",
                                mealType = mealType,
                                titleEn = mealName,
                                titleMr = mealName,
                                itemsEn = listOf(mealName),
                                itemsMr = listOf(mealName),
                                calories = cals,
                                proteinGrams = prot,
                                carbsGrams = 40,
                                fatGrams = 8,
                                portionInfoEn = "Custom user-added meal entry",
                                portionInfoMr = "वापरकर्त्याने स्वतः नोंदवलेला आहार",
                                foodPreference = FoodPreference.BOTH,
                                substitutionEn = "Alternative custom item",
                                substitutionMr = "पर्यायी पदार्थ",
                                isLogged = true
                            )
                            repository.addCustomMeal(newMeal)
                            showAddMealDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FitPrimaryGreen)
                ) {
                    Text(if (isMr) "जोडा" else "Add Meal")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddMealDialog = false }) {
                    Text(if (isMr) "रद्द करा" else "Cancel")
                }
            }
        )
    }
}

@Composable
private fun MacroBadge(title: String, amount: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = amount, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = color)
        Text(text = title, fontSize = 11.sp, color = FitTextSecondary)
    }
}

@Composable
private fun MealCard(
    meal: MealItem,
    isMr: Boolean,
    onOpenDetail: () -> Unit,
    onStartSwap: () -> Unit,
    onToggleLogged: () -> Unit
) {
    FitCard(backgroundColor = Color.White) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.clickable { onOpenDetail() }) {
                Text(
                    text = if (isMr) meal.mealType.mrLabel else meal.mealType.enLabel,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = FitDeepGreen
                )
                Text(
                    text = meal.mealType.timeSlot,
                    fontSize = 12.sp,
                    color = FitTeal,
                    fontWeight = FontWeight.Medium
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Surface(
                    color = if (meal.isLogged) FitEmerald.copy(alpha = 0.15f) else FitSurfaceVariant,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "~ ${meal.calories} kcal",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = if (meal.isLogged) FitEmerald else FitTextPrimary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                IconButton(
                    onClick = onStartSwap,
                    modifier = Modifier.size(32.dp).testTag("swap_btn_${meal.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = "Swap Meal",
                        tint = FitSaffron,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = if (isMr) meal.titleMr else meal.titleEn,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = FitTextPrimary,
            modifier = Modifier.clickable { onOpenDetail() }
        )

        Spacer(modifier = Modifier.height(6.dp))

        val itemsList = if (isMr) meal.itemsMr else meal.itemsEn
        itemsList.forEach { food ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(FitEmerald)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = food,
                    fontSize = 13.sp,
                    color = FitTextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Substitution guidance bar (clickable to trigger swap)
        Surface(
            color = FitSurfaceVariant,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth().clickable { onStartSwap() }
        ) {
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.SwapHoriz,
                    contentDescription = "Substitution",
                    tint = FitSaffron,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = (if (isMr) "पर्याय: " else "Swap: ") + if (isMr) meal.substitutionMr else meal.substitutionEn,
                    fontSize = 11.sp,
                    color = FitTextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Log meal action button
        Button(
            onClick = onToggleLogged,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("log_meal_${meal.id}"),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (meal.isLogged) FitEmerald else FitLightTeal,
                contentColor = if (meal.isLogged) Color.White else FitPrimaryGreen
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = if (meal.isLogged) Icons.Default.CheckCircle else Icons.Default.Check,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (meal.isLogged) (if (isMr) "आहार नोंदवला (Logged)" else "Meal Logged") else (if (isMr) "जेवणाची नोंद करा (Log Meal)" else "Log This Meal"),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun RecipeCard(
    title: String,
    time: String,
    calories: String,
    isVeg: Boolean,
    description: String,
    isMr: Boolean
) {
    FitCard(backgroundColor = Color.White) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = FitTextPrimary
            )
            Surface(
                color = FitLightTeal,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = time,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = FitTeal,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = calories,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = FitEmerald
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = description,
            fontSize = 12.sp,
            lineHeight = 17.sp,
            color = FitTextSecondary
        )
    }
}
