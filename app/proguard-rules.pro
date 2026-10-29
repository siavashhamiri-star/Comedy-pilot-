# ProGuard rules for ComedyPilot release build on Cafe Bazaar

-keep class com.example.model.** { *; }
-keep class com.example.audio.** { *; }
-keep class com.example.ai.** { *; }

# Keep Retrofit & Moshi classes
-keep class retrofit2.** { *; }
-keep class com.squareup.moshi.** { *; }

# Keep Compose reflection
-keep class androidx.compose.** { *; }
