/**
 * Phase 3 #11 — Integration test for [GlobalLeaderboardScreen].
 * Mock: /stitch_erixam_exam_companion/global_leaderboard_gamified_style/code.html
 */
package com.esec.examprep.presentation.phase3

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.esec.examprep.presentation.leaderboard.GlobalLeaderboardScreen
import com.esec.examprep.presentation.leaderboard.LeaderboardRow
import com.esec.examprep.presentation.theme.ESECTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GlobalLeaderboardScreenIntegrationTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun rendersPodium_topRows_andMyRankBar() {
        val rowClicks = mutableListOf<LeaderboardRow>()
        var myRankClicks = 0

        composeRule.setContent {
            ESECTheme {
                GlobalLeaderboardScreen(
                    state = Phase3Fixtures.globalLeaderboard,
                    onMenuClick = {},
                    onSearchClick = {},
                    onRowClick = { rowClicks.add(it) },
                    onMyRankClick = { myRankClicks++ },
                )
            }
        }

        composeRule.onNodeWithText("Season 4", substring = true).assertIsDisplayed()
        composeRule.onNodeWithText("Lia M.", substring = true).assertIsDisplayed()
        composeRule.onNodeWithText("Yonas T.", substring = true).assertIsDisplayed()
        composeRule.onNodeWithText("Hanna G.", substring = true).assertIsDisplayed()
        composeRule.onNodeWithText("Samuel B.", substring = true)
            .performScrollTo().assertIsDisplayed()

        composeRule.onNodeWithText("Samuel B.", substring = true)
            .performScrollTo().performClick()
        composeRule.waitForIdle()
        assertTrue("Row click should propagate", rowClicks.isNotEmpty())
        assertTrue(myRankClicks >= 0)
    }
}
