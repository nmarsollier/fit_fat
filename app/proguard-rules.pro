# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable

-keepnames class * { *; }

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Mantener las clases de Retrofit
-keep class retrofit2.** { *; }
-keep interface retrofit2.** { *; }
-keepattributes Signature
-keepattributes *Annotation*

# Mantener las clases de Room
-keep class androidx.room.** { *; }
-keep interface androidx.room.** { *; }
-keepattributes Signature
-keepattributes *Annotation*

# Mantener las clases de Firebase
-keep class com.google.firebase.** { *; }
-keep interface com.google.firebase.** { *; }
-keepattributes Signature
-keepattributes *Annotation*

# Mantener las clases de AndroidX
-keep class androidx.** { *; }
-keep interface androidx.** { *; }
-keepattributes Signature
-keepattributes *Annotation*

# Mantener las clases de ViewBinding y DataBinding
-keep class **.databinding.* { *; }
-keep class **.viewbinding.* { *; }
-keep class **.BR { *; }

# Mantener las clases de Dagger/Hilt
-keep class dagger.** { *; }
-keep interface dagger.** { *; }
-keep class hilt.** { *; }
-keep interface hilt.** { *; }
-keepattributes Signature
-keepattributes *Annotation*

# Mantener las clases de Koin
-keep class org.koin.** { *; }
-keep interface org.koin.** { *; }
-keepattributes Signature
-keepattributes *Annotation*

# Mantener las clases de Compose
-keep class androidx.compose.** { *; }
-keep interface androidx.compose.** { *; }
-keepattributes Signature
-keepattributes *Annotation*

# Mantener las clases de Gson
-keep class com.google.gson.** { *; }
-keep interface com.google.gson.** { *; }
-keepattributes Signature
-keepattributes *Annotation*

# Mantener las clases de OkHttp
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-keepattributes Signature
-keepattributes *Annotation*

# Mantener las clases de Koin
-keep class org.koin.** { *; }
-keep interface org.koin.** { *; }
-keepattributes Signature
-keepattributes *Annotation*
