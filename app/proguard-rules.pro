# =====================================================================
# ESEC ExamPrep — R8 / ProGuard rules
# =====================================================================
# These rules complement `proguard-android-optimize.txt` and the
# consumer rules shipped by AndroidX / Hilt / Room / Gson.
# Anything that is constructed reflectively (Gson DTOs, enums looked up
# by name, etc.) must be kept here, otherwise R8 will strip fields or
# obfuscate names and the encrypted question bank will appear empty in
# release builds.

# ---------- Kotlin metadata (needed by some reflective libs) ----------
-keepattributes RuntimeVisibleAnnotations,RuntimeVisibleParameterAnnotations,RuntimeVisibleTypeAnnotations
-keepattributes Signature,InnerClasses,EnclosingMethod
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
-keep class kotlin.Metadata { *; }

# ---------- Gson ------------------------------------------------------
# Keep generic signatures so TypeToken<...> works.
-keep,allowobfuscation,allowshrinking class com.google.gson.reflect.TypeToken
-keep,allowobfuscation,allowshrinking class * extends com.google.gson.reflect.TypeToken
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# ---------- ESEC JSON / domain models constructed by Gson -------------
-keep class com.esec.examprep.data.json.** { *; }
-keepclassmembers class com.esec.examprep.data.json.** {
    <init>(...);
    <fields>;
}
-keep class com.esec.examprep.domain.model.** { *; }

# Encrypted-bank crypto helpers reflectively reference the bank assets.
-keep class com.esec.examprep.data.crypto.** { *; }

# ---------- Enums (looked up by name via valueOf) ---------------------
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
# Keep the field names of every enum (Compose Navigation passes enums
# by name; Gson also depends on them).
-keepclassmembers enum * { *; }

# ---------- Application / Activity / ViewModel ------------------------
# Hilt-generated subclasses reference the user-declared Application/
# Activity/ViewModel classes by name from the manifest and generated
# components, so keep them explicitly.
-keep class com.esec.examprep.ESECApplication { *; }
-keep class com.esec.examprep.** extends android.app.Application { *; }
-keep class com.esec.examprep.** extends android.app.Activity { *; }
-keep class com.esec.examprep.** extends androidx.lifecycle.ViewModel { *; }
-keep class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

# ---------- Room ------------------------------------------------------
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao class * { *; }
# Room generates `*_Impl` classes for every DAO and Database.
-keep class **_Impl { *; }
-keepclassmembers class * {
    @androidx.room.* <methods>;
    @androidx.room.* <fields>;
}
-keep class com.esec.examprep.data.local.** { *; }

# ---------- Hilt / Dagger ---------------------------------------------
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.internal.GeneratedComponent { *; }
-keep class * extends dagger.hilt.android.internal.managers.* { *; }
-keep class com.esec.examprep.**_HiltModules** { *; }
-keep class com.esec.examprep.**_HiltModules$* { *; }
-keep class com.esec.examprep.**_GeneratedInjector { *; }
-keep class com.esec.examprep.Hilt_* { *; }
-keep class com.esec.examprep.**.Hilt_* { *; }
-keepclasseswithmembers class * {
    @dagger.hilt.android.lifecycle.HiltViewModel <init>(...);
}
-keep,allowobfuscation @interface dagger.hilt.android.lifecycle.HiltViewModel
-keep @dagger.hilt.android.lifecycle.HiltViewModel class * { *; }
-keep @javax.inject.Singleton class * { *; }
-keepclassmembers,allowobfuscation class * {
    @javax.inject.* *;
    @dagger.* *;
}

# ---------- WorkManager + Hilt-Work -----------------------------------
-keep class * extends androidx.work.Worker { *; }
-keep class * extends androidx.work.ListenableWorker { *; }
-keep class * extends androidx.work.CoroutineWorker { *; }
-keep @androidx.hilt.work.HiltWorker class * { *; }
-keep class com.esec.examprep.work.** { *; }

# ---------- Compose / Navigation --------------------------------------
-keep class androidx.compose.runtime.** { *; }
-keep class androidx.navigation.** { *; }
-dontwarn androidx.compose.**

# ---------- Coroutines ------------------------------------------------
-keepclassmembers class kotlinx.coroutines.** { volatile <fields>; }
-dontwarn kotlinx.coroutines.**

# ---------- AndroidX SplashScreen (defensive) -------------------------
-keep class androidx.core.splashscreen.** { *; }
-dontwarn androidx.core.splashscreen.**

# ---------- Misc ------------------------------------------------------
-dontwarn javax.annotation.**
-dontwarn org.jetbrains.annotations.**
-dontwarn java.lang.invoke.**

