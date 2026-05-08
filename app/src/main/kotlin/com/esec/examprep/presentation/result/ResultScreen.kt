package com.esec.examprep.presentation.result

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.esec.examprep.R
import com.esec.examprep.domain.model.ExamMode
import com.esec.examprep.domain.model.ExamResult
import com.esec.examprep.domain.model.Grade
import com.esec.examprep.domain.model.QuestionResult
import com.esec.examprep.presentation.components.OptionItem
import com.esec.examprep.presentation.components.ScoreRing
import com.esec.examprep.presentation.components.StatusPill
import com.esec.examprep.presentation.share.ShareUtil
import com.esec.examprep.presentation.theme.CorrectGreen
import com.esec.examprep.presentation.theme.Elevation
import com.esec.examprep.presentation.theme.Radius
import com.esec.examprep.presentation.theme.SkippedAmber
import com.esec.examprep.presentation.theme.Spacing
import com.esec.examprep.presentation.theme.WrongRed
import com.esec.examprep.presentation.theme.gradeColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    sessionId: String,
    onRetry: (subjectId: String, mode: String) -> Unit,
    onHome: () -> Unit,
    viewModel: ResultViewModel = hiltViewModel(),
) {
    val result by viewModel.result.collectAsState()
    val context = LocalContext.current

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.result_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                },
                actions = {
                    val r = result
                    if (r != null) {
                        androidx.compose.material3.IconButton(onClick = { ShareUtil.shareExamResult(context, r) }) {
                            Icon(Icons.Default.Share, contentDescription = "Share result")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
    ) { padding ->
        result?.let { r ->
            LazyColumn(
                contentPadding = PaddingValues(Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
                modifier = Modifier.padding(padding).fillMaxSize(),
            ) {
                item { ScoreSummaryCard(r) }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                        Button(
                            onClick = { onRetry(r.subjectId, ExamMode.TIMED.name) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(Radius.md),
                            contentPadding = PaddingValues(vertical = 12.dp),
                        ) {
                            Icon(Icons.Default.Refresh, null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.size(Spacing.xs))
                            Text(
                                stringResource(R.string.result_action_retry),
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                        OutlinedButton(
                            onClick = onHome,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(Radius.md),
                            contentPadding = PaddingValues(vertical = 12.dp),
                        ) {
                            Icon(Icons.Default.Home, null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.size(Spacing.xs))
                            Text(
                                stringResource(R.string.result_action_home),
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                }
                item {
                    Spacer(Modifier.height(Spacing.xs))
                    Text(
                        stringResource(R.string.result_review_header),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                itemsIndexed(r.questionBreakdown) { index, qr ->
                    QuestionReviewItem(
                        questionNumber = index + 1,
                        qr = qr,
                    )
                }
            }
        } ?: Box(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                stringResource(R.string.result_loading),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ScoreSummaryCard(result: ExamResult) {
    val accent = if (result.passed) CorrectGreen else WrongRed
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Radius.xl),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.sm),
    ) {
        Column(
            modifier = Modifier.padding(Spacing.xxl).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                result.subjectName,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            result.year?.let { y ->
                Spacer(Modifier.height(Spacing.xxs))
                Text(
                    stringResource(R.string.result_past_paper_subtitle, y),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Spacer(Modifier.height(Spacing.lg))
            ScoreRing(scorePercent = result.scorePercent)
            Spacer(Modifier.height(Spacing.lg))
            GradeBadge(grade = Grade.fromPercent(result.scorePercent))
            Spacer(Modifier.height(Spacing.md))
            StatusPill(
                text = stringResource(
                    if (result.passed) R.string.result_status_passed else R.string.result_status_failed
                ),
                color = accent,
            )
            Spacer(Modifier.height(Spacing.lg))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(Modifier.height(Spacing.md))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                StatItem(stringResource(R.string.result_stat_correct),
                    "${result.correctAnswers}", CorrectGreen)
                StatItem(stringResource(R.string.result_stat_wrong),
                    "${result.incorrectAnswers}", WrongRed)
                StatItem(stringResource(R.string.result_stat_skipped),
                    "${result.skippedAnswers}", SkippedAmber)
                StatItem(stringResource(R.string.result_stat_time),
                    formatDuration(result.durationSeconds), MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            style = MaterialTheme.typography.titleLarge,
            color = color,
            fontWeight = FontWeight.Bold,
        )
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun QuestionReviewItem(questionNumber: Int, qr: QuestionResult) {
    var expanded by rememberSaveable(qr.question.id) { mutableStateOf(false) }
    val (icon: ImageVector, color: Color, statusRes: Int) = when {
        qr.isCorrect -> Triple(Icons.Default.CheckCircle, CorrectGreen, R.string.result_status_correct)
        qr.selectedOptionId == null -> Triple(Icons.Default.RemoveCircle, SkippedAmber, R.string.result_status_skipped)
        else -> Triple(Icons.Default.Cancel, WrongRed, R.string.result_status_incorrect)
    }
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(250),
        label = "expandRotation",
    )
    val selectedOption = qr.question.options.firstOrNull { it.id == qr.selectedOptionId }
    val correctOption = qr.question.options.firstOrNull { it.id == qr.question.correctOptionId }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(Radius.lg),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.xs),
    ) {
        Column(modifier = Modifier.padding(Spacing.lg)) {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(22.dp),
                )
                Column(Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                    ) {
                        Text(
                            stringResource(R.string.result_question_number, questionNumber),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold,
                        )
                        StatusPill(text = stringResource(statusRes), color = color)
                    }
                    Spacer(Modifier.height(Spacing.xs))
                    Text(
                        qr.question.text,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                    )
                }
                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = stringResource(
                        if (expanded) R.string.cd_collapse_question else R.string.cd_expand_question
                    ),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp).rotate(rotation),
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = Spacing.md)) {
                    qr.question.options.forEach { option ->
                        OptionItem(
                            option = option,
                            isSelected = option.id == qr.selectedOptionId,
                            isCorrect = option.id == qr.question.correctOptionId,
                            revealAnswer = true,
                            onSelected = {},
                            modifier = Modifier.padding(bottom = Spacing.sm),
                        )
                    }

                    if (qr.selectedOptionId != null && !qr.isCorrect) {
                        Spacer(Modifier.height(Spacing.xs))
                        Text(
                            stringResource(
                                R.string.result_your_answer_prefix,
                                selectedOption?.text.orEmpty(),
                            ),
                            style = MaterialTheme.typography.labelSmall,
                            color = WrongRed,
                            fontWeight = FontWeight.SemiBold,
                        )
                    } else if (qr.selectedOptionId == null) {
                        Spacer(Modifier.height(Spacing.xs))
                        Text(
                            stringResource(
                                R.string.result_your_answer_prefix,
                                stringResource(R.string.result_your_answer_skipped),
                            ),
                            style = MaterialTheme.typography.labelSmall,
                            color = SkippedAmber,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                    Spacer(Modifier.height(Spacing.xxs))
                    Text(
                        stringResource(R.string.result_answer_prefix, correctOption?.text.orEmpty()),
                        style = MaterialTheme.typography.labelSmall,
                        color = CorrectGreen,
                        fontWeight = FontWeight.SemiBold,
                    )

                    val explanation = qr.question.explanation?.takeIf { it.isNotBlank() }
                    if (explanation != null) {
                        Spacer(Modifier.height(Spacing.sm))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                                    RoundedCornerShape(Radius.sm),
                                )
                                .padding(Spacing.sm),
                        ) {
                            Row(verticalAlignment = Alignment.Top) {
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp),
                                )
                                Spacer(Modifier.size(Spacing.xs))
                                Column {
                                    Text(
                                        "Explanation",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.primary,
                                    )
                                    Text(
                                        explanation,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GradeBadge(grade: Grade) {
    val color = gradeColor(grade)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        modifier = Modifier
            .background(color.copy(alpha = 0.12f), RoundedCornerShape(Radius.pill))
            .padding(horizontal = Spacing.lg, vertical = Spacing.sm),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                stringResource(R.string.result_grade_label),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                grade.letter,
                style = MaterialTheme.typography.headlineSmall,
                color = color,
                fontWeight = FontWeight.Bold,
            )
        }
        Box(
            modifier = Modifier
                .background(color.copy(alpha = 0.25f))
                .padding(horizontal = 1.dp, vertical = Spacing.lg),
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                stringResource(R.string.result_gpa_label),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                formatGpa(grade.gpa),
                style = MaterialTheme.typography.headlineSmall,
                color = color,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

private fun formatGpa(gpa: Float): String = "%.2f".format(gpa)

private fun formatDuration(seconds: Long): String {
    val m = seconds / 60; val s = seconds % 60
    return "%02d:%02d".format(m, s)
}
