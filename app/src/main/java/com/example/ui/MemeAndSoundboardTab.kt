package com.example.ui

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.audio.SoundEffectsEngine
import com.example.ui.theme.*

@Composable
fun MemeAndSoundboardTab(
    soundEngine: SoundEffectsEngine,
    customSoundList: List<String> = emptyList()
) {
    val context = LocalContext.current
    var subTabState by remember { mutableStateOf(0) } // 0: Soundboard, 1: Meme Studio, 2: Gallery

    var selectedCategory by remember { mutableStateOf("سینمای اصیل و قیصر 🎬") }
    val categories = listOf(
        "سینمای اصیل و قیصر 🎬",
        "لاله‌زار و کلاسیک 🎩", 
        "نوستالژی تلویزیون 📺", 
        "کارتون‌ها و خاطرات کودکی 🧸",
        "جوکر و طنز مدرن 🃏", 
        "طنز اینترنتی و وایرال 🚀", 
        "بین‌الملل و تاکشو 🌍"
    )

    var searchQuery by remember { mutableStateOf("") }
    var activePlayingTitle by remember { mutableStateOf<String?>(null) }

    val currentItems = when (selectedCategory) {
        "سینمای اصیل و قیصر 🎬" -> listOf(
            "قیصر! داش رو کشتن! 🔪" to JoyYellow,
            "بهمن مفید (ما بودیم...) 🧔‍♂️" to JoyOrange,
            "حبیب آقا (چاقو از پشت!) 🗡️" to JoyPink,
            "سوته‌دلان (مجید تو چرا؟) 🤦‍♂️" to JoyPurple,
            "«ما خودمون مؤلفِ این سبک‌هاییم!» 😎" to JoyYellow,
            "«دنیا دو روزه، بخور!» 🍲" to JoyGreen
        )
        "لاله‌زار و کلاسیک 🎩" -> listOf(
            "مرحوم میری (رقص و متلک) 🕺" to JoyYellow,
            "مرحوم ظهوری (چشم و ابرو) 👀" to JoyOrange,
            "محمدعلی فردین (شیرین‌کاری) 🎭" to JoyPink,
            "آرام‌سپهر و طنز قدیمی 🎬" to JoyPurple
        )
        "نوستالژی تلویزیون 📺" -> listOf(
            "دشواری نداره! 🧔‍♂️" to JoyYellow,
            "بخور، همش گوشته، همش سوسیسه! 🌭" to JoyOrange,
            "ننه من گشنمه 🍲" to JoyPink,
            "مربا بده بابا 🍓" to JoyGreen,
            "گردِ نخود برره 🥴" to JoyPurple
        )
        "کارتون‌ها و خاطرات کودکی 🧸" -> listOf(
            "پت و مت (هورا! هورا!) 🎉" to JoyYellow,
            "خرابکاری لوله‌کشی (پت و مت) 🚿" to JoyOrange,
            "بریدن صندلی زیر پا (پت و مت) 🪚" to JoyPink,
            "خانم کوچولو و صداهای بانمک 🍼" to JoyPurple,
            "ریتم شاد کارتون‌های قدیمی (هی را را) 🎶" to JoyGreen
        )
        "جوکر و طنز مدرن 🃏" -> listOf(
            "پلیس‌راه باید بیای! 🚔" to JoyYellow,
            "گاوِ فینال جوکر 🐮" to JoyGreen,
            "زلزله‌ی نقطه‌چین 📳" to JoyOrange,
            "کل‌کل با ایرج طهماسب 🎤" to JoyPink
        )
        "طنز اینترنتی و وایرال 🚀" -> listOf(
            "دوربین مخفی‌های خلاقانه و سالم 📷" to JoyYellow,
            "محمد بوقی و بوق‌های خیابانی 📯" to JoyOrange,
            "پرَنک ربات ترسناک در خیابان 🤖" to JoyPink,
            "صدای تصادف، کرش و جیغِ ناگهانی 💥" to JoyPurple,
            "شوک‌های ناگهانی و بامزه (خانوادگی) 🤪" to JoyGreen,
            "ری‌اکشن تیک‌تاکی معروف 🔥" to JoyYellow
        )
        else -> listOf(
            "توشیرو میفونه و هاراکیری (آفتاب سرخ) ⚔️" to JoyYellow,
            "کل‌کل میفونه و چارلز برانسون 🤠" to JoyOrange,
            "کلینت ایستوود (نگاه و سیگار وسترن) 🚬" to JoyPink,
            "موزیک سوت تعجب وسترن (خوب بد زشت) 🎶" to JoyPurple,
            "رقص عجیب تاکشو 🕺" to JoyGreen,
            "خنده‌های اسبی مجری 🐴" to JoyYellow
        )
    }

    val filteredItems = currentItems.filter { (title, _) ->
        searchQuery.isBlank() || title.contains(searchQuery)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Top Sub-Tabs Selector
        ScrollableTabRow(
            selectedTabIndex = subTabState,
            containerColor = CardDarkBg,
            contentColor = JoyYellow,
            edgePadding = 8.dp,
            divider = {}
        ) {
            Tab(
                selected = subTabState == 0,
                onClick = { subTabState = 0 },
                text = { Text("🎬 موزه ملی صداها و میم‌ها", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
            )
            Tab(
                selected = subTabState == 1,
                onClick = { subTabState = 1 },
                text = { Text("🎨 میم‌ساز (Meme Studio)", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
            )
            Tab(
                selected = subTabState == 2,
                onClick = { subTabState = 2 },
                text = { Text("🖼️ گالری میم‌ها (Gallery)", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
            )
        }

        when (subTabState) {
            0 -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp)
                ) {
                    Text(
                        text = "🎬 موزهی ملی صداها و میمهای طنز",
                        color = JoyPink,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Search Input
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("جستجوی صدای نوستالژی یا سینمایی...", fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = JoyYellow) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = JoyYellow,
                            unfocusedBorderColor = Color.Gray,
                            focusedContainerColor = CardDarkBg,
                            unfocusedContainerColor = CardDarkBg
                        )
                    )

                    // Categories Horizontal Scroll Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categories.forEach { category ->
                            FilterChip(
                                selected = selectedCategory == category,
                                onClick = { selectedCategory = category },
                                label = { Text(category, fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = JoyYellow,
                                    selectedLabelColor = Color.Black,
                                    containerColor = CardDarkBg,
                                    labelColor = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Sound Cards Grid
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredItems) { (title, color) ->
                            SoundCard(
                                title = title,
                                accentColor = color,
                                isPlaying = activePlayingTitle == title,
                                onClick = {
                                    activePlayingTitle = title
                                    soundEngine.playSoundEffect(mapTitleToEffectId(title))
                                }
                            )
                        }
                    }
                }
            }
            1 -> {
                MemeMakerView(
                    onMemeSaved = { msg ->
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    }
                )
            }
            2 -> {
                // Meme Gallery View
                LazyVerticalGrid(
                    columns = GridCells.Fixed(1),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = CardDarkBg),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Image(
                                    painter = painterResource(id = R.drawable.surprised_cat_meme_1793285772146),
                                    contentDescription = "Meme 1",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                        .clip(RoundedCornerShape(12.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("وقتی بعد ۳ ماه رفتی بانک و میگن کارتت منقضی شده! 🐱", color = JoyYellow, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("❤️ ۱,۴۲۰ لایک | 💬 ۳۴۰ نظر", color = Color.Gray, fontSize = 11.sp)
                                    IconButton(onClick = { Toast.makeText(context, "اشتراک‌گذاری میم 🚀", Toast.LENGTH_SHORT).show() }) {
                                        Icon(Icons.Default.Share, contentDescription = null, tint = JoyPink)
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = CardDarkBg),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Image(
                                    painter = painterResource(id = R.drawable.persian_tea_meme_1793285783738),
                                    contentDescription = "Meme 2",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                        .clip(RoundedCornerShape(12.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("وقتی یکی میگه من چای نمی‌خورم! ☕", color = JoyPink, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("❤️ ۲,۸۹۰ لایک | 💬 ۶۵۰ نظر", color = Color.Gray, fontSize = 11.sp)
                                    IconButton(onClick = { Toast.makeText(context, "اشتراک‌گذاری میم 🚀", Toast.LENGTH_SHORT).show() }) {
                                        Icon(Icons.Default.Share, contentDescription = null, tint = JoyPink)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SoundCard(
    title: String,
    accentColor: Color,
    isPlaying: Boolean = false,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(86.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isPlaying) accentColor.copy(alpha = 0.3f) else CardDarkBg
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isPlaying) 2.dp else 1.dp,
            color = if (isPlaying) accentColor else Color.Gray.copy(alpha = 0.25f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    color = if (isPlaying) accentColor else Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 2
                )
                if (isPlaying) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Icon(
                        Icons.Default.GraphicEq,
                        contentDescription = "در حال پخش",
                        tint = accentColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

private fun mapTitleToEffectId(title: String): String {
    return when {
        title.contains("قیصر") || title.contains("بهمن مفید") -> "dramatic_horn"
        title.contains("دشواری") || title.contains("سوسیس") || title.contains("مربا") -> "laugh_track"
        title.contains("پت و مت") -> "boing"
        title.contains("جوکر") || title.contains("پلیس‌راه") -> "cheetori"
        title.contains("بوق") || title.contains("جیغ") -> "fart"
        title.contains("میفونه") || title.contains("وسترن") -> "sad_violin"
        else -> "win_chime"
    }
}
