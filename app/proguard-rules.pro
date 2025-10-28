# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep DJI SDK classes
-keep class dji.** { *; }
-keep interface dji.** { *; }
-keep enum dji.** { *; }

# Keep ML Kit classes
-keep class com.google.mlkit.** { *; }
-keep interface com.google.mlkit.** { *; }

# Keep ZXing classes
-keep class com.google.zxing.** { *; }

# Keep data models
-keep class com.dronescan.app.data.** { *; }

# Gson
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }

# RxJava
-dontwarn java.util.concurrent.Flow*
