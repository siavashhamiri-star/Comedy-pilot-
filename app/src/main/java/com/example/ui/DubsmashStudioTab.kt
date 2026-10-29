package com.example.ui

import android.content.Context
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
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
import androidx.compose.material.icons.filled.CameraFront
import androidx.compose.material.icons.filled.Filter
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.audio.SoundEffectsEngine
import com.example.model.DubsmashDraft
import com.example.model.SoundClip
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DubsmashStudioTab(
    soundEngine: SoundEffectsEngine
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()

    val topSoundClips = listOf(
        SoundClip("1", "چطوری ایرانی؟ 🇮🇷", "دیالوگ", "😎", "دیالوگ ماندگار وایرال", 2000, JoyYellow, effectId = "cheetori"),
        SoundClip("2", "ای جان! ❤️", "ری‌اکشن", "😍", "صداپیشگی خنده‌دار", 1500, JoyPink, effectId = "ey_jan"),
        SoundClip("3", "خیلی هم عالی! 👌", "دیالوگ", "🥳", "تایید هیجان‌انگیز", 1800, JoyGreen, effectId = "kheili_ali"),
        SoundClip("4", "خنده‌ی قهقه‌دار 🤣", "افکت", "🤣", "صدای خنده ریمیکس", 2500, JoyOrange, effectId = "laugh_track"),
        SoundClip("5", "دوربین مخفی! 🎥", "افکت", "😱", "افکت هیجانی", 1200, JoyPurple, effectId = "dramatic_horn")
    )

    var selectedClip by remember { mutableStateOf(topSoundClips[0]) }
    var selectedSpeed by remember { mutableStateOf(1.0f) }
    var isRecordingVideo by remember { mutableStateOf(false) }
    var countdownSec by remember { mutableStateOf(0) }
    var recordTimeSec by remember { mutableStateOf(0) }
    var isFrontCamera by remember { mutableStateOf(true) }
    var activeFilter by remember { mutableStateOf("بدون فیلتر") }

    val savedDrafts = remember {
        mutableStateListOf(
            DubsmashDraft("1", "دابسمش چطوری ایرانی", "چطوری ایرانی؟ 🇮🇷", System.currentTimeMillis() - 86400000, 3, 1.0f),
            DubsmashDraft("2", "کمدی پیرمرد خسته", "ای جان! ❤️", System.currentTimeMillis() - 172800000, 4, 1.5f)
        )
    }

    val filters = listOf("بدون فیلتر 🔮", "عینک آفتابی 😎", "قاب میم 🖼️", "نئون دهه هشتاد ⚡", "سیاه سفید 🎬")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Title
        item {
            Text(
                text = "🎥 استودیوی دابسمش و ترکیب صدا و تصویر",
                color = JoyPink,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "صدای میم‌های وایرال را روی ویدیو و حرکات صورت خود سوار کنید!",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp
            )
        }

        // Camera Preview Box
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Black),
                border = androidx.compose.foundation.BorderStroke(
                    width = if (isRecordingVideo) 3.dp else 1.dp,
                    color = if (isRecordingVideo) JoyPink else JoyYellow.copy(alpha = 0.5f)
                )
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // CameraX Preview View
                    AndroidView(
                        factory = { ctx ->
                            val previewView = PreviewView(ctx)
                            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                            cameraProviderFuture.addListener({
                                try {
                                    val cameraProvider = cameraProviderFuture.get()
                                    val preview = Preview.Builder().build().also {
                                        it.setSurfaceProvider(previewView.surfaceProvider)
                                    }
                                    val cameraSelector = if (isFrontCamera) {
                                        CameraSelector.DEFAULT_FRONT_CAMERA
                                    } else {
                                        CameraSelector.DEFAULT_BACK_CAMERA
                                    }
                                    cameraProvider.unbindAll()
                                    cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }, ContextCompat.getMainExecutor(ctx))
                            previewView
                        },
                        modifier = Modifier.fillMaxSize()
                    )

                    // Overlay Filter Graphic Badge
                    if (activeFilter != "بدون فیلتر") {
                        Surface(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(12.dp),
                            color = JoyPurple.copy(alpha = 0.8f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = "فیلتر: $activeFilter",
                                color = Color.White,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Countdown Overlay
                    if (countdownSec > 0) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.6f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$countdownSec",
                                color = JoyYellow,
                                fontSize = 72.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    // Selected Sound Overlay Bar
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp),
                        color = Color.Black.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.MusicNote, contentDescription = null, tint = JoyYellow, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = selectedClip.title,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Bottom Control Row on Camera
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.5f))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Switch Camera
                        IconButton(onClick = { isFrontCamera = !isFrontCamera }) {
                            Icon(Icons.Default.CameraFront, contentDescription = "تغییر دوربین", tint = Color.White)
                        }

                        // Main Record Button
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(if (isRecordingVideo) JoyPink else JoyYellow)
                                .clickable {
                                    if (isRecordingVideo) {
                                        isRecordingVideo = false
                                        val newDraft = DubsmashDraft(
                                            id = System.currentTimeMillis().toString(),
                                            title = "دابسمش ${selectedClip.title}",
                                            audioTitle = selectedClip.title,
                                            timestampMs = System.currentTimeMillis(),
                                            durationSec = recordTimeSec.coerceAtLeast(3),
                                            speedRate = selectedSpeed
                                        )
                                        savedDrafts.add(0, newDraft)
                                        Toast.makeText(context, "🎬 ویدیو دابسمش ذخیره شد!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        coroutineScope.launch {
                                            countdownSec = 3
                                            delay(1000)
                                            countdownSec = 2
                                            delay(1000)
                                            countdownSec = 1
                                            delay(1000)
                                            countdownSec = 0
                                            
                                            isRecordingVideo = true
                                            soundEngine.playSoundEffect(selectedClip.effectId)
                                            
                                            recordTimeSec = 0
                                            for (i in 1..5) {
                                                if (!isRecordingVideo) break
                                                delay(1000)
                                                recordTimeSec = i
                                            }
                                            isRecordingVideo = false
                                        }
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                if (isRecordingVideo) Icons.Default.Videocam else Icons.Default.PlayArrow,
                                contentDescription = "ضبط",
                                tint = Color.Black,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        // Test Play Selected Audio
                        IconButton(onClick = { soundEngine.playSoundEffect(selectedClip.effectId) }) {
                            Icon(Icons.Default.VolumeUp, contentDescription = "پخش صدا", tint = JoyYellow)
                        }
                    }
                }
            }
        }

        // Sound Selector Bar
        item {
            Column {
                Text("انتخاب صدای دابسمش:", color = JoyYellow, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 6.dp)
                ) {
                    items(topSoundClips) { clip ->
                        val isSel = clip.id == selectedClip.id
                        Card(
                            onClick = {
                                selectedClip = clip
                                soundEngine.playSoundEffect(clip.effectId)
                            },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSel) JoyYellow else CardSurfaceVariant
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(clip.emoji, fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = clip.title,
                                    color = if (isSel) Color.Black else Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Speed & Filters Options Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Speed Selector
                Column(modifier = Modifier.weight(1f)) {
                    Text("سرعت پخش:", color = Color.White, fontSize = 12.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf(0.5f, 1.0f, 1.5f, 2.0f).forEach { spd ->
                            FilterChip(
                                selected = selectedSpeed == spd,
                                onClick = { selectedSpeed = spd },
                                label = { Text("${spd}x", fontSize = 11.sp) },
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
        }

        // Filters Row
        item {
            Column {
                Text("افکت و فیلتر تصویری:", color = JoyPurple, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    items(filters) { flt ->
                        SuggestionChip(
                            onClick = { activeFilter = flt.replace(Regex("[^a-zA-Z0-9آ-ی‌ ]"), "").trim() },
                            label = { Text(flt, fontSize = 11.sp) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = CardSurfaceVariant,
                                labelColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        // Saved Dubsmash Recordings
        item {
            Text(
                text = "📂 دابسمش‌های ضبط شده شما",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        items(savedDrafts) { draft ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardDarkBg),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🎬", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(draft.title, color = JoyYellow, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("مدت: ${draft.durationSec} ثانیه | سرعت: ${draft.speedRate}x", color = Color.Gray, fontSize = 11.sp)
                    }
                    IconButton(onClick = {
                        Toast.makeText(context, "در حال پخش دابسمش ${draft.title} ▶️", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "پخش", tint = JoyGreen)
                    }
                }
            }
        }
    }
}

// Extension to map SoundClip effect ID
private val SoundClip.effectId: String
    get() = when (id) {
        "1" -> "cheetori"
        "2" -> "ey_jan"
        "3" -> "kheili_ali"
        "4" -> "laugh_track"
        "5" -> "dramatic_horn"
        else -> "win_chime"
    }
