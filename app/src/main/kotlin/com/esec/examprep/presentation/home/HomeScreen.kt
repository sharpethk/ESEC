/**
 * Maps to /stitch_erixam_exam_companion/home_gamified_variant/code.html
 * Design tokens from /stitch_erixam_exam_companion/erixam_design_system/DESIGN.md.
 *
 * Layout note: the global AppNavGraph already provides the app-wide bottom
 * NavigationBar and content padding, so this screen renders only a top app-bar
 * row + scrollable content + FAB and does NOT use EriXamScaffold (which would
 * double-stack the bottom bar).
 */
package com.esec.examprep.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AddTask
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.esec.examprep.presentation.components.BentoCard
import com.esec.examprep.presentation.components.BentoSpan
import com.esec.examprep.presentation.components.HeroImageCard
import com.esec.examprep.presentation.components.ProgressRing
import com.esec.examprep.presentation.components.SectionHeader
import com.esec.examprep.presentation.theme.ESECTheme

/* ---------------- Stateful wrapper ---------------- */

@Composable
fun HomeScreen(
    onSubjectsClick: () -> Unit,
    onDashboardClick: () -> Unit,
    onStartDailyChallenge: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val challenge by viewModel.todayChallenge.collectAsState()
    val streak by viewModel.streak.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        when {
            isLoading -> LoadingState()
            error != null -> ErrorState(message = error!!, onRetry = viewModel::retryLoad)
            else -> HomeContent(
                streak = streak,
                hasTodayChallenge = (challenge?.questions?.isNotEmpty() == true),
                challengeCompleted = challenge?.isCompleted == true,
                challengeScorePercent = challenge?.scorePercent,
                onSubjectsClick = onSubjectsClick,
                onDashboardClick = onDashboardClick,
                onStartDailyChallenge = {
                    if (viewModel.stageDailyChallenge()) onStartDailyChallenge()
                },
            )
        }
    }
}

/* ---------------- Stateless content ---------------- */

@Composable
private fun HomeContent(
    streak: Int,
    hasTodayChallenge: Boolean,
    challengeCompleted: Boolean,
    challengeScorePercent: Float?,
    onSubjectsClick: () -> Unit,
    onDashboardClick: () -> Unit,
    onStartDailyChallenge: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
        ) {
            HomeTopBar(streak = streak)

            Spacer(Modifier.height(8.dp))

            // Welcome
            Text(
                text = "Selam!",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = "Ready to level up?",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(Modifier.height(24.dp))

            BentoGrid(
                streak = streak,
                onMasteryClick = onDashboardClick,
                onGradeClick = onSubjectsClick,
                onCollegeClick = onSubjectsClick,
            )

            Spacer(Modifier.height(32.dp))

            // Quest Log
            SectionHeader(
                title = "Quest Log",
                icon = Icons.AutoMirrored.Outlined.Assignment,
                actionLabel = "View All",
                onActionClick = onDashboardClick,
            )
            Spacer(Modifier.height(12.dp))

            if (hasTodayChallenge) {
                QuestItem(
                    title = "Today's Daily Challenge",
                    tag = "DAILY",
                    statusLabel = if (challengeCompleted) "COMPLETED" else "IN PROGRESS",
                    statusColor = if (challengeCompleted)
                        MaterialTheme.colorScheme.secondary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    leadingIcon = if (challengeCompleted) Icons.Filled.Verified else Icons.Filled.PlayCircle,
                    leadingBg = if (challengeCompleted)
                        MaterialTheme.colorScheme.secondaryContainer
                    else
                        MaterialTheme.colorScheme.primaryContainer,
                    leadingTint = if (challengeCompleted)
                        MaterialTheme.colorScheme.onSecondaryContainer
                    else
                        MaterialTheme.colorScheme.onPrimaryContainer,
                    trailingScorePercent = challengeScorePercent,
                    onClick = onStartDailyChallenge,
                )
            } else {
                EmptyQuestCard(onStart = onSubjectsClick)
            }

            Spacer(Modifier.height(32.dp))

            HeroImageCard(
                imageUrl = "",
                title = "National Mock Exam",
                subtitle = "Starts in 3 days. Are you prepared?",
                onClick = onSubjectsClick,
                contentDescription = "National Mock Exam preview",
            )

            // bottom safe-area for FAB + global bottom nav
            Spacer(Modifier.height(96.dp))
        }

        // Contextual FAB
        FloatingActionButton(
            onClick = onStartDailyChallenge,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 16.dp)
                .size(56.dp),
        ) {
            Icon(Icons.Filled.AddTask, contentDescription = "Start daily challenge")
        }
    }
}

/* ---------------- Top bar ---------------- */

@Composable
private fun HomeTopBar(streak: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { /* TODO: drawer */ }) {
                Icon(
                    Icons.Filled.Menu,
                    contentDescription = "Open menu",
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
            Text(
                text = "EriXam",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (streak > 0) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.2f))
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                ) {
                    Icon(
                        Icons.Filled.LocalFireDepartment,
                        contentDescription = "Daily streak: $streak days",
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(18.dp),
                    )
                    Text(
                        text = streak.toString(),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.tertiary,
                    )
                }
            }
            IconButton(onClick = { /* TODO: search */ }) {
                Icon(
                    Icons.Filled.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

/* ---------------- Bento grid ---------------- */

@Composable
private fun BentoGrid(
    streak: Int,
    onMasteryClick: () -> Unit,
    onGradeClick: () -> Unit,
    onCollegeClick: () -> Unit,
) {
    val gap = 12.dp
    Column(verticalArrangement = Arrangement.spacedBy(gap)) {

        // Row 1: Streak (wide)
        StreakBentoTile(streak = streak)

        // Row 2: Mastery tall (left) + two stacked squares (right)
        Row(horizontalArrangement = Arrangement.spacedBy(gap)) {
            Box(modifier = Modifier.weight(1f)) {
                MasteryBentoTile(progress = 0.65f, onClick = onMasteryClick)
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(gap),
                modifier = Modifier.weight(1f),
            ) {
                Grade12BentoTile(onClick = onGradeClick)
                Grade8BentoTile(onClick = onGradeClick)
            }
        }

        // Row 3: College entrance (wide)
        CollegeEntranceBentoTile(progress = 0.15f, onClick = onCollegeClick)
    }
}

@Composable
private fun StreakBentoTile(streak: Int) {
    val scheme = MaterialTheme.colorScheme
    val title = if (streak > 0) "$streak Day Learning Streak" else "Start your streak!"
    val subtitle = if (streak > 0) "Keep the fire burning!" else "Practice today to begin."

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    listOf(scheme.tertiary, scheme.onTertiaryContainer),
                ),
            )
            .padding(16.dp),
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    Icons.Filled.LocalFireDepartment,
                    contentDescription = null,
                    tint = scheme.onTertiary,
                    modifier = Modifier.size(28.dp),
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = scheme.onTertiary,
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelLarge,
                color = scheme.onTertiary.copy(alpha = 0.9f),
            )
        }
        // decorative bolt
        Icon(
            Icons.Filled.Bolt,
            contentDescription = null,
            tint = scheme.onTertiary.copy(alpha = 0.2f),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(96.dp)
                .rotate(12f),
        )
    }
}

@Composable
private fun MasteryBentoTile(progress: Float, onClick: () -> Unit) {
    BentoCard(
        span = BentoSpan.Tall,
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(208.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "Overall Mastery",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            Spacer(Modifier.height(12.dp))
            ProgressRing(
                progress = progress,
                size = 96.dp,
                centerLabel = "${(progress * 100).toInt()}%",
                trackColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f),
                progressColor = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                val filledStars = (progress * 3).toInt().coerceIn(0, 3)
                repeat(3) { i ->
                    Icon(
                        Icons.Filled.Star,
                        contentDescription = null,
                        tint = if (i < filledStars)
                            MaterialTheme.colorScheme.secondaryContainer
                        else
                            MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f),
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun Grade12BentoTile(onClick: () -> Unit) {
    val scheme = MaterialTheme.colorScheme
    BentoCard(
        span = BentoSpan.Square,
        containerColor = scheme.secondaryContainer,
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Icon(
                    Icons.Filled.School,
                    contentDescription = null,
                    tint = scheme.onSecondaryContainer,
                    modifier = Modifier.size(28.dp),
                )
                LevelBadge(
                    text = "LEVEL 12",
                    bg = scheme.onSecondaryContainer,
                    fg = scheme.secondaryContainer,
                )
            }
            Column {
                Text(
                    text = "Grade 12",
                    style = MaterialTheme.typography.titleLarge,
                    color = scheme.onSecondaryContainer,
                )
                Text(
                    text = "Mastery: 72%",
                    style = MaterialTheme.typography.labelSmall,
                    color = scheme.onSecondaryContainer.copy(alpha = 0.8f),
                )
            }
        }
    }
}

@Composable
private fun Grade8BentoTile(onClick: () -> Unit) {
    val scheme = MaterialTheme.colorScheme
    BentoCard(
        span = BentoSpan.Square,
        containerColor = scheme.surfaceContainerHighest,
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Icon(
                    Icons.Filled.HistoryEdu,
                    contentDescription = null,
                    tint = scheme.primary,
                    modifier = Modifier.size(28.dp),
                )
                LevelBadge(
                    text = "LEVEL 8",
                    bg = scheme.primary,
                    fg = scheme.onPrimary,
                )
            }
            Column {
                Text(
                    text = "Grade 8",
                    style = MaterialTheme.typography.titleLarge,
                    color = scheme.primary,
                )
                Text(
                    text = "Mastery: 48%",
                    style = MaterialTheme.typography.labelSmall,
                    color = scheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun CollegeEntranceBentoTile(progress: Float, onClick: () -> Unit) {
    val scheme = MaterialTheme.colorScheme
    BentoCard(
        containerColor = scheme.surfaceContainer,
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(scheme.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Filled.AccountBalance,
                    contentDescription = null,
                    tint = scheme.primary,
                    modifier = Modifier.size(24.dp),
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "College Entrance",
                    style = MaterialTheme.typography.titleLarge,
                    color = scheme.primary,
                )
                Spacer(Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    LinearProgressIndicator(
                        progress = { progress },
                        color = scheme.primary,
                        trackColor = scheme.outlineVariant.copy(alpha = 0.3f),
                        modifier = Modifier
                            .width(96.dp)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                    )
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = scheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun LevelBadge(text: String, bg: Color, fg: Color) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 2.dp),
    ) {
        Text(
            text = text,
            color = fg,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
        )
    }
}

/* ---------------- Quest Log items ---------------- */

@Composable
private fun QuestItem(
    title: String,
    tag: String,
    statusLabel: String,
    statusColor: Color,
    leadingIcon: ImageVector,
    leadingBg: Color,
    leadingTint: Color,
    trailingScorePercent: Float?,
    onClick: () -> Unit,
) {
    val scheme = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(scheme.surfaceContainerLowest)
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(leadingBg),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    leadingIcon,
                    contentDescription = null,
                    tint = leadingTint,
                )
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = scheme.onSurface,
                )
                Spacer(Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(scheme.surfaceContainer)
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                    ) {
                        Text(
                            text = tag,
                            style = MaterialTheme.typography.labelSmall,
                            color = scheme.onSurfaceVariant,
                        )
                    }
                    Text(
                        text = statusLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColor,
                    )
                }
            }
            if (trailingScorePercent != null) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${(trailingScorePercent * 100).toInt()}%",
                        style = MaterialTheme.typography.titleLarge,
                        color = scheme.onSurface,
                    )
                    Text(
                        text = "Score",
                        style = MaterialTheme.typography.labelSmall,
                        color = scheme.onSurfaceVariant,
                    )
                }
            } else {
                Icon(
                    Icons.Filled.PlayCircle,
                    contentDescription = null,
                    tint = scheme.outline,
                )
            }
        }
    }
}

@Composable
private fun EmptyQuestCard(onStart: () -> Unit) {
    val scheme = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(scheme.surfaceContainerLowest)
            .padding(20.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(scheme.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.MenuBook,
                    contentDescription = null,
                    tint = scheme.onPrimaryContainer,
                )
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "No quests yet",
                    style = MaterialTheme.typography.titleMedium,
                    color = scheme.onSurface,
                )
                Text(
                    text = "Tap a subject to begin your first practice.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = scheme.onSurfaceVariant,
                )
            }
            Button(
                onClick = onStart,
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = scheme.primary,
                    contentColor = scheme.onPrimary,
                ),
            ) { Text("Start") }
        }
    }
}

/* ---------------- Loading / Error ---------------- */

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Couldn't load data",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(16.dp))
            Button(onClick = onRetry) { Text("Retry") }
        }
    }
}

/* ---------------- Preview ---------------- */

@Preview(showBackground = true, widthDp = 412, heightDp = 900)
@Composable
private fun HomeContentPreview() {
    ESECTheme {
        HomeContent(
            streak = 5,
            hasTodayChallenge = true,
            challengeCompleted = false,
            challengeScorePercent = null,
            onSubjectsClick = {},
            onDashboardClick = {},
            onStartDailyChallenge = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 900)
@Composable
private fun HomeContentEmptyPreview() {
    ESECTheme {
        HomeContent(
            streak = 0,
            hasTodayChallenge = false,
            challengeCompleted = false,
            challengeScorePercent = null,
            onSubjectsClick = {},
            onDashboardClick = {},
            onStartDailyChallenge = {},
        )
    }
}
