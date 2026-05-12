/**
 * Maps to /stitch_erixam_exam_companion/exam_list_gamified_style/code.html
 * Design tokens from /stitch_erixam_exam_companion/erixam_design_system/DESIGN.md.
 *
 * "Quest Log" — gamified picker showing Active / Completed / Locked exams
 * for a single subject. Stateless; wires to a future ExamListViewModel.
 */
package com.esec.examprep.presentation.exam

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.esec.examprep.presentation.theme.ESECTheme
import com.esec.examprep.presentation.theme.Radius
import com.esec.examprep.presentation.theme.Spacing

// -------- UI Models --------

enum class QuestDifficulty { Easy, Medium, Hard }

@Immutable
data class ActiveQuest(
    val id: String,
    val title: String,
    val difficulty: QuestDifficulty,
    val xpReward: Int,
    val questionCount: Int,
    val durationMinutes: Int,
    val playerCountLabel: String,
)

@Immutable
data class CompletedQuest(
    val id: String,
    val title: String,
    val difficulty: QuestDifficulty,
    val scorePercent: Int,
    val xpEarned: Int,
)

@Immutable
data class LockedQuest(
    val id: String,
    val title: String,
    val unlockRequirement: String,
    val xpReward: Int,
)

enum class QuestFilter { Year, Grade, Difficulty, More }

@Immutable
data class ExamListUiState(
    val subjectName: String,
    val searchQuery: String,
    val activeFilter: QuestFilter,
    val active: List<ActiveQuest>,
    val completed: List<CompletedQuest>,
    val locked: List<LockedQuest>,
    val streakDays: Int,
)

// -------- Stateful entry --------

@Composable
fun ExamListScreen(
    state: ExamListUiState,
    onBack: () -> Unit,
    onMenuClick: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onFilterSelect: (QuestFilter) -> Unit,
    onStartQuest: (ActiveQuest) -> Unit,
    onReplayQuest: (CompletedQuest) -> Unit,
) {
    ExamListContent(
        state = state,
        onBack = onBack,
        onMenuClick = onMenuClick,
        onSearchQueryChange = onSearchQueryChange,
        onFilterSelect = onFilterSelect,
        onStartQuest = onStartQuest,
        onReplayQuest = onReplayQuest,
    )
}

// -------- Stateless content --------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExamListContent(
    state: ExamListUiState,
    onBack: () -> Unit,
    onMenuClick: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onFilterSelect: (QuestFilter) -> Unit,
    onStartQuest: (ActiveQuest) -> Unit,
    onReplayQuest: (CompletedQuest) -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Quest Log",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onMenuClick) {
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
                top = Spacing.sm,
                bottom = Spacing.xxxl,
            ),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            item("header") {
                ContextHeader(
                    subjectName = state.subjectName,
                    query = state.searchQuery,
                    onQueryChange = onSearchQueryChange,
                )
            }
            item("filters") {
                FilterRow(active = state.activeFilter, onSelect = onFilterSelect)
            }
            if (state.active.isNotEmpty()) {
                item("active-header") {
                    SectionRow(
                        title = "Active Quests",
                        icon = Icons.Default.RocketLaunch,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
                items(state.active.size, key = { state.active[it].id }) { i ->
                    ActiveQuestCard(quest = state.active[i], onStart = { onStartQuest(state.active[i]) })
                }
            }
            if (state.completed.isNotEmpty()) {
                item("completed-header") {
                    SectionRow(
                        title = "Completed",
                        icon = Icons.Default.CheckCircle,
                        tint = MaterialTheme.colorScheme.secondary,
                    )
                }
                item("completed-grid") {
                    CompletedGrid(
                        items = state.completed,
                        onReplay = onReplayQuest,
                    )
                }
            }
            if (state.locked.isNotEmpty()) {
                item("locked-header") {
                    SectionRow(
                        title = "Locked Quests",
                        icon = Icons.Default.Lock,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                items(state.locked.size, key = { state.locked[it].id }) { i ->
                    LockedQuestRow(quest = state.locked[i])
                }
            }
            item("streak-banner") { StreakBanner(streakDays = state.streakDays) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContextHeader(
    subjectName: String,
    query: String,
    onQueryChange: (String) -> Unit,
) {
    Column {
        Text(
            text = subjectName,
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.height(Spacing.xs))
        Text(
            text = "Select your next challenge",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(Spacing.md))
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Find a specific quest...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
            shape = RoundedCornerShape(Radius.md),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            ),
        )
    }
}

@Composable
private fun FilterRow(active: QuestFilter, onSelect: (QuestFilter) -> Unit) {
    val scroll = rememberScrollState()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scroll),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        FilterPill(QuestFilter.Year, "Year", Icons.Default.CalendarToday, active, onSelect)
        FilterPill(QuestFilter.Grade, "Grade", Icons.Default.School, active, onSelect)
        FilterPill(QuestFilter.Difficulty, "Difficulty", Icons.Default.Category, active, onSelect)
        FilterPill(QuestFilter.More, "More", Icons.Default.FilterList, active, onSelect)
    }
}

@Composable
private fun FilterPill(
    filter: QuestFilter,
    label: String,
    icon: ImageVector,
    active: QuestFilter,
    onSelect: (QuestFilter) -> Unit,
) {
    val isActive = filter == active
    val container = if (isActive) MaterialTheme.colorScheme.primaryContainer
    else MaterialTheme.colorScheme.surfaceContainerHighest
    val content = if (isActive) MaterialTheme.colorScheme.onPrimaryContainer
    else MaterialTheme.colorScheme.onSurfaceVariant
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(container)
            .clickable { onSelect(filter) }
            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = content, modifier = Modifier.size(18.dp))
        Text(label, style = MaterialTheme.typography.labelLarge, color = content)
    }
}

@Composable
private fun SectionRow(title: String, icon: ImageVector, tint: androidx.compose.ui.graphics.Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = tint)
        Text(text = title, style = MaterialTheme.typography.titleLarge, color = tint, fontWeight = FontWeight.SemiBold)
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(tint.copy(alpha = 0.2f)),
        )
    }
}

// -------- Cards --------

@Composable
private fun ActiveQuestCard(quest: ActiveQuest, onStart: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radius.lg))
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.20f),
                shape = RoundedCornerShape(Radius.lg),
            )
            .padding(Spacing.md),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                ) {
                    DifficultyBadge(quest.difficulty)
                    XpChip(amount = quest.xpReward)
                }
                Spacer(Modifier.height(Spacing.xs))
                Text(
                    text = quest.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(Radius.md))
                    .background(MaterialTheme.colorScheme.surfaceContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Default.Functions, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
        }
        Spacer(Modifier.height(Spacing.md))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            MetaIcon(Icons.Default.Description, "${quest.questionCount} Qs")
            MetaIcon(Icons.Default.Schedule, "${quest.durationMinutes}m")
            Spacer(Modifier.weight(1f))
            Text(
                text = quest.playerCountLabel,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(Modifier.height(Spacing.md))
        Button(
            onClick = onStart,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(Radius.md),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
        ) {
            Text("Start Quest", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.size(Spacing.xs))
            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun CompletedGrid(
    items: List<CompletedQuest>,
    onReplay: (CompletedQuest) -> Unit,
) {
    // Use a manual 2-column layout to avoid nesting LazyVerticalGrid inside LazyColumn.
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
        items.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                rowItems.forEach { quest ->
                    Box(modifier = Modifier.weight(1f)) {
                        CompletedQuestCard(quest = quest, onReplay = { onReplay(quest) })
                    }
                }
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun CompletedQuestCard(quest: CompletedQuest, onReplay: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radius.lg))
            .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.20f))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.30f),
                shape = RoundedCornerShape(Radius.lg),
            )
            .padding(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            DifficultyBadge(quest.difficulty)
            Icon(
                Icons.Default.Verified,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(20.dp),
            )
        }
        Text(
            text = quest.title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = "Scored: ${quest.scorePercent}%",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(Spacing.xs))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "+${quest.xpEarned} XP",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Bold,
            )
            IconButton(onClick = onReplay, modifier = Modifier.size(28.dp)) {
                Icon(
                    Icons.Default.Replay,
                    contentDescription = "Replay",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

@Composable
private fun LockedQuestRow(quest: LockedQuest) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radius.lg))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.50f))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.30f),
                shape = RoundedCornerShape(Radius.lg),
            )
            .padding(Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(Radius.md))
                .background(MaterialTheme.colorScheme.surfaceContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.outline)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = quest.title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
            ) {
                Icon(
                    Icons.Default.Stars,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(14.dp),
                )
                Text(
                    text = quest.unlockRequirement,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline,
                )
            }
        }
        Text(
            text = "+${quest.xpReward} XP",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.10f))
                .padding(horizontal = Spacing.sm, vertical = 2.dp),
        )
    }
}

@Composable
private fun StreakBanner(streakDays: Int) {
    val gradient = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.primaryContainer,
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
                .size(160.dp)
                .align(Alignment.BottomEnd),
        )
        Column {
            Text(
                text = "Maintain your Streak",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(Spacing.xs))
            Text(
                text = "Finish one quest today to keep your fire alive!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f),
            )
            Spacer(Modifier.height(Spacing.md))
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.20f))
                    .padding(horizontal = Spacing.md, vertical = Spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                Icon(
                    Icons.Default.LocalFireDepartment,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
                Text(
                    text = "$streakDays Day Streak",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }
        }
    }
}

// -------- Tiny pieces --------

@Composable
private fun DifficultyBadge(difficulty: QuestDifficulty) {
    val (container, content) = when (difficulty) {
        QuestDifficulty.Easy -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.10f) to MaterialTheme.colorScheme.secondary
        QuestDifficulty.Medium -> MaterialTheme.colorScheme.primary.copy(alpha = 0.10f) to MaterialTheme.colorScheme.primary
        QuestDifficulty.Hard -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.10f) to MaterialTheme.colorScheme.tertiary
    }
    Text(
        text = difficulty.name.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = content,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(container)
            .padding(horizontal = Spacing.sm, vertical = 2.dp),
    )
}

@Composable
private fun XpChip(amount: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        Icon(
            Icons.Default.Stars,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(14.dp),
        )
        Text(
            text = "+$amount XP",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.secondary,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun MetaIcon(icon: ImageVector, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp),
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

// -------- Preview --------

@Preview(showBackground = true, heightDp = 1500)
@Composable
private fun ExamListPreview() {
    ESECTheme {
        ExamListContent(
            state = ExamListUiState(
                subjectName = "Mathematics",
                searchQuery = "",
                activeFilter = QuestFilter.Year,
                streakDays = 4,
                active = listOf(
                    ActiveQuest(
                        id = "math-2023-g12",
                        title = "Mathematics 2023 - Grade 12",
                        difficulty = QuestDifficulty.Hard,
                        xpReward = 500,
                        questionCount = 50,
                        durationMinutes = 60,
                        playerCountLabel = "+12k Players",
                    ),
                ),
                completed = listOf(
                    CompletedQuest("mock-1", "Mock Paper 1", QuestDifficulty.Easy, 88, 250),
                    CompletedQuest("2022-quiz", "2022 Quiz", QuestDifficulty.Easy, 95, 300),
                ),
                locked = listOf(
                    LockedQuest(
                        id = "math-2024",
                        title = "Mathematics 2024 (Early Access)",
                        unlockRequirement = "Requires Level 10",
                        xpReward = 1000,
                    ),
                ),
            ),
            onBack = {},
            onMenuClick = {},
            onSearchQueryChange = {},
            onFilterSelect = {},
            onStartQuest = {},
            onReplayQuest = {},
        )
    }
}
