package com.esec.examprep.presentation.subject

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
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.esec.examprep.R
import com.esec.examprep.domain.model.ExamMode
import com.esec.examprep.domain.model.Subject
import com.esec.examprep.domain.model.YearStat
import com.esec.examprep.presentation.components.IconBadge
import com.esec.examprep.presentation.components.StatusPill
import com.esec.examprep.presentation.theme.AccentTeal
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
                modifier = Modifier.fillMaxSize().padding(padding),
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
                contentPadding = PaddingValues(Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
                modifier = Modifier.padding(padding),
            ) {
                items(subjects, key = { it.id }) { subject ->
                    SubjectCard(
                        subject = subject,
                        onClick = { pickerSubject = subject },
                    )
                }
            }
        }
    }

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

@Composable
private fun SubjectCard(
    subject: Subject,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Radius.lg),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.xs),
        onClick = onClick,
    ) {
        Column(modifier = Modifier.padding(Spacing.xl)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconBadge(
                    icon = Icons.AutoMirrored.Filled.MenuBook,
                    tint = MaterialTheme.colorScheme.primary,
                    size = 44.dp,
                )
                Spacer(Modifier.size(Spacing.md))
                Column(Modifier.weight(1f)) {
                    Text(
                        subject.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(Modifier.height(Spacing.xxs))
                    Text(
                        subject.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(Modifier.height(Spacing.md))
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                StatusPill(
                    text = stringResource(R.string.subject_question_count, subject.totalQuestions),
                    color = MaterialTheme.colorScheme.primary,
                )
                StatusPill(text = stringResource(R.string.subject_pill_offline), color = AccentTeal)
            }
        }
    }
}

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
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.none),
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
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.none),
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
