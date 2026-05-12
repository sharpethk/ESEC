/**
 * Maps to /stitch_erixam_exam_companion/exam_results_gamified_style/code.html
 * Design tokens from /stitch_erixam_exam_companion/erixam_design_system/DESIGN.md.
 *
 * Results celebration: score ring inside Level Up hero, achievement
 * unlocked panel (dashed border + floating medal), bento performance
 * analysis (Algebra wide + Geometry/Calculus split), Correct + Time
 * mini-stats, Claim/Try Again primary actions.
 */
package com.esec.examprep.presentation.exam

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Redeem
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.esec.examprep.presentation.components.ProgressRing
import com.esec.examprep.presentation.theme.ESECTheme
import com.esec.examprep.presentation.theme.Radius
import com.esec.examprep.presentation.theme.Spacing

// -------- UI Models --------

@Immutable
data class TopicMastery(
    val name: String,
    val percent: Int,
    val isPrimary: Boolean = false, // primary topic gets the wide card style
)

@Immutable
data class AchievementUnlock(
    val title: String,
    val description: String,
)

@Immutable
data class ExamResultsUiState(
    val accuracyPercent: Int,
    val xpEarned: Int,
    val tagline: String,                // e.g. "Mathematics Master status is within reach!"
    val achievement: AchievementUnlock?,
    val topics: List<TopicMastery>,     // 1 primary + N secondary
    val correctCount: Int,
    val timeLabel: String,              // e.g. "45m"
    val leveledUp: Boolean,
)

// -------- Stateful entry --------

@Composable
fun ExamResultsScreen(
    state: ExamResultsUiState,
    onClose: () -> Unit,
    onShare: () -> Unit,
    onAchievementClick: () -> Unit,
    onClaimRewards: () -> Unit,
    onTryAgain: () -> Unit,
) {
    ExamResultsContent(
        state = state,
        onClose = onClose,
        onShare = onShare,
        onAchievementClick = onAchievementClick,
        onClaimRewards = onClaimRewards,
        onTryAgain = onTryAgain,
    )
}

// -------- Stateless content --------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExamResultsContent(
    state: ExamResultsUiState,
    onClose: () -> Unit,
    onShare: () -> Unit,
    onAchievementClick: () -> Unit,
    onClaimRewards: () -> Unit,
    onTryAgain: () -> Unit,
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
                    IconButton(onClick = onClose) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onShare) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = "Share",
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
                top = Spacing.lg,
                bottom = Spacing.xxxl,
            ),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg),
        ) {
            item("hero") { LevelUpHero(state = state) }
            if (state.achievement != null) {
                item("achievement") {
                    AchievementCard(achievement = state.achievement, onClick = onAchievementClick)
                }
            }
            item("performance") { PerformanceAnalysis(topics = state.topics) }
            item("mini-stats") {
                MiniStats(correctCount = state.correctCount, timeLabel = state.timeLabel)
            }
            item("actions") {
                PrimaryActions(onClaim = onClaimRewards, onTryAgain = onTryAgain)
            }
        }
    }
}

// -------- Hero --------

@Composable
private fun LevelUpHero(state: ExamResultsUiState) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radius.xl))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.20f),
                        MaterialTheme.colorScheme.surface,
                    ),
                ),
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
                shape = RoundedCornerShape(Radius.xl),
            )
            .padding(vertical = Spacing.xl, horizontal = Spacing.lg),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            Box(contentAlignment = Alignment.Center) {
                ProgressRing(
                    progress = state.accuracyPercent / 100f,
                    size = 192.dp,
                    strokeWidth = 12.dp,
                    centerLabel = "${state.accuracyPercent}%",
                    trackColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    progressColor = MaterialTheme.colorScheme.secondary,
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 56.dp),
                ) {
                    Text(
                        text = "ACCURACY",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            if (state.leveledUp) {
                Text(
                    text = "LEVEL UP!",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Black,
                )
            }
            Row(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondary)
                    .padding(horizontal = Spacing.md, vertical = Spacing.xs),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
            ) {
                Icon(
                    Icons.Default.Bolt,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.size(16.dp),
                )
                Text(
                    text = "+${state.xpEarned} XP EARNED",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSecondary,
                    fontWeight = FontWeight.Bold,
                )
            }
            Text(
                text = state.tagline,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

// -------- Achievement --------

@Composable
private fun AchievementCard(achievement: AchievementUnlock, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radius.lg))
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.30f),
                shape = RoundedCornerShape(Radius.lg),
            )
            .padding(Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Default.WorkspacePremium,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(40.dp),
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = "NEW ACHIEVEMENT",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = achievement.title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = achievement.description,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.30f))
                .size(36.dp),
        ) {
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = "View",
                tint = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

// -------- Performance analysis --------

@Composable
private fun PerformanceAnalysis(topics: List<TopicMastery>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radius.lg))
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.30f),
                shape = RoundedCornerShape(Radius.lg),
            )
            .padding(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            Icon(
                Icons.Default.Analytics,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = "Performance Analysis",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold,
            )
        }
        val primary = topics.firstOrNull { it.isPrimary } ?: topics.firstOrNull()
        val rest = topics.filter { it != primary }
        if (primary != null) {
            PrimaryTopicCard(topic = primary)
        }
        if (rest.isNotEmpty()) {
            // 2-up grid manually
            rest.chunked(2).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                ) {
                    row.forEach { t ->
                        Box(modifier = Modifier.weight(1f)) {
                            SecondaryTopicCard(topic = t)
                        }
                    }
                    if (row.size == 1) Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun PrimaryTopicCard(topic: TopicMastery) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radius.md))
            .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.20f))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.10f),
                shape = RoundedCornerShape(Radius.md),
            )
            .padding(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text(
                    text = topic.name.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = "Mastery",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Bold,
                )
            }
            Text(
                text = "${topic.percent}%",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.secondary,
            )
        }
        LinearProgressIndicator(
            progress = { topic.percent / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape),
            color = MaterialTheme.colorScheme.secondary,
            trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
        )
    }
}

@Composable
private fun SecondaryTopicCard(topic: TopicMastery) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radius.md))
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.10f))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
                shape = RoundedCornerShape(Radius.md),
            )
            .padding(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = topic.name.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "${topic.percent}%",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
            )
        }
        LinearProgressIndicator(
            progress = { topic.percent / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
        )
    }
}

// -------- Mini stats --------

@Composable
private fun MiniStats(correctCount: Int, timeLabel: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        MiniStatTile(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Verified,
            iconTint = MaterialTheme.colorScheme.secondary,
            value = correctCount.toString(),
            label = "Correct",
        )
        MiniStatTile(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Timer,
            iconTint = MaterialTheme.colorScheme.primary,
            value = timeLabel,
            label = "Time",
        )
    }
}

@Composable
private fun MiniStatTile(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: androidx.compose.ui.graphics.Color,
    value: String,
    label: String,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(Radius.lg))
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.30f),
                shape = RoundedCornerShape(Radius.lg),
            )
            .padding(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg),
    ) {
        Icon(icon, contentDescription = null, tint = iconTint)
        Column {
            Text(
                text = value,
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

// -------- Actions --------

@Composable
private fun PrimaryActions(onClaim: () -> Unit, onTryAgain: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
        Button(
            onClick = onClaim,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(Radius.lg),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
        ) {
            Icon(Icons.Default.Redeem, contentDescription = null)
            Spacer(Modifier.size(Spacing.sm))
            Text(
                text = "Claim Rewards & Review",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
            )
        }
        OutlinedButton(
            onClick = onTryAgain,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(Radius.lg),
        ) {
            Icon(Icons.Default.Refresh, contentDescription = null)
            Spacer(Modifier.size(Spacing.sm))
            Text(
                text = "Try Again",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

// -------- Preview --------

@Preview(showBackground = true, heightDp = 1500)
@Composable
private fun ExamResultsPreview() {
    ESECTheme {
        ExamResultsContent(
            state = ExamResultsUiState(
                accuracyPercent = 84,
                xpEarned = 1240,
                tagline = "Mathematics Master status is within reach!",
                achievement = AchievementUnlock(
                    title = "Golden Wreath",
                    description = "Top 5% Algebra Score",
                ),
                topics = listOf(
                    TopicMastery("Algebra", 92, isPrimary = true),
                    TopicMastery("Geometry", 75),
                    TopicMastery("Calculus", 60),
                ),
                correctCount = 42,
                timeLabel = "45m",
                leveledUp = true,
            ),
            onClose = {},
            onShare = {},
            onAchievementClick = {},
            onClaimRewards = {},
            onTryAgain = {},
        )
    }
}
