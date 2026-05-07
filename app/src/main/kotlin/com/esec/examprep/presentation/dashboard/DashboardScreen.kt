package com.esec.examprep.presentation.dashboard

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.esec.examprep.R
import com.esec.examprep.domain.model.ExamResult
import com.esec.examprep.domain.model.UserProgress
import com.esec.examprep.domain.model.WeakTopic
import com.esec.examprep.presentation.components.IconBadge
import com.esec.examprep.presentation.components.StatTile
import com.esec.examprep.presentation.components.StatusPill
import com.esec.examprep.presentation.theme.Elevation
import com.esec.examprep.presentation.theme.Radius
import com.esec.examprep.presentation.theme.Spacing
import com.esec.examprep.presentation.theme.WrongRed
import com.esec.examprep.presentation.theme.scoreColor
import java.time.Duration
import java.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onBack: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val progress = state.progress
    var showClearDialog by remember { mutableStateOf(false) }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title   = { Text(stringResource(R.string.dashboard_clear_dialog_title)) },
            text    = { Text(stringResource(R.string.dashboard_clear_dialog_message)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.clearAllProgress(); showClearDialog = false
                }) {
                    Text(
                        stringResource(R.string.dashboard_clear),
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text(stringResource(R.string.dashboard_cancel))
                }
            },
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.dashboard_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back),
                        )
                    }
                },
                actions = {
                    if (progress.isNotEmpty()) {
                        TextButton(onClick = { showClearDialog = true }) {
                            Text(
                                stringResource(R.string.dashboard_clear),
                                color = MaterialTheme.colorScheme.error,
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
    ) { padding ->
        if (progress.isEmpty()) {
            EmptyProgressState(modifier = Modifier.fillMaxSize().padding(padding))
        } else {
            val attempts = progress.sumOf { it.totalAttempts }
            val avg = if (progress.isNotEmpty())
                progress.map { it.averageScore }.average().toInt() else 0
            val best = (progress.maxOfOrNull { it.bestScore } ?: 0f).toInt()
            val nameById = remember(progress) { progress.associate { it.subjectId to it.subjectName } }

            LazyColumn(
                contentPadding = PaddingValues(Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
                modifier = Modifier.padding(padding),
            ) {
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                        StatTile(
                            label = stringResource(R.string.dashboard_stat_attempts),
                            value = "$attempts",
                            icon = Icons.Default.Assessment,
                            accent = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f),
                        )
                        StatTile(
                            label = stringResource(R.string.dashboard_stat_best),
                            value = stringResource(R.string.dashboard_percent_int, best),
                            icon = Icons.Default.EmojiEvents,
                            accent = scoreColor(best.toFloat()),
                            modifier = Modifier.weight(1f),
                        )
                        StatTile(
                            label = stringResource(R.string.dashboard_stat_avg),
                            value = stringResource(R.string.dashboard_percent_int, avg),
                            icon = Icons.AutoMirrored.Filled.TrendingUp,
                            accent = scoreColor(avg.toFloat()),
                            modifier = Modifier.weight(1f),
                        )
                    }
                }

                item { TimeStatsCard(avgSeconds = state.avgTimePerQuestion) }

                if (state.recent.isNotEmpty()) {
                    item { SectionHeader("Recent exams") }
                    items(state.recent, key = { "recent-${it.sessionId}" }) { RecentExamRow(it) }
                }

                if (state.weakTopics.isNotEmpty()) {
                    item { SectionHeader("Weak topics") }
                    items(state.weakTopics, key = { "weak-${it.subjectId}" }) { topic ->
                        WeakTopicRow(topic, nameById[topic.subjectId] ?: topic.subjectId)
                    }
                }

                item {
                    Spacer(Modifier.height(Spacing.xs))
                    SectionHeader(stringResource(R.string.dashboard_section_by_subject))
                }
                items(progress, key = { "progress-${it.subjectId}" }) { p ->
                    val trendScores = remember(state.recent, p.subjectId) {
                        state.recent.asReversed()
                            .filter { it.subjectId == p.subjectId }
                            .map { it.scorePercent }
                            .takeLast(10)
                    }
                    ProgressCard(p, trendScores)
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
    )
}

@Composable
private fun EmptyProgressState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(Spacing.xxxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        IconBadge(
            icon = Icons.Default.Insights,
            tint = MaterialTheme.colorScheme.primary,
            size = 64.dp,
        )
        Spacer(Modifier.height(Spacing.lg))
        Text(
            stringResource(R.string.dashboard_empty_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.height(Spacing.xs))
        Text(
            stringResource(R.string.dashboard_empty_body),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun TimeStatsCard(avgSeconds: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Radius.lg),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.xs),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(Spacing.lg),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconBadge(
                icon = Icons.Default.Schedule,
                tint = MaterialTheme.colorScheme.primary,
            )
            Spacer(Modifier.height(0.dp))
            Column(modifier = Modifier.padding(start = Spacing.md).weight(1f)) {
                Text("Avg time per question", style = MaterialTheme.typography.bodyMedium)
                Text(
                    if (avgSeconds > 0) "${avgSeconds.toInt()}s" else "—",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun RecentExamRow(result: ExamResult) {
    val accent = scoreColor(result.scorePercent)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Radius.md),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.xs),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(Spacing.lg),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    result.subjectName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    relativeTime(result.completedAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            StatusPill(
                text = stringResource(R.string.dashboard_percent_int, result.scorePercent.toInt()),
                color = accent,
            )
        }
    }
}

@Composable
private fun WeakTopicRow(topic: WeakTopic, subjectName: String) {
    val errorPct = (topic.errorRate * 100f).toInt()
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Radius.md),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.xs),
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(Spacing.lg)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    subjectName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    "$errorPct% wrong",
                    style = MaterialTheme.typography.labelMedium,
                    color = WrongRed,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Spacer(Modifier.height(Spacing.xs))
            LinearProgressIndicator(
                progress = { topic.errorRate.coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().height(6.dp),
                color = WrongRed,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                strokeCap = StrokeCap.Round,
            )
            Spacer(Modifier.height(Spacing.xs))
            Text(
                "${topic.attempts} attempts",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ProgressCard(progress: UserProgress, trendScores: List<Float>) {
    val accent = scoreColor(progress.averageScore)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Radius.lg),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.xs),
    ) {
        Column(modifier = Modifier.padding(Spacing.xl)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    progress.subjectName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                )
                StatusPill(
                    text = stringResource(R.string.dashboard_percent_int, progress.averageScore.toInt()),
                    color = accent,
                )
            }
            Spacer(Modifier.height(Spacing.md))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                LabeledStat(
                    stringResource(R.string.dashboard_stat_attempts),
                    "${progress.totalAttempts}",
                )
                LabeledStat(
                    stringResource(R.string.dashboard_stat_best),
                    stringResource(R.string.dashboard_percent_int, progress.bestScore.toInt()),
                )
                LabeledStat(
                    stringResource(R.string.dashboard_stat_average),
                    stringResource(R.string.dashboard_percent_int, progress.averageScore.toInt()),
                )
            }
            Spacer(Modifier.height(Spacing.md))
            Text(
                stringResource(R.string.dashboard_average_label),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(Spacing.xs))
            LinearProgressIndicator(
                progress  = { (progress.averageScore / 100f).coerceIn(0f, 1f) },
                modifier  = Modifier.fillMaxWidth().height(8.dp),
                color     = accent,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                strokeCap = StrokeCap.Round,
            )
            if (trendScores.size >= 2) {
                Spacer(Modifier.height(Spacing.md))
                Text(
                    "Recent trend",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(Spacing.xs))
                TrendLineChart(scores = trendScores, color = accent)
            }
        }
    }
}

@Composable
private fun TrendLineChart(scores: List<Float>, color: Color) {
    val track = MaterialTheme.colorScheme.surfaceVariant
    Box(modifier = Modifier.fillMaxWidth().height(56.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (scores.size < 2) return@Canvas
            val w = size.width
            val h = size.height
            val maxY = 100f
            val stepX = w / (scores.size - 1)
            val points = scores.mapIndexed { i, s ->
                Offset(i * stepX, h - (s.coerceIn(0f, maxY) / maxY) * h)
            }
            drawLine(
                color = track,
                start = Offset(0f, h),
                end = Offset(w, h),
                strokeWidth = 2f,
            )
            for (i in 0 until points.lastIndex) {
                drawLine(
                    color = color,
                    start = points[i],
                    end = points[i + 1],
                    strokeWidth = 4f,
                    cap = StrokeCap.Round,
                )
            }
            points.forEach { p ->
                drawCircle(color = color, radius = 4f, center = p, style = Stroke(width = 2f))
            }
        }
    }
}

@Composable
private fun LabeledStat(label: String, value: String) {
    Column {
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private fun relativeTime(instant: Instant): String {
    val d = Duration.between(instant, Instant.now())
    val mins = d.toMinutes()
    return when {
        mins < 1 -> "just now"
        mins < 60 -> "${mins}m ago"
        mins < 60 * 24 -> "${mins / 60}h ago"
        mins < 60 * 24 * 7 -> "${mins / (60 * 24)}d ago"
        else -> "${mins / (60 * 24 * 7)}w ago"
    }
}
