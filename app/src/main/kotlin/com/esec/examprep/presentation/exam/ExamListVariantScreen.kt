/**
 * Maps to /stitch_erixam_exam_companion/exam_list_gamified_variant/code.html
 * Design tokens from /stitch_erixam_exam_companion/erixam_design_system/DESIGN.md.
 *
 * Variant of the Quest Log — leads with a "Level Up" progress hero,
 * grade segmented control, mixed bento cards (wide + split 50/50),
 * and a dashed weekly-streak panel.
 */
package com.esec.examprep.presentation.exam

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.esec.examprep.presentation.theme.ESECTheme
import com.esec.examprep.presentation.theme.Radius
import com.esec.examprep.presentation.theme.Spacing

// -------- UI Models --------

enum class VariantCardKind { Completed, Hard, InProgress, Ready }

@Immutable
data class VariantQuest(
    val id: String,
    val title: String,
    val subtitle: String,
    val difficulty: QuestDifficulty,
    val year: Int,
    val kind: VariantCardKind,
    val masteryPercent: Int? = null,  // for Completed
    val xpReward: Int? = null,         // for Hard
    val progressPercent: Int? = null,  // for InProgress
    val playerCountLabel: String? = null, // for Completed
)

@Immutable
data class DayStreak(val dayLabel: String, val state: DayState)

enum class DayState { Done, Today, Future }

@Immutable
data class ExamListVariantUiState(
    val streakCount: Int,
    val activeQuestTitle: String,
    val activeQuestSubtitle: String,
    val xpEarned: Int,
    val xpToNextLevel: Int,
    val searchQuery: String,
    val selectedGrade: String,
    val gradeOptions: List<String>,
    val quests: List<VariantQuest>,
    val weeklyStreak: List<DayStreak>,
    val totalQuestsAvailable: Int,
)

// -------- Stateful entry --------

@Composable
fun ExamListVariantScreen(
    state: ExamListVariantUiState,
    onMenuClick: () -> Unit,
    onSearchClick: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onGradeSelect: (String) -> Unit,
    onClaimDailyBonus: () -> Unit,
    onQuestClick: (VariantQuest) -> Unit,
) {
    ExamListVariantContent(
        state = state,
        onMenuClick = onMenuClick,
        onSearchClick = onSearchClick,
        onSearchQueryChange = onSearchQueryChange,
        onGradeSelect = onGradeSelect,
        onClaimDailyBonus = onClaimDailyBonus,
        onQuestClick = onQuestClick,
    )
}

// -------- Stateless content --------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExamListVariantContent(
    state: ExamListVariantUiState,
    onMenuClick: () -> Unit,
    onSearchClick: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onGradeSelect: (String) -> Unit,
    onClaimDailyBonus: () -> Unit,
    onQuestClick: (VariantQuest) -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "EriXam",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                        )
                    }
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
                            .clip(RoundedCornerShape(50))
                            .background(MaterialTheme.colorScheme.tertiaryContainer)
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f),
                                RoundedCornerShape(50),
                            )
                            .padding(horizontal = Spacing.sm, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        Icon(
                            Icons.Default.LocalFireDepartment,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.size(16.dp),
                        )
                        Text(
                            text = state.streakCount.toString(),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
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
                LevelUpHero(
                    title = state.activeQuestTitle,
                    subtitle = state.activeQuestSubtitle,
                    progress = state.xpEarned.toFloat() / state.xpToNextLevel.coerceAtLeast(1).toFloat(),
                    progressLabel = "Progress: ${state.xpEarned} / ${state.xpToNextLevel} XP",
                    onClaim = onClaimDailyBonus,
                )
            }
            item("search-filters") {
                SearchAndGrades(
                    query = state.searchQuery,
                    onQueryChange = onSearchQueryChange,
                    grades = state.gradeOptions,
                    selectedGrade = state.selectedGrade,
                    onGradeSelect = onGradeSelect,
                )
            }
            item("quest-log-header") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                    ) {
                        Icon(
                            Icons.Default.AutoStories,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                        Text(
                            text = "Quest Log",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                    Text(
                        text = "${state.totalQuestsAvailable} QUESTS AVAILABLE",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline,
                    )
                }
            }
            // Render quests using a manual layout so we can place wide + split rows.
            item("quests") {
                QuestBentoGrid(quests = state.quests, onQuestClick = onQuestClick)
            }
            item("weekly-streak") {
                WeeklyStreakPanel(days = state.weeklyStreak)
            }
        }
    }
}

// -------- Hero --------

@Composable
private fun LevelUpHero(
    title: String,
    subtitle: String,
    progress: Float,
    progressLabel: String,
    onClaim: () -> Unit,
) {
    val gradient = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.primary,
        ),
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radius.lg))
            .background(gradient)
            .padding(Spacing.lg),
    ) {
        Icon(
            imageVector = Icons.Default.MilitaryTech,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.20f),
            modifier = Modifier
                .size(120.dp)
                .align(Alignment.TopEnd),
        )
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            Text(
                text = "ACTIVE QUEST",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.20f))
                    .padding(horizontal = Spacing.sm, vertical = 2.dp),
            )
            Text(
                text = title,
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onPrimary,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f),
            )
            Spacer(Modifier.height(Spacing.xs))
            LinearProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(50)),
                color = MaterialTheme.colorScheme.secondaryContainer,
                trackColor = Color.Black.copy(alpha = 0.10f),
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = progressLabel,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
                Button(
                    onClick = onClaim,
                    shape = RoundedCornerShape(Radius.md),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.primary,
                    ),
                ) {
                    Text("Claim Daily Bonus", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

// -------- Search & grade segmented --------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchAndGrades(
    query: String,
    onQueryChange: (String) -> Unit,
    grades: List<String>,
    selectedGrade: String,
    onGradeSelect: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search quests by year or subject...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
            shape = RoundedCornerShape(Radius.md),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            ),
        )
        val scroll = rememberScrollState()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scroll),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            SegmentedPill(grades = grades, selected = selectedGrade, onSelect = onGradeSelect)
            DropdownPill(label = "Year")
            DropdownPill(label = "Type")
        }
    }
}

@Composable
private fun SegmentedPill(
    grades: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(Radius.md))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        grades.forEach { grade ->
            val isActive = grade == selected
            val container = if (isActive) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
            val content = if (isActive) MaterialTheme.colorScheme.onPrimaryContainer
            else MaterialTheme.colorScheme.onSurfaceVariant
            Text(
                text = grade,
                style = MaterialTheme.typography.labelLarge,
                color = content,
                modifier = Modifier
                    .clip(RoundedCornerShape(Radius.sm))
                    .background(container)
                    .clickable { onSelect(grade) }
                    .padding(horizontal = Spacing.md, vertical = Spacing.sm),
            )
        }
    }
}

@Composable
private fun DropdownPill(label: String) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(Radius.md))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Icon(
            Icons.Default.KeyboardArrowDown,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(16.dp),
        )
    }
}

// -------- Bento grid --------

@Composable
private fun QuestBentoGrid(
    quests: List<VariantQuest>,
    onQuestClick: (VariantQuest) -> Unit,
) {
    val wide = quests.filter { it.kind == VariantCardKind.Completed || it.kind == VariantCardKind.Hard }
    val splits = quests.filter { it.kind == VariantCardKind.InProgress || it.kind == VariantCardKind.Ready }

    Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
        wide.forEach { q ->
            when (q.kind) {
                VariantCardKind.Completed -> CompletedWideCard(quest = q, onClick = { onQuestClick(q) })
                VariantCardKind.Hard -> HardWideCard(quest = q, onClick = { onQuestClick(q) })
                else -> Unit
            }
        }
        splits.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                rowItems.forEach { q ->
                    Box(modifier = Modifier.weight(1f)) {
                        SplitCard(quest = q, onClick = { onQuestClick(q) })
                    }
                }
                if (rowItems.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun CompletedWideCard(quest: VariantQuest, onClick: () -> Unit) {
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
            .padding(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                DifficultyChip(quest.difficulty)
                YearChip(quest.year)
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(16.dp),
                )
                Text(
                    text = "Mastery ${quest.masteryPercent ?: 0}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary,
                )
            }
        }
        Column {
            Text(
                text = quest.title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = quest.subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = quest.playerCountLabel ?: "+0 players",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Button(
                onClick = onClick,
                shape = RoundedCornerShape(Radius.md),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f),
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            ) {
                Text("Review Quest", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
private fun HardWideCard(quest: VariantQuest, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clip(RoundedCornerShape(Radius.lg))
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(Radius.lg),
            ),
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.tertiary),
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                    DifficultyChip(quest.difficulty)
                    YearChip(quest.year)
                }
                Text(
                    text = "+${quest.xpReward ?: 0} XP",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .padding(horizontal = Spacing.sm, vertical = 2.dp),
                )
            }
            Column {
                Text(
                    text = quest.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = quest.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Button(
                onClick = onClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(Radius.md),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.size(Spacing.xs))
                Text("Start Quest", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
private fun SplitCard(quest: VariantQuest, onClick: () -> Unit) {
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
            .padding(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        DifficultyChip(quest.difficulty)
        Text(
            text = quest.title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.height(Spacing.sm))
        when (quest.kind) {
            VariantCardKind.InProgress -> {
                Text(
                    text = "STATUS",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline,
                    fontWeight = FontWeight.Bold,
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                ) {
                    LinearProgressIndicator(
                        progress = { (quest.progressPercent ?: 0) / 100f },
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp)
                            .clip(RoundedCornerShape(50)),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        trackColor = MaterialTheme.colorScheme.surfaceContainer,
                    )
                    Text(
                        text = "${quest.progressPercent ?: 0}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
            VariantCardKind.Ready -> {
                Button(
                    onClick = onClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    shape = RoundedCornerShape(Radius.md),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        contentColor = MaterialTheme.colorScheme.primary,
                    ),
                ) {
                    Text("Start", style = MaterialTheme.typography.labelLarge)
                }
            }
            else -> Unit
        }
    }
}

// -------- Weekly streak panel --------

@Composable
private fun WeeklyStreakPanel(days: List<DayStreak>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radius.lg))
            .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.10f))
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.40f),
                shape = RoundedCornerShape(Radius.lg),
            )
            .padding(Spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(50))
                .background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Default.CalendarMonth,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.size(28.dp),
            )
        }
        Text(
            text = "Maintain your Streak",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = "Don't let the fire go out! Complete any quest today to reach an 8-day streak.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(Spacing.xs))
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
            days.forEach { d ->
                DayBead(day = d)
            }
        }
    }
}

@Composable
private fun DayBead(day: DayStreak) {
    val (container, icon, iconTint, labelColor, alpha) = when (day.state) {
        DayState.Done -> Quintuple(
            MaterialTheme.colorScheme.secondaryContainer,
            Icons.Default.Check,
            MaterialTheme.colorScheme.onSecondaryContainer,
            MaterialTheme.colorScheme.outline,
            1f,
        )
        DayState.Today -> Quintuple(
            MaterialTheme.colorScheme.tertiaryContainer,
            Icons.Default.LocalFireDepartment,
            MaterialTheme.colorScheme.onTertiaryContainer,
            MaterialTheme.colorScheme.primary,
            1f,
        )
        DayState.Future -> Quintuple<Color, ImageVector?, Color, Color, Float>(
            MaterialTheme.colorScheme.surfaceContainerHighest,
            null,
            MaterialTheme.colorScheme.onSurfaceVariant,
            MaterialTheme.colorScheme.outline,
            0.30f,
        )
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(50))
                .background(container)
                .padding(2.dp),
            contentAlignment = Alignment.Center,
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
        Spacer(Modifier.height(2.dp))
        Text(
            text = day.dayLabel,
            style = MaterialTheme.typography.labelSmall,
            color = labelColor.copy(alpha = alpha),
            fontWeight = FontWeight.Bold,
        )
    }
}

// Local 5-tuple helper since Kotlin only ships Pair/Triple.
private data class Quintuple<A, B, C, D, E>(
    val a: A, val b: B, val c: C, val d: D, val e: E,
)

// -------- Tiny chips --------

@Composable
private fun DifficultyChip(difficulty: QuestDifficulty) {
    val (container, content, border) = when (difficulty) {
        QuestDifficulty.Easy -> Triple(
            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.20f),
            MaterialTheme.colorScheme.onSecondaryContainer,
            MaterialTheme.colorScheme.secondaryContainer,
        )
        QuestDifficulty.Medium -> Triple(
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.10f),
            MaterialTheme.colorScheme.secondary,
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.30f),
        )
        QuestDifficulty.Hard -> Triple(
            MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.20f),
            MaterialTheme.colorScheme.onTertiaryContainer,
            MaterialTheme.colorScheme.tertiaryContainer,
        )
    }
    Text(
        text = difficulty.name,
        style = MaterialTheme.typography.labelSmall,
        color = content,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(container)
            .border(1.dp, border, RoundedCornerShape(4.dp))
            .padding(horizontal = Spacing.sm, vertical = 2.dp),
    )
}

@Composable
private fun YearChip(year: Int) {
    Text(
        text = year.toString(),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(horizontal = Spacing.sm, vertical = 2.dp),
    )
}

// Compose imports cleanup

// -------- Preview --------

@Preview(showBackground = true, heightDp = 1700)
@Composable
private fun ExamListVariantPreview() {
    ESECTheme {
        ExamListVariantContent(
            state = ExamListVariantUiState(
                streakCount = 7,
                activeQuestTitle = "Unlock Level 13",
                activeQuestSubtitle = "Complete 3 more Math papers to prestige!",
                xpEarned = 650,
                xpToNextLevel = 1000,
                searchQuery = "",
                selectedGrade = "Grade 12",
                gradeOptions = listOf("Grade 12", "Grade 11"),
                totalQuestsAvailable = 8,
                quests = listOf(
                    VariantQuest(
                        id = "math-2023",
                        title = "General Mathematics",
                        subtitle = "Entrance Examination - Level 12",
                        difficulty = QuestDifficulty.Easy,
                        year = 2023,
                        kind = VariantCardKind.Completed,
                        masteryPercent = 92,
                        playerCountLabel = "+42 players completed",
                    ),
                    VariantQuest(
                        id = "phys-2022",
                        title = "Physics & Mechanics",
                        subtitle = "National High School Exit - Level 12",
                        difficulty = QuestDifficulty.Hard,
                        year = 2022,
                        kind = VariantCardKind.Hard,
                        xpReward = 500,
                    ),
                    VariantQuest(
                        id = "bio-1",
                        title = "Biology Basics",
                        subtitle = "",
                        difficulty = QuestDifficulty.Medium,
                        year = 2023,
                        kind = VariantCardKind.InProgress,
                        progressPercent = 40,
                    ),
                    VariantQuest(
                        id = "chem-1",
                        title = "Chemistry 101",
                        subtitle = "",
                        difficulty = QuestDifficulty.Medium,
                        year = 2023,
                        kind = VariantCardKind.Ready,
                    ),
                ),
                weeklyStreak = listOf(
                    DayStreak("M", DayState.Done),
                    DayStreak("T", DayState.Done),
                    DayStreak("W", DayState.Today),
                    DayStreak("T", DayState.Future),
                    DayStreak("F", DayState.Future),
                    DayStreak("S", DayState.Future),
                    DayStreak("S", DayState.Future),
                ),
            ),
            onMenuClick = {},
            onSearchClick = {},
            onSearchQueryChange = {},
            onGradeSelect = {},
            onClaimDailyBonus = {},
            onQuestClick = {},
        )
    }
}
