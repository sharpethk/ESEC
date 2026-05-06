package com.esec.examprep.presentation.subject

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.esec.examprep.domain.model.ExamMode
import com.esec.examprep.domain.model.Subject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectScreen(
    onSubjectSelected: (subjectId: String, mode: String) -> Unit,
    onBack: () -> Unit,
    viewModel: SubjectViewModel = hiltViewModel(),
) {
    val subjects by viewModel.subjects.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Choose a Subject") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
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

@Composable
private fun SubjectCard(
    subject: Subject,
    onTimedExam: () -> Unit,
    onPracticeExam: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(subject.name, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Text(subject.description, style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            Text("${subject.totalQuestions} questions",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onTimedExam, modifier = Modifier.weight(1f)) {
                    Text("Timed")
                }
                OutlinedButton(onClick = onPracticeExam, modifier = Modifier.weight(1f)) {
                    Text("Practice")
                }
            }
        }
    }
}
