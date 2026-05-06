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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.esec.examprep.R
import com.esec.examprep.domain.model.ExamMode
import com.esec.examprep.domain.model.Subject
import com.esec.examprep.presentation.components.IconBadge
import com.esec.examprep.presentation.components.StatusPill
import com.esec.examprep.presentation.theme.AccentTeal
import com.esec.examprep.presentation.theme.Elevation
import com.esec.examprep.presentation.theme.Radius
import com.esec.examprep.presentation.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectScreen(
    onSubjectSelected: (subjectId: String, mode: String) -> Unit,
    onBack: () -> Unit,
    viewModel: SubjectViewModel = hiltViewModel(),
) {
    val subjects by viewModel.subjects.collectAsState()

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
                        onTimedExam    = { onSubjectSelected(subject.id, ExamMode.TIMED.name) },
                        onPracticeExam = { onSubjectSelected(subject.id, ExamMode.PRACTICE.name) },
                    )
                }
            }
        }
    }
}

@Composable
private fun SubjectCard(
    subject: Subject,
    onTimedExam: () -> Unit,
    onPracticeExam: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Radius.lg),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.xs),
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
            Spacer(Modifier.height(Spacing.lg))
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                Button(
                    onClick = onTimedExam,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(Radius.md),
                    contentPadding = PaddingValues(vertical = 12.dp),
                ) {
                    Icon(Icons.Default.Schedule, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.size(Spacing.xs))
                    Text(stringResource(R.string.subject_mode_timed), fontWeight = FontWeight.SemiBold)
                }
                OutlinedButton(
                    onClick = onPracticeExam,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(Radius.md),
                    contentPadding = PaddingValues(vertical = 12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary,
                    ),
                ) {
                    Icon(Icons.Default.School, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.size(Spacing.xs))
                    Text(stringResource(R.string.subject_mode_practice), fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
