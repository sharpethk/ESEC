/**
 * Cross-screen NavHost flow test wiring all six Phase 3 gamified
 * screens (Daily / Leaderboard / Bookmarks / Wrong Answers / Parent /
 * Settings) into a single graph and walking through each, asserting
 * the destination's hero text appears after navigation.
 */
package com.esec.examprep.presentation.phase3

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.esec.examprep.presentation.bookmarks.BookmarkedQuestionsScreen
import com.esec.examprep.presentation.daily.DailyChallengesScreen
import com.esec.examprep.presentation.leaderboard.GlobalLeaderboardScreen
import com.esec.examprep.presentation.parent.ParentPortalScreen
import com.esec.examprep.presentation.review.WrongAnswersReviewScreen
import com.esec.examprep.presentation.settings.GamifiedSettingsScreen
import com.esec.examprep.presentation.theme.ESECTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class Phase3NavFlowIntegrationTest {

    @get:Rule
    val composeRule = createComposeRule()

    private object Route {
        const val Daily = "daily"
        const val Leaderboard = "leaderboard"
        const val Bookmarks = "bookmarks"
        const val WrongAnswers = "wrong_answers"
        const val Parent = "parent"
        const val Settings = "settings"
    }

    @Composable
    private fun NavButton(label: String, onClick: () -> Unit) {
        Button(onClick = onClick) { Text(label) }
    }

    @Test
    fun walksThroughAllPhase3GamifiedScreens() {
        composeRule.setContent {
            ESECTheme {
                val nav = rememberNavController()
                NavHost(navController = nav, startDestination = Route.Daily) {
                    composable(Route.Daily) {
                        DailyChallengesScreen(
                            state = Phase3Fixtures.dailyChallenges,
                            onMenuClick = {},
                            onSearchClick = { nav.navigate(Route.Leaderboard) },
                            onClaimDailyBonus = {},
                            onQuestClick = {},
                        )
                        NavButton("GoLeaderboard") { nav.navigate(Route.Leaderboard) }
                    }
                    composable(Route.Leaderboard) {
                        GlobalLeaderboardScreen(
                            state = Phase3Fixtures.globalLeaderboard,
                            onMenuClick = {},
                            onSearchClick = {},
                            onRowClick = {},
                            onMyRankClick = {},
                        )
                        NavButton("GoBookmarks") { nav.navigate(Route.Bookmarks) }
                    }
                    composable(Route.Bookmarks) {
                        BookmarkedQuestionsScreen(
                            state = Phase3Fixtures.bookmarks,
                            onMenuClick = {},
                            onSearchClick = {},
                            onQueryChange = {},
                            onTabChange = {},
                            onFilterClick = {},
                            onQuestionClick = {},
                            onRemoveBookmark = {},
                            onReviewNow = {},
                        )
                        NavButton("GoWrongAnswers") { nav.navigate(Route.WrongAnswers) }
                    }
                    composable(Route.WrongAnswers) {
                        WrongAnswersReviewScreen(
                            state = Phase3Fixtures.wrongAnswers,
                            onMenuClick = {},
                            onSearchClick = {},
                            onSubjectChipClick = {},
                            onYearClick = {},
                            onReattempt = {},
                            onViewExplanation = {},
                            onFeaturedActionClick = {},
                            onMagicFabClick = {},
                        )
                        NavButton("GoParent") { nav.navigate(Route.Parent) }
                    }
                    composable(Route.Parent) {
                        ParentPortalScreen(
                            state = Phase3Fixtures.parentPortal,
                            onMenuClick = {},
                            onSearchClick = {},
                            onSendMotivation = {},
                            onAddReward = {},
                            onRewardClick = {},
                        )
                        NavButton("GoSettings") { nav.navigate(Route.Settings) }
                    }
                    composable(Route.Settings) {
                        GamifiedSettingsScreen(
                            state = Phase3Fixtures.gamifiedSettings,
                            onMenuClick = {},
                            onSearchClick = {},
                            onRowClick = {},
                            onToggleChange = { _, _ -> },
                            onSwitchToParentView = {},
                        )
                    }
                }
            }
        }

        // Daily
        composeRule.onNodeWithText("Solve 5 algebra problems", substring = true)
            .performScrollTo().assertIsDisplayed()

        // -> Leaderboard
        composeRule.onNodeWithText("GoLeaderboard").performScrollTo().performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Season 4", substring = true).assertIsDisplayed()

        // -> Bookmarks
        composeRule.onNodeWithText("GoBookmarks").performScrollTo().performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Solve for x", substring = true)
            .performScrollTo().assertIsDisplayed()

        // -> Wrong Answers
        composeRule.onNodeWithText("GoWrongAnswers").performScrollTo().performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText("42").assertIsDisplayed()

        // -> Parent
        composeRule.onNodeWithText("GoParent").performScrollTo().performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Nahom", substring = true).assertIsDisplayed()

        // -> Settings
        composeRule.onNodeWithText("GoSettings").performScrollTo().performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Abraham Tekle", substring = true).assertIsDisplayed()
    }
}
