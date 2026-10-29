package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun AboutAndEcosystemView() {
    val appsList = listOf(
        "🛠️ Tavana Tool Box (جعبه ابزار پیشرفته توانا)",
        "🚀 Tavana Master Hub (مرکز فرماندهی هوشمند توانا)",
        "⚡ T-Deploy (سیستم استقرار و اتوماسیون سریع ابری)",
        "🗣️ لینگووی (برنامه آموزش زبان فارسی به انگلیسی‌زبانان و بالعکس)",
        "🛒 بازار فرش ایران و جهان",
        "🤖 دیجیتال مارکتینگ هوش مصنوعی احساس‌محور نکسوز",
        "⚡ خدمات هوش مصنوعی توانا",
        "💼 توان‌فورک (توانایی فریلنسری)",
        "🎓 توانا اجوکیشن (Tavana Education)",
        "📅 سالن‌یار و کلینیک‌یار",
        "🏗️ ساختمان‌یار و دکورمیت",
        "🛡️ Roseguard (روزگارد)",
        "🌟 و ده‌ها اپلیکیشن پیشرفته برای تسهیل کار کدنویسان و توسعه‌دهندگان"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkCharcoalBg)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Developer & Copyright Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardDarkBg),
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, JoyYellow)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🌟 خالق و توسعه‌دهنده", color = JoyYellow, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "کلیه حقوق مادی و معنوی این برنامه متعلق است به خالق توانا سیتی، آقای اکبر علی‌میری (توسعه‌دهنده).",
                        color = Color.White,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                }
            }
        }

        // Ecosystem Intro
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardSurfaceVariant),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🌐 اکوسیستم آفرینش و شهر نیومتاورسی جدید جهان", color = JoyPink, fontSize = 15.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "توآنا سیتی | Tavana City & FB New Meta",
                        color = JoyOrange,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "مجموعه‌ای بی‌نظیر از اپلیکیشن‌ها و خدمات هوش مصنوعی جهت توانمندسازی توسعه‌دهندگان و کاربران.",
                        color = Color.LightGray,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Ecosystem Apps List Header
        item {
            Text(
                text = "📱 سایر اپلیکیشن‌های این اکوسیستم:",
                color = JoyYellow,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )
        }

        items(appsList) { appName ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardDarkBg),
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = appName,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
