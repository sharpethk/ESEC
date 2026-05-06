package com.esec.examprep.presentation.dashboard

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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.esec.examprep.domain.model.UserProgress
import com.esec.examprep.presentation.theme.CorrectGreen
import com.esec.examprep.presentation.theme.WrongRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onBack: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val progress by viewModel.progress.collectAsState()
    var showClearDialog by remember { mutableStateOf(false) }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title   = { Text("Clear all progress?") },
            text    = { Text("This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.clearAllProgress(); showClearDialog = false
                }) { Text("Clear") }
            },
            dismissButton = { TextButton(onClick = { showClearDialog = false }) { Text("Cancel") } },
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Progress") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    if (progress.isNotEmpty()) {
                        TextButton(onClick = { showClearDialog = true }) { Text("Clear") }
                    }
                },
            )
        },
    ) { padding ->
        if (progress.isEmpty()) {
            Column(
                modifier = Modifier.padding(padding).padding(32.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                Text("No exams taken yet.", style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(padding),
            ) {
                items(progress, key = { it.subjectId }) { p ->
                    ProgressCard(p)
                }
            }
        }
    }
}

@Composable
private fun ProgressCard(progress: UserProgress) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(progress.subjectName, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                LabeledStat("Attempts", "${progress.totalAttempts}")
                LabeledStat("Best", "${progress.bestScore.toInt()}%")
                LabeledStat("Average", "${progress.averageScore.toInt()}%")
            }
            Spacer(Modifier.height(8.dp))
            Text("Average score", style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            LinearProgressIndicator(
                progress  = { (progress.averageScore / 100f).coerceIn(0f, 1f) },
                modifier  = Modifier.fillMaxWidth().height(8.dp),
                color     = if (progress.averageScore >= 50f) CorrectGreen else WrongRed,
                strokeCap = StrokeCap.Round,
            )
        }
    }
}

@Composable
private fun LabeledStat(label: String, value: String) {
    Column {
        Text(value, style = MaterialTheme.typography.titleMedium)
        Text(label, style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
