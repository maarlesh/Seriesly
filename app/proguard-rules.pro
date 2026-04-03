# Moshi
-keepclassmembers class * {
    @com.squareup.moshi.Json *;
}
-keep @com.squareup.moshi.JsonClass class * { *; }

# Room
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Retrofit
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn javax.annotation.**
-dontwarn kotlin.Unit
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**

# Seriesly domain — never obfuscate
-keep class com.seriesly.core.domain.model.** { *; }
-keep class com.seriesly.core.database.entity.** { *; }

# Google Tink (errorprone annotations are compile-time only, not present at runtime)
-dontwarn com.google.errorprone.annotations.CanIgnoreReturnValue
-dontwarn com.google.errorprone.annotations.CheckReturnValue
-dontwarn com.google.errorprone.annotations.Immutable
-dontwarn com.google.errorprone.annotations.RestrictedApi

# Kotlin
-keepattributes *Annotation*
-keepclassmembers class kotlin.Metadata { *; }
