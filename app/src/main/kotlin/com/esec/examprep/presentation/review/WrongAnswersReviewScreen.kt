/**
 * Maps to /stitch_erixam_exam_companion/wrong_answers_review/code.html
 * Design tokens from /stitch_erixam_exam_companion/erixam_design_system/DESIGN.md.
 *
 * "Improvement Hub" with mistake count hero, subject + year
 * filters, a review pool of cards (topic mastery progress +
 * Re-attempt / View Explanation), a dashed Mastery Tip, an
 * asymmetric featured action bento and a Magic Wand FAB.
 */
package com.esec.examprep.presentation.review

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.esec.examprep.presentation.theme.ESECTheme
import com.esec.examprep.presentation.theme.Radius
import com.esec.examprep.presentation.theme.Spacing

// -------- UI Models --------

@Immutable
data class SubjectFilterChip(
    val id: String,
    val label: String,
    val icon: ImageVector,
)

@Immutable
data class WrongAnswerCard(
    val id: String,
    val subjectAndYear: String,    // "Mathematics • 2023"
    val topic: String,             // "Topic: Quadratic Equations"
    val masteryPercent: Int,
    val snippet: String,
)

@Immutable
data class FeaturedAction(
    val id: String,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val accent: ActionAccent,
)

enum class ActionAccent { Secondary, Surface }

@Immutable
data class WrongAnswersUiState(
    val mistakesCount: Int,
    val subjectChips: List<SubjectFilterChip>,
    val activeSubjectId: String,
    val examYears: List<Int>,
    val activeYear: Int,
    val cards: List<WrongAnswerCard>,
    val masteryTip: String,
    val featuredActions: List<FeaturedAction>,
)

// -------- Stateful entry --------

@Composable
fun WrongAnswersReviewScreen(
    state: WrongAnswersUiState,
    onMenuClick: () -> Unit,
    onSearchClick: () -> Unit,
    onSubjectChipClick: (SubjectFilterChip) -> Unit,
    onYearClick: (Int) -> Unit,
    onReattempt: (WrongAnswerCard) -> Unit,
    onViewExplanation: (WrongAnswerCard) -> Unit,
    onFeaturedActionClick: (FeaturedAction) -> Unit,
    onMagicFabClick: () -> Unit,
) {
    WrongAnswersReviewContent(
        state = state,
        onMenuClick = onMenuClick,
        onSearchClick = onSearchClick,
        onSubjectChipClick = onSubjectChipClick,
        onYearClick = onYearClick,
        onReattempt = onReattempt,
        onViewExplanation = onViewExplanation,
        onFeaturedActionClick = onFeaturedActionClick,
        onMagicFabClick = onMagicFabClick,
    )
}

// -------- Stateless content --------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WrongAnswersReviewContent(
    state: WrongAnswersUiState,
    onMenuClick: () -> Unit,
    onSearchClick: () -> Unit,
    onSubjectChipClick: (SubjectFilterChip) -> Unit,
    onYearClick: (Int) -> Unit,
    onReattempt: (WrongAnswerCard) -> Unit,
    onViewExplanation: (WrongAnswerCard) -> Unit,
    onFeaturedActionClick: (FeaturedAction) -> Unit,
    onMagicFabClick: () -> Unit,
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
        floatingActionButton = {
            FloatingActionButton(
                onClick = onMagicFabClick,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                shape = RoundedCornerShape(Radius.lg),
            ) {
                Icon(Icons.Default.AutoFixHigh, contentDescription = "Auto-fix")
            }
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
                bottom = Spacing.xxxl + Spacing.xxl,
            ),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg),
        ) {
            item("hero") { ImprovementHero(count = state.mistakesCount) }
            item("subject-filter") {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    Text(
                        text = "Filter by Subject",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    SubjectChipsRow(
                        chips = state.subjectChips,
                        activeId = state.activeSubjectId,
                        onClick = onSubjectChipClick,
                    )
                }
            }
            item("year-filter") {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    Text(
                        text = "Exam Year",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    YearRow(
                        years = state.examYears,
                        active = state.activeYear,
                        onClick = onYearClick,
                    )
                }
            }
            item("list-header") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "Review Pool",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = "Sorted by: Recent",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline,
                    )
                }
            }
            items(state.cards, key = { it.id }) { c ->
                ReviewCard(
                    card = c,
                    onReattempt = { onReattempt(c) },
                    onViewExplanation = { onViewExplanation(c) },
                )
            }
            item("tip") { MasteryTipCard(tip = state.masteryTip) }
            item("featured") {
                FeaturedActionBento(
                    actions = state.featuredActions,
                    onClick = onFeaturedActionClick,
                )
            }
        }
    }
}

// -------- Improvement Hero --------

@Composable
private fun ImprovementHero(count: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radius.md))
            .background(MaterialTheme.colorScheme.primary)
            .padding(Spacing.lg),
    ) {
        Icon(
            Icons.Default.MilitaryTech,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.10f),
            modifier = Modifier
                .size(120.dp)
                .align(Alignment.BottomEnd),
        )
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
            Text(
                text = "Improvement Hub",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "Turn your weaknesses into strengths!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.90f),
            )
            Spacer(Modifier.size(Spacing.sm))
            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                Text(
                    text = count.toString(),
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "Mistakes to Conquer",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(bottom = 6.dp),
                )
            }
        }
    }
}

// -------- Subject chips --------

@Composable
private fun SubjectChipsRow(
    chips: List<SubjectFilterChip>,
    activeId: String,
    onClick: (SubjectFilterChip) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        chips.forEach { chip ->
            val selected = chip.id == activeId
            Row(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(
                        if (selected) MaterialTheme.colorScheme.secondaryContainer
                        else MaterialTheme.colorScheme.surfaceContainer,
                    )
                    .clickable { onClick(chip) }
                    .padding(horizontal = Spacing.md, vertical = Spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Icon(
                    chip.icon,
                    contentDescription = null,
                    tint = if (selected) MaterialTheme.colorScheme.onSecondaryContainer
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp),
                )
                Text(
                    text = chip.label,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (selected) MaterialTheme.colorScheme.onSecondaryContainer
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

// -------- Year filter --------

@Composable
private fun YearRow(years: List<Int>, active: Int, onClick: (Int) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        years.forEach { y ->
            val selected = y == active
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(Radius.sm))
                    .then(
                        if (selected)
                            Modifier.background(MaterialTheme.colorScheme.primary)
                        else
                            Modifier.border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outlineVariant,
                                shape = RoundedCornerShape(Radius.sm),
                            ),
                    )
                    .clickable { onClick(y) }
                    .padding(horizontal = Spacing.md, vertical = Spacing.xs),
            ) {
                Text(
                    text = y.toString(),
                    style = MaterialTheme.typography.labelLarge,
                    color = if (selected) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

// -------- Review card --------

@Composable
private fun ReviewCard(
    card: WrongAnswerCard,
    onReattempt: () -> Unit,
    onViewExplanation: () -> Unit,
) {
    val pct = card.masteryPercent.coerceIn(0, 100)
    val (masteryColor, barColor) = when {
        pct < 40 -> MaterialTheme.colorScheme.tertiary to MaterialTheme.colorScheme.tertiary
        pct < 75 -> MaterialTheme.colorScheme.secondary to MaterialTheme.colorScheme.secondaryContainer
        else -> MaterialTheme.colorScheme.secondary to MaterialTheme.colorScheme.secondary
    }
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
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = card.subjectAndYear.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = card.topic,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Mastery",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = "$pct%",
                    style = MaterialTheme.typography.titleLarge,
                    color = masteryColor,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        LinearProgressIndicator(
            progress = { pct / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape),
            color = barColor,
            trackColor = MaterialTheme.colorScheme.surfaceContainer,
        )
        Text(
            text = "\"${card.snippet}\"",
            style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            Button(
                onClick = onReattempt,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(Radius.sm),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            ) {
                Text(
                    "Re-attempt",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            OutlinedButton(
                onClick = onViewExplanation,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(Radius.sm),
            ) {
                Text(
                    "View Explanation",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

// -------- Mastery Tip --------

@Composable
private fun MasteryTipCard(tip: String) {
    val border = MaterialTheme.colorScheme.primaryContainer
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radius.md))
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.20f))
            .drawDashedBorder(border, Radius.md)
            .padding(Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(Radius.sm))
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Default.Lightbulb,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
        Column {
            Text(
                text = "Mastery Tip",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = tip,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.80f),
            )
        }
    }
}

private fun Modifier.drawDashedBorder(
    color: Color,
    radius: androidx.compose.ui.unit.Dp,
): Modifier = this.drawBehind {
    val strokeWidthPx = 2.dp.toPx()
    val cornerRadiusPx = radius.toPx()
    drawRoundRect(
        color = color,
        style = Stroke(
            width = strokeWidthPx,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f),
        ),
        cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx),
    )
}

// -------- Featured action bento --------

@Composable
private fun FeaturedActionBento(
    actions: List<FeaturedAction>,
    onClick: (FeaturedAction) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        actions.forEach { action ->
            val (bg, fg) = when (action.accent) {
                ActionAccent.Secondary -> MaterialTheme.colorScheme.secondaryContainer to
                    MaterialTheme.colorScheme.onSecondaryContainer
                ActionAccent.Surface -> MaterialTheme.colorScheme.surfaceContainerHighest to
                    MaterialTheme.colorScheme.onSurface
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(Radius.md))
                    .background(bg)
                    .clickable { onClick(action) }
                    .padding(Spacing.md),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Icon(action.icon, contentDescription = null, tint = fg, modifier = Modifier.size(32.dp))
                Column {
                    Text(
                        text = action.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = fg,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = action.subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = fg.copy(alpha = 0.70f),
                    )
                }
            }
        }
    }
}

// -------- Preview --------

@Preview(showBackground = true, heightDp = 1600)
@Composable
private fun WrongAnswersReviewPreview() {
    ESECTheme {
        WrongAnswersReviewContent(
            state = WrongAnswersUiState(
                mistakesCount = 42,
                subjectChips = listOf(
                    SubjectFilterChip("math", "Maths", Icons.Default.Functions),
                    SubjectFilterChip("bio", "Biology", Icons.Default.Science),
                    SubjectFilterChip("hist", "History", Icons.Default.HistoryEdu),
                    SubjectFilterChip("eng", "English", Icons.Default.Language),
                ),
                activeSubjectId = "math",
                examYears = listOf(2023, 2022, 2021, 2020),
                activeYear = 2023,
                cards = listOf(
                    WrongAnswerCard(
                        id = "q1",
                        subjectAndYear = "Mathematics • 2023",
                        topic = "Topic: Quadratic Equations",
                        masteryPercent = 20,
                        snippet = "Find the value of x that satisfies the equation 2x² - 5x + 3 = 0 using the quadratic formula...",
                    ),
                    WrongAnswerCard(
                        id = "q2",
                        subjectAndYear = "Mathematics • 2023",
                        topic = "Topic: Calculus Integrals",
                        masteryPercent = 65,
                        snippet = "Evaluate the definite integral of sin(x) from 0 to π/2...",
                    ),
                ),
                masteryTip = "Students who re-attempt mistakes within 24 hours show 4x better retention.",
                featuredActions = listOf(
                    FeaturedAction("fire", "Quick Fire Drill", "10 random mistakes", Icons.Default.Bolt, ActionAccent.Secondary),
                    FeaturedAction("wreath", "Earn Wreaths", "Master a full topic", Icons.Default.EmojiEvents, ActionAccent.Surface),
                ),
            ),
            onMenuClick = {},
            onSearchClick = {},
            onSubjectChipClick = {},
            onYearClick = {},
            onReattempt = {},
            onViewExplanation = {},
            onFeaturedActionClick = {},
            onMagicFabClick = {},
        )
    }
}
