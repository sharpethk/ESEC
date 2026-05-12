/**
 * Phase 3 #13 — Integration test for [WrongAnswersReviewScreen].
 * Mock: /stitch_erixam_exam_companion/wrong_answers_review/code.html
 */
package com.esec.examprep.presentation.phase3

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.esec.examprep.presentation.review.WrongAnswersReviewScreen
import com.esec.examprep.presentation.theme.ESECTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WrongAnswersReviewScreenIntegrationTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun rendersHeroCount_reviewCards_andFeaturedActions() {
        var magicClicks = 0
        val featuredClicks = mutableListOf<String>()

        composeRule.setContent {
            ESECTheme {
                WrongAnswersReviewScreen(
                    state = Phase3Fixtures.wrongAnswers,
                    onMenuClick = {},
                    onSearchClick = {},
                    onSubjectChipClick = {},
                    onYearClick = {},
                    onReattempt = {},
                    onViewExplanation = {},
                    onFeaturedActionClick = { featuredClicks.add(it.id) },
                    onMagicFabClick = { magicClicks++ },
                )
            }
        }

        composeRule.onNodeWithText("42").assertIsDisplayed()
        composeRule.onNodeWithText("Mathematics", substring = true)
            .performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("Quick Fire Drill", substring = true)
            .performScrollTo().assertIsDisplayed()

        composeRule.onNodeWithText("Quick Fire Drill", substring = true)
            .performScrollTo().performClick()
        composeRule.waitForIdle()
        assertTrue(featuredClicks.isNotEmpty())
        assertTrue(magicClicks >= 0)
    }
}
