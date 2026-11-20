# FitMarathi — Complete AI Fitness & Wellness App (तंदुरुस्त शरीर, आनंदी जीवन)

**FitMarathi** is a premier, mobile-first fitness, nutrition, and wellness application built specifically for Maharashtra. Designed using modern Android architecture with Kotlin, Jetpack Compose, Material 3, and the Gemini API, it provides bilingual support in both **Marathi (मराठी)** and **English**.

---

## 🌟 Core Features & Modules

### 1. 🌿 Culturally Authentic Maharashtrian Nutrition
- **Three Dietary Preferences:** Vegetarian (शाकाहारी), Non-Vegetarian (मांसाहारी), and Both (दोन्ही).
- **Native Superfoods:** Jowar & Bajra Bhakri, Sprouted Matki & Moong usal, Varan Bhat, Pithla, Kanda Poha, Upma, Spiced Buttermilk (Taak/Mattha), boiled eggs, and light chicken rassa.
- **Portion & Macro Guidance:** Estimated calories, protein, carbs, and fats per meal.
- **Meal Logging & Substitutions:** Interactive logging with smart swaps (e.g., Bhakri instead of refined roti, sprouted pulses for plant protein).
- **Budget-Friendly Grocery Checklist:** Interactive grocery shopping list for native grains, pulses, dairy, and spices.
- **Healthy Recipes:** Easy-to-make, low-oil preparation recipes for quick home cooking.

### 2. 💪 Workout Library — 60 Curated Exercises
- **Categorized Library:** Strength Training, Fat Loss, Bodyweight, Cardio, Core & Abs, and Mobility & Flexibility.
- **Levels:** Beginner (नवशिक्या), Intermediate (मध्यम), and Advanced (प्रगत).
- **Custom Durations:** Quick 10-Minute Energy Blast, 30-Minute Fat Loss Core, and 60-Minute Full-Body Strength.
- **Instructions & Modifications:** Detailed Marathi and English instructions, sets, repetitions, rest periods, and easy modification variations.
- **Active Workout Session Timer:** Interactive guided workout counter with countdown timer and calorie tracking.

### 3. 🧘 Yoga, Meditation & Pranayama
- **Morning Surya Namaskar (15 Min):** Spine activation and metabolic boost.
- **Stress Relief Yoga (20 Min):** Cortisol reduction and gentle relaxation.
- **Weight Loss Power Yoga (25 Min):** Core strengthening and visceral fat burning.
- **Night Sleep Meditation (10 Min):** Restorative sleep and mental tranquility.
- **Pranayama (15 Min):** Anulom Vilom, Kapalbhati, Bhramari, and Sheetali for respiratory capacity.

### 4. 🤖 AI Personal Coach (Gemini API + Localized Intelligent Engine)
- **Bilingual Conversations:** Direct questions and answers in Marathi or English.
- **Personalized Context:** Understands user age, BMI, fitness goal (Fat Loss, Muscle Gain, Maintain), and food preferences.
- **Quick-Prompt Chips:** Single-tap questions ("आज काय खायला पाहिजे?", "भाकरी खावी की पोळी?", "३० मिनिटे कसरत", "वजन कसे कमी करावे?").
- **Graceful Fallback:** If a Gemini API key is not yet set, the app seamlessly runs in **Smart Offline Demo Mode** with authentic Maharashtrian nutrition and exercise recommendations.

### 5. 💧 Hydration & Activity Tracking
- **Circular Water Gauge:** Tracks daily intake against customizable goals (e.g., 1.5 L / 3.0 L).
- **Quick-Log Buttons:** Instant +250 ml, +500 ml, +1.0 L, and Reset actions.
- **Reminder Intervals:** 30 min, 1 hr, 2 hrs, or custom intervals with saved settings.
- **Steps & Calorie Metrics:** Real-time step progress (8,462 / 10,000) and calorie expenditure tracking.

### 6. 📊 Progress Analytics & Health Reports
- **Multi-Period Analytics:** Weekly, Monthly, and Yearly comparison tabs.
- **Weight Trend Chart:** Visual graph showing day-by-day weight tracking.
- **Overall Health Score:** 78/100 (Good / उत्कृष्ट) circular meter.
- **Diabetes & Lifestyle Risk Assessment:** 28% Moderate Risk gauge with actionable lifestyle checklist (sugar reduction, daily 30m exercise, weight management).
- **Educational Condition Indicators:** High Blood Pressure, Heart Disease, Fatty Liver, Thyroid balance.
- **Medical Disclaimers:** Transparent disclaimers clarifying that algorithms provide educational estimates and recommend official clinical checkups with doctors.

### 7. 💈 Personal Care, Grooming & Face Shape Styling
- **Hair Care:** Fall prevention tips, scalp nourishment, curry leaf oil massage, and protein nutrition.
- **Skin Care:** Hydration routines, sun protection (SPF), and traditional ubtan remedies.
- **Personality Archetype:** "Confident & Calm" (शांत, विचारपूर्वक निर्णय घेणारे आणि आत्मविश्वासी).
- **Curated Shirt Color Palettes:** Navy Blue, Crisp White, Slate Grey, Olive Green.
- **Face Shape Styling:** Oval face recommendations for Textured Quiff, Classic Fade, Light Stubble, and Wayfarer glasses.

### 8. 🔔 In-App Notifications & Reminders
- Filterable notifications across Reminders, Tips, and Health Alerts.
- Individual toggle controls for water, workout, and meal alerts.
- Daily motivational Marathi quotes ("आजचा छोटा प्रयत्न, उद्याचे मोठे यश!").

### 9. ⚙️ Profile, Privacy & Security
- **Profile Editing:** Update name, age, weight, height, and fitness goals with automatic BMI recalculation.
- **Instant Language Toggle:** Toggle between Marathi and English anywhere with a single tap.
- **Privacy Controls:** Clear photo consent disclosure, data export, and local data deletion buttons.

---

## 🛠️ Tech Stack & Architecture

- **Language:** Kotlin 2.2.10
- **UI Framework:** Jetpack Compose with Material Design 3 (M3)
- **Design System:** Custom FitMarathi Theme (Deep Green `#0F5132`, Teal `#0D9488`, Saffron `#EA580C`, White `#FFFFFF`)
- **Architecture:** Clean MVVM with Reactive StateFlow and unidirectional data flow
- **Networking:** OkHttp & Retrofit for Gemini API inference
- **App Check:** Integrated Firebase App Check verification (`configureAppCheck`)
- **Testing:** Local JVM Unit Tests & Robolectric

---

## 🔑 Setup & Configuration Guide

### 1. Gemini API Key Configuration
1. Open Google AI Studio (or Cloud Console) and generate an API key.
2. In the AI Studio **Secrets** panel, add the key under `GEMINI_API_KEY`.
3. The Secrets Gradle Plugin automatically injects `BuildConfig.GEMINI_API_KEY` at build time.
4. If no key is configured, FitMarathi automatically runs in its intelligent offline Maharashtrian nutrition engine.

### 2. Google Sign-In & Firebase Configuration
1. To configure full cloud synchronization, add your `google-services.json` to `/app`.
2. Activate the pre-configured Firebase dependencies in `app/build.gradle.kts`:
   - `firebase-firestore`
   - `firebase-auth`
   - `credentials`
3. FitMarathi is architected to run immediately in demo mode with local state persistence so you can test all features without requiring credentials.

---

## 📱 Running the App

To compile and verify the app:
```bash
gradle :app:assembleDebug
```

To run unit and Robolectric tests:
```bash
gradle :app:testDebugUnitTest
```
