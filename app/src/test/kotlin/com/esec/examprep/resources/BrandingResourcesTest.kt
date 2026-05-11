package com.esec.examprep.resources

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.w3c.dom.Element
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Smoke tests for resources changed during the EriXam rebrand / splash overhaul.
 * Reads the XML files directly (no Android runtime required) so they run as a
 * fast JVM unit test in `:app:testReleaseUnitTest`.
 */
class BrandingResourcesTest {

    @Test
    fun `default app_name is EriXam`() {
        assertEquals("EriXam", stringResource("values", "app_name"))
    }

    @Test
    fun `default home_title is EriXam`() {
        assertEquals("EriXam", stringResource("values", "home_title"))
    }

    @Test
    fun `default home_section_why mentions EriXam not ESEC`() {
        val value = stringResource("values", "home_section_why")
        assertTrue(
            "home_section_why should reference EriXam — was: '$value'",
            value.contains("EriXam"),
        )
        assertTrue(
            "home_section_why should no longer reference ESEC — was: '$value'",
            !value.contains("ESEC"),
        )
    }

    @Test
    fun `tigrinya app_name is EriXam`() {
        assertEquals("EriXam", stringResource("values-ti", "app_name"))
    }

    @Test
    fun `tigrinya home_title is EriXam`() {
        assertEquals("EriXam", stringResource("values-ti", "home_title"))
    }

    @Test
    fun `splash_background color matches deep blue handoff color`() {
        val value = colorResource("splash_background")
        assertEquals("#0A2A6B", value.uppercase())
    }

    @Test
    fun `ic_launcher_background matches splash background`() {
        val value = colorResource("ic_launcher_background")
        assertEquals("#0A2A6B", value.uppercase())
    }

    @Test
    fun `splash_artwork drawable exists`() {
        val file = resourceFile("drawable/splash_artwork.xml")
            ?: resourceFile("drawable-nodpi/splash_artwork.png")
        assertNotNull("Either drawable/splash_artwork.xml or drawable-nodpi/splash_artwork.png must exist", file)
        assertTrue(file!!.length() > 0)
    }

    @Test
    fun `ic_splash_logo drawable exists`() {
        val file = resourceFile("drawable-nodpi/ic_splash_logo.png")
        assertNotNull("drawable-nodpi/ic_splash_logo.png must exist", file)
        assertTrue(file!!.length() > 0)
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private fun stringResource(qualifier: String, name: String): String {
        val file = resourceFile("$qualifier/strings.xml")
            ?: error("strings.xml not found under res/$qualifier")
        val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file)
        val nodes = doc.getElementsByTagName("string")
        for (i in 0 until nodes.length) {
            val el = nodes.item(i) as Element
            if (el.getAttribute("name") == name) return el.textContent
        }
        error("string '$name' not found in res/$qualifier/strings.xml")
    }

    private fun colorResource(name: String): String {
        val file = resourceFile("values/colors.xml")
            ?: error("colors.xml not found under res/values")
        val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file)
        val nodes = doc.getElementsByTagName("color")
        for (i in 0 until nodes.length) {
            val el = nodes.item(i) as Element
            if (el.getAttribute("name") == name) return el.textContent
        }
        error("color '$name' not found in res/values/colors.xml")
    }

    private fun resourceFile(relativePath: String): File? {
        // Unit tests run with working dir = `app/`. Walk up if needed so the
        // tests are robust to invocation from the project root too.
        val candidates = listOf(
            File("src/main/res/$relativePath"),
            File("app/src/main/res/$relativePath"),
        )
        return candidates.firstOrNull { it.exists() }
    }
}
