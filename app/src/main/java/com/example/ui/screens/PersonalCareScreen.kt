package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FaceAnalysisResult
import com.example.data.model.Language
import com.example.data.repository.FitMarathiRepository
import com.example.data.repository.SampleData
import com.example.ui.components.DisclaimerBox
import com.example.ui.components.FitCard
import com.example.ui.components.PillTabRow
import com.example.ui.theme.*

@Composable
fun PersonalCareScreen(
    repository: FitMarathiRepository,
    onNavigateBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val profile by repository.userProfile.collectAsState()
    val isMr = profile.language == Language.MARATHI

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Hair Care, 1: Skin Care, 2: Style & Looks, 3: Face Analysis
    var selectedLookCategory by remember { mutableStateOf("all") } // all, haircut, beard, glasses, clothing
    var activeDetailPlan by remember { mutableStateOf<String?>(null) } // "hair" or "skin"

    val faceAnalysis = remember { FaceAnalysisResult() }
    val groomingTips = SampleData.groomingTips
    val styleLooks = SampleData.styleLooks

    val filteredLooks = remember(selectedLookCategory, styleLooks) {
        if (selectedLookCategory == "all") styleLooks
        else styleLooks.filter { it.category == selectedLookCategory }
    }

    val screenTitle = when (selectedTab) {
        0 -> if (isMr) "केसांची काळजी (Hair Care)" else "Hair Care & Growth"
        1 -> if (isMr) "त्वचेची काळजी (Skin Care)" else "Skin Care Routine"
        2 -> if (isMr) "व्यक्तिमत्त्व व स्टाइल" else "Personality & Wardrobe"
        else -> if (isMr) "चेहरा विश्लेषण व हेअरस्टाइल" else "Face Shape & Looks"
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(FitBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .testTag("personal_care_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = FitTextPrimary
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = screenTitle,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = FitTextPrimary
                    )
                    Text(
                        text = if (isMr) "केस, त्वचा निगा, हेअरस्टाइल व व्यक्तिमत्त्व सल्ला" else "Hair health, skin routines, face shape styling & wardrobe",
                        fontSize = 12.sp,
                        color = FitTextSecondary
                    )
                }
            }
        }

        // Pill Tabs: Hair, Skin, Style, Face Analysis
        item {
            PillTabRow(
                tabs = listOf(
                    if (isMr) "केस (Hair)" else "Hair",
                    if (isMr) "त्वचा (Skin)" else "Skin",
                    if (isMr) "स्टाइल (Style)" else "Style",
                    if (isMr) "चेहरा व हेअरस्टाइल" else "Face & Looks"
                ),
                selectedIndex = selectedTab,
                onTabSelected = { selectedTab = it },
                activeColor = FitPrimaryGreen
            )
        }

        when (selectedTab) {
            0 -> {
                // Hair Care Section
                item {
                    val hairTip = groomingTips.firstOrNull { it.category == "hair" } ?: groomingTips[0]
                    GroomingDetailCard(
                        tip = hairTip,
                        isMr = isMr,
                        icon = Icons.Default.Face,
                        onViewPlan = { activeDetailPlan = "hair" }
                    )
                }

                item {
                    FitCard(backgroundColor = Color.White) {
                        Text(
                            text = if (isMr) "केसांसाठी महाराष्ट्रीयन सुपरफूड्स" else "Nutrition for Stronger Hair Roots",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = FitDeepGreen
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isMr) "• मोड आलेली मटकी व कडधान्ये (प्रथिने)\n• ताज्या कढीपत्त्याची चटणी (लोह व व्हिटॅमिन बी)\n• देशी ताक व आवळा सरबत\n• २ उकडलेली अंडी किंवा शेंगदाणे" else "• Sprouted Matki & Pulses (Plant Protein)\n• Fresh Curry Leaf Chutney (Iron & Folate)\n• Fresh Buttermilk & Amla juice\n• Boiled eggs or roasted peanuts",
                            fontSize = 13.sp,
                            color = FitTextSecondary,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            1 -> {
                // Skin Care Section
                item {
                    val skinTip = groomingTips.firstOrNull { it.category == "skin" } ?: groomingTips[1]
                    GroomingDetailCard(
                        tip = skinTip,
                        isMr = isMr,
                        icon = Icons.Default.Spa,
                        onViewPlan = { activeDetailPlan = "skin" }
                    )
                }

                item {
                    FitCard(backgroundColor = Color.White) {
                        Text(
                            text = if (isMr) "घरगुती उबटन व नैसर्गिक उपाय" else "Natural Home Skin Care Remedies",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = FitDeepGreen
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isMr) "१. बेसन, चिमूटभर हळद आणि घरचे ताजे दही मिसळून चेहऱ्यावर १० मिनिटे लावा.\n२. डोळ्यांवर काकडीच्या चकत्या ठेवून विश्रांती घ्या.\n३. रासायनिक उत्पादनांऐवजी गुलाबपाणी व कोरफडीचा (Aloe Vera) वापर करा." else "1. Besan + pinch of organic haldi + fresh dahi paste (weekly).\n2. Cucumber slices over eyes to relieve screen fatigue.\n3. Pure aloe vera gel for natural calming hydration.",
                            fontSize = 13.sp,
                            color = FitTextSecondary,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            2 -> {
                // Personality & Style Suggestions matching Storyboard 1 panel 12 & 14
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = FitDeepGreen
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isMr) "तुमची पर्सनॅलिटी (Personality Type)" else "Your Personality Profile",
                                    color = Color(0xFFA7F3D0),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Surface(
                                    color = FitGold,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = faceAnalysis.personalityTypeEn,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = FitDeepGreen,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (isMr) faceAnalysis.personalityTypeMr else "Confident, calm decision-maker with natural charisma.",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                // Recommended Color Palette
                item {
                    FitCard(backgroundColor = Color.White) {
                        Text(
                            text = if (isMr) "तुमच्यासाठी सर्वोत्तम कपड्यांचे रंग:" else "Recommended Shirt Color Palette:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            ColorSwatch(color = Color(0xFF1E3A8A), name = if (isMr) "नेव्ही ब्लू" else "Navy Blue")
                            ColorSwatch(color = Color(0xFFF8FAFC), name = if (isMr) "पांढरा" else "White", border = true)
                            ColorSwatch(color = Color(0xFF475569), name = if (isMr) "राखाडी" else "Slate Grey")
                            ColorSwatch(color = Color(0xFF3F6212), name = if (isMr) "ऑलिव्ह ग्रीन" else "Olive Green")
                        }
                    }
                }

                // Style Looks
                items(styleLooks) { look ->
                    FitCard(backgroundColor = Color.White) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isMr) look.titleMr else look.titleEn,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = FitTextPrimary
                            )
                            Surface(
                                color = FitLightTeal,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = look.category.capitalize(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FitTeal,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (isMr) look.descriptionMr else look.descriptionEn,
                            fontSize = 12.sp,
                            color = FitTextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = (if (isMr) "योग्य प्रसंग: " else "Best for: ") + if (isMr) look.suitableForMr else look.suitableForEn,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = FitPrimaryGreen
                        )
                    }
                }
            }

            3 -> {
                // Face Analysis Feature matching Storyboard 1 panel 13 & Storyboard 2 panel 17
                item {
                    FitCard(backgroundColor = Color.White) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (isMr) "Face Analysis" else "Face Analysis",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = FitDeepGreen
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            // Biometric AR face landmark visual
                            ArFaceMeshVisual()

                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = if (isMr) "AI तुमच्या चेहऱ्याचे विश्लेषण करत आहे..." else "AI Biometric Facial Analysis Complete",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = FitTeal
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            Surface(
                                color = FitEmerald.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = FitEmerald, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isMr) "चेहऱ्याचा आकार: Oval (अंडाकृती)" else "Face Shape: Oval",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = FitEmerald
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        FaceSpecRow(
                            label = if (isMr) "सर्वोत्तम हेअरस्टाइल (Best Hair Style):" else "Best Hair Style:",
                            value = if (isMr) faceAnalysis.bestHairStyleMr else faceAnalysis.bestHairStyleEn
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        FaceSpecRow(
                            label = if (isMr) "सर्वोत्तम दाढी स्टाइल (Best Beard):" else "Best Beard Style:",
                            value = if (isMr) faceAnalysis.bestBeardStyleMr else faceAnalysis.bestBeardStyleEn
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        FaceSpecRow(
                            label = if (isMr) "शोभून दिसणारा चष्मा (Best Glasses):" else "Best Glasses Style:",
                            value = if (isMr) faceAnalysis.bestGlassesMr else faceAnalysis.bestGlassesEn
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        FaceSpecRow(
                            label = if (isMr) "उत्कृष्ट कपडे (Best Clothing):" else "Best Clothing:",
                            value = if (isMr) "Casual + Formal (कॅज्युअल व फॉर्मल)" else "Casual + Formal"
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        FaceSpecRow(
                            label = if (isMr) "व्यक्तिमत्त्व (Personality):" else "Personality:",
                            value = if (isMr) "Confident & Calm (शांत व आत्मविश्वासी)" else "Confident & Calm"
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { selectedLookCategory = "haircut" },
                            modifier = Modifier.fillMaxWidth().testTag("view_face_recommendations_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = FitPrimaryGreen),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = if (isMr) "शिफारसी पहा (View Recommendations)" else "View Recommendations",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Category selector for Hairstyle, Beard, Glasses, Clothing (Storyboard 1 panel 14)
                item {
                    Text(
                        text = if (isMr) "हेअरस्टाइल व ग्रूमिंग गॅलरी" else "Hairstyle & Grooming Collection",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = FitTextPrimary
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            "all" to (if (isMr) "सर्व" else "All"),
                            "haircut" to (if (isMr) "केस (Hair)" else "Haircuts"),
                            "beard" to (if (isMr) "दाढी (Beard)" else "Beard"),
                            "glasses" to (if (isMr) "चष्मा" else "Glasses"),
                            "clothing" to (if (isMr) "कपडे" else "Clothing")
                        ).forEach { (cat, label) ->
                            FilterChip(
                                selected = selectedLookCategory == cat,
                                onClick = { selectedLookCategory = cat },
                                label = { Text(label, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = FitPrimaryGreen,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }

                items(filteredLooks) { look ->
                    FitCard(backgroundColor = Color.White) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isMr) look.titleMr else look.titleEn,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = FitTextPrimary
                            )
                            Surface(
                                color = FitLightTeal,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = look.category.capitalize(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FitTeal,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isMr) look.descriptionMr else look.descriptionEn,
                            fontSize = 12.sp,
                            color = FitTextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = (if (isMr) "चेहऱ्याचा आकार: " else "Best for: ") + if (isMr) look.suitableForMr else look.suitableForEn,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = FitPrimaryGreen
                        )
                    }
                }

                item {
                    DisclaimerBox(
                        textEn = "Grooming suggestions are aesthetic style recommendations. They do not infer medical conditions or sensitive attributes.",
                        textMr = "स्टाइल व ग्रूमिंग सूचना या केवळ सौंदर्यात्मक सल्ले आहेत. सततच्या समस्यांसाठी त्वचा/केस तज्ज्ञांचा सल्ला घ्या.",
                        isMarathi = isMr
                    )
                }
            }
        }
    }

    // Modal dialog for detailed Hair Plan or Skin Plan
    if (activeDetailPlan != null) {
        val isHair = activeDetailPlan == "hair"
        AlertDialog(
            onDismissRequest = { activeDetailPlan = null },
            title = {
                Text(
                    text = if (isHair) {
                        if (isMr) "७-दिवसीय आयुर्वेदिक केस निगा प्लॅन" else "7-Day Holistic Hair Care Plan"
                    } else {
                        if (isMr) "नैसर्गिक त्वचा काळजी दैनंदिनी" else "Natural Skin Care Daily Routine"
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = FitDeepGreen
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val steps = if (isHair) {
                        if (isMr) listOf(
                            "सोमवार: कढीपत्ता व मेथी दाणे गरम केलेल्या खोबरेल तेलाने हलकी मालिश करा.",
                            "बुधवार: सौम्य शिकेकाई किंवा सल्फेट-मुक्त शॅम्पूने केस धुवा.",
                            "शुक्रवार: दही आणि कोरफडीचा (Aloe Vera) हेअर मास्क २० मिनिटे लावा.",
                            "रविवार: कोमट पाण्याने केस धुवून ताज्या आवळा रसाचे सेवन करा."
                        ) else listOf(
                            "Monday: Scalp massage with warm coconut oil infused with curry leaves.",
                            "Wednesday: Wash with mild botanical or herbal shampoo.",
                            "Friday: Deep nourish mask of fresh curd and organic aloe vera gel.",
                            "Sunday: Rinse gently with lukewarm water and drink fresh amla juice."
                        )
                    } else {
                        if (isMr) listOf(
                            "सकाळी: उठल्यावर १ ग्लास कोमट लिंबू पाणी प्या व चेहरा पाण्याने स्वच्छ करा.",
                            "दुपारी: उन्हात जाताना सनस्क्रीन (SPF 30+) लावा व भरपूर पाणी प्या.",
                            "संध्याकाळी: ताक व काकडीचे सेवन करून शरीराची उष्णता कमी ठेवा.",
                            "रात्री: गुलाबपाणी लावून चेहऱ्याची स्वच्छता करा व ७-८ तास शांत झोप घ्या."
                        ) else listOf(
                            "Morning: Drink warm lemon water and cleanse face gently.",
                            "Noon: Apply broad-spectrum SPF 30+ sunscreen before sun exposure.",
                            "Evening: Hydrate with fresh buttermilk to balance gut and body heat.",
                            "Night: Cleanse with pure rose water and get 7-8 hours restful sleep."
                        )
                    }

                    steps.forEach { stepText ->
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = FitEmerald,
                                modifier = Modifier.size(16.dp).padding(top = 2.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = stepText, fontSize = 12.sp, color = FitTextSecondary, lineHeight = 16.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { activeDetailPlan = null },
                    colors = ButtonDefaults.buttonColors(containerColor = FitPrimaryGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(if (isMr) "समजले (Got it)" else "Got it", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
private fun GroomingDetailCard(
    tip: com.example.data.model.GroomingTip,
    isMr: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onViewPlan: () -> Unit = {}
) {
    FitCard(backgroundColor = Color.White) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(FitLightTeal),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = FitPrimaryGreen)
            }
            Column {
                Text(
                    text = if (isMr) tip.titleMr else tip.titleEn,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = FitTextPrimary
                )
                Text(
                    text = if (isMr) tip.actionAdviceMr else tip.actionAdviceEn,
                    fontSize = 12.sp,
                    color = FitTeal
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        val tips = if (isMr) tip.tipsMr else tip.tipsEn
        tips.forEach { t ->
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.padding(vertical = 3.dp)
            ) {
                Box(
                    modifier = Modifier
                        .padding(top = 6.dp)
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(FitEmerald)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = t, fontSize = 13.sp, color = FitTextSecondary, lineHeight = 18.sp)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = onViewPlan,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (tip.category == "hair") {
                    if (isMr) "केस निगा प्लॅन पहा (View Hair Plan)" else "View Hair Plan"
                } else {
                    if (isMr) "त्वचा निगा प्लॅन पहा (View Skin Plan)" else "View Skin Plan"
                },
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ColorSwatch(color: Color, name: String, border: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(color)
                .then(if (border) Modifier.border(1.dp, FitBorder, CircleShape) else Modifier)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = name, fontSize = 10.sp, color = FitTextSecondary)
    }
}

@Composable
private fun FaceSpecRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 13.sp, color = FitTextSecondary, fontWeight = FontWeight.Medium)
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = FitTextPrimary)
    }
}

@Composable
private fun ArFaceMeshVisual(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(150.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF0F172A)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Face silhouette contour
            drawOval(
                color = Color(0xFF1E293B),
                topLeft = Offset(w * 0.22f, h * 0.12f),
                size = Size(w * 0.56f, h * 0.76f)
            )

            // Landmarks (forehead, brows, eyes, nose bridge, tip, cheekbones, mouth, chin, jawline)
            val points = listOf(
                Offset(w * 0.5f, h * 0.22f), // Forehead center
                Offset(w * 0.38f, h * 0.28f), // Left brow
                Offset(w * 0.62f, h * 0.28f), // Right brow
                Offset(w * 0.36f, h * 0.38f), // Left eye
                Offset(w * 0.64f, h * 0.38f), // Right eye
                Offset(w * 0.5f, h * 0.42f), // Nose bridge
                Offset(w * 0.5f, h * 0.55f), // Nose tip
                Offset(w * 0.26f, h * 0.52f), // Left cheek
                Offset(w * 0.74f, h * 0.52f), // Right cheek
                Offset(w * 0.42f, h * 0.68f), // Left mouth
                Offset(w * 0.58f, h * 0.68f), // Right mouth
                Offset(w * 0.5f, h * 0.82f), // Chin
                Offset(w * 0.32f, h * 0.76f), // Left jaw
                Offset(w * 0.68f, h * 0.76f)  // Right jaw
            )

            // Connect mesh lines with subtle cyan/green glow
            val meshEdges = listOf(
                0 to 1, 0 to 2, 1 to 3, 2 to 4, 3 to 5, 4 to 5,
                5 to 6, 6 to 7, 6 to 8, 7 to 9, 8 to 10, 9 to 11,
                10 to 11, 7 to 12, 12 to 11, 8 to 13, 13 to 11, 9 to 10
            )

            for ((startIdx, endIdx) in meshEdges) {
                drawLine(
                    color = Color(0x8010B981),
                    start = points[startIdx],
                    end = points[endIdx],
                    strokeWidth = 2f
                )
            }

            // Draw glowing biometric dots
            for (pt in points) {
                drawCircle(
                    color = Color(0xFF34D399),
                    radius = 4f,
                    center = pt
                )
                drawCircle(
                    color = Color(0xFF10B981).copy(alpha = 0.4f),
                    radius = 8f,
                    center = pt
                )
            }
        }
    }
}

