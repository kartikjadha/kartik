package com.example.data.repository

import com.example.data.model.*

object SampleData {

    // 60 Complete Exercises across categories and levels
    val allExercises: List<ExerciseItem> = listOf(
        // Strength & Bodyweight
        ExerciseItem("ex_1", "Push Ups", "पुश-अप्स", ExerciseCategory.STRENGTH, WorkoutLevel.BEGINNER, "3 Sets × 10-12 Reps", "३ सेट्स × १०-१२ वेळा", 45, 30, 45, "Keep back straight and lower chest near floor.", "पाठ सरळ ठेवा आणि छाती जमिनीजवळ न्या.", "Knee push ups for easier variant", "गुडघे टेकवून सोपा प्रकार करा"),
        ExerciseItem("ex_2", "Bodyweight Squats", "स्क्वॉट्स (बैठका)", ExerciseCategory.STRENGTH, WorkoutLevel.BEGINNER, "3 Sets × 15 Reps", "३ सेट्स × १५ वेळा", 45, 30, 50, "Feet shoulder-width apart, sit hips back.", "पाय खांद्यांच्या अंतरावर ठेवा, नितंब मागे न्या.", "Hold a chair for balance", "सपोर्टसाठी खुर्चीचा आधार घ्या"),
        ExerciseItem("ex_3", "Walking Lunges", "लंजेस", ExerciseCategory.STRENGTH, WorkoutLevel.BEGINNER, "3 Sets × 12 Reps/leg", "३ सेट्स × १२ वेळा (दोन्ही पाय)", 45, 30, 48, "Step forward, lower rear knee toward floor.", "पुढे पाऊल टाका, मागील गुडघा खाली आणा.", "Stationary reverse lunges", "जागीच रिव्हर्स लंजेस करा"),
        ExerciseItem("ex_4", "Standard Plank", "प्लँक", ExerciseCategory.CORE, WorkoutLevel.BEGINNER, "3 Sets × 30-45 Sec", "३ सेट्स × ३०-४५ सेकंद", 45, 30, 35, "Forearms on ground, brace core like a table.", "कोपर जमिनीवर ठेवून पोट घट्ट धरून ठेवा.", "Plank on knees", "गुडघे टेकवून प्लँक करा"),
        ExerciseItem("ex_5", "Jumping Jacks", "जम्पिंग जॅक्स", ExerciseCategory.CARDIO, WorkoutLevel.BEGINNER, "3 Sets × 30 Sec", "३ सेट्स × ३० सेकंद", 30, 20, 40, "Jump feet out while clapping arms overhead.", "पाय पसरवत उडी मारा आणि हात डोक्यावर टाळी वाजवा.", "Step jacks (low impact)", "उडी न मारता हळूहळू पावले हलवा"),
        ExerciseItem("ex_6", "Mountain Climbers", "माउंटन क्लायम्बर्स", ExerciseCategory.CARDIO, WorkoutLevel.INTERMEDIATE, "3 Sets × 30 Sec", "३ सेट्स × ३० सेकंद", 30, 20, 55, "From pushup plank, drive knees to chest rapidly.", "प्लँकमधून आळीपाळीने गुडघे छातीजवळ वेगाने आणा.", "Slow controlled pace", "हळूहळू एका लयीत करा"),
        ExerciseItem("ex_7", "Burpees", "बर्पीज", ExerciseCategory.FAT_LOSS, WorkoutLevel.ADVANCED, "3 Sets × 10 Reps", "३ सेट्स × १० वेळा", 45, 40, 70, "Squat down, kick back to plank, push up, jump up.", "खाली बसा, पाय मागे फेका, पुशअप करून उंच उडी मारा.", "Step-back burpees without jump", "उडी न मारता फक्त पाय मागे घ्या"),
        ExerciseItem("ex_8", "Dips on Bench / Chair", "बेंच डिप्स", ExerciseCategory.STRENGTH, WorkoutLevel.BEGINNER, "3 Sets × 12 Reps", "३ सेट्स × १२ वेळा", 40, 30, 40, "Hands on stable edge, lower hips, push up.", "खुर्चीवर हात ठेवून कोपर वाकवून शरीरास खाली आणा.", "Bend knees at 90 degrees", "गुडघे ९० अंशात दुमडून ठेवा"),
        ExerciseItem("ex_9", "Glute Bridges", "ग्लूट ब्रिजेस", ExerciseCategory.CORE, WorkoutLevel.BEGINNER, "3 Sets × 15 Reps", "३ सेट्स × १५ वेळा", 40, 20, 35, "Lie on back, drive through heels, raise hips.", "पाठीवर झोपा आणि टाचांवर जोर देऊन कंबर वर उचला.", "Hold at top for 3 seconds", "वर आल्यावर ३ सेकंद थांबा"),
        ExerciseItem("ex_10", "High Knees", "हाय नीज (उंच गुडघे)", ExerciseCategory.CARDIO, WorkoutLevel.BEGINNER, "3 Sets × 30 Sec", "३ सेट्स × ३० सेकंद", 30, 20, 50, "Run in place driving knees up toward waist.", "जागीच धावत गुडघे कमरेपर्यंत उंच उचला.", "High knee marching", "हळूहळू उंच गुडघे उचलून चाला"),

        // Core & Abs
        ExerciseItem("ex_11", "Bicycle Crunches", "बायसिकल क्रंचेस", ExerciseCategory.CORE, WorkoutLevel.INTERMEDIATE, "3 Sets × 20 Reps", "३ सेट्स × २० वेळा", 45, 30, 45, "Touch opposite elbow to alternate knee.", "उलट कोपर विरुद्ध गुडघ्याला टेकवण्याचा प्रयत्न करा.", "Keep feet on floor between reps", "प्रत्येक रेप नंतर पाय टेकवा"),
        ExerciseItem("ex_12", "Side Plank", "साईड प्लँक", ExerciseCategory.CORE, WorkoutLevel.INTERMEDIATE, "3 Sets × 30 Sec/side", "३ सेट्स × ३० सेकंद (दोन्ही बाजू)", 40, 20, 35, "Prop on one forearm, raise hip off floor.", "एका कोपरावर शरीराचे वजन पेलून कंबर वर ठेवा.", "Bottom knee resting on floor", "खालचा गुडघा जमिनीवर टेकवा"),
        ExerciseItem("ex_13", "Russian Twists", "रशियन ट्विस्ट", ExerciseCategory.CORE, WorkoutLevel.INTERMEDIATE, "3 Sets × 20 Reps", "३ सेट्स × २० वेळा", 45, 30, 42, "Sit back slightly, twist torso left to right.", "किंचित मागे कलून डावीकडून उजवीकडे शरीर फिरवा.", "Keep heels on floor", "टाचा जमिनीवर टेकवून ठेवा"),
        ExerciseItem("ex_14", "Dead Bug", "डेड बग", ExerciseCategory.CORE, WorkoutLevel.BEGINNER, "3 Sets × 12 Reps", "३ सेट्स × १२ वेळा", 45, 20, 30, "Lower opposite arm and leg while core pressed.", "विरुद्ध हात व पाय हळूहळू खाली आणा, पाठ जमिनीला टेकवा.", "Keep knees bent 90 degrees", "गुडघे ९० अंशात ठेवा"),
        ExerciseItem("ex_15", "Bird Dog", "बर्ड डॉग", ExerciseCategory.CORE, WorkoutLevel.BEGINNER, "3 Sets × 12 Reps/side", "३ सेट्स × १२ वेळा", 40, 20, 28, "On hands and knees, extend opposite arm & leg.", "चौपाया स्थितीतून विरुद्ध हात आणि पाय सरळ करा.", "Lift arm only first", "सुरुवातीला फक्त हात उचला"),
        ExerciseItem("ex_16", "Leg Raises", "लेग रेजेस (पाय उचलणे)", ExerciseCategory.CORE, WorkoutLevel.INTERMEDIATE, "3 Sets × 12 Reps", "३ सेट्स × १२ वेळा", 45, 30, 40, "Lie flat, raise straight legs to 90 degrees.", "सरळ झोपून पाय गुडघ्यात न वाकवता ९० अंशात वर उचला.", "Bend knees slightly", "गुडघे किंचित वाकवून पाय उचला"),
        ExerciseItem("ex_17", "Flutter Kicks", "फ्लटर किक्स", ExerciseCategory.CORE, WorkoutLevel.INTERMEDIATE, "3 Sets × 30 Sec", "३ सेट्स × ३० सेकंद", 35, 25, 38, "Legs 6 inches off floor, alternate small kicks.", "पाय जमिनीपासून थोडे वर ठेवून पोहण्यासारखी हालचाल करा.", "Hands under lower back", "कमरेखाली हाताचा आधार द्या"),
        ExerciseItem("ex_18", "Hollow Body Hold", "हॉलो बॉडी होल्ड", ExerciseCategory.CORE, WorkoutLevel.ADVANCED, "3 Sets × 30 Sec", "३ सेट्स × ३० सेकंद", 30, 30, 45, "Lower back pinned, hold arms and legs raised.", "पाठ जमिनीवर ठेवून हात आणि पाय वर धरून ठेवा.", "Tuck knees toward chest", "गुडघे छातीजवळ दुमडा"),

        // Cardio & Fat Loss
        ExerciseItem("ex_19", "Shadow Boxing", "शॅडो बॉक्सिंग", ExerciseCategory.CARDIO, WorkoutLevel.BEGINNER, "3 Sets × 45 Sec", "३ सेट्स × ४५ सेकंद", 45, 20, 45, "Throw jabs, crosses, hooks with light bounce.", "हलके पंच मारत पावलांची चपळ हालचाल करा.", "Throw punches without foot bounce", "जागेवर स्थिर राहून पंच मारा"),
        ExerciseItem("ex_20", "Skaters", "स्केटर जम्प्स", ExerciseCategory.CARDIO, WorkoutLevel.INTERMEDIATE, "3 Sets × 30 Sec", "३ सेट्स × ३० सेकंद", 35, 25, 52, "Bound laterally from side to side.", "एका बाजूने दुसऱ्या बाजूला तोल सांभाळत उड्या मारा.", "Lateral step touch instead of jump", "उडी न मारता फक्त साइड स्टेप्स घ्या"),
        ExerciseItem("ex_21", "Squat Jumps", "स्क्वॉट जम्प्स", ExerciseCategory.FAT_LOSS, WorkoutLevel.INTERMEDIATE, "3 Sets × 12 Reps", "३ सेट्स × १२ वेळा", 40, 30, 60, "Squat down, explode vertically into jump.", "स्क्वॉट करून वर येताना हवेत उंच उडी मारा.", "Regular speed squats", "उडी न मारता नेहमीचे स्क्वॉट्स करा"),
        ExerciseItem("ex_22", "Step Ups (Stairs / Box)", "स्टेप अप्स", ExerciseCategory.CARDIO, WorkoutLevel.BEGINNER, "3 Sets × 15 Reps/leg", "३ सेट्स × १५ वेळा", 45, 25, 45, "Step up forcefully onto elevated stair.", "पायरीवर एक पाय ठेवून संपूर्ण शरीर वर उचला.", "Use lower step height", "कमी उंचीची पायरी वापरा"),
        ExerciseItem("ex_23", "Bear Crawl", "बेअर क्रॉल (अस्वल चाल)", ExerciseCategory.FAT_LOSS, WorkoutLevel.INTERMEDIATE, "3 Sets × 30 Sec", "३ सेट्स × ३० सेकंद", 30, 30, 50, "Crawl forward with knees 2 inches off floor.", "गुडघे जमिनीपासून थोडे वर ठेवून पुढे चाला.", "Hold static beast position", "पुढे न चालता एकाच जागी थांबा"),
        ExerciseItem("ex_24", "Jumping Rope (or Ghost Rope)", "दोरीवरच्या उड्या", ExerciseCategory.CARDIO, WorkoutLevel.BEGINNER, "3 Sets × 45 Sec", "३ सेट्स × ४५ सेकंद", 45, 20, 65, "Rhythmic double foot or alternating jump.", "दोरी फिरवत एका लयीत उड्या मारा.", "Single foot gentle calf hops", "एका पायाने हलक्या उड्या मारा"),

        // Upper Body & Strength
        ExerciseItem("ex_25", "Diamond Push Ups", "डायमंड पुश-अप्स", ExerciseCategory.STRENGTH, WorkoutLevel.ADVANCED, "3 Sets × 8-10 Reps", "३ सेट्स × ८-१० वेळा", 40, 40, 50, "Hands touching in diamond shape under chest.", "हात एकत्र जोडून छातीखाली डायमंड आकारात ठेवा.", "Perform on elevated surface", "हाताखाली उंच टेबल किंवा पायरी घ्या"),
        ExerciseItem("ex_26", "Pike Push Ups", "पाईक पुश-अप्स (खांदे)", ExerciseCategory.STRENGTH, WorkoutLevel.INTERMEDIATE, "3 Sets × 10 Reps", "३ सेट्स × १० वेळा", 45, 30, 45, "Hips high in inverted V, press crown down.", "उलटा 'V' आकार करून डोके हातांच्या मध्ये खाली आणा.", "Reduce hip height angle", "कमर थोडी कमी उंच ठेवा"),
        ExerciseItem("ex_27", "Doorframe Rows", "डोअरफ्रेम रो (पाठीचा व्यायाम)", ExerciseCategory.STRENGTH, WorkoutLevel.BEGINNER, "3 Sets × 12 Reps", "३ सेट्स × १२ वेळा", 40, 30, 35, "Grip door frame, lean back, pull chest in.", "दाराची चौकट पकडून मागे कलून शरीर पुढे ओढा.", "Stand more upright for less resistance", "शरीर थोडे अधिक सरळ उभे ठेवा"),
        ExerciseItem("ex_28", "Superman Holds", "सुपरमॅन होल्ड", ExerciseCategory.STRENGTH, WorkoutLevel.BEGINNER, "3 Sets × 12 Reps", "३ सेट्स × १२ वेळा", 35, 20, 30, "Lie prone, lift arms and legs simultaneously.", "पोटावर झोपा आणि हात व पाय एकाच वेळी वर उचला.", "Lift arms only, then legs", "आधी फक्त हात, मग फक्त पाय उचला"),
        ExerciseItem("ex_29", "Tricep Desk Dips", "ट्रायसेप डिप्स", ExerciseCategory.STRENGTH, WorkoutLevel.BEGINNER, "3 Sets × 12 Reps", "३ सेट्स × १२ वेळा", 40, 25, 38, "Press hands onto chair, work back of arms.", "हातांनी खुर्ची दाबून कोपर सरळ करा.", "Keep feet closer to chair", "पाय खुर्चीच्या जवळ ठेवा"),
        ExerciseItem("ex_30", "Wide Push Ups", "वाईड पुश-अप्स", ExerciseCategory.STRENGTH, WorkoutLevel.INTERMEDIATE, "3 Sets × 12 Reps", "३ सेट्स × १२ वेळा", 45, 30, 48, "Hands placed wider than shoulders.", "हात खांद्यांपेक्षा जास्त रुंद अंतरावर ठेवा.", "Wide incline push ups", "टेबलावर हात ठेवून करा"),

        // Lower Body & Legs
        ExerciseItem("ex_31", "Wall Sit", "वॉल सिट (भिंतीचा आधार)", ExerciseCategory.STRENGTH, WorkoutLevel.BEGINNER, "3 Sets × 30-45 Sec", "३ सेट्स × ३०-४५ सेकंद", 45, 30, 38, "Back against wall, thighs parallel to ground.", "भिंतीला पाठ टेकवून खुर्चीसारखे ९० अंशात बसा.", "Higher hip angle (45 degrees)", "कमर थोडी वर ठेवा"),
        ExerciseItem("ex_32", "Calf Raises", "काफ रेजेस (घोट्याचा व्यायाम)", ExerciseCategory.STRENGTH, WorkoutLevel.BEGINNER, "3 Sets × 20 Reps", "३ सेट्स × २० वेळा", 35, 20, 25, "Rise up on tiptoes, pause, lower slowly.", "पावलांच्या बोटांवर उभे राहून टाचा वर उचला.", "Two feet on flat ground", "जमिनीवर दोन्ही पायांवर करा"),
        ExerciseItem("ex_33", "Sumo Squats", "सुमो स्क्वॉट्स", ExerciseCategory.STRENGTH, WorkoutLevel.BEGINNER, "3 Sets × 15 Reps", "३ सेट्स × १५ वेळा", 45, 30, 48, "Wide stance, toes pointed 45 degrees out.", "पाय जास्त पसरून बोटे बाहेरच्या बाजूला वळवा.", "Partial squat depth", "अर्ध्या खोलीपर्यंत खाली जा"),
        ExerciseItem("ex_34", "Bulgarian Split Squats", "बल्गेरियन स्प्लिट स्क्वॉट्स", ExerciseCategory.STRENGTH, WorkoutLevel.ADVANCED, "3 Sets × 10 Reps/leg", "३ सेट्स × १० वेळा/पाय", 50, 40, 60, "Rear foot elevated on bed/chair, squat.", "मागील पाय खुर्चीवर ठेवून पुढील पायावर खाली बसा.", "Standard static lunge", "नेहमीचे स्टॅटिक लंज करा"),
        ExerciseItem("ex_35", "Single-Leg Glute Bridge", "सिंगल-लेग ग्लूट ब्रिज", ExerciseCategory.CORE, WorkoutLevel.INTERMEDIATE, "3 Sets × 10 Reps/leg", "३ सेट्स × १० वेळा/पाय", 40, 30, 40, "Drive through one heel while other leg in air.", "एक पाय हवेत ठेवून दुसऱ्या पायाने कंबर वर उचला.", "Double leg bridge", "दोन्ही पायांचा वापर करा"),
        ExerciseItem("ex_36", "Side Leg Raises", "साइड लेग रेजेस", ExerciseCategory.FLEXIBILITY, WorkoutLevel.BEGINNER, "3 Sets × 15 Reps/side", "३ सेट्स × १५ वेळा/बाजू", 35, 20, 25, "Lie on side, lift top leg with control.", "एका कुशीवर झोपून वरचा पाय सरळ वर उचला.", "Support back with wall", "भिंतीचा आधार घ्या"),
        ExerciseItem("ex_37", "Jump Lunges", "जम्प लंजेस", ExerciseCategory.FAT_LOSS, WorkoutLevel.ADVANCED, "3 Sets × 10 Reps", "३ सेट्स × १० वेळा", 45, 40, 65, "Switch lunge legs mid-air with spring.", "लंजेसमधून हवेत उडी मारून पाय बदला.", "Fast alternating stepping lunges", "उडी न मारता वेगाने पावले बदला"),
        ExerciseItem("ex_38", "Duck Walks", "डक वॉक (बदक चाल)", ExerciseCategory.STRENGTH, WorkoutLevel.ADVANCED, "3 Sets × 20 Steps", "३ सेट्स × २० पावले", 40, 35, 50, "Maintain low squat and step forward.", "खाली बसलेल्या स्थितीत तोल सांभाळत पुढे चाला.", "Quarter squat march", "थोडे कमी खाली वाका"),

        // Mobility & Flexibility
        ExerciseItem("ex_39", "Cat Cow Stretch", "मार्जरी आसन (कॅट-काऊ)", ExerciseCategory.FLEXIBILITY, WorkoutLevel.BEGINNER, "3 Sets × 10 Cycles", "३ सेट्स × १० वेळा", 45, 20, 20, "Alternate arching and rounding back with breath.", "श्वास घेत पाठ खाली आणि श्वास सोडत पाठ वर वाकवा.", "Perform seated in chair", "खुर्चीवर बसून पाठीची हालचाल करा"),
        ExerciseItem("ex_40", "Downward Dog to Cobra", "अधोमुख श्वानासन ते भुजंगासन", ExerciseCategory.FLEXIBILITY, WorkoutLevel.BEGINNER, "3 Sets × 8 Reps", "३ सेट्स × ८ वेळा", 50, 30, 32, "Flow from inverted V to chest-up cobra pose.", "उलटा 'V' करून हळूच पुढे सरकत छाती वर उचला.", "Hold each pose statically", "प्रत्येक आसनात स्थिर थांबा"),
        ExerciseItem("ex_41", "World's Greatest Stretch", "फुल बॉडी मोबिलिटी स्ट्रेच", ExerciseCategory.FLEXIBILITY, WorkoutLevel.INTERMEDIATE, "3 Sets × 6 Reps/side", "३ सेट्स × ६ वेळा/बाजू", 50, 25, 30, "Lunge forward, plant hand, rotate chest up.", "पुढे पाऊल टाका, एक हात जमिनीवर ठेवून छाती वर फिरवा.", "Knee on floor", "मागील गुडघा खाली टेकवून ठेवा"),
        ExerciseItem("ex_42", "Cossack Squats", "कॉसॅक स्क्वॉट्स (इनर थाई)", ExerciseCategory.FLEXIBILITY, WorkoutLevel.INTERMEDIATE, "3 Sets × 8 Reps/side", "३ सेट्स × ८ वेळा/बाजू", 45, 30, 40, "Shift weight deeply into one side squat.", "एका पायावर खाली बसून दुसरा पाय लांब सरळ ठेवा.", "Hold onto sturdy rail", "सपोर्टसाठी कशाला तरी पकडा"),
        ExerciseItem("ex_43", "Thoracic Spine Rotations", "छाती व पाठीचा रोटेशन स्ट्रेच", ExerciseCategory.FLEXIBILITY, WorkoutLevel.BEGINNER, "3 Sets × 10 Reps/side", "३ सेट्स × १० वेळा/बाजू", 40, 20, 20, "Hand behind head, rotate elbow skyward.", "हात डोक्यामागे ठेवून कोपर आकाशाकडे फिरवा.", "Seated trunk twist", "खुर्चीवर बसून शरीर फिरवा"),
        ExerciseItem("ex_44", "Deep Pigeon Pose", "कपोतासन स्ट्रेच", ExerciseCategory.FLEXIBILITY, WorkoutLevel.INTERMEDIATE, "3 Sets × 45 Sec/side", "३ सेट्स × ४५ सेकंद/बाजू", 45, 20, 22, "Front leg bent 90 under hip, rear straight.", "पुढील पाय दुमडून नितंबाखाली ठेवा, मागचा पाय लांब करा.", "Figure four stretch on back", "पाठीवर झोपून पाय दुमडा"),
        ExerciseItem("ex_45", "Butterfly Stretch", "बद्धकोणासन (फुलपाखरू स्ट्रेच)", ExerciseCategory.FLEXIBILITY, WorkoutLevel.BEGINNER, "3 Sets × 45 Sec", "३ सेट्स × ४५ सेकंद", 45, 20, 18, "Soles of feet together, flutter knees gently.", "पावलांचे तळवे एकमेकांना जोडून गुडघे हलवा.", "Support knees with pillows", "गुडघ्यांखाली उशा ठेवा"),

        // Additional Functional & Core
        ExerciseItem("ex_46", "Side Plank Hip Dips", "साइड प्लँक हिप डिप्स", ExerciseCategory.CORE, WorkoutLevel.ADVANCED, "3 Sets × 12 Reps/side", "३ सेट्स × १२ वेळा/बाजू", 40, 30, 42, "From side plank, touch hip to floor, raise.", "साईड प्लँकमधून कंबर खाली टेकवून पुन्हा वर उचला.", "Hold static side plank", "हालचाल न करता फक्त स्थिर राहा"),
        ExerciseItem("ex_47", "Inchworms", "इंचवर्म्स", ExerciseCategory.BODYWEIGHT, WorkoutLevel.BEGINNER, "3 Sets × 8 Reps", "३ सेट्स × ८ वेळा", 45, 30, 40, "Walk hands out from standing to plank, walk back.", "उभे राहून हातांनी चालत प्लँक करा व पुन्हा मागे या.", "Bend knees slightly as you walk", "हात नेताना गुडघे थोडे वाकवा"),
        ExerciseItem("ex_48", "Tuck Jumps", "टक जम्प्स", ExerciseCategory.FAT_LOSS, WorkoutLevel.ADVANCED, "3 Sets × 8 Reps", "३ सेट्स × ८ वेळा", 35, 40, 65, "Explode upward pulling knees to chest.", "वर उडी मारून हवेतच दोन्ही गुडघे छातीजवळ ओढा.", "Speed squats without jump", "उडी न मारता जलद स्क्वॉट्स करा"),
        ExerciseItem("ex_49", "Flutter Arm Pulses", "आर्म पल्स स्ट्रेन्थ", ExerciseCategory.STRENGTH, WorkoutLevel.BEGINNER, "3 Sets × 45 Sec", "३ सेट्स × ४५ सेकंद", 45, 20, 30, "Arms extended horizontally, small circular pulses.", "हात सरळ बाजूला पसरून लहान गोलाकार फिरवा.", "Rest every 15 seconds", "१५ सेकंदांनी हात खाली घ्या"),
        ExerciseItem("ex_50", "Frogger Jumps", "फ्रॉगर जम्प्स (बेडूक उडी)", ExerciseCategory.FAT_LOSS, WorkoutLevel.INTERMEDIATE, "3 Sets × 12 Reps", "३ सेट्स × १२ वेळा", 40, 30, 52, "From plank, jump feet outside hands, tap ground.", "प्लँकमधून पाय हातांच्या बाहेर उडी मारून आणा.", "Step feet forward one at a time", "एकेक पाय पुढे पाऊल टाका"),

        // Full Body Conditioning
        ExerciseItem("ex_51", "Cross-Body Climbers", "क्रॉस-बॉडी क्लायम्बर्स", ExerciseCategory.CARDIO, WorkoutLevel.INTERMEDIATE, "3 Sets × 30 Sec", "३ सेट्स × ३० सेकंद", 30, 20, 50, "Knee drives diagonally to opposite elbow.", "गुडघा तिरपा उलट कोपराकडे वेगाने आणा.", "Slow oblique knee tuck", "हळूहळू एका लयीत करा"),
        ExerciseItem("ex_52", "Prone Cobra Holds", "कोब्रा होल्ड (पाठीचा कणा)", ExerciseCategory.STRENGTH, WorkoutLevel.BEGINNER, "3 Sets × 30 Sec", "३ सेट्स × ३० सेकंद", 35, 20, 25, "Thumbs up, shoulder blades squeezed together.", "अंगठे वर करून खांद्याचे पाते पाठीमागे आवळा.", "Rest forehead on towel between sets", "मधे कपाळ जमिनीवर टेकवा"),
        ExerciseItem("ex_53", "Lateral Bounds", "लॅटरल बाउंड्स", ExerciseCategory.CARDIO, WorkoutLevel.ADVANCED, "3 Sets × 20 Reps", "३ सेट्स × २० वेळा", 40, 30, 55, "Bound sideways sticking single-leg landing.", "एका बाजूला लांब उडी मारून एका पायावर तोल सांभाळा.", "Smaller hop distance", "कमी अंतरावर लहान उड्या मारा"),
        ExerciseItem("ex_54", "Crab Walk", "क्रॅब वॉक (खेकडा चाल)", ExerciseCategory.BODYWEIGHT, WorkoutLevel.BEGINNER, "3 Sets × 30 Sec", "३ सेट्स × ३० सेकंद", 30, 25, 40, "Hands and feet on ground, belly toward ceiling.", "हात व पायांवर उलटे होऊन पोट आकाशाकडे ठेवून चाला.", "Hold static reverse tabletop", "न चालता फक्त स्थिती धरून ठेवा"),
        ExerciseItem("ex_55", "Good Mornings (Hands on Head)", "गुड मॉर्निंग्स (कमर वाकवणे)", ExerciseCategory.STRENGTH, WorkoutLevel.BEGINNER, "3 Sets × 15 Reps", "३ सेट्स × १५ वेळा", 40, 20, 30, "Hinge at hips, keep flat back, return standing.", "कमरेतून पुढे ९० अंशात वाका, पाठ सरळ ठेवा.", "Slight knee bend", "गुडघे किंचित वाकवून ठेवा"),
        ExerciseItem("ex_56", "Shadow Skip", "शॅडो स्किप (वॉर्म अप)", ExerciseCategory.CARDIO, WorkoutLevel.BEGINNER, "3 Sets × 45 Sec", "३ सेट्स × ४५ सेकंद", 45, 15, 45, "Light skipping motion shaking off tension.", "हळुवार उड्या मारत संपूर्ण शरीर सैल सोडा.", "Walk in place briskly", "जागेवर जलद चाला"),
        ExerciseItem("ex_57", "V-Ups", "व्ही-अप्स", ExerciseCategory.CORE, WorkoutLevel.ADVANCED, "3 Sets × 10 Reps", "३ सेट्स × १० वेळा", 40, 35, 48, "Simultaneously raise torso and legs to V shape.", "हात आणि पाय एकाच वेळी वर आणून 'V' आकार बनवा.", "Bent-knee seated tucks", "गुडघे दुमडून छातीजवळ आणा"),
        ExerciseItem("ex_58", "Standing Oblique Crunch", "स्टँडिंग ओब्लिक क्रंच", ExerciseCategory.CORE, WorkoutLevel.BEGINNER, "3 Sets × 15 Reps/side", "३ सेट्स × १५ वेळा/बाजू", 35, 20, 32, "Bring elbow to same-side knee while standing.", "उभे राहून कोपर आणि गुडघा एका बाजूला एकत्र आणा.", "Hold chair for balance", "एका हाताने खुर्ची पकडा"),
        ExerciseItem("ex_59", "Ankle & Wrist Rotations", "घोटे व मनगट फिरवणे", ExerciseCategory.FLEXIBILITY, WorkoutLevel.BEGINNER, "2 Sets × 30 Sec", "२ सेट्स × ३० सेकंद", 30, 10, 15, "Circular movements to prepare joints.", "सांधे मोकळे करण्यासाठी मनगट व घोटे गोलाकार फिरवा.", "Seated gentle rolls", "बसून हळूच फिरवा"),
        ExerciseItem("ex_60", "Shavasana & Deep Relaxation", "शवासन व संपूर्ण विश्रांती", ExerciseCategory.FLEXIBILITY, WorkoutLevel.BEGINNER, "1 Set × 3-5 Min", "१ सेट × ३-५ मिनिटे", 180, 0, 10, "Lie flat, close eyes, release all muscular tension.", "पाठीवर शांत झोपा, डोळे मिटून प्रत्येक स्नायू सैल सोडा.", "Supported knees with bolster", "गुडघ्याखाली लोड किंवा उशी घ्या")
    )

    // Yoga, Meditation & Pranayama Sessions
    val yogaRoutines: List<YogaRoutine> = listOf(
        YogaRoutine(
            id = "yoga_1",
            titleEn = "Morning Surya Namaskar & Energy (15 Min)",
            titleMr = "सकाळची योगासने व सूर्यनमस्कार (१५ मिनिटे)",
            type = YogaType.YOGA,
            durationMinutes = 15,
            level = WorkoutLevel.BEGINNER,
            benefitEn = "Boosts metabolism, activates spine, strengthens core.",
            benefitMr = "पचनक्रिया सुधारते, पाठीचा कणा लवचिक होतो आणि ताजेतवाने वाटते.",
            stepsEn = listOf("Pranamasana (Prayer Pose)", "Hastauttanasana (Raised Arms)", "Padahastasana (Hand to Foot)", "Ashwa Sanchalanasana (Equestrian)", "Dandasana (Plank)", "Ashtanga Namaskara", "Bhujangasana (Cobra)", "Adho Mukha Svanasana (Downward Dog)"),
            stepsMr = listOf("प्रणामासन (हात जोडून उभे राहणे)", "हस्तउत्तानासन (हात वर नेणे)", "पादहस्तासन (पुढे वाकून पाय पकडणे)", "अश्वसंचालासना (एक पाय मागे)", "दंडासन (प्लँक स्थिती)", "अष्टांग नमस्कार", "भुजंगासन (छाती वर उचला)", "अधोमुख श्वानासन (पाय सरळ)")
        ),
        YogaRoutine(
            id = "yoga_2",
            titleEn = "Stress Relief & Relaxation Yoga (20 Min)",
            titleMr = "ताण कमी करणारी योगासने (२० मिनिटे)",
            type = YogaType.YOGA,
            durationMinutes = 20,
            level = WorkoutLevel.BEGINNER,
            benefitEn = "Reduces cortisol, calms nervous system, relieves back tension.",
            benefitMr = "मानसिक ताण कमी होतो, रक्तदाब नियंत्रणात राहतो आणि मन शांत होते.",
            stepsEn = listOf("Balasana (Child's Pose) - 3 min", "Marjaryasana (Cat-Cow) - 3 min", "Viparita Karani (Legs Up Wall) - 5 min", "Supta Baddha Konasana - 4 min", "Shavasana - 5 min"),
            stepsMr = listOf("बालासन (मुलासारखे झुकणे) - ३ मिनिटे", "मार्जरी आसन (मांजर पोझ) - ३ मिनिटे", "विपरीत करणी (भिंतीवर पाय ठेवणे) - ५ मिनिटे", "सुप्त बद्धकोणासन - ४ मिनिटे", "शवासन - ५ मिनिटे")
        ),
        YogaRoutine(
            id = "yoga_3",
            titleEn = "Weight Loss & Core Power Yoga (25 Min)",
            titleMr = "वजन कमी करण्यासाठी योगा (२५ मिनिटे)",
            type = YogaType.YOGA,
            durationMinutes = 25,
            level = WorkoutLevel.INTERMEDIATE,
            benefitEn = "Burns visceral fat, activates thyroid, tones abs and thighs.",
            benefitMr = "पोटावरील चरबी कमी होते, थायरॉईड ग्रंथी सक्रिय होतात आणि स्नायू मजबूत होतात.",
            stepsEn = listOf("Virabhadrasana I & II (Warrior Pose)", "Utkatasana (Chair Pose)", "Navasana (Boat Pose)", "Phalakasana (High Plank)", "Setu Bandhasana (Bridge Pose)"),
            stepsMr = listOf("वीरभद्रासन १ व २ (योद्धा आसन)", "उत्कटासन (खुर्ची आसन)", "नावासन (नौकासन - पोट कमी करण्यासाठी)", "फलकासन (प्लँक)", "सेतुबंधासन (कंबर वर उचला)")
        ),
        YogaRoutine(
            id = "yoga_4",
            titleEn = "Night Deep Sleep Meditation (10 Min)",
            titleMr = "रात्रीचे ध्यान व शांत झोप (१० मिनिटे)",
            type = YogaType.MEDITATION,
            durationMinutes = 10,
            level = WorkoutLevel.BEGINNER,
            benefitEn = "Releases racing thoughts, promotes deep restorative REM sleep.",
            benefitMr = "अनावश्‍यक विचार दूर होऊन गाढ, शांत आणि आरामदायी झोप लागते.",
            stepsEn = listOf("Find a comfortable reclined position", "Scan body from toes to head", "Focus gently on natural breathing", "Let go of today's thoughts with gratitude", "Drift into restful sleep"),
            stepsMr = listOf("शांतपणे झोपून आरामदायी स्थिती घ्या", "पावलांच्या बोटांपासून डोक्यापर्यंत लक्ष केंद्रित करा", "श्वासाच्या नैसर्गिक लयीकडे शांतपणे पहा", "आजच्या दिवसाबद्दल कृतज्ञता व्यक्त करा", "गाढ विश्रांतीमध्ये शांत व्हा")
        ),
        YogaRoutine(
            id = "yoga_5",
            titleEn = "Pranayama Breathing & Lung Capacity (15 Min)",
            titleMr = "प्राणायाम व श्वसन व्यायाम (१५ मिनिटे)",
            type = YogaType.BREATHING,
            durationMinutes = 15,
            level = WorkoutLevel.BEGINNER,
            benefitEn = "Enhances lung capacity, balances brain hemispheres, lowers blood pressure.",
            benefitMr = "फुफ्फुसांची ताकद वाढते, मेंदू शांत होतो आणि शरीरात ऑक्सिजनचा पुरवठा वाढतो.",
            stepsEn = listOf("Anulom Vilom (Alternate Nostril) - 5 min", "Kapalbhati (Skull Shining Breath) - 3 rounds", "Bhramari (Humming Bee Breath) - 3 min", "Sheetali / Sheetkari (Cooling Breath) - 2 min", "Silent stillness - 2 min"),
            stepsMr = listOf("अनुलोम विलोम (नाडी शोधन) - ५ मिनिटे", "कपालभाती (हलके आवर्तन) - ३ फेऱ्या", "भ्रामरी प्राणायाम (मधमाशी गुंजन) - ३ मिनिटे", "शीतली प्राणायाम (थंडावा देणारा श्वास) - २ मिनिटे", "शांत मौन - २ मिनिटे")
        )
    )

    // Authentic Maharashtrian Diet Plans
    val sampleMeals: List<MealItem> = listOf(
        MealItem(
            id = "meal_1",
            mealType = MealType.BREAKFAST,
            titleEn = "Sprouted Moong Chilla + Curd + Almonds",
            titleMr = "मूग डाळ / उसळ चीला + १ वाटी दही + बदाम",
            itemsEn = listOf("2 Moong Dal Chillas (steamed/less oil)", "1 small bowl fresh homemade curd (dahi)", "5-6 soaked almonds + 1 walnut"),
            itemsMr = listOf("२ मूग डाळ चीला (कमी तेलात)", "१ वाटी ताजे घरचे दही", "५-६ भिजवलेले बदाम + १ अक्रोड"),
            calories = 420,
            proteinGrams = 22,
            carbsGrams = 45,
            fatGrams = 12,
            portionInfoEn = "Medium size chilla (6-inch), made with ginger, green chilli and coriander.",
            portionInfoMr = "मध्यम आकाराचा चीला, आलं, हिरवी मिरची व कोथिंबीर घालून केलेला.",
            foodPreference = FoodPreference.VEGETARIAN,
            substitutionEn = "Kanda Poha with roasted peanuts OR 2 Boiled Eggs with toast",
            substitutionMr = "कांदा पोहे (भाजलेले शेंगदाणे घालून) किंवा २ उकडलेली अंडी"
        ),
        MealItem(
            id = "meal_2",
            mealType = MealType.MID_MORNING,
            titleEn = "Seasonal Fruit + Buttermilk (Taak)",
            titleMr = "ताजी फळे (केळी/सफरचंद) + जिरे ताक",
            itemsEn = listOf("1 Apple or 1 Pomegranate bowl", "1 glass roasted cumin spiced buttermilk (Mattha/Taak)"),
            itemsMr = listOf("१ सफरचंद किंवा १ वाटी डाळिंब", "१ मोठा ग्लास जिरे व कोथिंबीर घातलेले ताक (मठ्ठा)"),
            calories = 160,
            proteinGrams = 4,
            carbsGrams = 32,
            fatGrams = 2,
            portionInfoEn = "1 medium fruit + 250ml thin salted buttermilk.",
            portionInfoMr = "१ मध्यम फळ + २५० मिली हलके मीठ घातलेले ताक.",
            foodPreference = FoodPreference.VEGETARIAN,
            substitutionEn = "Coconut Water (Nariyal Pani) + 5 roasted almonds",
            substitutionMr = "शहाळ्याचे पाणी + ५ भाजलेले बदाम"
        ),
        MealItem(
            id = "meal_3",
            mealType = MealType.LUNCH,
            titleEn = "Maharashtrian Thali: Jowar Bhakri + Usal + Dal + Koshimbir",
            titleMr = "ज्वारीची भाकरी + मटकी उसळ + वरण + काकडी कोशिंबीर",
            itemsEn = listOf("1 medium Jowar (Sorghum) Bhakri", "1 cup Sprouted Matki Usal with curry leaves", "1 cup Tur Dal / Varan with lemon", "1 cup Cucumber-Carrot Koshimbir (with crushed peanuts)"),
            itemsMr = listOf("१ मध्यम ज्वारीची भाकरी", "१ वाटी मोड आलेल्या मटकीची उसळ", "१ वाटी तूर डाळीचे वरण (लिंबू पिळून)", "१ वाटी काकडी-गाजराची दाण्याचा कूट घातलेली कोशिंबीर"),
            calories = 580,
            proteinGrams = 24,
            carbsGrams = 85,
            fatGrams = 14,
            portionInfoEn = "Warm freshly made bhakri with thick sprouted legume usal for sustained energy.",
            portionInfoMr = "गरमागरम ताज्या ज्वारीच्या भाकरीसोबत प्रथिनयुक्त उसळ आणि पौष्टिक कोशिंबीर.",
            foodPreference = FoodPreference.BOTH,
            substitutionEn = "Non-veg: Replace Matki Usal with Chicken Rassa / Boiled Fish (Surmai/Rohu)",
            substitutionMr = "मांसाहारी पर्याय: उसळीऐवजी वाटीभर गावरान चिकन रस्सा किंवा उकडलेला/कमी तेलातील मासा"
        ),
        MealItem(
            id = "meal_4",
            mealType = MealType.EVENING_SNACK,
            titleEn = "Roasted Chana + Jaggery or Sattu Drink",
            titleMr = "भाजलेले फुटाणे + गूळ किंवा सत्तू / प्रोटीन शेक",
            itemsEn = listOf("Handful roasted gram (Futane/Chana)", "1 small piece natural jaggery (Gud)", "Cup of Green Tea or Lemongrass Tea (Gavati Chaha)"),
            itemsMr = listOf("मूठभर भाजलेले फुटाणे (चणे)", "१ लहान खडा सेंद्रिय गूळ", "१ कप गवती चहा किंवा ग्रीन टी"),
            calories = 190,
            proteinGrams = 10,
            carbsGrams = 30,
            fatGrams = 3,
            portionInfoEn = "High-fiber Maharashtrian afternoon energy booster without refined sugar.",
            portionInfoMr = "साखरेऐवजी शुद्ध गूळ आणि भरपूर फायबर देणारे चणे.",
            foodPreference = FoodPreference.VEGETARIAN,
            substitutionEn = "Boiled egg white chaat with onions and coriander",
            substitutionMr = "कांदा व कोथिंबीर घातलेली उकडलेली अंडी चाट"
        ),
        MealItem(
            id = "meal_5",
            mealType = MealType.DINNER,
            titleEn = "Light Meal: Khichdi with Kadhi OR Rice Bhakri with Sukka",
            titleMr = "हलकी मूग डाळ खिचडी + कढी किंवा तांदूळ भाकरी",
            itemsEn = listOf("1 bowl Moong Dal Khichdi (less ghee)", "1 small bowl Gujarati/Maharashtrian spiced Kadhi", "Roasted Papad & Lemon slice"),
            itemsMr = listOf("१ वाटी मूग डाळ तांदूळ खिचडी (कमी तूप)", "१ वाटी सुंठ-हिंग घातलेली कढी", "भाजलेला उडीद पापड व काकडी चकत्या"),
            calories = 430,
            proteinGrams = 16,
            carbsGrams = 68,
            fatGrams = 8,
            portionInfoEn = "Easy to digest evening dinner before 8:30 PM to support fat loss and restful sleep.",
            portionInfoMr = "पचायला अत्यंत हलका आहार, रात्री ८:३० पूर्वी घेतल्यास झोप चांगली लागते.",
            foodPreference = FoodPreference.BOTH,
            substitutionEn = "Non-Veg: 1 Rice Bhakri + 150g Egg Bhurji or Grilled Chicken breast",
            substitutionMr = "मांसाहारी पर्याय: १ तांदळाची भाकरी + अंड्याची भुर्जी / उकडलेले चिकन"
        )
    )

    // Alternative Meal Swaps for personalization
    val mealSwaps: Map<MealType, List<MealItem>> = mapOf(
        MealType.BREAKFAST to listOf(
            MealItem(
                id = "swap_b1",
                mealType = MealType.BREAKFAST,
                titleEn = "Kanda Poha with Peanuts + Sprouted Moong",
                titleMr = "शेंगदाणे घातलेले कांदा पोहे + मोड आलेले मूग",
                itemsEn = listOf("1 medium bowl thick poha with curry leaves & peanuts", "1/2 cup lightly steamed sprouted moong", "1 cup lemon spiced green tea"),
                itemsMr = listOf("१ मध्यम वाटी खमंग कांदा पोहे (भाजलेले शेंगदाणे)", "१/२ वाटी वाफवलेले मोड आलेले मूग", "१ कप लिंबू घातलेला ग्रीन टी"),
                calories = 380,
                proteinGrams = 14,
                carbsGrams = 58,
                fatGrams = 9,
                portionInfoEn = "Light on oil with fresh lemon juice and fresh coriander.",
                portionInfoMr = "कमी तेलात फोडणी, भरपूर लिंबू व कोथिंबीर घालून केलेला.",
                foodPreference = FoodPreference.VEGETARIAN,
                substitutionEn = "Moong Dal Chilla or 2 Boiled Eggs",
                substitutionMr = "मूग डाळ चीला किंवा २ उकडलेली अंडी"
            ),
            MealItem(
                id = "swap_b2",
                mealType = MealType.BREAKFAST,
                titleEn = "2 Boiled Farm Eggs + 1 Multigrain Poli",
                titleMr = "२ उकडलेली गावरान अंडी + १ पोळी",
                itemsEn = listOf("2 whole boiled eggs with black pepper", "1 thin wheat-jowar mixed poli", "1 cup warm black coffee or green tea"),
                itemsMr = listOf("२ उकडलेली अंडी (काळी मिरी पूड घालून)", "१ पातळ मिश्र पिठाची पोळी", "१ कप कोमट पाणी किंवा ग्रीन टी"),
                calories = 340,
                proteinGrams = 20,
                carbsGrams = 30,
                fatGrams = 12,
                portionInfoEn = "High-protein morning breakfast to maintain muscle tissue during fat loss.",
                portionInfoMr = "स्नायू बळकटीसाठी उत्तम प्रथिनयुक्त सकाळचा नाश्ता.",
                foodPreference = FoodPreference.NON_VEGETARIAN,
                substitutionEn = "Paneer bhurji with 1 poli",
                substitutionMr = "पनीर भुर्जी व १ पोळी"
            )
        ),
        MealType.LUNCH to listOf(
            MealItem(
                id = "swap_l1",
                mealType = MealType.LUNCH,
                titleEn = "Bajra Bhakri + Methi Bhaji + Spiced Taak",
                titleMr = "बाजरीची भाकरी + मेथीची भाजी + लसूण चटणी + ताक",
                itemsEn = listOf("1 medium Bajra (pearl millet) Bhakri", "1 cup fresh Methi (fenugreek) leaves stir fry", "1 cup Tur Dal", "1 large glass Cumin Buttermilk (Taak)"),
                itemsMr = listOf("१ बाजरीची भाकरी", "१ वाटी ताज्या मेथीची भाजी (कमी तेल)", "१ वाटी साधे वरण", "१ मोठा ग्लास जिरे ताक"),
                calories = 520,
                proteinGrams = 19,
                carbsGrams = 78,
                fatGrams = 11,
                portionInfoEn = "Rich in iron, magnesium, and dietary fiber. Excellent for digestion.",
                portionInfoMr = "लोह आणि फायबरने परिपूर्ण, मधुमेहींसाठी अतिशय गुणकारी.",
                foodPreference = FoodPreference.VEGETARIAN,
                substitutionEn = "Jowar Bhakri with Matki Usal",
                substitutionMr = "ज्वारी भाकरी व मटकी उसळ"
            ),
            MealItem(
                id = "swap_l2",
                mealType = MealType.LUNCH,
                titleEn = "Gaothan Chicken Rassa + 1 Jowar Bhakri + Salad",
                titleMr = "गावरान चिकन रस्सा + १ ज्वारीची भाकरी + कांदा-लिंबू",
                itemsEn = listOf("150g tender country chicken in light spiced gravy", "1 warm Jowar Bhakri", "Cucumber & sliced red onion with lemon"),
                itemsMr = listOf("१५० ग्रॅम कमी तेलातील चिकन रस्सा", "१ गरमागरम ज्वारीची भाकरी", "काकडी व कांदा लिंबू पिळून"),
                calories = 560,
                proteinGrams = 38,
                carbsGrams = 52,
                fatGrams = 16,
                portionInfoEn = "High-protein lean lunch without heavy coconut or dalda oil.",
                portionInfoMr = "जास्त खोबरे न वापरता केलेला पौष्टिक व प्रथिनयुक्त मांसाहारी आहार.",
                foodPreference = FoodPreference.NON_VEGETARIAN,
                substitutionEn = "Egg curry with 1 bhakri",
                substitutionMr = "अंडा करी व १ भाकरी"
            )
        ),
        MealType.DINNER to listOf(
            MealItem(
                id = "swap_d1",
                mealType = MealType.DINNER,
                titleEn = "Egg Bhurji + 1 Jowar Bhakri + Tomato Soup",
                titleMr = "अंड्याची भुर्जी + १ ज्वारी भाकरी + टोमॅटो सूप",
                itemsEn = listOf("2 scrambled eggs with onion, tomato & green chillies", "1 small Jowar Bhakri", "1 cup light homemade tomato soup"),
                itemsMr = listOf("२ अंड्यांची खमंग भुर्जी (कांदा-टोमॅटो)", "१ लहान ज्वारीची भाकरी", "१ वाटी घरगुती टोमॅटो सूप"),
                calories = 390,
                proteinGrams = 22,
                carbsGrams = 42,
                fatGrams = 12,
                portionInfoEn = "Lean dinner rich in amino acids to accelerate overnight muscle repair.",
                portionInfoMr = "रात्रीच्या वेळी हलका आणि प्रथिनयुक्त आहार.",
                foodPreference = FoodPreference.NON_VEGETARIAN,
                substitutionEn = "Tofu or Paneer bhurji",
                substitutionMr = "पनीर किंवा टोफू भुर्जी"
            ),
            MealItem(
                id = "swap_d2",
                mealType = MealType.DINNER,
                titleEn = "Moong Soup + Steamed Rice + Roasted Papad",
                titleMr = "मूग डाळ सूप + थोडा भात + भाजलेला पापड",
                itemsEn = listOf("1 large bowl sprouted moong soup with ginger", "1 small cup steamed rice", "1 roasted urad papad & fresh cucumber slices"),
                itemsMr = listOf("१ मोठा बाऊल आले-लसूण घातलेले मुगाचे कढण / सूप", "१ लहान वाटी वाफाळलेला भात", "१ भाजलेला उडीद पापड व काकडी चकत्या"),
                calories = 360,
                proteinGrams = 16,
                carbsGrams = 62,
                fatGrams = 4,
                portionInfoEn = "Super light on stomach, ideal for detox days or sedentary evenings.",
                portionInfoMr = "पचनासाठी अत्यंत हलके आणि शरीराला थंडावा देणारे.",
                foodPreference = FoodPreference.VEGETARIAN,
                substitutionEn = "Moong Dal Khichdi with Kadhi",
                substitutionMr = "मूग डाळ खिचडी व कढी"
            )
        )
    )

    // Maharashtrian Health Grocery List
    val groceryList: List<GroceryItem> = listOf(
        GroceryItem("g_1", "Jowar Flour (ज्वारीचे पीठ)", "ज्वारीचे पीठ", "2 kg", "Flours & Grains", "धान्य व पिठे"),
        GroceryItem("g_2", "Bajra Flour (बाजरीचे पीठ)", "बाजरीचे पीठ", "1 kg", "Flours & Grains", "धान्य व पिठे"),
        GroceryItem("g_3", "Sprouted Matki (मोड आलेली मटकी)", "मोड आलेली मटकी", "500 g", "Proteins & Legumes", "डाळी व उसळी"),
        GroceryItem("g_4", "Yellow Moong Dal (पिवळी मूग डाळ)", "पिवळी मूग डाळ", "1 kg", "Proteins & Legumes", "डाळी व उसळी"),
        GroceryItem("g_5", "Desi Fresh Curd (घरचे ताजे दही)", "घरचे ताजे दही", "1 kg", "Dairy & Eggs", "दुग्धजन्य पदार्थ"),
        GroceryItem("g_6", "Eggs (अंडी)", "गावरान अंडी", "1 dozen", "Dairy & Eggs", "अंडी"),
        GroceryItem("g_7", "Roasted Peanuts (भाजलेले शेंगदाणे)", "शेंगदाणे", "500 g", "Nuts & Seeds", "सुकामेवा"),
        GroceryItem("g_8", "Organic Jaggery (सेंद्रिय गूळ)", "सेंद्रिय गूळ", "500 g", "Sweeteners", "गोड पदार्थ"),
        GroceryItem("g_9", "Fresh Lemons & Coriander (लिंबू व कोथिंबीर)", "लिंबू व कोथिंबीर", "1 bunch", "Produce", "भाजीपाला"),
        GroceryItem("g_10", "Kokum / Amsul (कोकम / आमसूल)", "कोकम", "200 g", "Spices", "मसाले")
    )

    // Health Risk & Condition Breakdown
    val healthRisks: List<HealthConditionRisk> = listOf(
        HealthConditionRisk(
            conditionNameEn = "Diabetes Risk",
            conditionNameMr = "डायबेटीस (मधुमेह) जोखीम",
            riskLevel = RiskLevel.MODERATE,
            percentage = 28,
            explanationEn = "Based on sedentary hours, BMI, and family history. Moderate lifestyle risk is well manageable.",
            explanationMr = "तुमच्या दैनंदिन बैठी जीवनशैली, वजन आणि आहारानुसार डायबिटीस होण्याची मध्यम जोखीम आहे. व्यायामाने ही सहज कमी करता येते.",
            recommendationsEn = listOf("Reduce refined sugar, maida, and sweets", "Walk 30 minutes daily after meals", "Keep weight in optimal BMI range (18.5 - 24.9)", "Annual fasting blood glucose checkup"),
            recommendationsMr = listOf("साखर, गूळ-गोड पदार्थ व मैदा कमी करा", "दररोज ३० मिनिटे न चुकता व्यायाम करा", "वजन नियंत्रणात ठेवा (बीएमआय तपासा)", "दरवर्षी न चुकता रक्तातील साखरेची तपासणी करा")
        ),
        HealthConditionRisk(
            conditionNameEn = "High Blood Pressure",
            conditionNameMr = "हाय ब्लड प्रेशर (उच्च रक्तदाब)",
            riskLevel = RiskLevel.LOW,
            percentage = 14,
            explanationEn = "Normal heart rate and low sodium traditional home-cooked diet keep risk low.",
            explanationMr = "घरगुती ताजा आहार व मर्यादित मीठ यामुळे रक्तदाबाची जोखीम कमी आहे.",
            recommendationsEn = listOf("Maintain low salt intake", "Drink 2.5-3 Liters water daily", "Practice daily Pranayama (Anulom Vilom)"),
            recommendationsMr = listOf("आहारात मिठाचे प्रमाण योग्य ठेवा", "रोज २.५ ते ३ लिटर पाणी प्या", "दररोज अनुलोम-विलोम प्राणायाम करा")
        ),
        HealthConditionRisk(
            conditionNameEn = "Heart Health & Cardio",
            conditionNameMr = "हृदय आरोग्य (Heart Health)",
            riskLevel = RiskLevel.LOW,
            percentage = 12,
            explanationEn = "Active daily steps (8,000+) and regular workout routines support cardiovascular strength.",
            explanationMr = "दररोज ८,००० पेक्षा जास्त पावले चालल्याने हृदयाचे आरोग्य उत्तम राहते.",
            recommendationsEn = listOf("Engage in brisk walking or jogging", "Eat fiber-rich jowar bhakri and vegetables", "Avoid deep-fried farsan and junk food"),
            recommendationsMr = listOf("जलद चालणे किंवा जॉगिंग सुरू ठेवा", "ज्वारी-बाजरीच्या भाकरीचा आहारात वापर करा", "तळलेले फरसाण व जंक फूड टाळा")
        ),
        HealthConditionRisk(
            conditionNameEn = "Fatty Liver & Digestion",
            conditionNameMr = "फॅटी लिव्हर व पचनक्रिया",
            riskLevel = RiskLevel.MODERATE,
            percentage = 26,
            explanationEn = "Metabolism can be improved with timely meals and reducing late-night heavy dinners.",
            explanationMr = "रात्री उशिरा जेवणे टाळल्यास आणि कोकम सरबत/ताक घेतल्यास यकृत निरोगी राहते.",
            recommendationsEn = listOf("Finish dinner before 8:30 PM", "Add buttermilk (Taak) to lunch daily", "Drink warm lemon water in morning"),
            recommendationsMr = listOf("रात्रीचे जेवण ८:३० च्या आधी पूर्ण करा", "दुपारी जेवणात जिरे ताक नियमित घ्या", "सकाळी उठल्यावर कोमट पाणी प्या")
        ),
        HealthConditionRisk(
            conditionNameEn = "PCOS / Hormonal Balance (for females)",
            conditionNameMr = "PCOS / हार्मोनल संतुलन (महिलांसाठी)",
            riskLevel = RiskLevel.LOW,
            percentage = 11,
            explanationEn = "Active physical routine and low glycemic native millets promote hormonal balance.",
            explanationMr = "नियमित व्यायाम आणि ज्वारी-बाजरीचा वापर हार्मोन्स संतुलित ठेवण्यास मदत करतो.",
            recommendationsEn = listOf("Include flaxseeds and spearmint tea", "Daily 30-minute moderate resistance exercise", "Limit high-sugar bakery products"),
            recommendationsMr = listOf("जवस (Flaxseeds) व बडीशेपचा आहारात वापर करा", "दररोज ३० मिनिटे हलका व्यायाम करा", "मैदा व जास्त साखरेचे बेकरी पदार्थ टाळा")
        ),
        HealthConditionRisk(
            conditionNameEn = "Thyroid Balance",
            conditionNameMr = "थायरॉईड आरोग्य",
            riskLevel = RiskLevel.LOW,
            percentage = 10,
            explanationEn = "No reported symptoms of lethargy or sudden weight fluctuations.",
            explanationMr = "थायरॉईडचा कोणताही त्रास सध्या जाणवत नाही, ऊर्जा पातळी उत्तम आहे.",
            recommendationsEn = listOf("Include iodine-rich salt and seeds", "Do Sarvangasana or Matsyasana weekly"),
            recommendationsMr = listOf("आयोडीनयुक्त मीठ व सुकामेवा खा", "सर्वांगासन किंवा मत्स्यासन योगासने करा")
        )
    )

    // Progress History (Days of week)
    val weeklyProgress: List<DailyProgressRecord> = listOf(
        DailyProgressRecord("सोम (Mon)", 74.0f, 7820, 1540, 7.0f, 2.5f),
        DailyProgressRecord("मंगळ (Tue)", 73.6f, 8910, 1480, 7.5f, 2.8f),
        DailyProgressRecord("बुध (Wed)", 73.2f, 6500, 1620, 6.5f, 2.0f),
        DailyProgressRecord("गुरू (Thu)", 72.8f, 9200, 1400, 8.0f, 3.0f),
        DailyProgressRecord("शुक्र (Fri)", 72.5f, 8462, 1420, 7.5f, 2.7f),
        DailyProgressRecord("शनि (Sat)", 72.2f, 10400, 1380, 8.0f, 3.2f),
        DailyProgressRecord("रवि (Sun)", 72.0f, 8462, 1420, 7.5f, 1.5f)
    )

    // Personal Care Grooming Tips
    val groomingTips: List<GroomingTip> = listOf(
        GroomingTip(
            id = "groom_1",
            category = "hair",
            titleEn = "Hair Fall Prevention & Scalp Health",
            titleMr = "केस गळणे रोखणे व केसांची निगा",
            tipsEn = listOf(
                "Shampoo 2-3 times a week with mild sulphate-free cleanser",
                "Warm coconut oil massage with curry leaves (kadi patta) weekly",
                "Eat protein-rich sprouted pulses, eggs and curry leaves",
                "Avoid extreme hot water baths on hair"
            ),
            tipsMr = listOf(
                "आठवड्यातून २-३ वेळा सौम्य शॅम्पूने केस धुवा",
                "कढीपत्ता घालून गरम केलेले खोबरेल तेल आठवड्यातून एकदा चोळा",
                "मोड आलेली कडधान्ये, अंडी आणि हिरव्या पालेभाज्या भरपूर खा",
                "केसांवर अतिउष्ण पाणी ओतणे टाळा"
            ),
            actionAdviceEn = "Balanced nutrition is 80% of hair strength. Avoid chronic stress.",
            actionAdviceMr = "केसांच्या मुळांना मजबूत ठेवण्यासाठी सकस आहार आणि पुरेशी झोप सर्वात महत्त्वाची आहे."
        ),
        GroomingTip(
            id = "groom_2",
            category = "skin",
            titleEn = "Clear Glowing Skin Guidance",
            titleMr = "तजेलदार व निरोगी त्वचेची काळजी",
            tipsEn = listOf(
                "Drink 3 Liters of water daily to flush toxins",
                "Wash face twice daily with gentle foam wash",
                "Apply SPF 30+ sunscreen when stepping out in Maharashtra sun",
                "Use besan (gram flour) + turmeric + curd natural face pack weekly"
            ),
            tipsMr = listOf(
                "दररोज भरपूर (३ लिटर) पाणी पिऊन शरीरातील विषारी घटक बाहेर टाका",
                "दिवसातून दोनदा चेहरा स्वच्छ धुवा",
                "उन्हात बाहेर पडताना सनस्क्रीन क्रीम अवश्य वापरा",
                "आठवड्यातून एकदा बेसन, हळद आणि दह्याचा घरगुती लेप लावा"
            ),
            actionAdviceEn = "Healthy gut equals glowing skin. Add buttermilk and salads daily.",
            actionAdviceMr = "पचनसंस्था निरोगी असेल तर त्वचा आपोआप तजेलदार बनते."
        )
    )

    val styleLooks: List<StyleLook> = listOf(
        // Haircuts matching Storyboard 1 panel 14
        StyleLook("look_1", "Textured Quiff", "टेक्सचर्ड क्विफ", "haircut", "Voluminous top with clean modern taper.", "वरच्या बाजूला हलका व्हॉल्यूम व आकर्षक कट.", "Oval & Square face shapes", "अंडाकृती व चौकोनी चेहरा"),
        StyleLook("look_2", "Classic Fade", "क्लासिक फेड", "haircut", "Short gradient on sides, sharp and fresh.", "बाजूने बारीक व स्वच्छ, कार्यालयासाठी उत्तम.", "Oval & Round face shapes", "अंडाकृती व गोल चेहरा"),
        StyleLook("look_3", "Side Part Executive", "साइड पार्ट (सुसंस्कृत लूक)", "haircut", "Polished gentleman look with neat comb line.", "ऑफिस आणि फॉर्मल कार्यक्रमांसाठी शोभून दिसणारा लूक.", "Formal & Business meetings", "कार्यालयीन कामासाठी उत्तम"),
        StyleLook("look_4", "Curly Top Fade", "कर्ली टॉप फेड", "haircut", "Natural curls preserved with tidy high taper.", "कुरळ्या केसांसाठी आधुनिक व स्टायलिश स्वरूप.", "Wavy & Curly hair textures", "कुरळे व जाड केस"),
        StyleLook("look_5", "Clean Buzz Cut", "क्लीन बझ कट", "haircut", "Minimalist, sharp military-grade low maintenance.", "अत्यंत कमी निगा लागणारा आणि स्वच्छ लूक.", "Athletes & Hot climate", "खेळाडू व उन्हाळ्यासाठी"),
        StyleLook("look_6", "Long Natural Waves", "लॉंग नॅचरल वेव्ह्स", "haircut", "Medium-length wavy flow with light styling paste.", "मध्यम लांबीचे नैसर्गिक व रुबाबदार केस.", "Creative & Casual style", "कला व आधुनिक व्यक्तिमत्त्व"),

        // Beard Styles matching Storyboard
        StyleLook("look_7", "Light Stubble", "लाईट स्टबल (हलकी दाढी)", "beard", "3-day groomed stubble, defines jawline cleanly.", "३ दिवसांची हलकी दाढी, जबड्याचा आकार उठावदार करते.", "Oval & Diamond face", "अंडाकृती व गोल जबडा"),
        StyleLook("look_8", "Clean Shaven", "क्लीन शेव्हन", "beard", "Fresh, youthful and strictly professional.", "स्वच्छ, टवटवीत आणि कार्यालयीन लूक.", "All face shapes", "सर्व चेहऱ्याचे प्रकार"),
        StyleLook("look_9", "Short Boxed Beard", "शॉर्ट बॉक्स्ड दाढी", "beard", "Trimmed cheek lines with full chin coverage.", "व्यवस्थित ट्रिम केलेली भारदस्त दाढी.", "Round & Oval face", "गोल व अंडाकृती चेहरा"),
        StyleLook("look_10", "French / Goatee Beard", "फ्रेंच दाढी (गॉटी)", "beard", "Focused around mouth and chin, stylish touch.", "ओठांखालील व हनुवटीवरील आकर्षक दाढी.", "Square & Oval face", "चौकोनी व लांबट चेहरा"),

        // Eyewear Glasses matching Storyboard
        StyleLook("look_11", "Wayfarer Classic", "वेफेरर चष्मा", "glasses", "Timeless frame that flatters almost every cheekbone.", "क्लासिक डिझाइन, कोणत्याही चेहऱ्यावर शोभून दिसतो.", "Oval & Round faces", "अंडाकृती व गोल चेहरा"),
        StyleLook("look_12", "Round Vintage Frames", "राउंड विंटेज चष्मा", "glasses", "Intelligent retro appeal with lightweight metal rim.", "बुद्धीमान व स्टायलिश लुक देणारा हलका चष्मा.", "Square & Angular jawlines", "चौकोनी जबडा असलेले चेहरे"),
        StyleLook("look_13", "Aviator Style", "एव्हिएटर फ्रेम्स", "glasses", "Bold teardrop outline for outdoor & driving.", "रुबाबदार आणि बाहेर फिरण्यासाठी उत्तम.", "Broad cheekbones", "रुंद कपाळ व गाल"),

        // Clothing Suggestions matching Storyboard
        StyleLook("look_14", "Casual Smart Cotton", "कॅज्युअल कॉटन शर्ट्स", "clothing", "Breathable linen, polo tees and solid pastel shirts.", "उष्ण हवामानात आरामदायी असणारे हलक्या रंगांचे शर्ट्स.", "Daily wear & casual outings", "दैनंदिन वापर व मित्रांसोबत"),
        StyleLook("look_15", "Formal Executive Blazer", "फॉर्मल ब्लेझर व ट्राउझर्स", "clothing", "Crisp tailored button-downs with sharp trousers.", "कार्यालयीन बैठका व सादरीकरणासाठी अतिशय सुटसुटीत.", "Interviews & Business meets", "मुलाखत व ऑफिस काम"),
        StyleLook("look_16", "Sports & Active Wear", "स्पोर्ट्स व ॲक्टिव्ह वेअर", "clothing", "Dry-fit stretchable t-shirts and joggers.", "जिम, रनिंग आणि योगाभ्यासासाठी सर्वोत्तम.", "Workouts, running & gym", "व्यायाम, रनिंग व सायकलिंग"),
        StyleLook("look_17", "Traditional Kurta Pajama", "पारंपारिक कुर्ता पायजमा", "clothing", "Festive khadi silk or linen kurta with Nehru jacket.", "सण, उत्सव, लग्नकार्य आणि मराठमोळ्या सोहळ्यांसाठी.", "Ganesh Utsav, Diwali & Family functions", "सण, लग्न व कौटुंबिक समारंभ")
    )

    // Local Notification Reminders (Water Intake, Steps, Workout, Meals & Health)
    val defaultReminders: List<ReminderItem> = listOf(
        ReminderItem(
            id = "rem_water_morning",
            type = NotificationType.WATER,
            titleEn = "Morning Water Check-In",
            titleMr = "सकाळी पाणी पिण्याची आठवण",
            messageEn = "Start hydrated! Log your morning 500 ml water intake.",
            messageMr = "दिवसाची ताजेतवाने सुरुवात! सकाळचे ५०० मिली पाणी पिऊन नोंदवा.",
            timeSlot = "9:00 AM",
            isEnabled = true,
            hourOfDay = 9,
            minute = 0
        ),
        ReminderItem(
            id = "rem_water_midday",
            type = NotificationType.WATER,
            titleEn = "Midday Hydration Reminder",
            titleMr = "दुपारी पाणी पिण्याची आठवण",
            messageEn = "Time to hydrate! Drink a glass of water and log your progress.",
            messageMr = "पाणी पिण्याची वेळ झाली! १ ग्लास पाणी प्या व प्रगती नोंदवा.",
            timeSlot = "11:30 AM",
            isEnabled = true,
            hourOfDay = 11,
            minute = 30
        ),
        ReminderItem(
            id = "rem_water_afternoon",
            type = NotificationType.WATER,
            titleEn = "Afternoon Water Boost",
            titleMr = "दुपारनंतर पाणी पिण्याचा अलर्ट",
            messageEn = "Beat afternoon fatigue — drink 250 ml water and log it now.",
            messageMr = "दुपारचा थकवा घालवा — २५० मिली पाणी पिऊन अॅपमध्ये नोंदवा.",
            timeSlot = "3:30 PM",
            isEnabled = true,
            hourOfDay = 15,
            minute = 30
        ),
        ReminderItem(
            id = "rem_steps_morning",
            type = NotificationType.STEPS,
            titleEn = "Morning Walk & Steps Log",
            titleMr = "सकाळची पावले नोंदवा",
            messageEn = "Take a brisk 15-min walk and log your morning steps toward 10,000!",
            messageMr = "१५ मिनिटे जलद चाला आणि १०,००० ध्येयाकडे सकाळची पावले नोंदवा!",
            timeSlot = "7:30 AM",
            isEnabled = true,
            hourOfDay = 7,
            minute = 30
        ),
        ReminderItem(
            id = "rem_steps_evening",
            type = NotificationType.STEPS,
            titleEn = "Evening Steps Check-In",
            titleMr = "संध्याकाळची पावले पूर्ण करा",
            messageEn = "Check your daily steps goal and take an evening stroll to finish strong.",
            messageMr = "आजचे पावलांचे ध्येय तपासा व संध्याकाळच्या फेरफटक्याने लक्ष्य गाठा.",
            timeSlot = "6:30 PM",
            isEnabled = true,
            hourOfDay = 18,
            minute = 30
        ),
        ReminderItem(
            id = "rem_2",
            type = NotificationType.WORKOUT,
            titleEn = "Workout Reminder",
            titleMr = "वर्कआउटची वेळ",
            messageEn = "Your 30-min FitMarathi exercise session awaits.",
            messageMr = "आजचा ३० मिनिटांचा व्यायाम सुरू करण्याची वेळ झाली.",
            timeSlot = "12:00 PM",
            isEnabled = true,
            hourOfDay = 12,
            minute = 0
        ),
        ReminderItem(
            id = "rem_3",
            type = NotificationType.MEAL,
            titleEn = "Meal Suggestion",
            titleMr = "दुपारचे पौष्टिक जेवण",
            messageEn = "Have your jowar bhakri, dal and koshimbir on time.",
            messageMr = "दुपारच्या जेवणात भाकरी, उसळ व कोशिंबीर वेळेवर घ्या.",
            timeSlot = "1:15 PM",
            isEnabled = true,
            hourOfDay = 13,
            minute = 15
        ),
        ReminderItem(
            id = "rem_4",
            type = NotificationType.HEALTH,
            titleEn = "Daily Health Review",
            titleMr = "दैनिक आरोग्य आढावा",
            messageEn = "Review your steps, water, and calorie rings before bedtime.",
            messageMr = "झोपण्यापूर्वी आजची पावले, पाणी व कॅलरीज प्रगती तपासा.",
            timeSlot = "8:30 PM",
            isEnabled = true,
            hourOfDay = 20,
            minute = 30
        ),
        ReminderItem(
            id = "rem_5",
            type = NotificationType.QUOTE,
            titleEn = "Motivational Quote",
            titleMr = "प्रेरणादायी सुविचार",
            messageEn = "\"Your body is your first home — nourish it daily!\"",
            messageMr = "\"आजचा छोटा प्रयत्न, उद्याचे मोठे यश! तंदुरुस्त राहा.\"",
            timeSlot = "9:00 PM",
            isEnabled = true,
            hourOfDay = 21,
            minute = 0
        )
    )
}

