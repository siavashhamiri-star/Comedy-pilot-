package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Museum
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.*

@Composable
fun OnboardingDialog(
    onDismiss: () -> Unit
) {
    var step by remember { mutableStateOf(0) }

    val pages = listOf(
        OnboardingPage(
            title = "🏛️ موزه صدا و میم‌های نوستالژی",
            description = "پدها را لمس کنید تا آنی و بدون اینترنت، صداهای قیصر، برره، پت و مت و میم‌های وایرال پخش شوند!",
            icon = Icons.Default.Museum,
            color = JoyYellow
        ),
        OnboardingPage(
            title = "🎙️ تغییر صدای حرفه‌ای",
            description = "صدایتان را ضبط کنید و با یک کلیک به صدای سنجاب، غول، ربات یا هلیوم تبدیل کنید و در موزه بگذارید!",
            icon = Icons.Default.Mic,
            color = JoyPink
        ),
        OnboardingPage(
            title = "🤖 سناریوساز هوش مصنوعی",
            description = "موضوع طنز بدهید تا هوش مصنوعی برای استندآپ، ریلز و دوربین مخفی برای شما سناریوی وایرال بنویسد!",
            icon = Icons.Default.AutoAwesome,
            color = JoyGreen
        )
    )

    val currentPage = pages[step]

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardDarkBg),
            shape = RoundedCornerShape(24.dp),
            border = androidx.compose.foundation.BorderStroke(2.dp, currentPage.color)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Badge
                Surface(
                    color = currentPage.color.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "راهنمای ComedyPilot 🎭",
                        color = currentPage.color,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Page Icon
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(currentPage.color.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        currentPage.icon,
                        contentDescription = null,
                        tint = currentPage.color,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title & Description
                Text(
                    text = currentPage.title,
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = currentPage.description,
                    color = Color.LightGray,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Indicator Dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    pages.indices.forEach { index ->
                        Box(
                            modifier = Modifier
                                .size(if (index == step) 10.dp else 6.dp)
                                .clip(CircleShape)
                                .background(if (index == step) currentPage.color else Color.Gray)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Button
                Button(
                    onClick = {
                        if (step < pages.size - 1) {
                            step++
                        } else {
                            onDismiss()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = currentPage.color,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = if (step < pages.size - 1) "بعدی ➔" else "متوجه شدم، بریم برای خنده! 🚀",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

private data class OnboardingPage(
    val title: String,
    val description: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color
)
