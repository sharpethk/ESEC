/**
 * Maps to /stitch_erixam_exam_companion/daily_challenges_gamified_style/code.html
 * Design tokens from /stitch_erixam_exam_companion/erixam_design_system/DESIGN.md.
 *
 * Daily reward + today's quests + weekly streak grid + tier/rank
 * mini bento.
 */
package com.esec.examprep.presentation.daily

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.esec.examprep.presentation.theme.ESECTheme
import com.esec.examprep.presentation.theme.Radius
import com.esec.examprep.presentation.theme.Spacing

// -------- UI Models --------

enum class QuestTone { Primary, Secondary, Tertiary }
enum class QuestProgressState { NotStarted, InProgress, Completed }

@Immutable
data class DailyQuest(
    val id: String,
    val title: String,
    val xpReward: Int,
    val tone: QuestTone,
    val icon: ImageVector,
    val current: Int,
    val target: Int,
    val unit: String? = null,   // e.g. "m" for minutes
    val state: QuestProgressState,
    val locked: Boolean = false,
)

enum class WeekDayState { Done, Today, Future }

@Immutable
data class WeekDayCell(
    val label: String,
    val dayNumber: Int? = null,
    val state: WeekDayState,
)

@Immutable
data class DailyChallengesUiState(
    val streakCount: Int,
    val dailyBonusXp: Int,
    val tasksLeft: Int,
    val quests: List<DailyQuest>,
    val weekCells: List<WeekDayCell>,    // 7 days
    val weeklyHint: String,
    val tierName: String,
    val globalRank: Int,
)

// -------- Stateful entry --------

@Composable
fun DailyChallengesScreen(
    state: DailyChallengesUiState,
    onMenuClick: () -> Unit,
    onSearchClick: () -> Unit,
    onClaimDailyBonus: () -> Unit,
    onQuestClick: (DailyQuest) -> Unit,
) {
    DailyChallengesContent(
        state = state,
        onMenuClick = onMenuClick,
        onSearchClick = onSearchClick,
        onClaimDailyBonus = onClaimDailyBonus,
        onQuestClick = onQuestClick,
    )
}

// -------- Stateless content --------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DailyChallengesContent(
    state: DailyChallengesUiState,
    onMenuClick: () -> Unit,
    onSearchClick: () -> Unit,
    onClaimDailyBonus: () -> Unit,
    onQuestClick: (DailyQuest) -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "EriXam",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(
                            Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                },
                actions = {
                    Row(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                            .padding(horizontal = Spacing.sm, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        Icon(
                            Icons.Default.LocalFireDepartment,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(16.dp),
                        )
                        Text(
                            text = state.streakCount.toString(),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    IconButton(onClick = onSearchClick) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(
                start = Spacing.lg,
                end = Spacing.lg,
                top = Spacing.md,
                bottom = Spacing.xxxl,
            ),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg),
        ) {
            item("hero") {
                DailyBonusHero(bonusXp = state.dailyBonusXp, onClaim = onClaimDailyBonus)
            }
            item("quests-header") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "Today's Quests",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = "${state.tasksLeft} Tasks Left",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
            items(state.quests) { q ->
                QuestCard(quest = q, onClick = { onQuestClick(q) })
            }
            item("weekly") {
                WeeklyProgressCard(cells = state.weekCells, hint = state.weeklyHint)
            }
            item("bento") {
                TierRankBento(tierName = state.tierName, globalRank = state.globalRank)
            }
        }
    }
}

// -------- Hero --------

@Composable
private fun DailyBonusHero(bonusXp: Int, onClaim: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radius.lg))
            .background(MaterialTheme.colorScheme.primary)
            .padding(Spacing.lg),
    ) {
        Box(
            modifier = Modifier
                .size(128.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.20f))
                .align(Alignment.TopEnd),
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            Text(
                text = "Daily Reward Ready!",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Keep your streak alive and earn $bonusXp bonus XP.",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f),
            )
            Spacer(Modifier.size(Spacing.xs))
            Button(
                onClick = onClaim,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                ),
            ) {
                Icon(Icons.Default.CardGiftcard, contentDescription = null)
                Spacer(Modifier.size(Spacing.sm))
                Text(
                    text = "Claim Daily Bonus",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

// -------- Quest card --------

@Composable
private fun QuestCard(quest: DailyQuest, onClick: () -> Unit) {
    val (iconBg, iconTint) = when (quest.tone) {
        QuestTone.Primary -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.10f) to
            MaterialTheme.colorScheme.primary
        QuestTone.Secondary -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.10f) to
            MaterialTheme.colorScheme.secondary
        QuestTone.Tertiary -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.10f) to
            MaterialTheme.colorScheme.tertiary
    }
    val barColor = when (quest.tone) {
        QuestTone.Primary -> MaterialTheme.colorScheme.primaryContainer
        QuestTone.Secondary -> MaterialTheme.colorScheme.secondary
        QuestTone.Tertiary -> MaterialTheme.colorScheme.tertiary
    }
    val effectiveBarColor = if (quest.locked) MaterialTheme.colorScheme.outlineVariant else barColor
    val progressAlpha = if (quest.locked) 0.5f else 1f
    val progressFraction = if (quest.target == 0) 0f
    else quest.current.toFloat() / quest.target.toFloat()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radius.lg))
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(Radius.lg),
            )
            .clickable(enabled = !quest.locked, onClick = onClick)
            .padding(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(Radius.md))
                        .background(iconBg),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(quest.icon, contentDescription = null, tint = iconTint)
                }
                Column {
                    Text(
                        text = quest.title,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(Modifier.size(2.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(16.dp),
                        )
                        Text(
                            text = "${quest.xpReward} XP Reward",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.tertiary,
                        )
                    }
                }
            }
            if (quest.locked) {
                Icon(
                    Icons.Default.Lock,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outlineVariant,
                )
            }
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = when (quest.state) {
                        QuestProgressState.NotStarted -> "Not Started"
                        QuestProgressState.InProgress -> "Progress"
                        QuestProgressState.Completed -> "Completed"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = progressAlpha),
                )
                val unit = quest.unit ?: ""
                Text(
                    text = "${quest.current}${unit}/${quest.target}${unit}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = progressAlpha),
                )
            }
            LinearProgressIndicator(
                progress = { progressFraction.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(CircleShape),
                color = effectiveBarColor,
                trackColor = MaterialTheme.colorScheme.surfaceContainer,
            )
        }
    }
}

// -------- Weekly progress --------

@Composable
private fun WeeklyProgressCard(cells: List<WeekDayCell>, hint: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radius.lg))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            Icon(
                Icons.Default.CalendarMonth,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = "Weekly Progress",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            cells.forEach { cell ->
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = cell.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (cell.state == WeekDayState.Today) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = if (cell.state == WeekDayState.Today) FontWeight.Bold else FontWeight.Normal,
                    )
                    WeekDayBead(cell = cell)
                }
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.30f))
        Text(
            text = hint,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth(),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
    }
}

@Composable
private fun WeekDayBead(cell: WeekDayCell) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(
                when (cell.state) {
                    WeekDayState.Done -> MaterialTheme.colorScheme.secondaryContainer
                    WeekDayState.Today -> MaterialTheme.colorScheme.surface
                    WeekDayState.Future -> MaterialTheme.colorScheme.surfaceContainerHighest
                },
            )
            .let {
                if (cell.state == WeekDayState.Today) {
                    it.border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                } else it
            },
        contentAlignment = Alignment.Center,
    ) {
        when (cell.state) {
            WeekDayState.Done -> Icon(
                Icons.Default.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.size(20.dp),
            )
            WeekDayState.Today -> Icon(
                Icons.Default.HourglassEmpty,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp),
            )
            WeekDayState.Future -> Text(
                text = cell.dayNumber?.toString() ?: "",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline,
            )
        }
    }
}

// -------- Tier / rank bento --------

@Composable
private fun TierRankBento(tierName: String, globalRank: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
        Column(
            modifier = Modifier
                .weight(1f)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(Radius.lg))
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                .padding(Spacing.md),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Icon(
                Icons.Default.MilitaryTech,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp),
            )
            Column {
                Text(
                    text = "Current Tier",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = tierName,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(Radius.lg))
                .background(MaterialTheme.colorScheme.surfaceContainerHigh),
        ) {
            Icon(
                Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                modifier = Modifier
                    .size(80.dp)
                    .align(Alignment.BottomEnd),
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(Spacing.md),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.TrendingUp,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(32.dp),
                )
                Column {
                    Text(
                        text = "Global Rank",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = "#%,d".format(globalRank),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

// -------- Preview --------

@Preview(showBackground = true, heightDp = 1500)
@Composable
private fun DailyChallengesPreview() {
    ESECTheme {
        DailyChallengesContent(
            state = DailyChallengesUiState(
                streakCount = 12,
                dailyBonusXp = 500,
                tasksLeft = 3,
                quests = listOf(
                    DailyQuest(
                        id = "math",
                        title = "Complete 2 Math Quests",
                        xpReward = 200,
                        tone = QuestTone.Primary,
                        icon = Icons.Default.Functions,
                        current = 1,
                        target = 2,
                        state = QuestProgressState.InProgress,
                    ),
                    DailyQuest(
                        id = "timer",
                        title = "Study for 30 mins",
                        xpReward = 150,
                        tone = QuestTone.Secondary,
                        icon = Icons.Default.Timer,
                        current = 12,
                        target = 30,
                        unit = "m",
                        state = QuestProgressState.InProgress,
                    ),
                    DailyQuest(
                        id = "history",
                        title = "History Quiz Perfect Score",
                        xpReward = 300,
                        tone = QuestTone.Tertiary,
                        icon = Icons.Default.HistoryEdu,
                        current = 0,
                        target = 1,
                        state = QuestProgressState.NotStarted,
                        locked = true,
                    ),
                ),
                weekCells = listOf(
                    WeekDayCell("M", state = WeekDayState.Done),
                    WeekDayCell("T", state = WeekDayState.Done),
                    WeekDayCell("W", state = WeekDayState.Done),
                    WeekDayCell("T", state = WeekDayState.Today),
                    WeekDayCell("F", dayNumber = 18, state = WeekDayState.Future),
                    WeekDayCell("S", dayNumber = 19, state = WeekDayState.Future),
                    WeekDayCell("S", dayNumber = 20, state = WeekDayState.Future),
                ),
                weeklyHint = "Study 3 more days to earn the Golden Wreath!",
                tierName = "Master Student",
                globalRank = 4231,
            ),
            onMenuClick = {},
            onSearchClick = {},
            onClaimDailyBonus = {},
            onQuestClick = {},
        )
    }
}

