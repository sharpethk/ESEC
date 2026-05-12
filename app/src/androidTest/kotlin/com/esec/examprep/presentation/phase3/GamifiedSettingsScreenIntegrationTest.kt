/**
 * Phase 3 #15 — Integration test for [GamifiedSettingsScreen].
 * Mock: /stitch_erixam_exam_companion/settings/code.html
 */
package com.esec.examprep.presentation.phase3

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.esec.examprep.presentation.settings.GamifiedSettingsScreen
import com.esec.examprep.presentation.theme.ESECTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GamifiedSettingsScreenIntegrationTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun rendersProfile_groupsHeaders_andSwitchToParentCta() {
        val rowClicks = mutableListOf<String>()
        val toggleEvents = mutableListOf<Pair<String, Boolean>>()
        var parentClicks = 0

        composeRule.setContent {
            ESECTheme {
                GamifiedSettingsScreen(
                    state = Phase3Fixtures.gamifiedSettings,
                    onMenuClick = {},
                    onSearchClick = {},
                    onRowClick = { rowClicks.add(it.id) },
                    onToggleChange = { row, checked -> toggleEvents.add(row.id to checked) },
                    onSwitchToParentView = { parentClicks++ },
                )
            }
        }

        composeRule.onNodeWithText("Abraham Tekle", substring = true).assertIsDisplayed()
        composeRule.onNodeWithText("ACCOUNT").assertIsDisplayed()
        composeRule.onNodeWithText("Personal Info", substring = true).assertIsDisplayed()
        composeRule.onNodeWithText("Switch to Parent View", substring = true)
            .performScrollTo().assertIsDisplayed()

        composeRule.onNodeWithText("Personal Info", substring = true)
            .performScrollTo().performClick()
        composeRule.waitForIdle()
        assertTrue(rowClicks.contains("personal"))

        composeRule.onNodeWithText("Switch to Parent View", substring = true)
            .performScrollTo().performClick()
        composeRule.waitForIdle()
        assertTrue("Parent CTA should fire", parentClicks > 0)
    }
}
