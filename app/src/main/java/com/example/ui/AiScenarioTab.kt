package com.example.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.ComedyScenarioGenerator
import com.example.audio.TextToSpeechEngine
import com.example.model.ComedySkit
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiScenarioTab(
    scenarioGenerator: ComedyScenarioGenerator,
    ttsEngine: TextToSpeechEngine
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val formats = listOf("استندآپ کمدی 🎤", "کلیپ کوتاه / ریلز 🎬", "دوربین مخفی 🎥", "دیالوگ طنز ۲ نفره 🎭", "طنز اجتماعی 🗞️")
    val characters = listOf("مادر شاکی 👵", "پدر خسته 👨‍🦳", "راننده تاکسی 🚕", "دهه‌هشتادی 📱", "رفیق جوگیر 🤪", "استاد سخت‌گیر 👨‍🏫")
    val predefinedTopics = listOf("عروسی لاکچری 💍", "سفر شمال 🌊", "خرید ماشین 🚗", "صف نانوایی 🍞", "قبولی کنکور 🎓", "قبض برق ⚡")
    val tones = listOf("فوق‌العاده خنده‌دار 🤣", "کنایه‌آمیز 😏", "خانوادگی 👨‍👩‍👧‍👦", "سورئال 🤯")

    var selectedFormat by remember { mutableStateOf(formats[0]) }
    var selectedCharacter by remember { mutableStateOf(characters[0]) }
    var topicInput by remember { mutableStateOf("عروسی لاکچری") }
    var selectedTone by remember { mutableStateOf(tones[0]) }

    var isLoading by remember { mutableStateOf(false) }
    var currentSkit by remember { mutableStateOf<ComedySkit?>(null) }
    val isSpeaking by ttsEngine.isSpeaking.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardDarkBg),
                shape = RoundedCornerShape(20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(JoyYellow.copy(alpha = 0.2f), JoyPink.copy(alpha = 0.2f))
                            )
                        )
                        .padding(16.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "🎬 استودیوی سناریوساز هوش مصنوعی",
                                color = JoyYellow,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(
                                Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = JoyPink
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "تولید ایده، متن‌های وایرال و دیالوگ‌های طنز بومی با هوش مصنوعی برای تولیدکنندگان محتوا",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Format Selection
        item {
            Column {
                Text("قالب محتوا:", color = JoyYellow, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 6.dp)
                ) {
                    items(formats) { fmt ->
                        FilterChip(
                            selected = selectedFormat == fmt,
                            onClick = { selectedFormat = fmt },
                            label = { Text(fmt, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = JoyYellow,
                                selectedLabelColor = Color.Black,
                                containerColor = CardSurfaceVariant,
                                labelColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        // Character Selection
        item {
            Column {
                Text("شخصیت اصلی:", color = JoyPink, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 6.dp)
                ) {
                    items(characters) { chr ->
                        FilterChip(
                            selected = selectedCharacter == chr,
                            onClick = { selectedCharacter = chr },
                            label = { Text(chr, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = JoyPink,
                                selectedLabelColor = Color.White,
                                containerColor = CardSurfaceVariant,
                                labelColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        // Topic Input & Predefined Chips
        item {
            Column {
                Text("موضوع یا سوژه طنز:", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = topicInput,
                    onValueChange = { topicInput = it },
                    placeholder = { Text("مثلاً: گران شدن چیپس و پفک...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = JoyYellow,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = JoyYellow
                    ),
                    singleLine = true
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    items(predefinedTopics) { top ->
                        SuggestionChip(
                            onClick = { topicInput = top.replace(Regex("[^a-zA-Z0-9آ-ی‌ ]"), "").trim() },
                            label = { Text(top, fontSize = 11.sp) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = CardSurfaceVariant,
                                labelColor = JoyOrange
                            )
                        )
                    }
                }
            }
        }

        // Generate Button
        item {
            Button(
                onClick = {
                    isLoading = true
                    coroutineScope.launch {
                        currentSkit = scenarioGenerator.generateComedySkit(
                            format = selectedFormat,
                            characterPreset = selectedCharacter,
                            situationTopic = topicInput,
                            tone = selectedTone
                        )
                        isLoading = false
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = JoyYellow,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.Black,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("در حال نگارش سناریوی خنده‌دار...", fontWeight = FontWeight.Bold)
                } else {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("تولید سناریوی هوش مصنوعی 🚀", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
        }

        // Result Skit Display Card
        item {
            AnimatedVisibility(
                visible = currentSkit != null,
                enter = fadeIn() + expandVertically()
            ) {
                currentSkit?.let { skit ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = CardDarkBg),
                        shape = RoundedCornerShape(18.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, JoyYellow.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = skit.title,
                                    color = JoyYellow,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f)
                                )
                                Surface(
                                    color = JoyPink.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = skit.tone,
                                        color = JoyPink,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Divider(
                                color = Color.Gray.copy(alpha = 0.3f),
                                modifier = Modifier.padding(vertical = 12.dp)
                            )

                            // Dialogue lines
                            skit.dialogueLines.forEach { line ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text(line.emotionEmoji, fontSize = 20.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = line.speaker,
                                            color = JoyOrange,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = line.text,
                                            color = Color.White,
                                            fontSize = 14.sp,
                                            lineHeight = 20.sp
                                        )
                                    }
                                }
                            }

                            // Punchline Badge
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp),
                                color = JoyPurple.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, JoyPurple)
                            ) {
                                Text(
                                    text = skit.punchline,
                                    color = JoyPurple,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(12.dp),
                                    textAlign = TextAlign.Center
                                )
                            }

                            // Action Bar for Result
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // TTS Speak button
                                IconButton(
                                    onClick = {
                                        if (isSpeaking) {
                                            ttsEngine.stop()
                                        } else {
                                            val fullTextToSpeak = skit.dialogueLines.joinToString(". ") { "${it.speaker}: ${it.text}" } + ". " + skit.punchline
                                            ttsEngine.speak(fullTextToSpeak)
                                        }
                                    },
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(if (isSpeaking) JoyPink else JoyYellow)
                                ) {
                                    Icon(
                                        if (isSpeaking) Icons.Default.Stop else Icons.Default.VolumeUp,
                                        contentDescription = "خوانش با صدا",
                                        tint = Color.Black
                                    )
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    // Copy script button
                                    OutlinedButton(
                                        onClick = {
                                            val fullText = skit.dialogueLines.joinToString("\n") { "${it.speaker}: ${it.text}" } + "\n" + skit.punchline
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            clipboard.setPrimaryClip(ClipData.newPlainText("Comedy Skit", fullText))
                                            Toast.makeText(context, "متن سناریو کپی شد! 📋", Toast.LENGTH_SHORT).show()
                                        },
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("کپی", fontSize = 12.sp)
                                    }

                                    // Share script
                                    Button(
                                        onClick = {
                                            Toast.makeText(context, "آماده ارسال به اینستاگرام و تلگرام 🚀", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = JoyPink),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("اشتراک", fontSize = 12.sp)
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
