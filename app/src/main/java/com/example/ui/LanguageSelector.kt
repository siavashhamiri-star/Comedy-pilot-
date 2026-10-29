package com.example.ui

import androidx.compose.foundation.layout.*
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
// 🌍 ماژول انتخاب زبان (فارسی و انگلیسی)
// ==========================================
@Composable
fun LanguageSelector(currentLang: String, onLangChanged: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("زبان / Language: ", color = Color.Gray, fontSize = 12.sp)
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // دکمه تغییر زبان
        Button(
            onClick = { 
                val newLang = if (currentLang == "FA") "EN" else "FA"
                onLangChanged(newLang)
            },
            colors = ButtonDefaults.buttonColors(containerColor = CardDarkBg),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Text(
                text = if (currentLang == "FA") "🇮🇷 فارسی" else "🇬🇧 English",
                color = JoyYellow,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
