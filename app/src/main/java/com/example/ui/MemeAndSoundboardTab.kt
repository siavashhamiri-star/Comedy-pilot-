package com.example.ui

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SoundEffectsEngine
import com.example.model.SoundClip
import com.example.ui.theme.*

@Composable
fun MemeAndSoundboardTab(
    soundEngine: SoundEffectsEngine,
    customSoundList: List<String> = emptyList()
) {
    val context = LocalContext.current
    var subTabState by remember { mutableStateOf(0) } // 0: Soundboard, 1: Meme Maker

    val soundClips = remember {
        mutableStateListOf(
            SoundClip("1", "چطوری ایرانی؟ 🇮🇷", "وایرال", "😎", "دیالوگ وایرال شبکه‌های اجتماعی", 2000, JoyYellow),
            SoundClip("2", "ای جان! ❤️", "دیالوگ", "😍", "تکیه‌کلام عاشقانه کمدی", 1500, JoyPink),
            SoundClip("3", "خیلی هم عالی! 👌", "وایرال", "🥳", "تایید باحال و انرژی‌بخش", 1800, JoyGreen),
            SoundClip("4", "خنده‌ی قهقه‌دار 🤣", "خنده‌دار", "🤣", "صدای خنده‌های دسته‌جمعی", 2500, JoyOrange),
            SoundClip("5", "دراما و شوک 🎺", "افکت", "😱", "شیپور هراس و تعجب", 1500, JoyPurple),
            SoundClip("6", "ویولن غمگین 🎻", "افکت", "😭", "موسیقی صحنه‌های شکست عشقی", 2500, JoyCyan),
            SoundClip("7", "افکت فنر 🌀", "افکت", "🤪", "صدای پریدن کمدی", 1200, JoyYellow),
            SoundClip("8", "زنگ پیروزی 🔔", "افکت", "🏆", "چایم برنده شدن", 1800, JoyGreen),
            SoundClip("9", "پاسخ غلط ❌", "افکت", "🛑", "آلارم اشتباه گفتن", 1200, JoyOrange),
            SoundClip("10", "تشویق و دست 👏", "ری‌اکشن", "👏", "صدای تشویق تماشاگران", 2200, JoyPink),
            SoundClip("11", "ریمپ شات کمدی 🥁", "افکت", "🥁", "ضرب زدن بعد جوک", 1000, JoyCyan),
            SoundClip("12", "گاز کمدی 💨", "خنده‌دار", "💨", "افکت صوتی شوخ‌طبعانه", 1000, JoyYellow)
        )
    }

    val categories = listOf("همه 🌟", "وایرال 🔥", "افکت ✨", "دیالوگ 💬", "خنده‌دار 🤣", "ری‌اکشن 🎯")
    var selectedCategory by remember { mutableStateOf("همه 🌟") }
    var searchQuery by remember { mutableStateOf("") }
    var playingClipId by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Top Sub-Tabs Selector
        TabRow(
            selectedTabIndex = subTabState,
            containerColor = CardDarkBg,
            contentColor = JoyYellow,
            divider = {}
        ) {
            Tab(
                selected = subTabState == 0,
                onClick = { subTabState = 0 },
                text = { Text("🏛️ موزه و تخته صدا (Soundboard)", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
            )
            Tab(
                selected = subTabState == 1,
                onClick = { subTabState = 1 },
                text = { Text("🎨 میم‌ساز (Meme Maker)", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
            )
        }

        if (subTabState == 0) {
            // Soundboard View
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                // Search Field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("جستجوی افکت صوتی، میم یا دیالوگ...", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = JoyYellow) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = JoyYellow,
                        unfocusedBorderColor = Color.Gray,
                        focusedContainerColor = CardDarkBg,
                        unfocusedContainerColor = CardDarkBg
                    )
                )

                // Category Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = JoyYellow,
                                selectedLabelColor = Color.Black,
                                containerColor = CardSurfaceVariant,
                                labelColor = Color.White
                            )
                        )
                    }
                }

                // Filtered List
                val filteredClips = soundClips.filter { clip ->
                    val matchesCategory = selectedCategory == "همه 🌟" || clip.category == selectedCategory.replace(Regex("[^a-zA-Z0-9آ-ی‌ ]"), "").trim()
                    val matchesSearch = searchQuery.isBlank() || clip.title.contains(searchQuery) || clip.description.contains(searchQuery)
                    matchesCategory && matchesSearch
                }

                // Sound Buttons Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(filteredClips) { clip ->
                        val isPlaying = playingClipId == clip.id

                        Card(
                            onClick = {
                                playingClipId = clip.id
                                val effectMap = mapOf(
                                    "1" to "cheetori",
                                    "2" to "ey_jan",
                                    "3" to "kheili_ali",
                                    "4" to "laugh_track",
                                    "5" to "dramatic_horn",
                                    "6" to "sad_violin",
                                    "7" to "boing",
                                    "8" to "win_chime",
                                    "9" to "wrong_buzzer",
                                    "10" to "cheer",
                                    "11" to "rimshot",
                                    "12" to "fart"
                                )
                                soundEngine.playSoundEffect(effectMap[clip.id] ?: "rimshot")
                            },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isPlaying) clip.accentColor.copy(alpha = 0.35f) else CardDarkBg
                            ),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                width = if (isPlaying) 2.dp else 1.dp,
                                color = if (isPlaying) clip.accentColor else Color.Gray.copy(alpha = 0.25f)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(clip.emoji, fontSize = 28.sp)
                                    IconButton(
                                        onClick = {
                                            val index = soundClips.indexOfFirst { it.id == clip.id }
                                            if (index >= 0) {
                                                soundClips[index] = clip.copy(isFavorite = !clip.isFavorite)
                                            }
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            if (clip.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                            contentDescription = "علاقه‌مندی",
                                            tint = if (clip.isFavorite) JoyPink else Color.Gray,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = clip.title,
                                    color = if (isPlaying) clip.accentColor else Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )

                                Text(
                                    text = clip.description,
                                    color = Color.Gray,
                                    fontSize = 10.sp,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1
                                )

                                if (isPlaying) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Icon(
                                        Icons.Default.GraphicEq,
                                        contentDescription = "در حال پخش",
                                        tint = clip.accentColor,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Meme Maker Sub-Tab
            MemeMakerView(
                onMemeSaved = { msg ->
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}
