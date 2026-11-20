package com.example.data.model

data class GroomingTip(
    val id: String,
    val category: String, // "hair" or "skin"
    val titleEn: String,
    val titleMr: String,
    val tipsEn: List<String>,
    val tipsMr: List<String>,
    val actionAdviceEn: String,
    val actionAdviceMr: String
)

data class StyleLook(
    val id: String,
    val titleEn: String,
    val titleMr: String,
    val category: String, // "haircut", "clothing", "beard", "glasses"
    val descriptionEn: String,
    val descriptionMr: String,
    val suitableForEn: String,
    val suitableForMr: String
)

data class FaceAnalysisResult(
    val faceShapeEn: String = "Oval",
    val faceShapeMr: String = "अंडाकृती (Oval)",
    val bestHairStyleEn: String = "Textured Quiff / Classic Fade",
    val bestHairStyleMr: String = "टेक्सचर्ड क्विफ / क्लासिक फेड",
    val bestBeardStyleEn: String = "Light Stubble",
    val bestBeardStyleMr: String = "हलकी दाढी (Light Stubble)",
    val bestGlassesEn: String = "Wayfarer / Round Frames",
    val bestGlassesMr: String = "वेफेरर / गोल फ्रेम चष्मा",
    val clothingColorsEn: List<String> = listOf("Navy Blue", "White", "Slate Grey", "Olive Green"),
    val clothingColorsMr: List<String> = listOf("नेव्ही ब्लू", "पांढरा", "राखाडी", "ऑलिव्ह ग्रीन"),
    val personalityTypeEn: String = "Confident & Calm",
    val personalityTypeMr: String = "शांत, विचारपूर्वक निर्णय घेणारे आणि आत्मविश्वासी",
    val stylingNotesEn: String = "Clean minimalist lines with structured shoulders flatter your silhouette.",
    val stylingNotesMr: String = "साधे, व्यवस्थित आणि फिटिंग असलेले कपडे तुमच्या व्यक्तिमत्त्वाला खुलवून दिसतात."
)

enum class NotificationType {
    WATER, STEPS, WORKOUT, MEAL, HEALTH, QUOTE
}

data class ReminderItem(
    val id: String,
    val type: NotificationType,
    val titleEn: String,
    val titleMr: String,
    val messageEn: String,
    val messageMr: String,
    val timeSlot: String,
    val isEnabled: Boolean = true,
    val hourOfDay: Int = 10,
    val minute: Int = 0
) {
    companion object {
        fun formatTime12Hour(hourOfDay: Int, minute: Int): String {
            val safeHour = hourOfDay.coerceIn(0, 23)
            val safeMinute = minute.coerceIn(0, 59)
            val amPm = if (safeHour < 12) "AM" else "PM"
            val hour12 = when {
                safeHour == 0 -> 12
                safeHour > 12 -> safeHour - 12
                else -> safeHour
            }
            val minStr = safeMinute.toString().padStart(2, '0')
            return "$hour12:$minStr $amPm"
        }
    }
}
