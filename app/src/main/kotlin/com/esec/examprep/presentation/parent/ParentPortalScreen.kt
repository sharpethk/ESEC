/**
 * Maps to /stitch_erixam_exam_companion/parent_portal/code.html
 * Design tokens from /stitch_erixam_exam_companion/erixam_design_system/DESIGN.md.
 *
 * Parent Dashboard monitoring a single child: 3-up KPI bento,
 * "Send Motivation" primary action, 7-day bar chart, mastery
 * radar mini-bento, reward center with progress, and an
 * "Investing in Excellence" image promo.
 */
package com.esec.examprep.presentation.parent

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.esec.examprep.presentation.theme.ESECTheme
import com.esec.examprep.presentation.theme.Radius
import com.esec.examprep.presentation.theme.Spacing
import kotlin.math.cos
import kotlin.math.sin

// -------- UI Models --------

@Immutable
data class ParentKpi(
    val label: String,
    val value: String,
    val delta: String,
)

@Immutable
data class StudyBar(
    val dayLabel: String,
    val heightFraction: Float, // 0f..1f
    val tone: BarTone,
)

enum class BarTone { Muted, Soft, Primary }

@Immutable
data class MasteryAxis(
    val name: String,
    val percent: Int,
    val tone: MasteryTone,
)

enum class MasteryTone { High, Mid, Low }

@Immutable
data class RewardItem(
    val id: String,
    val title: String,
    val goal: String,
    val progressLabel: String,
    val progressFraction: Float, // 0f..1f
    val icon: ImageVector,
    val tone: RewardTone,
)

enum class RewardTone { Secondary, Primary }

@Immutable
data class ParentBanner(
    val headline: String,
    val tagline: String,
    val imageUrl: String? = null,
)

@Immutable
data class ParentPortalUiState(
    val childName: String,
    val kpis: List<ParentKpi>, // 3
    val studyBars: List<StudyBar>, // 7
    val masteryAxes: List<MasteryAxis>,
    val rewards: List<RewardItem>,
    val banner: ParentBanner,
)

// -------- Stateful entry --------

@Composable
fun ParentPortalScreen(
    state: ParentPortalUiState,
    onMenuClick: () -> Unit,
    onSearchClick: () -> Unit,
    onSendMotivation: () -> Unit,
    onAddReward: () -> Unit,
    onRewardClick: (RewardItem) -> Unit,
) {
    ParentPortalContent(
        state = state,
        onMenuClick = onMenuClick,
        onSearchClick = onSearchClick,
        onSendMotivation = onSendMotivation,
        onAddReward = onAddReward,
        onRewardClick = onRewardClick,
    )
}

// -------- Stateless content --------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ParentPortalContent(
    state: ParentPortalUiState,
    onMenuClick: () -> Unit,
    onSearchClick: () -> Unit,
    onSendMotivation: () -> Unit,
    onAddReward: () -> Unit,
    onRewardClick: (RewardItem) -> Unit,
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
            item("title") { TitleBlock(childName = state.childName) }
            item("kpis") { KpiRow(kpis = state.kpis) }
            item("send-motivation") { SendMotivationButton(onClick = onSendMotivation) }
            item("activity") { StudyActivityCard(bars = state.studyBars) }
            item("radar") { MasteryRadarCard(axes = state.masteryAxes) }
            item("reward-header") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "Reward Center",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    TextButton(onClick = onAddReward) {
                        Icon(
                            Icons.Default.AddCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                        Spacer(Modifier.size(4.dp))
                        Text(
                            text = "Add",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
            items(
                count = state.rewards.size,
                key = { state.rewards[it].id },
            ) { idx ->
                val r = state.rewards[idx]
                RewardRow(reward = r, onClick = { onRewardClick(r) })
            }
            item("banner") { AcademicBanner(banner = state.banner) }
        }
    }
}

// -------- Title --------

@Composable
private fun TitleBlock(childName: String) {
    Column {
        Text(
            text = "Parent Dashboard",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = buildAnnotatedString {
                append("Monitoring ")
                withStyle(
                    SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                    ),
                ) { append(childName) }
                append("'s Progress")
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

// -------- KPI row --------

@Composable
private fun KpiRow(kpis: List<ParentKpi>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        kpis.forEach { k ->
            KpiTile(kpi = k, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun KpiTile(kpi: ParentKpi, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(Radius.md))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(Spacing.md),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(
            text = kpi.label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = kpi.value,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = kpi.delta,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.secondary,
            fontWeight = FontWeight.Bold,
        )
    }
}

// -------- Send motivation --------

@Composable
private fun SendMotivationButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ),
    ) {
        Icon(Icons.Default.Send, contentDescription = null)
        Spacer(Modifier.size(Spacing.sm))
        Text(
            text = "Send Motivation",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

// -------- Study activity card --------

@Composable
private fun StudyActivityCard(bars: List<StudyBar>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radius.md))
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(Radius.md),
            )
            .padding(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "Study Activity",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = "Last 7 Days",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(128.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            bars.forEach { b ->
                val color = when (b.tone) {
                    BarTone.Muted -> MaterialTheme.colorScheme.surfaceContainerHigh
                    BarTone.Soft -> MaterialTheme.colorScheme.primaryContainer
                    BarTone.Primary -> MaterialTheme.colorScheme.primary
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize(),
                    contentAlignment = Alignment.BottomCenter,
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(b.heightFraction.coerceIn(0.02f, 1f))
                            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                            .background(color),
                    )
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            bars.forEach { b ->
                Text(
                    text = b.dayLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                )
            }
        }
    }
}

// -------- Mastery radar --------

@Composable
private fun MasteryRadarCard(axes: List<MasteryAxis>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radius.md))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            Text(
                text = "Mastery Radar",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            axes.forEach { a ->
                val (dotColor, textColor) = when (a.tone) {
                    MasteryTone.High -> MaterialTheme.colorScheme.secondary to
                        MaterialTheme.colorScheme.onSurface
                    MasteryTone.Mid -> MaterialTheme.colorScheme.primaryContainer to
                        MaterialTheme.colorScheme.onSurface
                    MasteryTone.Low -> MaterialTheme.colorScheme.tertiaryContainer to
                        MaterialTheme.colorScheme.onSurfaceVariant
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(dotColor),
                    )
                    Text(
                        text = "${a.name} (${a.percent}%)",
                        style = MaterialTheme.typography.labelSmall,
                        color = textColor,
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .aspectRatio(1f),
            contentAlignment = Alignment.Center,
        ) {
            RadarMini(axes = axes)
            Icon(
                Icons.Default.Stars,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp),
            )
        }
    }
}

@Composable
private fun RadarMini(axes: List<MasteryAxis>) {
    val grid = MaterialTheme.colorScheme.outlineVariant
    val fill = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.60f)
    Canvas(modifier = Modifier.fillMaxSize()) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val radius = (size.minDimension / 2f) * 0.9f
        val n = axes.size.coerceAtLeast(3)
        fun ring(scale: Float): Path {
            val p = Path()
            for (i in 0 until n) {
                val angle = (-Math.PI / 2 + 2 * Math.PI * i / n).toFloat()
                val x = cx + radius * scale * cos(angle)
                val y = cy + radius * scale * sin(angle)
                if (i == 0) p.moveTo(x, y) else p.lineTo(x, y)
            }
            p.close()
            return p
        }
        // grid rings
        drawPath(ring(0.4f), color = grid.copy(alpha = 0.4f), style = Stroke(width = 1f))
        drawPath(ring(0.7f), color = grid.copy(alpha = 0.4f), style = Stroke(width = 1f))
        drawPath(ring(1.0f), color = grid.copy(alpha = 0.4f), style = Stroke(width = 1f))
        // data poly
        val data = Path()
        axes.forEachIndexed { i, a ->
            val angle = (-Math.PI / 2 + 2 * Math.PI * i / n).toFloat()
            val r = radius * (a.percent / 100f).coerceIn(0f, 1f)
            val x = cx + r * cos(angle)
            val y = cy + r * sin(angle)
            if (i == 0) data.moveTo(x, y) else data.lineTo(x, y)
        }
        data.close()
        drawPath(data, color = fill)
    }
}

// -------- Reward row --------

@Composable
private fun RewardRow(reward: RewardItem, onClick: () -> Unit) {
    val accent = when (reward.tone) {
        RewardTone.Secondary -> MaterialTheme.colorScheme.secondary
        RewardTone.Primary -> MaterialTheme.colorScheme.primary
    }
    val accentContainer = when (reward.tone) {
        RewardTone.Secondary -> MaterialTheme.colorScheme.secondaryContainer
        RewardTone.Primary -> MaterialTheme.colorScheme.primaryContainer
    }
    val onAccentContainer = when (reward.tone) {
        RewardTone.Secondary -> MaterialTheme.colorScheme.onSecondaryContainer
        RewardTone.Primary -> MaterialTheme.colorScheme.onPrimaryContainer
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radius.md))
            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            .padding(Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        // accent stripe
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(56.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(accent),
        )
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(Radius.sm))
                .background(accentContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(reward.icon, contentDescription = null, tint = onAccentContainer)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = reward.title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = reward.goal,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = reward.progressLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = accent,
                    fontWeight = FontWeight.Bold,
                )
            }
            LinearProgressIndicator(
                progress = { reward.progressFraction.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape)
                    .padding(top = 4.dp),
                color = accent,
                trackColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            )
        }
    }
}

// -------- Academic banner --------

@Composable
private fun AcademicBanner(banner: ParentBanner) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(192.dp)
            .clip(RoundedCornerShape(Radius.lg))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh),
    ) {
        if (banner.imageUrl != null) {
            AsyncImage(
                model = banner.imageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.70f),
                        ),
                    ),
                ),
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(Spacing.md),
        ) {
            Text(
                text = banner.headline,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = banner.tagline,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.80f),
            )
        }
    }
}

// -------- Preview --------

@Preview(showBackground = true, heightDp = 1700)
@Composable
private fun ParentPortalPreview() {
    ESECTheme {
        ParentPortalContent(
            state = ParentPortalUiState(
                childName = "Nahom",
                kpis = listOf(
                    ParentKpi("Time Studied", "12.4h", "+15%"),
                    ParentKpi("Accuracy", "82%", "Stable"),
                    ParentKpi("Quests", "24", "Mastery"),
                ),
                studyBars = listOf(
                    StudyBar("Mon", 0.40f, BarTone.Muted),
                    StudyBar("Tue", 0.75f, BarTone.Soft),
                    StudyBar("Wed", 0.50f, BarTone.Muted),
                    StudyBar("Thu", 0.90f, BarTone.Soft),
                    StudyBar("Fri", 0.65f, BarTone.Soft),
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
                        id = "trip",
                        title = "Weekend Trip to Massawa",
                        goal = "Goal: Level 15 Mastery",
                        progressLabel = "75% Complete",
                        progressFraction = 0.75f,
                        icon = Icons.Default.FlightTakeoff,
                        tone = RewardTone.Secondary,
                    ),
                    RewardItem(
                        id = "gaming",
                        title = "Extra Gaming Hour",
                        goal = "Goal: 5 Study Streak",
                        progressLabel = "2/5 Days",
                        progressFraction = 0.40f,
                        icon = Icons.Default.SportsEsports,
                        tone = RewardTone.Primary,
                    ),
                ),
                banner = ParentBanner(
                    headline = "Investing in Excellence",
                    tagline = "Consistency is the bridge between goals and accomplishment.",
                ),
            ),
            onMenuClick = {},
            onSearchClick = {},
            onSendMotivation = {},
            onAddReward = {},
            onRewardClick = {},
        )
    }
}
