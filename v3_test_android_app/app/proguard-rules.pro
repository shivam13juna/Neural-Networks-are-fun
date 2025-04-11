# Keep Room model classes
-keep class androidx.room.** { *; }

# If using Kotlin reflection or coroutines:
-keepclassmembers class kotlinx.coroutines.** { *; }

# Keep application classes with @Keep
-keep @interface androidx.annotation.Keep
