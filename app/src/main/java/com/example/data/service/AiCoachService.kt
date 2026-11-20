package com.example.data.service

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.FitnessGoal
import com.example.data.model.FoodPreference
import com.example.data.model.Language
import com.example.data.model.UserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: MessageSender,
    val text: String,
    val timestamp: String = "Now",
    val isDemoResponse: Boolean = false
)

enum class MessageSender {
    USER, COACH
}

class AiCoachService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val tag = "AiCoachService"

    fun getApiKey(): String {
        return try {
            val field = BuildConfig::class.java.getField("GEMINI_API_KEY")
            (field.get(null) as? String).orEmpty()
        } catch (e: Throwable) {
            ""
        }
    }

    fun hasValidApiKey(): Boolean {
        val key = getApiKey()
        return key.isNotBlank() && key != "MY_GEMINI_API_KEY" && key.length > 10
    }

    suspend fun getCoachResponse(
        userMessage: String,
        profile: UserProfile,
        chatHistory: List<ChatMessage>
    ): ChatMessage = withContext(Dispatchers.IO) {
        if (hasValidApiKey()) {
            try {
                val apiKey = getApiKey()
                val responseText = queryGeminiApi(userMessage, profile, apiKey)
                return@withContext ChatMessage(
                    sender = MessageSender.COACH,
                    text = responseText,
                    isDemoResponse = false
                )
            } catch (e: Exception) {
                Log.e(tag, "Gemini API call failed, falling back to intelligent localized coach", e)
            }
        }

        // Offline / Demo Intelligent Maharashtrian Coach Engine
        val localizedReply = generateIntelligentMaharashtrianReply(userMessage, profile)
        return@withContext ChatMessage(
            sender = MessageSender.COACH,
            text = localizedReply,
            isDemoResponse = true
        )
    }

    private fun queryGeminiApi(prompt: String, profile: UserProfile, apiKey: String): String {
        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"

        val systemContext = """
            You are the official FitMarathi AI Coach, an expert fitness trainer, clinical nutritionist, and lifestyle mentor for Maharashtra.
            You must reply primarily in ${if (profile.language == Language.MARATHI) "Marathi (मराठी) with clear Devanagari script" else "English with familiar Marathi cultural terms (bhakri, poli, varan bhat, usal, koshimbir)"}.
            User details: Name: ${profile.name}, Age: ${profile.age}, Weight: ${profile.weightKg}kg, Height: ${profile.heightCm}cm, Goal: ${profile.goal.englishLabel}, Food Preference: ${profile.foodPreference.englishLabel}.
            Emphasize Maharashtrian superfoods (Jowar, Bajra, Sprouted Matki, Moong Dal, Taak/Buttermilk, Kokum, Poha, boiled eggs/chicken).
            Keep advice encouraging, realistic, scientific, and free from extreme crash diets.
        """.trimIndent()

        val rootJson = JSONObject().apply {
            val contents = JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "user")
                    put("parts", JSONArray().apply {
                        put(JSONObject().put("text", "$systemContext\n\nUser Question: $prompt"))
                    })
                })
            }
            put("contents", contents)
        }

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = rootJson.toString().toRequestBody(mediaType)
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw Exception("HTTP ${response.code}: ${response.message}")
            }
            val resBody = response.body?.string().orEmpty()
            val parsed = JSONObject(resBody)
            val candidates = parsed.getJSONArray("candidates")
            if (candidates.length() > 0) {
                val first = candidates.getJSONObject(0)
                val content = first.getJSONObject("content")
                val parts = content.getJSONArray("parts")
                val text = parts.getJSONObject(0).getString("text")
                return text
            }
            throw Exception("Empty candidates in response")
        }
    }

    fun generateIntelligentMaharashtrianReply(query: String, profile: UserProfile): String {
        val lower = query.lowercase().trim()
        val isMr = profile.language == Language.MARATHI
        val isVeg = profile.foodPreference == FoodPreference.VEGETARIAN

        return when {
            // Food / Diet questions
            lower.contains("eat") || lower.contains("खा") || lower.contains("आहार") || lower.contains("diet") || lower.contains("meal") -> {
                if (isMr) {
                    """
                    नमस्कार ${profile.name}! तुमच्या ${profile.goal.marathiLabel} ध्येयासाठी आजचा संतुलित महाराष्ट्रीयन आहार खालीलप्रमाणे आहे:

                    • सकाळचा नाश्ता: २ मूग डाळ / उसळ चीला + १ वाटी ताजे दही (किंवा कांदा पोहे शेंगदाणे घालून) ~ ४०० kcal
                    • दुपारचे जेवण: १-२ ज्वारीची भाकरी + १ वाटी मोड आलेली मटकी उसळ + वरण + भरपूर काकडी-टोमॅटो कोशिंबीर ~ ५५० kcal
                    • संध्याकाळी: मूठभर भाजलेले फुटाणे + १ तुकडा गूळ + ताक ~ १५० kcal
                    • रात्रीचे जेवण: ${if (isVeg) "हलकी मूग डाळ खिचडी + कढी" else "१ भाकरी + वाटीभर गावरान चिकन रस्सा / अंड्याची भुर्जी"} ~ ४०० kcal

                    💡 टीप: जेवणानंतर लगेच पाणी पिणे टाळा, किमान ४० मिनिटांनी कोमट पाणी प्या.
                    """.trimIndent()
                } else {
                    """
                    Hello ${profile.name}! Here is your personalized Maharashtrian nutrition plan for ${profile.goal.englishLabel}:

                    • Breakfast: 2 Sprouted Moong Chillas + 1 bowl fresh curd (or Kanda Poha with peanuts) ~ 400 kcal
                    • Lunch: 1-2 Jowar Bhakris + Sprouted Matki Usal + Yellow Tur Dal + fresh Cucumber Koshimbir ~ 550 kcal
                    • Evening Snack: Handful of roasted chana + a small bite of organic jaggery + Green Tea ~ 150 kcal
                    • Dinner: ${if (isVeg) "Light Moong Dal Khichdi + spiced Kadhi" else "1 Rice Bhakri + light Chicken Rassa / 2 Boiled Eggs"} ~ 400 kcal

                    💡 Pro Tip: Hydrate well with spiced buttermilk (Taak) between meals for digestion and sustained fat loss.
                    """.trimIndent()
                }
            }

            // Bhakri / Poli / Rice / Dal specific queries
            lower.contains("bhakri") || lower.contains("भाकरी") || lower.contains("poli") || lower.contains("पोळी") || lower.contains("rice") || lower.contains("भात") || lower.contains("usal") -> {
                if (isMr) {
                    """
                    होय, नक्कीच! महाराष्ट्रीयन जेवणात भाकरी आणि डाळ-भात हे आरोग्यासाठी अतिशय फायदेशीर आहेत:

                    १. ज्वारी व बाजरीची भाकरी: यामध्ये ग्लुटेन नसते आणि फायबर भरपूर असते. हे वजन कमी करण्यासाठी सर्वोत्तम आहे.
                    २. गव्हाची पोळी: पोळी खाताना त्यावर जास्त तेल किंवा तूप लावू नका; एका वेळी १-२ पोळ्या पुरेशा आहेत.
                    ३. डाळ-भात: भात पूर्णपणे बंद करण्याची गरज नाही! भाताचे प्रमाण १ लहान वाटी ठेवून त्यावर भरपूर डाळ किंवा वरण आणि कोशिंबीर घ्या.
                    ४. उसळ: मोड आलेली मटकी किंवा मूग हे प्रथिनांचा (Protein) उत्तम स्त्रोत आहेत.
                    """.trimIndent()
                } else {
                    """
                    Absolutely! Authentic Maharashtrian foods are naturally nutrient-dense:

                    1. Jowar & Bajra Bhakri: Gluten-free and loaded with complex fiber. Keeps you full for hours and prevents sugar spikes.
                    2. Poli / Chapati: Stick to whole wheat with minimal oil; 1-2 chapatis per meal are ideal for steady energy.
                    3. Rice & Dal: You don't need to quit rice! Follow the 1:2 ratio—one small bowl of rice paired with two bowls of hearty dal/varan and veggies.
                    4. Sprouted Usal: Matki and Moong usal are powerhouses of plant protein, B vitamins, and potassium.
                    """.trimIndent()
                }
            }

            // Workout / Exercise queries
            lower.contains("workout") || lower.contains("exercise") || lower.contains("व्यायाम") || lower.contains("कसरत") || lower.contains("training") -> {
                if (isMr) {
                    """
                    आजचा ३० मिनिटांचा घरगुती व्यायाम दिनक्रम:

                    १. वॉर्म-अप (५ मिनिटे): जागच्या जागी धावणे (Spot Jogging) + हात-पाय स्ट्रेचिंग
                    २. मुख्य व्यायाम (२० मिनिटे):
                       • पुश-अप्स: ३ सेट्स × १२ वेळा
                       • स्क्वॉट्स (बैठका): ३ सेट्स × १५ वेळा
                       • प्लँक (Plank): ३ सेट्स × ४० सेकंद
                       • जम्पिंग जॅक्स: ३ सेट्स × ३० सेकंद
                    ३. कूल डाऊन (५ मिनिटे): शवासन व खोल श्वासोच्छ्वास

                    🔥 हा व्यायाम केल्याने शरीरातील अतिरिक्त कॅलरीज जळतील व ऊर्जा वाढेल!
                    """.trimIndent()
                } else {
                    """
                    Here is your targeted 30-minute workout plan for today:

                    1. Warm-Up (5 Min): High knee marching, arm swings, and dynamic ankle rotations.
                    2. Circuit Routine (20 Min):
                       • Push-Ups: 3 Sets × 12 Reps (Upper body strength)
                       • Bodyweight Squats: 3 Sets × 15 Reps (Thigh & glute power)
                       • Standard Plank: 3 Sets × 45 Sec (Core stability)
                       • Mountain Climbers: 3 Sets × 30 Sec (Cardio burn)
                    3. Cool-Down (5 Min): Deep hamstring stretch, child's pose, and deep diaphragmatic breathing.
                    """.trimIndent()
                }
            }

            // Weight loss / Fat loss queries
            lower.contains("weight") || lower.contains("fat") || lower.contains("वजन") || lower.contains("चरबी") || lower.contains("बारीक") -> {
                if (isMr) {
                    """
                    वजन सुरक्षितपणे आणि कायमस्वरूपी कमी करण्याचे ४ सुवर्ण नियम:

                    १. साखरेचे प्रमाण शून्य करा: चहामध्ये साखर कमी करा, कोल्ड्रिंक्स व फरसाण बंद करा.
                    २. रोज ८,००० ते १०,००० पावले चाला: दररोज न चुकता चालणे हे चरबी घटवण्याचा सर्वात सोपा मार्ग आहे.
                    ३. रात्रीचे जेवण हलके व वेळेवर: रात्री ८:३० च्या आधी जेवा; रात्री भात किंवा जड अन्न टाळून उसळ किंवा सूप घ्या.
                    ४. दररोज ३ लिटर पाणी प्या: डिहायड्रेशनमुळे भूक जास्त लागते; भरपूर पाणी प्यायल्याने चयापचय (Metabolism) जलद राहतो.
                    """.trimIndent()
                } else {
                    """
                    4 Golden Principles for Sustainable Weight Loss:

                    1. Cut liquid calories: Skip sugary sodas, sweetened chai, and fried snacks.
                    2. Hit 8,000–10,000 steps daily: Consistent daily movement beats occasional intense workouts.
                    3. Early & light dinners: Wrap up dinner before 8:30 PM with steamed proteins or Moong soup.
                    4. Drink 3 Liters of water: Proper hydration accelerates metabolic rate and curbs cravings.
                    """.trimIndent()
                }
            }

            // Default warm encouraging reply
            else -> {
                if (isMr) {
                    """
                    नमस्कार ${profile.name}! मी तुमचा FitMarathi AI Coach आहे. 

                    तुम्ही मला खालीलपैकी कोणताही प्रश्न विचारू शकता:
                    • "आज काय खायला पाहिजे?" (दैनंदिन आहार सल्ला)
                    • "वजन कसे कमी करावे?" (फॅट लॉस मार्गदर्शन)
                    • "भाकरी खावी की पोळी?" (महाराष्ट्रीयन आहार शंका)
                    • "कोणता व्यायाम करू?" (३० मिनिटांचा वर्कआउट)
                    • "पाणी किती प्यावे?" (हायड्रेशन टिप्स)

                    तुमच्या आरोग्यासाठी मी नेहमी मदतीला तयार आहे! 🌿
                    """.trimIndent()
                } else {
                    """
                    Hello ${profile.name}! I am your personal FitMarathi AI Coach.

                    You can ask me anything about:
                    • "What should I eat today?" (Custom meal plan)
                    • "Can I eat bhakri, rice, and usal?" (Maharashtrian nutrition guidance)
                    • "Which 30-min workout should I do?" (Targeted home training)
                    • "How to stay consistent with water & steps?" (Daily habit building)

                    Let's work together to reach your health goals! 🌿
                    """.trimIndent()
                }
            }
        }
    }
}
