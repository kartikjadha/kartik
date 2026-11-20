package com.example.data.model

enum class MealType(val enLabel: String, val mrLabel: String, val timeSlot: String) {
    BREAKFAST("Breakfast", "सकाळचा नाश्ता", "7:30 - 9:00 AM"),
    MID_MORNING("Mid Morning", "दुपारपूर्व अल्पोपहार", "11:00 AM"),
    LUNCH("Lunch", "दुपारचे जेवण", "1:00 - 2:00 PM"),
    EVENING_SNACK("Evening Snack", "संध्याकाळचा नाश्ता", "5:00 - 6:00 PM"),
    DINNER("Dinner", "रात्रीचे जेवण", "8:00 - 9:00 PM")
}

data class MealItem(
    val id: String,
    val mealType: MealType,
    val titleEn: String,
    val titleMr: String,
    val itemsEn: List<String>,
    val itemsMr: List<String>,
    val calories: Int,
    val proteinGrams: Int,
    val carbsGrams: Int,
    val fatGrams: Int,
    val portionInfoEn: String,
    val portionInfoMr: String,
    val foodPreference: FoodPreference,
    val substitutionEn: String,
    val substitutionMr: String,
    val isLogged: Boolean = false
)

data class RecipeItem(
    val id: String,
    val nameEn: String,
    val nameMr: String,
    val prepTimeMin: Int,
    val calories: Int,
    val proteinGrams: Int,
    val isVeg: Boolean,
    val ingredientsEn: List<String>,
    val ingredientsMr: List<String>,
    val instructionsEn: String,
    val instructionsMr: String
)

data class GroceryItem(
    val id: String,
    val nameEn: String,
    val nameMr: String,
    val quantity: String,
    val categoryEn: String,
    val categoryMr: String,
    val isChecked: Boolean = false
)
