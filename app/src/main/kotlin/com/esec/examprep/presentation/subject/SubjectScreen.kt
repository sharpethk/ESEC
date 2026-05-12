/**
 * Maps to /stitch_erixam_exam_companion/subjects_gamified_style/code.html
 * Design tokens from /stitch_erixam_exam_companion/erixam_design_system/DESIGN.md.
 *
 * Preserves the existing public signature:
 *   SubjectScreen(onSubjectSelected, onBack, viewModel)
 * and the existing ModalBottomSheet picker for mode + past papers,
 * but rebuilds the list as gamified bento mastery cards.
 */
package com.esec.examprep.presentation.subject

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.esec.examprep.R
import com.esec.examprep.domain.model.ExamMode
import com.esec.examprep.domain.model.Subject
import com.esec.examprep.domain.model.YearStat
import com.esec.examprep.presentation.components.IconBadge
import com.esec.examprep.presentation.components.LevelPill
import com.esec.examprep.presentation.components.PillVariant
import com.esec.examprep.presentation.theme.AccentTeal
import com.esec.examprep.presentation.theme.ESECTheme
import com.esec.examprep.presentation.theme.Elevation
import com.esec.examprep.presentation.theme.Radius
import com.esec.examprep.presentation.theme.Spacing
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectScreen(
    onSubjectSelected: (subjectId: String, mode: ExamMode, year: Int?) -> Unit,
    onBack: () -> Unit,
    viewModel: SubjectViewModel = hiltViewModel(),
) {
    val subjects by viewModel.subjects.collectAsStateWithLifecycle()
    val yearStats by viewModel.yearStats.collectAsStateWithLifecycle()

    var pickerSubject by remember { mutableStateOf<Subject?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    SubjectScreenContent(
        subjects = subjects,
        onBack = onBack,
        onSubjectClick = { pickerSubject = it },
    )

    val current = pickerSubject
    if (current != null) {
        LaunchedEffect(current.id) { viewModel.loadYearStats(current.id) }

        val years = yearStats[current.id].orEmpty()
        val dismissThen: (() -> Unit) -> Unit = { action ->
            scope.launch { sheetState.hide() }.invokeOnCompletion {
                pickerSubject = null
                action()
            }
        }
        ModalBottomSheet(
            onDismissRequest = { pickerSubject = null },
            sheetState = sheetState,
        ) {
            ModePickerSheet(
                subject = current,
                years = years,
                onPickMode = { mode ->
                    val subjectId = current.id
                    dismissThen { onSubjectSelected(subjectId, mode, null) }
                },
                onPickYear = { year ->
                    val subjectId = current.id
                    dismissThen { onSubjectSelected(subjectId, ExamMode.TIMED, year) }
                },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SubjectScreenContent(
    subjects: List<Subject>,
    onBack: () -> Unit,
    onSubjectClick: (Subject) -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            stringResource(R.string.subject_title),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            stringResource(R.string.subject_subtitle_count, subjects.size),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
    ) { padding ->
        if (subjects.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    stringResource(R.string.subject_empty),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(
                    start = Spacing.lg,
                    end = Spacing.lg,
                    top = Spacing.sm,
                    bottom = Spacing.xxxl,
                ),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
                modifier = Modifier.padding(padding),
            ) {
                item("mastery-summary") { MasterySummary() }
                items(count = subjects.size, key = { subjects[it].id }) { index ->
                    val subject = subjects[index]
                    val accent = accentFor(index)
                    SubjectMasteryCard(
                        subject = subject,
                        accent = accent,
                        onClick = { onSubjectClick(subject) },
                    )
                }
            }
        }
    }
}

// -------- Mastery summary bento --------

@Composable
private fun MasterySummary() {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(Radius.xl))
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(Spacing.lg),
        ) {
            Icon(
                imageVector = Icons.Default.MilitaryTech,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.15f),
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.BottomEnd),
            )
            Column {
                Text(
                    text = "OVERALL MASTERY",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f),
                )
                Spacer(Modifier.height(Spacing.xs))
                Text(
                    text = "45.7%",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
                Spacer(Modifier.height(Spacing.xs))
                Text(
                    text = "Almost halfway to Elite status!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            StatTile(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.LocalFireDepartment,
                container = MaterialTheme.colorScheme.secondaryContainer,
                onContainer = MaterialTheme.colorScheme.onSecondaryContainer,
                title = "5 Day",
                subtitle = "Streak",
            )
            StatTile(
                modifier = Modifier.weight(1f),
                icon = Icons.AutoMirrored.Filled.TrendingUp,
                container = MaterialTheme.colorScheme.surfaceContainerHighest,
                onContainer = MaterialTheme.colorScheme.onSurface,
                title = "LVL 24",
                subtitle = "Global Rank",
                iconTint = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun StatTile(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    container: Color,
    onContainer: Color,
    title: String,
    subtitle: String,
    iconTint: Color = onContainer,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(Radius.xl))
            .background(container)
            .padding(Spacing.md)
            .height(112.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(28.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = onContainer,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelLarge,
                color = onContainer.copy(alpha = 0.85f),
            )
        }
    }
}

// -------- Subject mastery card (full-width) --------

private data class CardAccent(
    val container: Color,
    val onContainer: Color,
    val accentContent: Color,
    val ctaLabel: String,
    val ctaIcon: ImageVector,
)

@Composable
private fun accentFor(index: Int): CardAccent {
    val cs = MaterialTheme.colorScheme
    return when (index % 3) {
        0 -> CardAccent(
            container = cs.primary,
            onContainer = cs.onPrimary,
            accentContent = cs.primary,
            ctaLabel = "Continue Journey",
            ctaIcon = Icons.Default.PlayArrow,
        )
        1 -> CardAccent(
            container = cs.secondary,
            onContainer = cs.onSecondary,
            accentContent = cs.secondary,
            ctaLabel = "Resume Mission",
            ctaIcon = Icons.Default.PlayArrow,
        )
        else -> CardAccent(
            container = cs.tertiary,
            onContainer = cs.onTertiary,
            accentContent = cs.tertiary,
            ctaLabel = "Start Learning",
            ctaIcon = Icons.Default.PlayArrow,
        )
    }
}

@Composable
private fun SubjectMasteryCard(
    subject: Subject,
    accent: CardAccent,
    onClick: () -> Unit,
) {
    // Deterministic placeholders until backend mastery/level ship.
    val mastery = remember(subject.id) {
        (kotlin.math.abs(subject.id.hashCode()) % 70 + 15)
    }
    val level = remember(subject.id) {
        (kotlin.math.abs(subject.id.hashCode()) % 24 + 1)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Radius.xl),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.sm),
        onClick = onClick,
    ) {
        Column(modifier = Modifier.padding(Spacing.lg)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                LevelPill(level = level, variant = PillVariant.Outlined)
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "$mastery%",
                        style = MaterialTheme.typography.headlineMedium,
                        color = accent.accentContent,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "MASTERY",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Spacer(Modifier.height(Spacing.md))
            Text(
                text = subject.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.height(Spacing.xs))
            Text(
                text = subject.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(Spacing.md))
            LinearProgressIndicator(
                progress = { mastery / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(50)),
                color = accent.accentContent,
                trackColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            )
            Spacer(Modifier.height(Spacing.lg))
            Button(
                onClick = onClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(Radius.lg),
                colors = ButtonDefaults.buttonColors(
                    containerColor = accent.container,
                    contentColor = accent.onContainer,
                ),
            ) {
                Text(text = accent.ctaLabel, style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.size(Spacing.xs))
                Icon(accent.ctaIcon, contentDescription = null, modifier = Modifier.size(18.dp))
            }
        }
    }
}

// -------- Mode picker sheet --------

@Composable
private fun ModePickerSheet(
    subject: Subject,
    years: List<YearStat>,
    onPickMode: (ExamMode) -> Unit,
    onPickYear: (Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.lg)
            .padding(bottom = Spacing.xl),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Text(
            subject.name,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            stringResource(R.string.subject_picker_subtitle),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(Spacing.sm))

        ModeRow(
            icon = Icons.Default.Schedule,
            title = stringResource(R.string.subject_mode_timed),
            subtitle = stringResource(R.string.subject_mode_timed_desc),
            tint = MaterialTheme.colorScheme.primary,
            onClick = { onPickMode(ExamMode.TIMED) },
        )
        ModeRow(
            icon = Icons.Default.School,
            title = stringResource(R.string.subject_mode_practice),
            subtitle = stringResource(R.string.subject_mode_practice_desc),
            tint = AccentTeal,
            onClick = { onPickMode(ExamMode.PRACTICE) },
        )

        Spacer(Modifier.height(Spacing.sm))
        Text(
            stringResource(R.string.subject_past_papers_header),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
        )
        if (years.isEmpty()) {
            Text(
                stringResource(R.string.subject_past_papers_empty),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            years.forEach { stat ->
                YearRow(
                    stat = stat,
                    onClick = { onPickYear(stat.year) },
                )
            }
        }
    }
}

@Composable
private fun ModeRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    tint: Color,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Radius.md),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier.padding(Spacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconBadge(icon = icon, tint = tint, size = 36.dp)
            Spacer(Modifier.size(Spacing.md))
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun YearRow(
    stat: YearStat,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Radius.md),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier.padding(Spacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconBadge(icon = Icons.Default.CalendarMonth, tint = MaterialTheme.colorScheme.primary, size = 36.dp)
            Spacer(Modifier.size(Spacing.md))
            Column(Modifier.weight(1f)) {
                Text(
                    stringResource(R.string.subject_past_paper_year, stat.year),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    stringResource(R.string.subject_past_paper_count, stat.questionCount),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

// -------- Preview --------

@Preview(showBackground = true, heightDp = 1200)
@Composable
private fun SubjectScreenPreview() {
    ESECTheme {
        SubjectScreenContent(
            subjects = listOf(
                Subject(
                    id = "math",
                    name = "Mathematics",
                    description = "15/18 Modules unlocked",
                    iconRes = 0,
                    totalQuestions = 240,
                    category = "G12",
                ),
                Subject(
                    id = "science",
                    name = "Science",
                    description = "Explore biology & physics",
                    iconRes = 0,
                    totalQuestions = 180,
                    category = "G12",
                ),
                Subject(
                    id = "english",
                    name = "English",
                    description = "Grammar & Literature intensive",
                    iconRes = 0,
                    totalQuestions = 120,
                    category = "G12",
                ),
            ),
            onBack = {},
            onSubjectClick = {},
        )
    }
}
