package com.example.ui

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
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
import com.example.model.VoiceEffectItem
import com.example.ui.theme.*

@Composable
fun VoiceChangerTab(
    soundEngine: SoundEffectsEngine,
    onSaveCustomSound: (String) -> Unit
) {
    val context = LocalContext.current
    val isRecording by soundEngine.isRecording.collectAsState()
    val recordedAudioBuffer by soundEngine.recordedAudioBuffer.collectAsState()
    val waveformLevels by soundEngine.audioWaveformLevels.collectAsState()

    val effects = listOf(
        VoiceEffectItem("chipmunk", "سنجاب خنده‌دار", "Chipmunk", "🐿️", "صدای زیر و نازک و تند", 1.8f, 1.3f, accentColor = JoyYellow),
        VoiceEffectItem("monster", "غول و هیولا", "Monster", "👹", "صدای بم و ترسناک و سنگین", 0.6f, 0.8f, accentColor = JoyOrange),
        VoiceEffectItem("robot", "ربات فضایی", "Robot", "🤖", "افکت رباتیک فلزی", 1.0f, 1.0f, isRobotEffect = true, accentColor = JoyGreen),
        VoiceEffectItem("helium", "بادکنک هلیوم", "Helium", "🎈", "جیغ‌مانند و بادکنکی", 2.2f, 1.1f, accentColor = JoyPink),
        VoiceEffectItem("megaphone", "مگافون / بلندگو", "Megaphone", "📢", "صدای خیابانی و اکو", 1.2f, 1.0f, accentColor = JoyCyan),
        VoiceEffectItem("echo", "انعکاس غار", "Echo", "🌊", "افکت پژواک و اکو", 1.0f, 0.9f, accentColor = JoyPurple),
        VoiceEffectItem("slow", "اسلو موشن", "Slow Mo", "🐢", "کُند و آهسته خنده‌دار", 0.7f, 0.6f, accentColor = JoyYellow),
        VoiceEffectItem("fast", "دور تند", "Fast", "🚀", "شتاب‌زده و عجولانه", 1.5f, 1.6f, accentColor = JoyPink)
    )

    var selectedEffect by remember { mutableStateOf(effects[0]) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        Text(
            text = "🎙️ ماژول تغییر صدای پیشرفته",
            color = JoyGreen,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start
        )
        Text(
            text = "صدای خود را ضبط کنید و افکت‌های خنده‌دار سنجاب، غول، ربات و هلیوم رویش اعمال کنید!",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 12.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            textAlign = TextAlign.Start
        )

        // Recording Visualizer Panel
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp),
            colors = CardDefaults.cardColors(containerColor = CardDarkBg),
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(
                width = if (isRecording) 2.dp else 1.dp,
                color = if (isRecording) JoyPink else JoyGreen.copy(alpha = 0.5f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (isRecording) "🔴 در حال ضبط صدا... بگو هلو چطوری!" else if (recordedAudioBuffer != null) "✅ صدای شما آماده‌ی تغییر افکت است" else "برای شروع ضبط، روی میکروفون کلیک کنید",
                    color = if (isRecording) JoyPink else Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                // Animated Waveform Bar Visualizer
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val displayLevels = if (waveformLevels.isNotEmpty()) waveformLevels else listOf(0.2f, 0.4f, 0.8f, 0.3f, 0.6f, 0.9f, 0.5f, 0.2f, 0.7f, 0.3f)
                    displayLevels.forEach { lvl ->
                        Box(
                            modifier = Modifier
                                .width(6.dp)
                                .fillMaxHeight(fraction = if (isRecording) lvl else 0.3f)
                                .clip(CircleShape)
                                .background(if (isRecording) JoyPink else JoyGreen)
                        )
                    }
                }

                // Record Mic Button
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            if (isRecording) {
                                soundEngine.stopRecordingMicrophone()
                            } else {
                                soundEngine.startRecordingMicrophone()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isRecording) JoyPink else JoyGreen,
                            contentColor = Color.Black
                        ),
                        shape = CircleShape,
                        modifier = Modifier.size(54.dp)
                    ) {
                        Icon(
                            if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                            contentDescription = "ضبط صدا",
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    // Play converted audio button
                    if (recordedAudioBuffer != null && !isRecording) {
                        Button(
                            onClick = {
                                soundEngine.playRecordedAudioWithEffect(
                                    selectedEffect.pitchMultiplier,
                                    selectedEffect.speedMultiplier,
                                    selectedEffect.isRobotEffect
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = JoyYellow, contentColor = Color.Black),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.VolumeUp, contentDescription = "پخش")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("پخش افکت ${selectedEffect.nameFa}", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Effects Grid Selector
        Text(
            text = "انتخاب افکت صوتی خنده‌دار:",
            color = JoyYellow,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .weight(1f)
                .padding(top = 8.dp)
        ) {
            items(effects) { fx ->
                val isSel = fx.id == selectedEffect.id
                Card(
                    onClick = {
                        selectedEffect = fx
                        if (recordedAudioBuffer != null) {
                            soundEngine.playRecordedAudioWithEffect(
                                fx.pitchMultiplier,
                                fx.speedMultiplier,
                                fx.isRobotEffect
                            )
                        } else {
                            soundEngine.playSoundEffect("win_chime")
                        }
                    },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSel) fx.accentColor.copy(alpha = 0.25f) else CardDarkBg
                    ),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        width = if (isSel) 2.dp else 1.dp,
                        color = if (isSel) fx.accentColor else Color.Gray.copy(alpha = 0.3f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(fx.emoji, fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = fx.nameFa,
                                color = if (isSel) fx.accentColor else Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = fx.descriptionFa,
                                color = Color.Gray,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }

        // Save to My Soundboard Button
        if (recordedAudioBuffer != null) {
            Button(
                onClick = {
                    onSaveCustomSound("صدای ${selectedEffect.nameFa}")
                    Toast.makeText(context, "✅ صدای تغییر یافته به تخته صدای شما اضافه شد!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = JoyPink, contentColor = Color.White),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.Bookmark, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("ذخیره در موزه صدا / دابسمش", fontWeight = FontWeight.Bold)
            }
        }
    }
}
