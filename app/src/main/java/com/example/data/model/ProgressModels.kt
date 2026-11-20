package com.example.data.model

enum class RiskLevel(val enLabel: String, val mrLabel: String, val colorHex: Long) {
    LOW("Low Risk", "कमी जोखीम", 0xFF16A34A),
    MODERATE("Moderate Risk", "मध्यम जोखीम", 0xFFD97706),
    HIGH("High Risk", "जास्त जोखीम", 0xFFDC2626)
}

data class HealthConditionRisk(
    val conditionNameEn: String,
    val conditionNameMr: String,
    val riskLevel: RiskLevel,
    val percentage: Int,
    val explanationEn: String,
    val explanationMr: String,
    val recommendationsEn: List<String>,
    val recommendationsMr: List<String>
)

data class HealthReportData(
    val overallScore: Int = 78,
    val scoreRatingEn: String = "Good",
    val scoreRatingMr: String = "उत्कृष्ट (Good)",
    val bodyFatPercentage: Float = 18.0f,
    val bmi: Float = 24.8f,
    val bmiCategoryEn: String = "Normal",
    val bmiCategoryMr: String = "सामान्य (Normal)",
    val heartHealthEn: String = "Good",
    val heartHealthMr: String = "उत्तम",
    val liverHealthEn: String = "Normal",
    val liverHealthMr: String = "सामान्य",
    val boneHealthEn: String = "Good",
    val boneHealthMr: String = "मजबूत",
    val diabetesRiskPercent: Int = 28,
    val risks: List<HealthConditionRisk> = emptyList()
)

data class DailyProgressRecord(
    val dayLabel: String,
    val weightKg: Float,
    val steps: Int,
    val calories: Int,
    val sleepHours: Float,
    val waterLiters: Float
)
