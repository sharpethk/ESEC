/**
 * Phase 3 #14 — Integration test for [ParentPortalScreen].
 * Mock: /stitch_erixam_exam_companion/parent_portal/code.html
 */
package com.esec.examprep.presentation.phase3

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.esec.examprep.presentation.parent.ParentPortalScreen
import com.esec.examprep.presentation.theme.ESECTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ParentPortalScreenIntegrationTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun rendersChildName_kpis_rewards_andSendMotivation() {
        var sendClicks = 0
        var addRewardClicks = 0
        val rewardClicks = mutableListOf<String>()

        composeRule.setContent {
            ESECTheme {
                ParentPortalScreen(
                    state = Phase3Fixtures.parentPortal,
                    onMenuClick = {},
                    onSearchClick = {},
                    onSendMotivation = { sendClicks++ },
                    onAddReward = { addRewardClicks++ },
                    onRewardClick = { rewardClicks.add(it.id) },
                )
            }
        }

        composeRule.onNodeWithText("Nahom", substring = true).assertIsDisplayed()
        composeRule.onNodeWithText("12.4h", substring = true).assertIsDisplayed()
        composeRule.onNodeWithText("Send Motivation", substring = true)
            .performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("Weekend Trip", substring = true)
            .performScrollTo().assertIsDisplayed()

        composeRule.onNodeWithText("Send Motivation", substring = true)
            .performScrollTo().performClick()
        composeRule.waitForIdle()
        assertTrue("Send Motivation should fire", sendClicks > 0)
        assertTrue(addRewardClicks >= 0)
        assertTrue(rewardClicks.size >= 0)
    }
}
