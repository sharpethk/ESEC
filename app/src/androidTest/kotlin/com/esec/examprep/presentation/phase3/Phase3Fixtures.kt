/**
 * Sample [UiState] fixtures for Phase 3 gamified screen integration tests.
 * Centralised so multiple test classes can reuse the same realistic data.
 */
package com.esec.examprep.presentation.phase3

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VolumeUp
import com.esec.examprep.presentation.bookmarks.BookmarkChip
import com.esec.examprep.presentation.bookmarks.BookmarkDifficulty
import com.esec.examprep.presentation.bookmarks.BookmarkPromo
import com.esec.examprep.presentation.bookmarks.BookmarkTab
import com.esec.examprep.presentation.bookmarks.BookmarkedQuestion
import com.esec.examprep.presentation.bookmarks.BookmarksUiState
import com.esec.examprep.presentation.daily.DailyChallengesUiState
import com.esec.examprep.presentation.daily.DailyQuest
import com.esec.examprep.presentation.daily.QuestProgressState
import com.esec.examprep.presentation.daily.QuestTone
import com.esec.examprep.presentation.daily.WeekDayCell
import com.esec.examprep.presentation.daily.WeekDayState
import com.esec.examprep.presentation.leaderboard.CurrentUserRank
import com.esec.examprep.presentation.leaderboard.GlobalLeaderboardUiState
import com.esec.examprep.presentation.leaderboard.LeaderboardRow
import com.esec.examprep.presentation.leaderboard.PodiumEntry
import com.esec.examprep.presentation.parent.BarTone
import com.esec.examprep.presentation.parent.MasteryAxis
import com.esec.examprep.presentation.parent.MasteryTone
import com.esec.examprep.presentation.parent.ParentBanner
import com.esec.examprep.presentation.parent.ParentKpi
import com.esec.examprep.presentation.parent.ParentPortalUiState
import com.esec.examprep.presentation.parent.RewardItem
import com.esec.examprep.presentation.parent.RewardTone
import com.esec.examprep.presentation.parent.StudyBar
import com.esec.examprep.presentation.review.ActionAccent
import com.esec.examprep.presentation.review.FeaturedAction
import com.esec.examprep.presentation.review.SubjectFilterChip
import com.esec.examprep.presentation.review.WrongAnswerCard
import com.esec.examprep.presentation.review.WrongAnswersUiState
import com.esec.examprep.presentation.settings.GamifiedSettingsGroup
import com.esec.examprep.presentation.settings.GamifiedSettingsProfile
import com.esec.examprep.presentation.settings.GamifiedSettingsRow
import com.esec.examprep.presentation.settings.GamifiedSettingsUiState

object Phase3Fixtures {

    val dailyChallenges = DailyChallengesUiState(
        streakCount = 7,
        dailyBonusXp = 50,
        tasksLeft = 3,
        quests = listOf(
            DailyQuest(
                id = "q1",
                title = "Solve 5 algebra problems",
                xpReward = 30,
                tone = QuestTone.Primary,
                icon = Icons.Default.Calculate,
                current = 3,
                target = 5,
                state = QuestProgressState.InProgress,
            ),
            DailyQuest(
                id = "q2",
                title = "Read science article",
                xpReward = 20,
                tone = QuestTone.Secondary,
                icon = Icons.Default.Science,
                current = 0,
                target = 1,
                state = QuestProgressState.NotStarted,
            ),
        ),
        weekCells = listOf(
            WeekDayCell("M", 6, WeekDayState.Done),
            WeekDayCell("T", 7, WeekDayState.Today),
            WeekDayCell("W", null, WeekDayState.Future),
            WeekDayCell("T", null, WeekDayState.Future),
            WeekDayCell("F", null, WeekDayState.Future),
            WeekDayCell("S", null, WeekDayState.Future),
            WeekDayCell("S", null, WeekDayState.Future),
        ),
        weeklyHint = "Keep going to finish your week!",
        tierName = "Gold Tier",
        globalRank = 124,
    )

    val globalLeaderboard = GlobalLeaderboardUiState(
        seasonLabel = "Season 4",
        first = PodiumEntry(rank = 1, name = "Lia M.", xp = 16800, level = 28),
        second = PodiumEntry(rank = 2, name = "Yonas T.", xp = 15500, level = 26),
        third = PodiumEntry(rank = 3, name = "Hanna G.", xp = 14900, level = 25),
        topRows = listOf(
            LeaderboardRow(rank = 4, name = "Samuel B.", level = 24, xp = 14100),
            LeaderboardRow(rank = 5, name = "Mehret K.", level = 23, xp = 13700),
        ),
        nearbyRows = listOf(
            LeaderboardRow(rank = 41, name = "Abel N.", level = 14, xp = 6200),
            LeaderboardRow(rank = 42, name = "You", level = 14, xp = 6100),
            LeaderboardRow(rank = 43, name = "Sara T.", level = 13, xp = 6000),
        ),
        totalStudents = 4820,
        currentUser = CurrentUserRank(rank = 42, level = 14, xp = 6100),
    )

    val bookmarks = BookmarksUiState(
        level = 12,
        intro = "Your saved questions and notes",
        searchQuery = "",
        activeTab = BookmarkTab.Bookmarked,
        filters = listOf(
            BookmarkChip("all", "All"),
            BookmarkChip("math", "Math"),
            BookmarkChip("science", "Science"),
        ),
        activeFilterId = "all",
        questions = listOf(
            BookmarkedQuestion(
                id = "b1",
                subject = "Mathematics",
                difficulty = BookmarkDifficulty.Hard,
                text = "Solve for x: 2x^2 + 3x - 5 = 0",
                savedAgo = "Saved 2 days ago",
                icon = Icons.Default.Calculate,
            ),
            BookmarkedQuestion(
                id = "b2",
                subject = "Physics",
                difficulty = BookmarkDifficulty.Medium,
                text = "What is the SI unit of force?",
                savedAgo = "Saved 1 week ago",
                icon = Icons.Default.Science,
            ),
        ),
        promo = BookmarkPromo(
            title = "Master Your Weak Spots",
            subtitle = "Review your saved questions and ace the next exam.",
        ),
    )

    val wrongAnswers = WrongAnswersUiState(
        mistakesCount = 42,
        subjectChips = listOf(
            SubjectFilterChip("all", "All", Icons.Default.MenuBook),
            SubjectFilterChip("math", "Math", Icons.Default.Calculate),
            SubjectFilterChip("science", "Science", Icons.Default.Science),
        ),
        activeSubjectId = "all",
        examYears = listOf(2021, 2022, 2023, 2024),
        activeYear = 2023,
        cards = listOf(
            WrongAnswerCard(
                id = "w1",
                subjectAndYear = "Mathematics • 2023",
                topic = "Topic: Quadratic Equations",
                masteryPercent = 35,
                snippet = "You selected x = 2, but the correct answer is x = -1 or x = 5/2.",
            ),
            WrongAnswerCard(
                id = "w2",
                subjectAndYear = "Physics • 2023",
                topic = "Topic: Newton's Second Law",
                masteryPercent = 60,
                snippet = "F = ma — review free-body diagrams.",
            ),
        ),
        masteryTip = "Tackle low-mastery topics first to compound progress.",
        featuredActions = listOf(
            FeaturedAction(
                id = "drill",
                title = "Quick Fire Drill",
                subtitle = "10 mistakes • 5 minutes",
                icon = Icons.Default.Bolt,
                accent = ActionAccent.Secondary,
            ),
            FeaturedAction(
                id = "wreath",
                title = "Earn Wreaths",
                subtitle = "Master streaks",
                icon = Icons.Default.EmojiEvents,
                accent = ActionAccent.Surface,
            ),
        ),
    )

    val parentPortal = ParentPortalUiState(
        childName = "Nahom",
        kpis = listOf(
            ParentKpi("Time Studied", "12.4h", "+15%"),
            ParentKpi("Accuracy", "82%", "Stable"),
            ParentKpi("Quests", "24", "Mastery"),
        ),
        studyBars = listOf(
            StudyBar("Mon", 0.45f, BarTone.Muted),
            StudyBar("Tue", 0.55f, BarTone.Soft),
            StudyBar("Wed", 0.40f, BarTone.Muted),
            StudyBar("Thu", 0.70f, BarTone.Soft),
            StudyBar("Fri", 0.62f, BarTone.Soft),
            StudyBar("Sat", 1.00f, BarTone.Primary),
            StudyBar("Sun", 0.30f, BarTone.Muted),
        ),
        masteryAxes = listOf(
            MasteryAxis("Math", 92, MasteryTone.High),
            MasteryAxis("Physics", 74, MasteryTone.Mid),
            MasteryAxis("Civics", 45, MasteryTone.Low),
        ),
        rewards = listOf(
            RewardItem(
                id = "r1",
                title = "Weekend Trip",
                goal = "Reach 90% accuracy",
                progressLabel = "75%",
                progressFraction = 0.75f,
                icon = Icons.Default.FlightTakeoff,
                tone = RewardTone.Secondary,
            ),
            RewardItem(
                id = "r2",
                title = "Extra Gaming Hour",
                goal = "Complete 5 quests",
                progressLabel = "2/5",
                progressFraction = 0.4f,
                icon = Icons.Default.SportsEsports,
                tone = RewardTone.Primary,
            ),
        ),
        banner = ParentBanner(
            headline = "Investing in Excellence",
            tagline = "Track, motivate, celebrate.",
        ),
    )

    val gamifiedSettings = GamifiedSettingsUiState(
        profile = GamifiedSettingsProfile(
            displayName = "Abraham Tekle",
            tagline = "Aspiring Medical Student",
            level = 12,
            xpCurrent = 2450,
            xpTarget = 3000,
            streakDays = 14,
        ),
        groups = listOf(
            GamifiedSettingsGroup(
                title = "Account",
                rows = listOf(
                    GamifiedSettingsRow.Navigation("personal", Icons.Default.PersonOutline, "Personal Info"),
                    GamifiedSettingsRow.Navigation("security", Icons.Default.Security, "Security"),
                    GamifiedSettingsRow.Navigation("plan", Icons.Default.CalendarMonth, "Study Plan"),
                ),
            ),
            GamifiedSettingsGroup(
                title = "Notifications",
                rows = listOf(
                    GamifiedSettingsRow.Toggle("quest", Icons.Default.NotificationsActive, "Quest Alerts", true),
                    GamifiedSettingsRow.Toggle("reminders", Icons.Default.Schedule, "Reminders", true),
                    GamifiedSettingsRow.Toggle("sounds", Icons.Default.VolumeUp, "Sound Effects", false),
                ),
            ),
            GamifiedSettingsGroup(
                title = "Learning",
                rows = listOf(
                    GamifiedSettingsRow.Trailing(
                        id = "offline",
                        icon = Icons.Default.CloudDownload,
                        label = "Offline Mode",
                        trailingText = "Downloaded (2.4GB)",
                    ),
                    GamifiedSettingsRow.Trailing(
                        id = "language",
                        icon = Icons.Default.Translate,
                        label = "Language",
                        trailingText = "English",
                        trailingHighlighted = true,
                        showChevron = true,
                    ),
                ),
            ),
            GamifiedSettingsGroup(
                title = "Support",
                rows = listOf(
                    GamifiedSettingsRow.Navigation("help", Icons.Default.HelpOutline, "Help Center"),
                    GamifiedSettingsRow.Navigation("feedback", Icons.Default.RateReview, "Feedback"),
                ),
            ),
        ),
        versionLabel = "EriXam v2.4.1 • Made with ❤ in Eritrea",
    )
}
