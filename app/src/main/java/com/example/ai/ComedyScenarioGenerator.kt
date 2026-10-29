package com.example.ai

import android.content.Context
import android.util.Log
import com.example.model.ComedySkit
import com.example.model.SkitDialogueLine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.util.UUID

class ComedyScenarioGenerator(private val context: Context) {

    suspend fun generateComedySkit(
        format: String,
        characterPreset: String,
        situationTopic: String,
        tone: String
    ): ComedySkit = withContext(Dispatchers.IO) {
        // Simulate thoughtful AI creation delay
        delay(800)
        generateBuiltInPersianSkit(format, characterPreset, situationTopic, tone)
    }

    private fun generateBuiltInPersianSkit(
        format: String,
        characterPreset: String,
        situationTopic: String,
        tone: String
    ): ComedySkit {
        val topicLower = situationTopic.lowercase()

        val dialogue = when {
            topicLower.contains("عروسی") || topicLower.contains("میهمانی") -> listOf(
                SkitDialogueLine(characterPreset, "چرا کت و شلوار داداشتو پوشیدی؟! استیناش ۵ متر بلندتره!"),
                SkitDialogueLine("مادر شاکی 👵", "ساکت شو! مال مد جدیده‌! بعدشم این کت شانس میاره، بابا هم توش خواستگاری اومد!"),
                SkitDialogueLine(characterPreset, "بابا که تو اون عروسی دستگیر شد به خاطر کادوی فیک!"),
                SkitDialogueLine("پدر خسته 👨‍🦳", "باز اسم اون سالن کذایی رو آوردی؟! ۵۰ هزار تومان شاباش دادم هنوز دارم قسطشو میدم!")
            )
            topicLower.contains("تاکسی") || topicLower.contains("سفر") -> listOf(
                SkitDialogueLine("راننده‌تاکسی کارشناس 🚕", "ببین جوون! مشکل از تورم نیست، مشکل از فرکانس کره ماهه!"),
                SkitDialogueLine(characterPreset, "استاد ولی من فقط گفتم کولر رو بزنید بوی بنزین میاد..."),
                SkitDialogueLine("راننده‌تاکسی کارشناس 🚕", "کولر؟! تو این هوا کولر بزنم واشر سرسیلندر میسوزه، ۵ میلیون افت قیمت پیدا می‌کنه!"),
                SkitDialogueLine(characterPreset, "پس شیشه رو بکشید پایین..."),
                SkitDialogueLine("راننده‌تاکسی کارشناس 🚕", "دستگیره شیشه شکسته، با انبردست میچرخه! بکش تا نشکسته!")
            )
            topicLower.contains("کنکور") || topicLower.contains("دانشگاه") || topicLower.contains("قبولی") -> listOf(
                SkitDialogueLine("دانشجوی خسته 📱", "استاد باور کنید سگ دفترچه سوالم رو خورد!"),
                SkitDialogueLine("استاد سخت‌گیر 👨‍🏫", "سگتون تست‌های دیفرانسیل رو هم پاسخبرگ زد یا فقط تشریحی‌ها رو جوید؟"),
                SkitDialogueLine(characterPreset, "والا استاد تست‌ها رو با روش حذف گزینه درست زده بود!"),
                SkitDialogueLine("استاد سخت‌گیر 👨‍🏫", "پس نمره سگتو ۱۰ میدم، خودت می‌افتی!")
            )
            topicLower.contains("نان") || topicLower.contains("صف") || topicLower.contains("خرید") -> listOf(
                SkitDialogueLine(characterPreset, "رفت یکی نون سنگک بگیره، دید ۵۰ نفر تو صفن همه‌شون دارن با گوشی معامله ارزی می‌کنن!"),
                SkitDialogueLine("رفیق جوگیر 🤪", "داداش کارت بکش سریع، من با هوش مصنوعی قیمت ۳ ثانیه بعد نون رو پیش‌بینی کردم!"),
                SkitDialogueLine(characterPreset, "چی شد؟!"),
                SkitDialogueLine("رفیق جوگیر 🤪", "هیچی، هوش مصنوعی هم هنگ کرد گفت برو نون لواش بخر بخور!")
            )
            else -> listOf(
                SkitDialogueLine(characterPreset, "امروز رفتم جنس بخرم، فروشنده گفت قیمت الان یا ۵ ثانیه پیش؟!"),
                SkitDialogueLine("رفیق جوگیر 🤪", "داداش کارت بکش سریع، من با هوش مصنوعی قیمت ۳ ثانیه بعد رو پیش‌بینی کردم!"),
                SkitDialogueLine(characterPreset, "چی شد؟!"),
                SkitDialogueLine("رفیق جوگیر 🤪", "هیچی، هوش مصنوعی هم هنگ کرد گفت برو نون لواش بخر بخور!")
            )
        }

        val punchline = when (tone) {
            "فوق‌العاده خنده‌دار 🤣" -> "💥 نتیجه: هر وقت فکر کردی اوضاع بدتر نمیشه، کارتخوان مغازه قطعی میزنه!"
            "کنایه‌آمیز 😏" -> "😏 نتیجه: طوری زنده ماندیم که خودمون هم تعجب کردیم!"
            else -> "🤣 و این‌گونه بود که سوژه وایرال بعدی اینستاگرام متولد شد!"
        }

        return ComedySkit(
            id = UUID.randomUUID().toString(),
            title = "سناریو: $situationTopic - $characterPreset",
            format = format,
            characterPreset = characterPreset,
            situationTopic = situationTopic,
            dialogueLines = dialogue,
            punchline = punchline,
            tone = tone
        )
    }
}
