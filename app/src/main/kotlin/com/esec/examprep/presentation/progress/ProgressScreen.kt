/**
 * Maps to /stitch_erixam_exam_companion/progress_gamified_style/code.html
 * Design tokens from /stitch_erixam_exam_companion/erixam_design_system/DESIGN.md.
 *
 * "My Journey" progress dashboard: Mastery Radar (custom Canvas pentagon
 * with axis labels), bento grid (Streak with 7-day strip + Total XP with
 * progress bar), horizontally scrolling Badges row, and "You're on Fire!"
 * primary illustration card.
 */
package com.esec.examprep.presentation.progress

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.esec.examprep.presentation.theme.ESECTheme
import com.esec.examprep.presentation.theme.Radius
import com.esec.examprep.presentation.theme.Spacing
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

// -------- UI Models --------

@Immutable
data class RadarAxis(val label: String, val value: Float) // value in 0f..1f

enum class BadgeTone { Secondary, Primary, Tertiary, Locked }

@Immutable
data class JourneyBadge(
    val name: String,
    val icon: ImageVector,
    val tone: BadgeTone,
)

@Immutable
data class ProgressUiState(
    val level: Int,
    val radar: List<RadarAxis>,         // 4 or 5 axes
    val radarInsight: String,
    val streakDays: Int,
    val weekDayLabels: List<String>,    // 7 labels e.g. ["M","T","W","T","F","S","S"]
    val weekDoneCount: Int,             // first N days are done
    val totalXp: Int,
    val xpToNextLevel: Int,
    val xpEarnedTowardNextLevel: Int,
    val nextLevelLabel: String,         // e.g. "LVL 13"
    val badges: List<JourneyBadge>,
    val milestoneTitle: String,
    val milestoneBody: String,
)

// -------- Stateful entry --------

@Composable
fun ProgressScreen(
    state: ProgressUiState,
    onMenuClick: () -> Unit,
    onViewAllBadges: () -> Unit,
) {
    ProgressContent(
        state = state,
        onMenuClick = onMenuClick,
        onViewAllBadges = onViewAllBadges,
    )
}

// -------- Stateless content --------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProgressContent(
    state: ProgressUiState,
    onMenuClick: () -> Unit,
    onViewAllBadges: () -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Journey",
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
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                            .padding(horizontal = Spacing.md, vertical = 4.dp),
                    ) {
                        Text(
                            text = "LVL ${state.level}",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Spacer(Modifier.size(Spacing.sm))
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
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            item("radar") {
                MasteryRadarCard(axes = state.radar, insight = state.radarInsight)
            }
            item("streak-xp") {
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                    Box(modifier = Modifier.weight(1f)) {
                        StreakCard(
                            days = state.streakDays,
                            labels = state.weekDayLabels,
                            doneCount = state.weekDoneCount,
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        TotalXpCard(
                            totalXp = state.totalXp,
                            nextLevelLabel = state.nextLevelLabel,
                            xpToNext = state.xpToNextLevel - state.xpEarnedTowardNextLevel,
                            progress = state.xpEarnedTowardNextLevel.toFloat() /
                                state.xpToNextLevel.coerceAtLeast(1).toFloat(),
                        )
                    }
                }
            }
            item("badges") {
                BadgesCard(badges = state.badges, onViewAll = onViewAllBadges)
            }
            item("milestone") {
                MilestoneCard(title = state.milestoneTitle, body = state.milestoneBody)
            }
        }
    }
}

// -------- Mastery Radar --------

@Composable
private fun MasteryRadarCard(axes: List<RadarAxis>, insight: String) {
    BentoCardOutlined {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "Mastery Radar",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Icon(
                Icons.Default.Insights,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
        }
        Spacer(Modifier.size(Spacing.md))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(192.dp),
            contentAlignment = Alignment.Center,
        ) {
            RadarChart(axes = axes)
            // Axis labels overlay using approximate positions
            // top, right, bottom, left for 4-axis, else evenly placed
            val n = axes.size
            axes.forEachIndexed { index, axis ->
                val angle = (-Math.PI / 2.0 + 2 * Math.PI * index / n)
                val xFraction = 0.5f + 0.46f * cos(angle).toFloat()
                val yFraction = 0.5f + 0.46f * sin(angle).toFloat()
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.TopStart,
                ) {
                    Text(
                        text = axis.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier
                            .align(BiasAlignment(xFraction, yFraction)),
                    )
                }
            }
        }
        Spacer(Modifier.size(Spacing.sm))
        Text(
            text = insight,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth(),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
    }
}

@Composable
private fun RadarChart(axes: List<RadarAxis>) {
    val fill = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.40f)
    val stroke = MaterialTheme.colorScheme.primaryContainer
    val grid = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.30f)
    Canvas(
        modifier = Modifier.size(160.dp),
    ) {
        val n = axes.size
        if (n < 3) return@Canvas
        val cx = size.width / 2f
        val cy = size.height / 2f
        val maxR = min(cx, cy) - 4f

        // Grid: concentric polygons at 0.25, 0.5, 0.75, 1.0
        listOf(0.25f, 0.5f, 0.75f, 1.0f).forEach { r ->
            val path = Path()
            for (i in 0 until n) {
                val angle = -Math.PI / 2.0 + 2 * Math.PI * i / n
                val x = cx + r * maxR * cos(angle).toFloat()
                val y = cy + r * maxR * sin(angle).toFloat()
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            path.close()
            drawPath(path = path, color = grid, style = Stroke(width = 1f))
        }

        // Data polygon
        val data = Path()
        for (i in 0 until n) {
            val angle = -Math.PI / 2.0 + 2 * Math.PI * i / n
            val r = axes[i].value.coerceIn(0f, 1f) * maxR
            val x = cx + r * cos(angle).toFloat()
            val y = cy + r * sin(angle).toFloat()
            if (i == 0) data.moveTo(x, y) else data.lineTo(x, y)
        }
        data.close()
        drawPath(path = data, color = fill)
        drawPath(path = data, color = stroke, style = Stroke(width = 4f))

        // Center dot
        drawCircle(color = stroke, radius = 3f, center = Offset(cx, cy))
    }
}

// -------- Streak card --------

@Composable
private fun StreakCard(days: Int, labels: List<String>, doneCount: Int) {
    BentoCardOutlined(minHeightDp = 160) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Icon(
                    Icons.Default.LocalFireDepartment,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(20.dp),
                )
                Text(
                    text = "Streak",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            Spacer(Modifier.size(Spacing.xs))
            Text(
                text = "$days Days",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(Modifier.size(Spacing.md))
        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            labels.forEachIndexed { index, label ->
                val isDone = index < doneCount
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(24.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            if (isDone) MaterialTheme.colorScheme.secondaryContainer
                            else MaterialTheme.colorScheme.surfaceContainerHigh,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isDone) MaterialTheme.colorScheme.onSecondaryContainer
                        else MaterialTheme.colorScheme.outline,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

// -------- Total XP card --------

@Composable
private fun TotalXpCard(
    totalXp: Int,
    nextLevelLabel: String,
    xpToNext: Int,
    progress: Float,
) {
    BentoCardOutlined(minHeightDp = 160) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp),
                )
                Text(
                    text = "Total XP",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            Spacer(Modifier.size(Spacing.xs))
            Text(
                text = "%,d".format(totalXp),
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(Modifier.size(Spacing.md))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "To $nextLevelLabel",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "$xpToNext XP",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(Modifier.size(4.dp))
        LinearProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape),
            color = MaterialTheme.colorScheme.primaryContainer,
            trackColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        )
    }
}

// -------- Badges --------

@Composable
private fun BadgesCard(badges: List<JourneyBadge>, onViewAll: () -> Unit) {
    BentoCardOutlined {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "Badges Earned",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            TextButton(onClick = onViewAll) {
                Text(
                    text = "View All",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
        Spacer(Modifier.size(Spacing.sm))
        val scroll = rememberScrollState()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scroll),
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            badges.forEach { badge -> BadgeChip(badge = badge) }
        }
    }
}

@Composable
private fun BadgeChip(badge: JourneyBadge) {
    val (container, content) = when (badge.tone) {
        BadgeTone.Secondary -> MaterialTheme.colorScheme.secondaryContainer to
            MaterialTheme.colorScheme.onSecondaryContainer
        BadgeTone.Primary -> MaterialTheme.colorScheme.primaryContainer to
            MaterialTheme.colorScheme.onPrimary
        BadgeTone.Tertiary -> MaterialTheme.colorScheme.tertiaryContainer to
            MaterialTheme.colorScheme.onTertiaryContainer
        BadgeTone.Locked -> MaterialTheme.colorScheme.surfaceContainerHigh to
            MaterialTheme.colorScheme.outline
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(container),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = badge.icon,
                contentDescription = badge.name,
                tint = content,
                modifier = Modifier.size(32.dp),
            )
        }
        Spacer(Modifier.size(4.dp))
        Text(
            text = badge.name,
            style = MaterialTheme.typography.labelSmall,
            color = if (badge.tone == BadgeTone.Locked) MaterialTheme.colorScheme.outline
            else MaterialTheme.colorScheme.onSurface,
        )
    }
}

// -------- Milestone --------

@Composable
private fun MilestoneCard(title: String, body: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(RoundedCornerShape(Radius.lg))
            .background(MaterialTheme.colorScheme.primary)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(Radius.lg),
            ),
    ) {
        Icon(
            Icons.Default.MilitaryTech,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.20f),
            modifier = Modifier
                .size(160.dp)
                .align(Alignment.BottomEnd),
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.lg),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.size(Spacing.xs))
            Text(
                text = body,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.90f),
                modifier = Modifier.fillMaxWidth(0.6f),
            )
        }
    }
}

// -------- Bento surface helper --------

@Composable
private fun BentoCardOutlined(
    minHeightDp: Int = 0,
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .let { if (minHeightDp > 0) it.height(minHeightDp.dp) else it }
            .clip(RoundedCornerShape(Radius.lg))
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.30f),
                shape = RoundedCornerShape(Radius.lg),
            )
            .padding(Spacing.md),
        content = content,
    )
}

// Simple alignment used to scatter radar labels around the chart.
private data class BiasAlignment(val xFraction: Float, val yFraction: Float) : Alignment {
    override fun align(
        size: androidx.compose.ui.unit.IntSize,
        space: androidx.compose.ui.unit.IntSize,
        layoutDirection: androidx.compose.ui.unit.LayoutDirection,
    ): androidx.compose.ui.unit.IntOffset {
        val x = ((space.width - size.width) * xFraction).toInt()
        val y = ((space.height - size.height) * yFraction).toInt()
        return androidx.compose.ui.unit.IntOffset(x, y)
    }
}

// -------- Preview --------

@Preview(showBackground = true, heightDp = 1300)
@Composable
private fun ProgressPreview() {
    ESECTheme {
        ProgressContent(
            state = ProgressUiState(
                level = 12,
                radar = listOf(
                    RadarAxis("Math", 0.55f),
                    RadarAxis("Science", 0.85f),
                    RadarAxis("Geography", 0.45f),
                    RadarAxis("History", 0.65f),
                ),
                radarInsight = "Your English skills are peaking! Focus on Mathematics next.",
                streakDays = 5,
                weekDayLabels = listOf("M", "T", "W", "T", "F", "S", "S"),
                weekDoneCount = 5,
                totalXp = 14_250,
                xpToNextLevel = 5_000,
                xpEarnedTowardNextLevel = 4_250,
                nextLevelLabel = "LVL 13",
                badges = listOf(
                    JourneyBadge("Early Bird", Icons.Default.WbSunny, BadgeTone.Secondary),
                    JourneyBadge("Math Whiz", Icons.Default.Functions, BadgeTone.Primary),
                    JourneyBadge("Streak Master", Icons.Default.AutoAwesome, BadgeTone.Tertiary),
                    JourneyBadge("Finalist", Icons.Default.EmojiEvents, BadgeTone.Locked),
                ),
                milestoneTitle = "You're on Fire!",
                milestoneBody = "Completed 24 mock exams this week. Keep the momentum going!",
            ),
            onMenuClick = {},
            onViewAllBadges = {},
        )
    }
}

@Suppress("unused")
private val _stableImports = listOf<Any>(Icons.AutoMirrored.Filled.MenuBook, Color.Transparent)
