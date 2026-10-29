package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.*

data class SkitDialogueLine(
    val speaker: String,
    val text: String,
    val emotionEmoji: String = "😃"
)

data class ComedySkit(
    val id: String,
    val title: String,
    val format: String,
    val characterPreset: String,
    val situationTopic: String,
    val dialogueLines: List<SkitDialogueLine>,
    val punchline: String,
    val tone: String,
    val isFavorite: Boolean = false
)

data class SoundClip(
    val id: String,
    val title: String,
    val category: String, // e.g. "وایرال", "دیالوگ", "افکت", "ری‌اکشن"
    val emoji: String,
    val description: String,
    val durationMs: Int = 2000,
    val accentColor: Color = JoyYellow,
    val effectId: String = "rimshot",
    val soundType: SoundType = SoundType.SYNTHETIC_SFX,
    val isFavorite: Boolean = false
)

enum class SoundType {
    SYNTHETIC_SFX,
    VOICE_LAUGH,
    RECORDED_USER
}

data class VoiceEffectItem(
    val id: String,
    val nameFa: String,
    val nameEn: String,
    val emoji: String,
    val descriptionFa: String,
    val pitchMultiplier: Float,
    val speedMultiplier: Float,
    val isRobotEffect: Boolean = false,
    val accentColor: Color = JoyPink
)

data class MemeTemplateItem(
    val id: String,
    val titleFa: String,
    val imageDrawableRes: Int?,
    val defaultTopText: String,
    val defaultBottomText: String
)

data class DubsmashDraft(
    val id: String,
    val title: String,
    val audioTitle: String,
    val timestampMs: Long,
    val durationSec: Int,
    val speedRate: Float
)
