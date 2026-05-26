package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.*
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.*
import com.example.ui.StudyViewModel
import com.example.ui.components.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                StudyShieldApp()
            }
        }
    }
}

// Translations Maps
private val labelsEn = mapOf(
    "focus" to "Shield Timer",
    "garden" to "Virtual Garden",
    "buddies" to "Rooms",
    "insights" to "Insights",
    "parent" to "Parent Portal",
    "start_timer" to "Start Focus Session",
    "pause" to "Pause Focus",
    "resume" to "Resume",
    "reset" to "Reset",
    "select_category" to "Select Study Theme",
    "app_blocking" to "Social Blocker Limits",
    "sim_distraction" to "Simulate App Switch Distraction",
    "sim_desc" to "Simulates opening Instagram to test AI warnings & plant impact",
    "eye_rule" to "20-20-20 Eye Care Protection",
    "eye_desc" to "Continuous screen study can strain eyes. Take 20 seconds, look 20 feet away.",
    "nurture_plant" to "Nurture Focus Plant",
    "water_btn" to "Water (-15 Coins)",
    "ai_boost" to "Ask AI Plant Blessing",
    "parent_mode" to "Parental Wellness Center",
    "parent_desc" to "Anonymized, privacy-safe guard for study schedules.",
    "blue_light" to "Simulate Bedtime Blue-Light Filter",
    "bedtime_lock" to "Configure Bedtime Shield (11PM - 5AM)",
    "streak_label" to "Daily Streak",
    "coins_label" to "Study Coins",
    "offline_notice" to "StudyShield secures your privacy: app usage stays fully offline."
)

private val labelsHi = mapOf(
    "focus" to "शील्ड टाइमर",
    "garden" to "वर्चुअल बगीचा",
    "buddies" to "स्टडी रूम्स",
    "insights" to "आँकड़े विश्लेषण",
    "parent" to "अभिभावक शील्ड",
    "start_timer" to "फोकस सत्र शुरू करें",
    "pause" to "फोकस रोकें",
    "resume" to "पुनः आरंभ करें",
    "reset" to "रीसेट",
    "select_category" to "सक्रिय विषय चुनें",
    "app_blocking" to "फोकस ब्लॉकर्स",
    "sim_distraction" to "ऐप बदलने की जांच करें",
    "sim_desc" to "जांचें कि ध्यान टूटने पर एआई और पौधे पर क्या प्रभाव होता है",
    "eye_rule" to "20-10-20 आई केयर सुरक्षा नियम",
    "eye_desc" to "आँखों के तनाव को रोकने के लिए प्रत्येक 20 मिनट में 20 फीट दूर देखें।",
    "nurture_plant" to "अध्ययन पौधे का पोषण करें",
    "water_btn" to "पानी दें (-15 सिक्के)",
    "ai_boost" to "एआई प्रेरक आशीर्वाद",
    "parent_mode" to "अभिभावक डिजिटल पोर्टल",
    "parent_desc" to "अध्ययन समय को बढ़ावा देने के लिए डिजिटल सीमाएं सुरक्षित रखें।",
    "blue_light" to "ब्लू लाइट एम्बर शील्ड शुरू करें",
    "bedtime_lock" to "देर रात उपयोग ब्लॉक करें (11PM - 5AM)",
    "streak_label" to "लगातार दिन",
    "coins_label" to "अर्जित सिक्के",
    "offline_notice" to "ध्यान दें: अध्ययन गोपनीयता सुरक्षित रखने के लिए स्वतंत्र रूप से चलता है।"
)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun StudyShieldApp() {
    val viewModel: StudyViewModel = viewModel()
    val stats by viewModel.userStats.collectAsState()
    val sessions by viewModel.focusSessions.collectAsState()
    val rooms by viewModel.friendRooms.collectAsState()
    val blocks by viewModel.appBlocks.collectAsState()
    val isOnline by viewModel.isOnline.collectAsState()

    // Base active user configuration
    val activeStats = stats ?: UserStats()
    val isHindi = activeStats.language == "hi"
    val labels = if (isHindi) labelsHi else labelsEn

    // Bottom Tab State
    var activeTab by remember { mutableStateOf("focus") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(com.example.ui.theme.BentoBackground)
    ) {
        Scaffold(
            topBar = {
                // Bento Style Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left Logo & Applet Title
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(com.example.ui.theme.BentoViolet, RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "S",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Column {
                                Text(
                                    text = labels["app_title"] ?: "StudyShield",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "PRIVACY PROTECTED",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF64748B), // Slate 500
                                    letterSpacing = 1.sp
                                )
                            }
                        }

                        // Right Statuses & Global State Quick Indicator Controls
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Flame Streak Tracker
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(com.example.ui.theme.BentoSurface)
                                    .border(1.dp, com.example.ui.theme.BentoBorder, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Whatshot,
                                    contentDescription = "Streak",
                                    tint = com.example.ui.theme.BentoOrange,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${activeStats.streak} 🔥",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            // Live network status indicator
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(com.example.ui.theme.BentoSurface)
                                    .border(1.dp, com.example.ui.theme.BentoBorder, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(if (isOnline) com.example.ui.theme.BentoEmerald else Color(0xFF64748B))
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isOnline) "ONLINE" else "OFFLINE",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (isOnline) com.example.ui.theme.BentoEmerald else Color(0xFF94A3B8)
                                )
                            }

                            // Coins Inventory Balance
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(com.example.ui.theme.BentoSurface)
                                    .border(1.dp, com.example.ui.theme.BentoBorder, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MonetizationOn,
                                    contentDescription = "Coins",
                                    tint = Color(0xFFFBBF24),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${activeStats.coins}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            // English/Hindi Toggle Button
                            IconButton(
                                onClick = {
                                    val nextLang = if (activeStats.language == "en") "hi" else "en"
                                    viewModel.setLanguage(nextLang)
                                },
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(com.example.ui.theme.BentoSurface, RoundedCornerShape(8.dp))
                                    .border(1.dp, com.example.ui.theme.BentoBorder, RoundedCornerShape(8.dp))
                                    .testTag("language_toggle")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Language,
                                    contentDescription = "Language",
                                    tint = com.example.ui.theme.BentoViolet,
                                    modifier = Modifier.size(14.dp)
                                )
                            }

                            // Personal Profile Initial Avatar
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .border(1.dp, com.example.ui.theme.BentoBorder, CircleShape)
                                    .padding(1.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color(0xFF334155), CircleShape), // Slate 700
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "JD",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFCBD5E1) // Slate 300
                                    )
                                }
                            }
                        }
                    }
                }
            },
            bottomBar = {
                // Bottom tab row styled as a dark bento panel
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF161618)
                        ),
                        border = BorderStroke(1.dp, com.example.ui.theme.BentoBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TabNavItem(
                                icon = Icons.Default.Timer,
                                label = labels["focus"] ?: "Timer",
                                isActive = activeTab == "focus",
                                onClick = { activeTab = "focus" },
                                tag = "tab_timer"
                            )
                            TabNavItem(
                                icon = Icons.Default.Yard,
                                label = labels["garden"] ?: "Garden",
                                isActive = activeTab == "garden",
                                onClick = { activeTab = "garden" },
                                tag = "tab_garden"
                            )
                            TabNavItem(
                                icon = Icons.Default.Groups,
                                label = labels["buddies"] ?: "Rooms",
                                isActive = activeTab == "buddies",
                                onClick = { activeTab = "buddies" },
                                tag = "tab_buddies"
                            )
                            TabNavItem(
                                icon = Icons.Default.Analytics,
                                label = labels["insights"] ?: "Insights",
                                isActive = activeTab == "insights",
                                onClick = { activeTab = "insights" },
                                tag = "tab_insights"
                            )
                            TabNavItem(
                                icon = Icons.Default.FamilyRestroom,
                                label = labels["parent"] ?: "Parents",
                                isActive = activeTab == "parent",
                                onClick = { activeTab = "parent" },
                                tag = "tab_parent"
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Handle different tabs with animated content
                AnimatedContent(
                    targetState = activeTab,
                    transitionSpec = {
                        slideInHorizontally { width -> width / 3 } + fadeIn() togetherWith
                        slideOutHorizontally { width -> -width / 3 } + fadeOut()
                    },
                    modifier = Modifier.fillMaxSize()
                ) { currentTab ->
                    when (currentTab) {
                        "focus" -> FocusScreen(viewModel, blocks, activeStats, labels)
                        "garden" -> GardenScreen(viewModel, activeStats, labels)
                        "buddies" -> BuddiesScreen(viewModel, rooms, activeStats, labels)
                        "insights" -> InsightsScreen(sessions, labels)
                        "parent" -> ParentScreen(viewModel, activeStats, labels)
                    }
                }
            }
        }

        // Night Amber Shield overlay if enabled (simulating eye protective amber screens)
        if (activeStats.nightModeEnabled) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF59E0B).copy(alpha = 0.16f)) // Warm safe gold protection
                    .clickable(enabled = false) {}
            )
        }

        // Active 20-20-20 Eye Care Overlays
        val isEyeBreak by viewModel.isEyeCareBreakShowing.collectAsState()
        val eyeCountdown by viewModel.eyeCareActiveCountdown.collectAsState()

        if (isEyeBreak) {
            Card(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth(0.85f)
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = "Eye Break",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(56.dp)
                    )
                    Text(
                        text = labels["eye_rule"] ?: "20-20-20 Eye Care",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = labels["eye_desc"] ?: "Every 20 minutes, look at a distance of 20 feet for 20 seconds.",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$eyeCountdown",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                    Button(
                        onClick = { viewModel.dismissEyeCareBreak() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(labels["reset"] ?: "Done")
                    }
                }
            }
        }

        // AI Warning Distraction overlays
        val warning by viewModel.distractionWarning.collectAsState()
        val aiLoading by viewModel.isAiLoading.collectAsState()

        if (warning != null) {
            Card(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth(0.9f)
                    .padding(16.dp),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF161618)
                ),
                border = BorderStroke(1.dp, com.example.ui.theme.BentoBorder),
                elevation = CardDefaults.cardElevation(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .background(Color(0xFFEF4444).copy(alpha = 0.15f), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationImportant,
                            contentDescription = "Warning",
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (activeStats.language == "hi") "एआई एकाग्रता चेतावनी" else "AI Distraction Shield",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isOnline) "ONLINE GEMINI LIVE COACH" else "OFFLINE SHIELD CACHE",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isOnline) com.example.ui.theme.BentoEmerald else Color(0xFF64748B),
                            letterSpacing = 1.sp
                        )
                    }

                    if (aiLoading) {
                        CircularProgressIndicator(
                            color = com.example.ui.theme.BentoViolet,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(36.dp)
                        )
                    } else {
                        Text(
                            text = warning ?: "",
                            fontSize = 13.sp,
                            color = Color(0xFFCBD5E1),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { viewModel.clearDistractionWarning() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = com.example.ui.theme.BentoViolet
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(42.dp)
                        ) {
                            Text("Stay Focused 💪", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TabNavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isActive: Boolean,
    onClick: () -> Unit,
    tag: String
) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp, horizontal = 12.dp)
            .testTag(tag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isActive) com.example.ui.theme.BentoViolet else Color(0xFF71717A),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
            color = if (isActive) Color.White else Color(0xFF71717A)
        )
    }
}

// ---------------- TIMER TAB SCREEN ----------------
@Composable
fun FocusScreen(
    viewModel: StudyViewModel,
    blocks: List<AppUsageBlock>,
    stats: UserStats,
    labels: Map<String, String>
) {
    val secRemaining by viewModel.timerSeconds.collectAsState()
    val totalSec by viewModel.timerTotalSeconds.collectAsState()
    val isRunning by viewModel.isTimerRunning.collectAsState()
    val sessionType by viewModel.currentSessionType.collectAsState()
    val selectedCat by viewModel.selectedCategory.collectAsState()

    val mins = secRemaining / 60
    val secs = secRemaining % 60
    val timeFormatted = String.format("%02d:%02d", mins, secs)
    val progressValue = if (totalSec > 0) secRemaining.toFloat() / totalSec else 1f

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // AI Study Timer Bento Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = com.example.ui.theme.BentoSurface),
                border = BorderStroke(1.dp, com.example.ui.theme.BentoBorder)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = labels["focus"] ?: "Shield Timer",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF71717A) // Slate 500
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    if (sessionType == "Study") com.example.ui.theme.BentoEmerald.copy(alpha = 0.15f)
                                    else com.example.ui.theme.BentoBlue.copy(alpha = 0.15f)
                                )
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = sessionType.uppercase(),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (sessionType == "Study") com.example.ui.theme.BentoEmerald else com.example.ui.theme.BentoBlue
                            )
                        }
                    }

                    // Graphical Timer Ring
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .drawBehind {
                                // Background circle
                                drawCircle(
                                    color = Color.White.copy(alpha = 0.05f),
                                    style = Stroke(width = 8.dp.toPx())
                                )
                                // Active progress sweep
                                drawArc(
                                    color = if (sessionType == "Study") com.example.ui.theme.BentoEmerald else com.example.ui.theme.BentoOrange,
                                    startAngle = -90f,
                                    sweepAngle = 360f * progressValue,
                                    useCenter = false,
                                    style = Stroke(width = 8.dp.toPx())
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = timeFormatted,
                                fontSize = 38.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                color = if (sessionType == "Study") com.example.ui.theme.BentoEmerald else com.example.ui.theme.BentoOrange,
                                letterSpacing = (-1).sp
                            )
                            Text(
                                text = selectedCat,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF71717A)
                            )
                        }
                    }

                    // Primary control button bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { if (isRunning) viewModel.pauseTimer() else viewModel.startTimer() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isRunning) com.example.ui.theme.BentoOrangeDark else com.example.ui.theme.BentoViolet
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .weight(1.3f)
                                .height(44.dp)
                                .testTag("toggle_timer")
                        ) {
                            Text(
                                text = if (isRunning) labels["pause"] ?: "Pause" else labels["resume"] ?: "Start",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Button(
                            onClick = { viewModel.stopTimer() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF27272A)),
                            border = BorderStroke(1.dp, com.example.ui.theme.BentoBorder),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("stop_timer")
                        ) {
                            Text(
                                text = labels["reset"] ?: "Reset",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE4E4E7)
                            )
                        }
                    }

                    // Fast setting triggers
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val durations = listOf(Pair(25, "Study"), Pair(5, "Short Break"), Pair(15, "Long Break"))
                        durations.forEach { (mins, type) ->
                            OutlinedButton(
                                onClick = { viewModel.setTimerDuration(mins, type) },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = Color.Transparent,
                                    contentColor = Color.White
                                ),
                                border = BorderStroke(1.dp, com.example.ui.theme.BentoBorder),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("${mins}m", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Active Subject category selector Bento Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = com.example.ui.theme.BentoSurface),
                border = BorderStroke(1.dp, com.example.ui.theme.BentoBorder)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = labels["select_category"] ?: "Select Theme",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF71717A)
                    )

                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val categories = listOf("Exam Prep 📚", "Math Solving 🧮", "Chemistry Lab 🧪", "Coding Lab 💻")
                        categories.forEach { cat ->
                            val isSel = selectedCat == cat
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSel) com.example.ui.theme.BentoViolet
                                        else Color(0xFF27272A)
                                    )
                                    .border(
                                        1.dp,
                                        if (isSel) com.example.ui.theme.BentoViolet else com.example.ui.theme.BentoBorder,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable { viewModel.setSelectedCategory(cat) }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                                    .testTag("cat_$cat")
                            ) {
                                Text(
                                    text = cat,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        // Simulated background app blocking switchers
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = com.example.ui.theme.BentoSurface),
                border = BorderStroke(1.dp, com.example.ui.theme.BentoBorder)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = labels["app_blocking"] ?: "Distraction Filters",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF71717A)
                    )

                    blocks.forEach { b ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(b.appName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text(b.category, fontSize = 11.sp, color = Color(0xFF71717A))
                            }
                            Switch(
                                checked = b.isBlocked,
                                onCheckedChange = { viewModel.toggleAppBlock(b) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = com.example.ui.theme.BentoViolet,
                                    uncheckedThumbColor = Color(0xFF71717A),
                                    uncheckedTrackColor = Color(0xFF27272A)
                                )
                            )
                        }
                    }
                }
            }
        }

        // Permanent Eye Care Bento Widget
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E3A8A).copy(alpha = 0.15f)),
                border = BorderStroke(1.dp, Color(0xFF3B82F6).copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color(0xFF3B82F6).copy(alpha = 0.2f), RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = "Eye Care",
                            tint = Color(0xFF60A5FA),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "20-20-20 RULE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF60A5FA),
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Time to look away from your screen for 20 seconds.",
                            fontSize = 12.sp,
                            color = Color(0xFFBFDBFE)
                        )
                    }
                }
            }
        }

        // Quick Stats row mapping the design dashboard (3 small equivalent Bento columns)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Col 1: Instag Blocked
                val instaBlocked = blocks.firstOrNull { it.appName.lowercase().contains("instagram") }?.isBlocked ?: true
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(84.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = com.example.ui.theme.BentoSurface),
                    border = BorderStroke(1.dp, com.example.ui.theme.BentoBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "INSTAGRAM",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF71717A)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (instaBlocked) "Blocked" else "Allowed",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (instaBlocked) com.example.ui.theme.BentoRed else com.example.ui.theme.BentoOrange
                        )
                    }
                }

                // Col 2: Productivity Index
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(84.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = com.example.ui.theme.BentoSurface),
                    border = BorderStroke(1.dp, com.example.ui.theme.BentoBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "PRODUCTIVITY",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF71717A)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "+18%",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = com.example.ui.theme.BentoEmerald
                        )
                    }
                }

                // Col 3: Sleep Safeguard
                val sleepProtect = stats.nightModeEnabled
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(84.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = com.example.ui.theme.BentoSurface),
                    border = BorderStroke(1.dp, com.example.ui.theme.BentoBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "SLEEP GUARD",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF71717A)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (sleepProtect) "Active" else "Standby",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (sleepProtect) com.example.ui.theme.BentoBlue else Color(0xFF71717A)
                        )
                    }
                }
            }
        }

        // Simulated distraction overlay test activator
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = com.example.ui.theme.BentoRed.copy(alpha = 0.08f)),
                border = BorderStroke(1.dp, com.example.ui.theme.BentoRed.copy(alpha = 0.2f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = labels["sim_distraction"] ?: "Simulate Distraction",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = com.example.ui.theme.BentoRed
                    )
                    Text(
                        text = labels["sim_desc"] ?: "Mocks opening blocked Instagram Reels tab during study to show AI protection action.",
                        fontSize = 11.sp,
                        color = Color(0xFFCBD5E1)
                    )
                    Button(
                        onClick = { viewModel.simulateAppSwitchAndDetect() },
                        colors = ButtonDefaults.buttonColors(containerColor = com.example.ui.theme.BentoRed),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .testTag("simulate_distraction")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Dangerous, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Simulate App Switch Now", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

// ---------------- GARDEN TAB SCREEN ----------------
@Composable
fun GardenScreen(
    viewModel: StudyViewModel,
    stats: UserStats,
    labels: Map<String, String>
) {
    val aiTip by viewModel.aiTip.collectAsState()
    val isAiLoading by viewModel.isAiLoading.collectAsState()
    val isOnline by viewModel.isOnline.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Virtual Plant Frame Bento Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = com.example.ui.theme.BentoSurface),
                border = BorderStroke(1.dp, com.example.ui.theme.BentoBorder)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = labels["nurture_plant"] ?: "My Focus Branch",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF71717A)
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(com.example.ui.theme.BentoEmerald.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "LEVEL ${(stats.plantLevel * 10).toInt()} PLANT",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = com.example.ui.theme.BentoEmerald
                            )
                        }
                    }

                    // Majestic custom drawn Canvas plant view!
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(190.dp)
                            .background(
                                Color(0xFF161618),
                                RoundedCornerShape(18.dp)
                            )
                            .border(1.dp, com.example.ui.theme.BentoBorder, RoundedCornerShape(18.dp))
                            .padding(8.dp)
                    ) {
                        VirtualPlantView(
                            plantType = stats.plantType,
                            plantLevel = stats.plantLevel,
                            plantHealth = stats.plantHealth
                        )
                    }

                    // Progress parameters indicators
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Type: ${stats.plantType}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Growth level progress: ${(stats.plantLevel * 100).toInt()}%",
                                fontSize = 11.sp,
                                color = Color(0xFF71717A)
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Health: ${(stats.plantHealth * 100).toInt()}%",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (stats.plantHealth < 0.4f) com.example.ui.theme.BentoRed else com.example.ui.theme.BentoEmerald
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .width(80.dp)
                                    .height(6.dp)
                                    .background(Color(0xFF27272A), CircleShape)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(stats.plantHealth)
                                        .fillMaxHeight()
                                        .background(
                                            if (stats.plantHealth < 0.4f) com.example.ui.theme.BentoRed else com.example.ui.theme.BentoEmerald,
                                            CircleShape
                                        )
                                )
                            }
                        }
                    }

                    // Plant modification bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val types = listOf("Bonsai", "Sunflower", "Succulent")
                        types.forEach { t ->
                            val isSel = stats.plantType == t
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSel) com.example.ui.theme.BentoViolet.copy(alpha = 0.2f)
                                        else Color.Transparent
                                    )
                                    .border(
                                        1.dp,
                                        if (isSel) com.example.ui.theme.BentoViolet else com.example.ui.theme.BentoBorder,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable { viewModel.changePlantType(t) }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = t,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) Color.White else Color(0xFF71717A)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Water/Nurture Interactive Actions buttons Bento Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = com.example.ui.theme.BentoSurface),
                border = BorderStroke(1.dp, com.example.ui.theme.BentoBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { viewModel.waterPlant() },
                        enabled = stats.coins >= 15,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = com.example.ui.theme.BentoBlue,
                            disabledContainerColor = Color(0xFF27272A)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("button_water_plant")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.WaterDrop, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = labels["water_btn"] ?: "Water (-15c)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Button(
                        onClick = { viewModel.fetchStudyTipManual() },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = com.example.ui.theme.BentoViolet),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("button_ai_tip")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = labels["ai_boost"] ?: "AI Coach Tip",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // Active generated study blessing tips card
        if (aiTip != null || isAiLoading) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = com.example.ui.theme.BentoSurface),
                    border = BorderStroke(1.dp, com.example.ui.theme.BentoBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = null,
                                    tint = com.example.ui.theme.BentoOrange,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = "AI Coach Study Tip 💡",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = if (isOnline) "ONLINE GEMINI SPEED" else "OFFLINE ENCOURAGEMENT",
                                        fontSize = 7.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isOnline) com.example.ui.theme.BentoEmerald else Color(0xFF71717A),
                                        letterSpacing = 0.5.sp
                                    )
                                }
                            }
                            IconButton(onClick = { viewModel.clearAiTip() }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF71717A), modifier = Modifier.size(14.dp))
                            }
                        }

                        if (isAiLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = com.example.ui.theme.BentoViolet)
                        } else {
                            Text(
                                text = aiTip ?: "",
                                fontSize = 12.sp,
                                color = Color(0xFFCBD5E1),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ---------------- BUDDIES TAB SCREEN ----------------
@Composable
fun BuddiesScreen(
    viewModel: StudyViewModel,
    rooms: List<FriendRoom>,
    stats: UserStats,
    labels: Map<String, String>
) {
    var showAddRoomDialog by remember { mutableStateOf(false) }
    var inputRoomName by remember { mutableStateOf("") }
    var inputMemberName by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Study Rooms 👥",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "MULTIPLAYER FOCUS SESSIONS",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B),
                        letterSpacing = 1.sp
                    )
                }

                Button(
                    onClick = { showAddRoomDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = com.example.ui.theme.BentoViolet),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("+ Create", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (rooms.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = com.example.ui.theme.BentoSurface),
                    border = BorderStroke(1.dp, com.example.ui.theme.BentoBorder)
                ) {
                    Text(
                        "No study rooms created yet! Get started by clicking Create above.",
                        modifier = Modifier.padding(24.dp),
                        fontSize = 12.sp,
                        color = Color(0xFF71717A),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        items(rooms) { r ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = com.example.ui.theme.BentoSurface),
                border = BorderStroke(1.dp, com.example.ui.theme.BentoBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Groups,
                                contentDescription = null,
                                tint = com.example.ui.theme.BentoViolet,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(r.roomName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Text("Studying Partner: ${r.memberName}", fontSize = 12.sp, color = Color(0xFFCBD5E1))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(com.example.ui.theme.BentoEmerald.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = r.status.uppercase(),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = com.example.ui.theme.BentoEmerald
                            )
                        }
                    }

                    // Flame Streak metric
                    Column(horizontalAlignment = Alignment.End) {
                        Text("STREAK 🔥", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color(0xFF71717A))
                        Text("${r.streakDecimal} days", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = com.example.ui.theme.BentoOrange)
                    }
                }
            }
        }
    }

    if (showAddRoomDialog) {
        AlertDialog(
            onDismissRequest = { showAddRoomDialog = false },
            containerColor = Color(0xFF161618),
            titleContentColor = Color.White,
            textContentColor = Color(0xFF71717A),
            title = { Text("Create Study Room", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = inputRoomName,
                        onValueChange = { inputRoomName = it },
                        label = { Text("Room Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = inputMemberName,
                        onValueChange = { inputMemberName = it },
                        label = { Text("Friend Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val name = inputRoomName.ifBlank { "Study Group #2" }
                        val friend = inputMemberName.ifBlank { "Ramesh Chawla" }
                        viewModel.createFriendRoom(name, friend)
                        showAddRoomDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = com.example.ui.theme.BentoViolet),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Save", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showAddRoomDialog = false }
                ) {
                    Text("Cancel", color = Color(0xFF71717A))
                }
            }
        )
    }
}

// ---------------- INSIGHTS TAB SCREEN ----------------
@Composable
fun InsightsScreen(
    sessions: List<FocusSession>,
    labels: Map<String, String>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                Text(
                    text = "Weekly Analytics 📊",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "PERSONAL CONCENTRATION PERFORMANCE",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B),
                    letterSpacing = 1.sp
                )
            }
        }

        // Canvas comparative usage chart Bento card
        item {
            val focusData = listOf(45, 90, 25, 120, 60, 45, 30) // Mon to Sun mock values
            val screenLimits = listOf(200, 150, 120, 180, 240, 210, 110)
            val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

            WeeklyUsageChart(
                focusMinutes = focusData,
                screenMinutes = screenLimits,
                daysOfWeek = days
            )
        }

        // Focus topic apportionment calculations Bento card
        item {
            val categorisedMinutes = listOf(
                Pair("General Study", 120),
                Pair("Math Solving", 80),
                Pair("Coding Lab", 50),
                Pair("Exam Prep", 40)
            )
            CategoryDistributionChart(categories = categorisedMinutes)
        }

        // Night protections statistics Bento card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = com.example.ui.theme.BentoSurface),
                border = BorderStroke(1.dp, com.example.ui.theme.BentoBorder)
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(com.example.ui.theme.BentoBlue.copy(alpha = 0.15f), RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bedtime,
                            contentDescription = "Night Protection State",
                            tint = com.example.ui.theme.BentoBlue,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "SLEEP GUARD SCORE: EXCELLENT ✅",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = com.example.ui.theme.BentoBlue,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Limits on evening screen time have successfully prevented late-night doomscrolling recently.",
                            fontSize = 12.sp,
                            color = Color(0xFFCBD5E1)
                        )
                    }
                }
            }
        }
    }
}

// ---------------- PARENT PORTAL TAB SCREEN ----------------
@Composable
fun ParentScreen(
    viewModel: StudyViewModel,
    stats: UserStats,
    labels: Map<String, String>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Parent Title
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Text(
                    text = labels["parent_mode"] ?: "Wellness Center",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "ANONYMIZED STUDENT DIAGNOSTICS",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B),
                    letterSpacing = 1.sp
                )
            }
        }

        // Shared linking code card Bento card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = com.example.ui.theme.BentoSurface),
                border = BorderStroke(1.dp, com.example.ui.theme.BentoBorder)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "ANONYMIZED STUDENT ACCESS KEY",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF71717A)
                    )
                    Text(
                        text = stats.parentCode,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        color = com.example.ui.theme.BentoViolet,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "Pair this code on parent's device to mirror student stats securely while keeping personal messages or browser histories completely private.",
                        fontSize = 11.sp,
                        color = Color(0xFFCBD5E1),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Parent controls switches Bento Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = com.example.ui.theme.BentoSurface),
                border = BorderStroke(1.dp, com.example.ui.theme.BentoBorder)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "DEVICE PROTECTIVE SETTINGS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF71717A)
                    )

                    // Blue Light simulation toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(com.example.ui.theme.BentoBlue.copy(alpha = 0.15f), RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bedtime,
                                    contentDescription = null,
                                    tint = com.example.ui.theme.BentoBlue,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = labels["blue_light"] ?: "Night Protection Screen",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Filters display to relieve cognitive stress and support sleep cycles.",
                                    fontSize = 10.sp,
                                    color = Color(0xFF71717A)
                                )
                            }
                        }
                        Switch(
                            checked = stats.nightModeEnabled,
                            onCheckedChange = { viewModel.toggleNightMode(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = com.example.ui.theme.BentoViolet,
                                uncheckedThumbColor = Color(0xFF71717A),
                                uncheckedTrackColor = Color(0xFF27272A)
                            ),
                            modifier = Modifier.testTag("parent_toggle_night")
                        )
                    }

                    // 20-20-20 Eye health rule active toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(com.example.ui.theme.BentoViolet.copy(alpha = 0.15f), RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Visibility,
                                    contentDescription = null,
                                    tint = com.example.ui.theme.BentoViolet,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Enforce 20-20-20 Breaks",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Alerts student to take ocular breaks periodically.",
                                    fontSize = 10.sp,
                                    color = Color(0xFF71717A)
                                )
                            }
                        }
                        Switch(
                            checked = stats.eyeCareEnabled,
                            onCheckedChange = { viewModel.toggleEyeCare(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = com.example.ui.theme.BentoViolet,
                                uncheckedThumbColor = Color(0xFF71717A),
                                uncheckedTrackColor = Color(0xFF27272A)
                            ),
                            modifier = Modifier.testTag("parent_toggle_eye")
                        )
                    }

                    // Bedtime Lock
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(com.example.ui.theme.BentoOrange.copy(alpha = 0.15f), RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TimerOff,
                                    contentDescription = null,
                                    tint = com.example.ui.theme.BentoOrange,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Bedtime App Block (11PM)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Limits access to social media apps after hours.",
                                    fontSize = 10.sp,
                                    color = Color(0xFF71717A)
                                )
                            }
                        }
                        var blockActive by remember { mutableStateOf(false) }
                        Switch(
                            checked = blockActive,
                            onCheckedChange = { blockActive = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = com.example.ui.theme.BentoViolet,
                                uncheckedThumbColor = Color(0xFF71717A),
                                uncheckedTrackColor = Color(0xFF27272A)
                            ),
                            modifier = Modifier.testTag("parent_toggle_bedtime")
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = labels["offline_notice"] ?: "StudyShield operations stay offline.",
                fontSize = 11.sp,
                color = Color(0xFF71717A),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 12.dp)
            )
        }
    }
}
