package com.esec.examprep.resources

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Verifies the resource-shrinker keep list at `res/raw/keep.xml` preserves
 * the splash/branding resources that the shrinker would otherwise strip in
 * release builds. Without these entries, the splash screen disappears from
 * the release APK (see [colors.xml] `splash_background` and the splash
 * theme styles in `themes.xml`).
 */
class KeepXmlTest {

    private val keepXml: File by lazy {
        listOf(File("src/main/res/raw/keep.xml"), File("app/src/main/res/raw/keep.xml"))
            .firstOrNull { it.exists() }
            ?: error("res/raw/keep.xml not found")
    }

    @Test
    fun `keep xml exists and parses as valid XML`() {
        val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(keepXml)
        assertEquals("resources", doc.documentElement.tagName)
    }

    @Test
    fun `tools shrinkMode is safe`() {
        val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(keepXml)
        val mode = doc.documentElement.getAttributeNS(TOOLS_NS, "shrinkMode")
            .ifEmpty { doc.documentElement.getAttribute("tools:shrinkMode") }
        assertEquals("safe", mode)
    }

    @Test
    fun `keep list preserves splash drawables, color and themes`() {
        val keep = readKeepAttribute()
        val required = listOf(
            "@drawable/ic_splash_logo",
            "@drawable/splash_artwork",
            "@color/splash_background",
            "@style/Theme.ESEC",
            "@style/Theme.ESEC.Main",
        )
        for (entry in required) {
            assertTrue(
                "tools:keep must contain '$entry' to survive resource shrinking — was: '$keep'",
                keep.contains(entry),
            )
        }
    }

    private fun readKeepAttribute(): String {
        val doc = DocumentBuilderFactory.newInstance().apply { isNamespaceAware = true }
            .newDocumentBuilder().parse(keepXml)
        val root = doc.documentElement
        val nsValue = root.getAttributeNS(TOOLS_NS, "keep")
        if (nsValue.isNotEmpty()) return nsValue
        // Fallback for non-namespace-aware parsing.
        return root.getAttribute("tools:keep")
    }

    private companion object {
        const val TOOLS_NS = "http://schemas.android.com/tools"
    }
}
