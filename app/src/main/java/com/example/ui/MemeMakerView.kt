package com.example.ui

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.MemeTemplateItem
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemeMakerView(
    onMemeSaved: (String) -> Unit
) {
    val templates = listOf(
        MemeTemplateItem("1", "گربه متعجب 🐱", R.drawable.surprised_cat_meme_1793285772146, "وقتی بعد ۳ ماه رفتی بانک...", "...و میگن کارتت منقضی شده!"),
        MemeTemplateItem("2", "چای ایرانی ☕", R.drawable.persian_tea_meme_1793285783738, "وقتی یکی میگه چای نمی‌خورم!", "مگه میشه چای نخوری برادر من؟!"),
        MemeTemplateItem("3", "کارمند خسته 💼", null, "وقتی شنبه ساعت ۸ صبحه...", "...و قرار جلسه مهم داری!"),
        MemeTemplateItem("4", "تاکسی کارشناس 🚕", null, "استاد راننده تاکسی:", "تحلیل کامل اقتصاد جهانی در ۱۰ دقیقه!")
    )

    var selectedTemplate by remember { mutableStateOf(templates[0]) }
    var topText by remember { mutableStateOf(selectedTemplate.defaultTopText) }
    var bottomText by remember { mutableStateOf(selectedTemplate.defaultBottomText) }
    var textColor by remember { mutableStateOf(Color.White) }
    var fontSizeSp by remember { mutableStateOf(24f) }
    var selectedSticker by remember { mutableStateOf("😂") }
    var showSavedSnackbar by remember { mutableStateOf(false) }

    val stickers = listOf("😂", "🤣", "😭", "🤯", "🗿", "🤡", "🔥", "⚡", "☕", "🚕")
    val colors = listOf(Color.White, JoyYellow, JoyPink, JoyGreen, JoyCyan, JoyOrange)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        Text(
            text = "🎨 ساخت میم خنده‌دار اختصاصی",
            color = JoyYellow,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Meme Canvas Preview Container
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp),
            colors = CardDefaults.cardColors(containerColor = CardSurfaceVariant),
            shape = RoundedCornerShape(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                // Background image or solid color canvas
                if (selectedTemplate.imageDrawableRes != null) {
                    Image(
                        painter = painterResource(id = selectedTemplate.imageDrawableRes!!),
                        contentDescription = "Meme Template",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(CardDarkBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🎭 ${selectedTemplate.titleFa}",
                            color = JoyPink,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Top Text Overlay
                if (topText.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.4f))
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = topText,
                            color = textColor,
                            fontSize = fontSizeSp.sp,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Sticker Overlay
                Text(
                    text = selectedSticker,
                    fontSize = 36.sp,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(12.dp)
                )

                // Bottom Text Overlay
                if (bottomText.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.4f))
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = bottomText,
                            color = textColor,
                            fontSize = fontSizeSp.sp,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Template Selector List
        Text("انتخاب قالب میم:", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(vertical = 6.dp)
        ) {
            items(templates) { template ->
                val isSel = template.id == selectedTemplate.id
                FilterChip(
                    selected = isSel,
                    onClick = {
                        selectedTemplate = template
                        topText = template.defaultTopText
                        bottomText = template.defaultBottomText
                    },
                    label = { Text(template.titleFa, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = JoyYellow,
                        selectedLabelColor = Color.Black,
                        containerColor = CardSurfaceVariant,
                        labelColor = Color.White
                    )
                )
            }
        }

        // Text Inputs
        OutlinedTextField(
            value = topText,
            onValueChange = { topText = it },
            label = { Text("متن بالای میم", fontSize = 12.sp) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = JoyYellow,
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = JoyYellow,
                unfocusedLabelColor = Color.Gray
            )
        )

        OutlinedTextField(
            value = bottomText,
            onValueChange = { bottomText = it },
            label = { Text("متن پایین میم", fontSize = 12.sp) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = JoyPink,
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = JoyPink,
                unfocusedLabelColor = Color.Gray
            )
        )

        // Color & Sticker Controls Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Color options
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                colors.forEach { col ->
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(col)
                            .border(
                                width = if (textColor == col) 2.dp else 0.dp,
                                color = if (textColor == col) Color.White else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable { textColor = col }
                    )
                }
            }

            // Sticker Selector Row
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(stickers) { stk ->
                    Text(
                        text = stk,
                        fontSize = 20.sp,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (selectedSticker == stk) CardSurfaceVariant else Color.Transparent)
                            .clickable { selectedSticker = stk }
                            .padding(4.dp)
                    )
                }
            }
        }

        // Action Buttons Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    showSavedSnackbar = true
                    onMemeSaved("میم '${selectedTemplate.titleFa}' با موفقیت ذخیره شد!")
                },
                colors = ButtonDefaults.buttonColors(containerColor = JoyYellow, contentColor = Color.Black),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Download, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("ذخیره میم", fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = {
                    onMemeSaved("آماده اشتراک‌گذاری در شبکه‌های اجتماعی!")
                },
                colors = ButtonDefaults.buttonColors(containerColor = JoyPink, contentColor = Color.White),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Share, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("اشتراک‌گذاری", fontWeight = FontWeight.Bold)
            }
        }

        if (showSavedSnackbar) {
            Snackbar(
                action = {
                    TextButton(onClick = { showSavedSnackbar = false }) {
                        Text("باشه", color = JoyYellow)
                    }
                },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("✅ میم اختصاصی شما آماده شد!")
            }
        }
    }
}
