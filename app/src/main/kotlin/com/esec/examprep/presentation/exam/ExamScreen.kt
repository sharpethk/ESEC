package com.esec.examprep.presentation.exam

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.esec.examprep.R
import com.esec.examprep.presentation.components.QuestionCard
import com.esec.examprep.presentation.components.TimerBar
import com.esec.examprep.presentation.theme.Radius
import com.esec.examprep.presentation.theme.Spacing

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
            title   = { Text(stringResource(R.string.exam_exit_dialog_title)) },
            text    = { Text(stringResource(R.string.exam_exit_dialog_message)) },
            confirmButton = {
                TextButton(onClick = onBack) {
                    Text(
                        stringResource(R.string.exam_exit_dialog_leave),
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.showExitDialog(false) }) {
                    Text(stringResource(R.string.exam_exit_dialog_stay))
                }
            },
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            stringResource(R.string.exam_question_index, state.currentIndex + 1),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            stringResource(R.string.exam_question_of, state.questions.size),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.showExitDialog(true) }) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = stringResource(R.string.cd_exit),
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = viewModel::submitExam,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary,
                        ),
                    ) {
                        Icon(Icons.Default.Check, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.size(Spacing.xs))
                        Text(
                            stringResource(R.string.exam_action_submit),
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
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
                        .padding(horizontal = Spacing.lg)
                        .verticalScroll(rememberScrollState()),
                ) {
                    LinearProgressIndicator(
                        progress  = { state.progress },
                        modifier  = Modifier.fillMaxWidth().height(6.dp),
                        color     = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        strokeCap = StrokeCap.Round,
                    )
                    Spacer(Modifier.height(Spacing.md))
                    if (state.isTimedMode) {
                        TimerBar(
                            remainingSeconds = state.remainingSeconds,
                            totalSeconds     = state.timeLimitSeconds,
                        )
                        Spacer(Modifier.height(Spacing.md))
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
                    Spacer(Modifier.height(Spacing.xxl))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                    ) {
                        OutlinedButton(
                            onClick  = viewModel::previousQuestion,
                            enabled  = state.currentIndex > 0,
                            modifier = Modifier.weight(1f),
                            shape    = RoundedCornerShape(Radius.md),
                            contentPadding = PaddingValues(vertical = 12.dp),
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.size(Spacing.xs))
                            Text(stringResource(R.string.exam_action_prev))
                        }
                        if (state.isLastQuestion) {
                            Button(
                                onClick = viewModel::submitExam,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(Radius.md),
                                contentPadding = PaddingValues(vertical = 12.dp),
                            ) {
                                Icon(Icons.Default.Check, null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.size(Spacing.xs))
                                Text(
                                    stringResource(R.string.exam_action_finish),
                                    fontWeight = FontWeight.SemiBold,
                                )
                            }
                        } else {
                            Button(
                                onClick = viewModel::nextQuestion,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(Radius.md),
                                contentPadding = PaddingValues(vertical = 12.dp),
                            ) {
                                Text(
                                    stringResource(R.string.exam_action_next),
                                    fontWeight = FontWeight.SemiBold,
                                )
                                Spacer(Modifier.size(Spacing.xs))
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, null, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                    Spacer(Modifier.height(Spacing.xxxl))
                }
            }
        }
    }
}
