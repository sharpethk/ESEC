package com.esec.examprep.presentation.result

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.esec.examprep.domain.model.ExamMode
import com.esec.examprep.domain.model.ExamResult
import com.esec.examprep.domain.model.QuestionResult
import com.esec.examprep.presentation.components.ScoreRing
import com.esec.examprep.presentation.theme.CorrectGreen
import com.esec.examprep.presentation.theme.CorrectGreenLight
import com.esec.examprep.presentation.theme.WrongRed
import com.esec.examprep.presentation.theme.WrongRedLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    sessionId: String,
    onRetry: (subjectId: String, mode: String) -> Unit,
    onHome: () -> Unit,
    viewModel: ResultViewModel = hiltViewModel(),
) {
    val result by viewModel.result.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Results") }) },
    ) { padding ->
        result?.let { r ->
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(padding).fillMaxSize(),
            ) {
                item { ScoreSummaryCard(r) }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick  = { onRetry(r.subjectId, ExamMode.TIMED.name) },
                            modifier = Modifier.weight(1f),
                        ) {
                            Icon(Icons.Default.Refresh, null, Modifier.size(18.dp))
                            Spacer(Modifier.size(4.dp))
                            Text("Retry")
                        }
                        OutlinedButton(
                            onClick  = onHome,
                            modifier = Modifier.weight(1f),
                        ) {
                            Icon(Icons.Default.Home, null, Modifier.size(18.dp))
                            Spacer(Modifier.size(4.dp))
                            Text("Home")
                        }
                    }
                }
                item { Text("Question Review", style = MaterialTheme.typography.titleMedium) }
                items(r.questionBreakdown) { qr -> QuestionReviewItem(qr) }
            }
        }
    }
}

@Composable
private fun ScoreSummaryCard(result: ExamResult) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(24.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(result.subjectName, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))
            ScoreRing(scorePercent = result.scorePercent)
            Spacer(Modifier.height(16.dp))
            Text(
                text  = if (result.passed) "PASSED" else "FAILED",
                style = MaterialTheme.typography.headlineSmall,
                color = if (result.passed) CorrectGreen else WrongRed,
            )
            Spacer(Modifier.height(16.dp))
            Divider()
            Spacer(Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                StatItem("Correct",   "${result.correctAnswers}",   CorrectGreen)
                StatItem("Wrong",     "${result.incorrectAnswers}", WrongRed)
                StatItem("Skipped",   "${result.skippedAnswers}",   MaterialTheme.colorScheme.onSurfaceVariant)
                StatItem("Time",      formatDuration(result.durationSeconds), MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String, color: androidx.compose.ui.graphics.Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleLarge, color = color)
        Text(label, style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun QuestionReviewItem(qr: QuestionResult) {
    val bgColor = when {
        qr.isCorrect              -> CorrectGreenLight
        qr.selectedOptionId == null -> MaterialTheme.colorScheme.surfaceVariant
        else                      -> WrongRedLight
    }
    Card(
        colors   = CardDefaults.cardColors(containerColor = bgColor),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = if (qr.isCorrect) CorrectGreen else WrongRed,
                modifier = Modifier.size(20.dp),
            )
            Column {
                Text(qr.question.text, style = MaterialTheme.typography.bodySmall)
                val correctText = qr.question.options
                    .firstOrNull { it.id == qr.question.correctOptionId }?.text.orEmpty()
                Text("Answer: $correctText",
                    style = MaterialTheme.typography.labelSmall,
                    color = CorrectGreen)
            }
        }
    }
}

private fun formatDuration(seconds: Long): String {
    val m = seconds / 60; val s = seconds % 60
    return "%02d:%02d".format(m, s)
}
