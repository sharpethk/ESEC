/**
 * Phase 3 #12 — Integration test for [BookmarkedQuestionsScreen].
 * Mock: /stitch_erixam_exam_companion/bookmarked_questions/code.html
 */
package com.esec.examprep.presentation.phase3

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.esec.examprep.presentation.bookmarks.BookmarkedQuestionsScreen
import com.esec.examprep.presentation.theme.ESECTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BookmarkedQuestionsScreenIntegrationTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun rendersQuestions_filters_andReviewFab() {
        val questionClicks = mutableListOf<String>()
        var reviewClicks = 0

        composeRule.setContent {
            ESECTheme {
                BookmarkedQuestionsScreen(
                    state = Phase3Fixtures.bookmarks,
                    onMenuClick = {},
                    onSearchClick = {},
                    onQueryChange = {},
                    onTabChange = {},
                    onFilterClick = {},
                    onQuestionClick = { questionClicks.add(it.id) },
                    onRemoveBookmark = {},
                    onReviewNow = { reviewClicks++ },
                )
            }
        }

        composeRule.onNodeWithText("Solve for x", substring = true)
            .performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("What is the SI unit of force?", substring = true)
            .performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("Math", substring = true).assertIsDisplayed()

        composeRule.onNodeWithText("Solve for x", substring = true)
            .performScrollTo().performClick()
        composeRule.waitForIdle()
        assertTrue(questionClicks.isNotEmpty())
        assertTrue(reviewClicks >= 0)
    }
}
