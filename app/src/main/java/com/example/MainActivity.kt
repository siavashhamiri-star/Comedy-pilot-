package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Museum
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.ComedyScenarioGenerator
import com.example.audio.SoundEffectsEngine
import com.example.audio.TextToSpeechEngine
import com.example.ui.AboutAndEcosystemView
import com.example.ui.AiScenarioTab
import com.example.ui.DubsmashStudioTab
import com.example.ui.LanguageSelector
import com.example.ui.MemeAndSoundboardTab
import com.example.ui.OnboardingDialog
import com.example.ui.RewardsAndXpTab
import com.example.ui.VoiceChangerTab
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {

    private lateinit var soundEngine: SoundEffectsEngine
    private lateinit var ttsEngine: TextToSpeechEngine
    private lateinit var scenarioGenerator: ComedyScenarioGenerator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        soundEngine = SoundEffectsEngine(this)
        ttsEngine = TextToSpeechEngine(this)
        scenarioGenerator = ComedyScenarioGenerator(this)

        setContent {
            ComedySuperAppTheme {
                MainContainer(
                    soundEngine = soundEngine,
                    ttsEngine = ttsEngine,
                    scenarioGenerator = scenarioGenerator
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ttsEngine.shutdown()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContainer(
    soundEngine: SoundEffectsEngine,
    ttsEngine: TextToSpeechEngine,
    scenarioGenerator: ComedyScenarioGenerator
) {
    var selectedTab by remember { mutableStateOf(0) }
    var currentLanguage by remember { mutableStateOf("FA") }
    var showOnboarding by remember { mutableStateOf(true) }
    val customSavedSounds = remember { mutableStateListOf<String>() }

    val tabs = listOf(
        TabItem("سناریو 🤖", Icons.Default.AutoAwesome),
        TabItem("دابسمش 🎬", Icons.Default.Movie),
        TabItem("صدا 🎙️", Icons.Default.Mic),
        TabItem("میم‌ها 🏛️", Icons.Default.Museum),
        TabItem("پاداش ⚡", Icons.Default.Stars),
        TabItem("تواناسیتی 🌐", Icons.Default.Info)
    )

    if (showOnboarding) {
        OnboardingDialog(
            onDismiss = { showOnboarding = false }
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Surface(
                color = DarkCharcoalBg,
                modifier = Modifier.padding(top = 28.dp)
            ) {
                LanguageSelector(
                    currentLang = currentLanguage,
                    onLangChanged = { newLang ->
                        currentLanguage = newLang
                    }
                )
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = CardDarkBg,
                windowInsets = WindowInsets.navigationBars
            ) {
                tabs.forEachIndexed { index, item ->
                    val isSelected = selectedTab == index
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            selectedTab = index
                            soundEngine.triggerHapticFeedback()
                        },
                        icon = {
                            Icon(
                                item.icon,
                                contentDescription = item.title,
                                tint = if (isSelected) JoyYellow else Color.Gray
                            )
                        },
                        label = {
                            Text(
                                item.title,
                                fontSize = 9.sp,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Normal,
                                color = if (isSelected) JoyYellow else Color.Gray
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = JoyYellow.copy(alpha = 0.2f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> AiScenarioTab(
                    scenarioGenerator = scenarioGenerator,
                    ttsEngine = ttsEngine
                )
                1 -> DubsmashStudioTab(
                    soundEngine = soundEngine
                )
                2 -> VoiceChangerTab(
                    soundEngine = soundEngine,
                    onSaveCustomSound = { soundName ->
                        customSavedSounds.add(soundName)
                    }
                )
                3 -> MemeAndSoundboardTab(
                    soundEngine = soundEngine,
                    customSoundList = customSavedSounds
                )
                4 -> RewardsAndXpTab()
                5 -> AboutAndEcosystemView()
            }
        }
    }
}

private data class TabItem(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)
