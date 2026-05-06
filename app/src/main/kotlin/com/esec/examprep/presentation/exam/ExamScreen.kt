package com.esec.examprep.presentation.exam

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.esec.examprep.presentation.components.QuestionCard
import com.esec.examprep.presentation.components.TimerBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamScreen(
    subjectId: String,
    mode: String,
    onFinished: (sessionId: String) -> Unit,
    onBack: () -> Unit,
    viewModel: ExamViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.isFinished, state.resultSessionId) {
        if (state.isFinished && state.resultSessionId != null) {
            onFinished(state.resultSessionId!!)
        }
    }

    BackHandler { viewModel.showExitDialog(true) }

    if (state.showExitDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.showExitDialog(false) },
            title   = { Text("Abandon exam?") },
            text    = { Text("Your progress will be lost.") },
            confirmButton = { TextButton(onClick = onBack) { Text("Leave") } },
            dismissButton = { TextButton(onClick = { viewModel.showExitDialog(false) }) { Text("Stay") } },
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("${state.currentIndex + 1} / ${state.questions.size}") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.showExitDialog(true) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Exit")
                    }
                },
                actions = {
                    TextButton(onClick = viewModel::submitExam) { Text("Submit") }
                },
            )
        },
    ) { padding ->
        when {
            state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState()),
                ) {
                    LinearProgressIndicator(
                        progress  = { state.progress },
                        modifier  = Modifier.fillMaxWidth(),
                        strokeCap = StrokeCap.Round,
                    )
                    Spacer(Modifier.height(12.dp))
                    if (state.isTimedMode) {
                        TimerBar(
                            remainingSeconds = state.remainingSeconds,
                            totalSeconds     = state.timeLimitSeconds,
                        )
                        Spacer(Modifier.height(12.dp))
                    }
                    state.currentQuestion?.let { q ->
                        QuestionCard(
                            question         = q,
                            questionIndex    = state.currentIndex,
                            totalQuestions   = state.questions.size,
                            selectedOptionId = state.answers[q.id],
                            revealAnswer     = state.mode == com.esec.examprep.domain.model.ExamMode.PRACTICE,
                            onOptionSelected = viewModel::selectAnswer,
                        )
                    }
                    Spacer(Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        OutlinedButton(
                            onClick  = viewModel::previousQuestion,
                            enabled  = state.currentIndex > 0,
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                            Text("Prev")
                        }
                        if (state.isLastQuestion) {
                            Button(onClick = viewModel::submitExam) { Text("Finish") }
                        } else {
                            Button(onClick = viewModel::nextQuestion) {
                                Text("Next")
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, null)
                            }
                        }
                    }
                    Spacer(Modifier.height(32.dp))
                }
            }
        }
    }
}
