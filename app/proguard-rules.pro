# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# ============================================
# DJI Mobile SDK V5 - CRITICAL RULES
# ============================================
# Keep all DJI SDK classes - NoClassDefFoundError fix
-keep class dji.** { *; }
-keep interface dji.** { *; }
-keep enum dji.** { *; }
-keepclassmembers class * implements dji.** { *; }

# SDK Manager - Critical for initialization
-keep class dji.v5.manager.** { *; }
-keep class dji.v5.manager.SDKManager { *; }
-keep class dji.v5.manager.interfaces.** { *; }

# Common classes
-keep class dji.v5.common.** { *; }
-keep class dji.v5.common.error.** { *; }
-keep class dji.v5.common.register.** { *; }

# Network implementation
-keep class dji.v5.network.** { *; }

# Keep all public methods and fields
-keepclassmembers class dji.** {
    public *;
    protected *;
}

# Keep reflection capabilities
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keepattributes InnerClasses

# ============================================
# RxJava (used by DJI SDK)
# ============================================
-dontwarn io.reactivex.**
-keep class io.reactivex.** { *; }
-keep interface io.reactivex.** { *; }
-dontwarn java.util.concurrent.Flow*

# RxJava 3
-keep class io.reactivex.rxjava3.** { *; }
-dontwarn io.reactivex.rxjava3.**

# ============================================
# ML Kit - Barcode Scanning
# ============================================
-keep class com.google.mlkit.** { *; }
-keep interface com.google.mlkit.** { *; }
-dontwarn com.google.mlkit.**

# ============================================
# ZXing - Barcode Library
# ============================================
-keep class com.google.zxing.** { *; }
-dontwarn com.google.zxing.**

# ============================================
# App Data Models
# ============================================
-keep class com.dronescan.msdksample.data.** { *; }
-keep class com.dronescan.msdksample.**.data.** { *; }

# ============================================
# Gson - JSON Serialization
# ============================================
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Keep data classes for Gson
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

# ============================================
# Kotlin
# ============================================
-keep class kotlin.** { *; }
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**
-keepclassmembers class **$WhenMappings {
    <fields>;
}
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}

# ============================================
# Coroutines
# ============================================
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# ============================================
# AndroidX
# ============================================
-keep class androidx.** { *; }
-keep interface androidx.** { *; }
-dontwarn androidx.**

# ============================================
# General Android
# ============================================
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

# ============================================
# Warnings to suppress
# ============================================
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**
