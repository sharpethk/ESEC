package com.esec.examprep.resources

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Guards against accidental regressions in [app/proguard-rules.pro]. The rules
 * file is the only thing standing between R8 and an empty Exam tab in release
 * builds (Gson DTO fields get stripped) and a missing splash screen (themes /
 * activities get obfuscated). These tests assert the critical keep rules are
 * present so a future "cleanup" PR doesn't silently break the release APK.
 */
class ProguardRulesTest {

    private val rules: String by lazy {
        listOf(File("proguard-rules.pro"), File("app/proguard-rules.pro"))
            .firstOrNull { it.exists() }
            ?.readText()
            ?: error("proguard-rules.pro not found")
    }

    @Test
    fun `keeps Gson DTOs for the encrypted question bank`() {
        assertContains("-keep class com.esec.examprep.data.json.**")
        assertContains("@com.google.gson.annotations.SerializedName")
    }

    @Test
    fun `keeps domain models and crypto helpers`() {
        assertContains("-keep class com.esec.examprep.domain.model.**")
        assertContains("-keep class com.esec.examprep.data.crypto.**")
    }

    @Test
    fun `keeps Room database, DAOs and generated _Impl classes`() {
        assertContains("androidx.room.RoomDatabase")
        assertContains("@androidx.room.Entity")
        assertContains("@androidx.room.Dao")
        assertContains("**_Impl")
        assertContains("-keep class com.esec.examprep.data.local.**")
    }

    @Test
    fun `keeps Hilt generated injectors, modules and Application`() {
        assertContains("com.esec.examprep.ESECApplication")
        assertContains("_HiltModules")
        assertContains("_GeneratedInjector")
        assertContains("dagger.hilt.android.lifecycle.HiltViewModel")
    }

    @Test
    fun `keeps WorkManager workers`() {
        assertContains("androidx.work.Worker")
        assertContains("androidx.work.CoroutineWorker")
        assertContains("@androidx.hilt.work.HiltWorker")
        assertContains("-keep class com.esec.examprep.work.**")
    }

    @Test
    fun `keeps enum values for valueOf reflection`() {
        assertContains("public static **[] values()")
        assertContains("public static ** valueOf(java.lang.String)")
    }

    @Test
    fun `keeps Kotlin metadata for reflective libs`() {
        assertContains("kotlin.Metadata")
    }

    @Test
    fun `keeps AndroidX splash screen package`() {
        assertContains("androidx.core.splashscreen")
    }

    @Test
    fun `keeps signature and inner classes attributes`() {
        assertContains("Signature")
        assertContains("InnerClasses")
    }

    private fun assertContains(needle: String) {
        assertTrue(
            "proguard-rules.pro must contain '$needle' but it was missing",
            rules.contains(needle),
        )
    }
}
