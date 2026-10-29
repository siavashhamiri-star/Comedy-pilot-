package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

// ==========================================
// 🎮 ماژول پاداش‌ها، ماموریت‌های XP و شبکه‌های اجتماعی (رایگان و انگیزشی)
// ==========================================
@Composable
fun RewardsAndXpTab() {
    var subTabState by remember { mutableStateOf(0) } // 0: XP & Missions, 1: Referral & Legends

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkCharcoalBg)
    ) {
        // Sub-tabs for Rewards & Legends
        TabRow(
            selectedTabIndex = subTabState,
            containerColor = CardDarkBg,
            contentColor = JoyYellow,
            divider = {}
        ) {
            Tab(
                selected = subTabState == 0,
                onClick = { subTabState = 0 },
                text = { Text("⚡ ماموریت‌ها و XP", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
            )
            Tab(
                selected = subTabState == 1,
                onClick = { subTabState = 1 },
                text = { Text("🏆 لیگ معرفان و اسطوره‌ها", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
            )
        }

        if (subTabState == 0) {
            XpMissionsView()
        } else {
            ReferralAndLegendsTab()
        }
    }
}

@Composable
fun XpMissionsView() {
    var userXp by remember { mutableStateOf(350) } // امتیاز فعلی کاربر
    var instagramFollowed by remember { mutableStateOf(false) }
    var telegramJoined by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkCharcoalBg)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("⚡ مرکز امتیازات و ماموریتهای رایگان (XP)", color = JoyYellow, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text("با انجام ماموریتها و دعوت از دوستان، XP رایگان بگیر و هوش مصنوعی رو شارژ کن!", color = Color.Gray, fontSize = 12.sp)

        Spacer(modifier = Modifier.height(20.dp))

        // کارت نمایش XP کاربر
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardDarkBg)
        ) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("موجودی امتیاز شما", color = Color.White, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("💎 $userXp XP", color = JoyGreen, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("🎯 ماموریتهای دریافت XP رایگان", color = JoyPink, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(10.dp))

        // ماموریت فالو اینستاگرام
        MissionCard(
            title = "فالو کردن اینستاگرام کمدیپایلوت 📸",
            reward = "+150 XP",
            isCompleted = instagramFollowed,
            onActionClick = { 
                if (!instagramFollowed) {
                    instagramFollowed = true
                    userXp += 150
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // ماموریت جوین تلگرام
        MissionCard(
            title = "عضویت در کانال تلگرام ✈️",
            reward = "+150 XP",
            isCompleted = telegramJoined,
            onActionClick = { 
                if (!telegramJoined) {
                    telegramJoined = true
                    userXp += 150
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // ماموریت معرفی به دوستان (لیگ معرفان)
        MissionCard(
            title = "دعوت از دوستان (لینک اختصاصی) 🤝",
            reward = "+300 XP به ازای هر نفر",
            isCompleted = false,
            onActionClick = {
                userXp += 300 // به عنوان تست یا اشتراکگذاری لینک
            }
        )
    }
}

@Composable
fun MissionCard(title: String, reward: String, isCompleted: Boolean, onActionClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardDarkBg)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = reward, color = JoyYellow, fontSize = 11.sp)
            }
            Button(
                onClick = onActionClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isCompleted) Color.DarkGray else JoyPink
                )
            ) {
                Text(if (isCompleted) "انجام شد ✅" else "انجام ماموریت", fontSize = 11.sp, color = Color.White)
            }
        }
    }
}

// ==========================================
// 🏆 ماژول لیگ معرفان و اسطورههای وفاداری (Referral & Loyalty Legends)
// ==========================================
@Composable
fun ReferralAndLegendsTab() {
    var referralCount by remember { mutableStateOf(12) } // تعداد افرادی که معرفی کرده
    val targetGoal = 15 // هدف برای ورود به لیگ اسطورهها

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkCharcoalBg)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("🏆 لیگ معرفان و اسطورههای وفاداری", color = JoyYellow, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text("دوستانت رو دعوت کن، به لیگ اسطورههای کمدی راه پیدا کن!", color = Color.Gray, fontSize = 13.sp)

        Spacer(modifier = Modifier.height(24.dp))

        // کارت وضعیت دعوتها
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardDarkBg),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("📊 وضعیت دعوتهای شما", color = JoyPink, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                
                Text("تعداد معرفیشدهها: $referralCount / $targetGoal نفر", color = Color.White, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(8.dp))
                
                // خط پیشرفت (Progress Bar)
                LinearProgressIndicator(
                    progress = { referralCount.toFloat() / targetGoal.toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = JoyYellow,
                    trackColor = Color.DarkGray,
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                if (referralCount >= targetGoal) {
                    Text("🌟 تبریک! شما به مقامِ «اسطورهی وفاداری» رسیدید!", color = JoyGreen, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                } else {
                    Text("فقط ${targetGoal - referralCount} نفر دیگه تا ورود به لیگ اسطورهها مونده!", color = JoyOrange, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // جدول ردهبندی اسطورهها
        Text("🔥 برترین اسطورههای کمدیهاب", color = JoyYellow, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        val legendsList = listOf(
            "۱. سیاوش (پادشاهِ کمدی) - ۴۵ معرفی 👑",
            "۲. آرش (استادِ پرَنک) - ۳۲ معرفی 🥈",
            "۳. سارا (ملکهی دابسمش) - ۲۸ معرفی 🥉",
            "۴. رضا (موجِ خنده) - ۱۹ معرفی ⭐"
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(legendsList) { legend ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CardDarkBg)
                ) {
                    Box(modifier = Modifier.padding(16.dp)) {
                        Text(text = legend, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
