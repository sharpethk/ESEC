/**
 * Maps to /stitch_erixam_exam_companion/exam_practice_gamified_variant/code.html
 * Design tokens from /stitch_erixam_exam_companion/erixam_design_system/DESIGN.md.
 *
 * Gamified question runner: ACTIVE QUEST progress bar, bento question
 * card with optional image + faded science glyph, lettered MCQ options
 * with vibrant selected state, streak-at-risk nudge, SUBMIT ANSWER CTA,
 * Previous/Next floating control. Sibling to existing ExamScreen.kt.
 */
package com.esec.examprep.presentation.exam

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.esec.examprep.presentation.theme.ESECTheme
import com.esec.examprep.presentation.theme.Radius
import com.esec.examprep.presentation.theme.Spacing

// -------- UI Models --------

@Immutable
data class PracticeOption(
    val letter: String,
    val text: String,
)

enum class StreakNudge { None, AtRisk }

@Immutable
data class ExamPracticeUiState(
    val examTitle: String,
    val questionIndex: Int,        // 0-based
    val totalQuestions: Int,
    val xpReward: Int,
    val streakCount: Int,
    val timerLabel: String,        // e.g. "18:45"
    val questionText: String,
    val imageUrl: String? = null,
    val options: List<PracticeOption>,
    val selectedLetter: String? = null,
    val nudge: StreakNudge = StreakNudge.None,
    val canSubmit: Boolean = false,
    val canGoPrevious: Boolean = true,
    val canGoNext: Boolean = true,
)

// -------- Stateful entry --------

@Composable
fun ExamPracticeScreen(
    state: ExamPracticeUiState,
    onMenuClick: () -> Unit,
    onOptionSelect: (String) -> Unit,
    onSubmit: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
) {
    ExamPracticeContent(
        state = state,
        onMenuClick = onMenuClick,
        onOptionSelect = onOptionSelect,
        onSubmit = onSubmit,
        onPrevious = onPrevious,
        onNext = onNext,
    )
}

// -------- Stateless content --------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExamPracticeContent(
    state: ExamPracticeUiState,
    onMenuClick: () -> Unit,
    onOptionSelect: (String) -> Unit,
    onSubmit: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "EriXam",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(
                            Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                },
                actions = {
                    StreakPill(count = state.streakCount)
                    Spacer(Modifier.size(Spacing.sm))
                    TimerPill(label = state.timerLabel)
                    Spacer(Modifier.size(Spacing.sm))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
        bottomBar = {
            BottomActions(
                canPrevious = state.canGoPrevious,
                canNext = state.canGoNext,
                onPrevious = onPrevious,
                onNext = onNext,
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(
                    PaddingValues(
                        start = Spacing.lg,
                        end = Spacing.lg,
                        top = Spacing.md,
                        bottom = Spacing.xxxl,
                    ),
                ),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg),
        ) {
            ProgressHeader(state = state)
            QuestionCard(text = state.questionText, imageUrl = state.imageUrl)
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
                state.options.forEach { opt ->
                    OptionRow(
                        option = opt,
                        isSelected = opt.letter == state.selectedLetter,
                        onClick = { onOptionSelect(opt.letter) },
                    )
                }
            }
            if (state.nudge == StreakNudge.AtRisk) {
                StreakNudgeRow()
            }
            SubmitButton(enabled = state.canSubmit, onClick = onSubmit)
        }
    }
}

// -------- Top bar pills --------

@Composable
private fun StreakPill(count: Int) {
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.tertiaryContainer)
            .border(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f), CircleShape)
            .padding(horizontal = Spacing.sm, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Icon(
            Icons.Default.LocalFireDepartment,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onTertiaryContainer,
            modifier = Modifier.size(16.dp),
        )
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onTertiaryContainer,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun TimerPill(label: String) {
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.errorContainer)
            .padding(horizontal = Spacing.sm, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Icon(
            Icons.Default.Timer,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onErrorContainer,
            modifier = Modifier.size(16.dp),
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onErrorContainer,
            fontWeight = FontWeight.Bold,
        )
    }
}

// -------- Progress header --------

@Composable
private fun ProgressHeader(state: ExamPracticeUiState) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "ACTIVE QUEST",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f))
                        .padding(horizontal = Spacing.sm, vertical = 2.dp),
                )
                Text(
                    text = "Question ${state.questionIndex + 1} of ${state.totalQuestions}",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(16.dp),
                    )
                    Text(
                        text = "+${state.xpReward} XP",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Text(
                    text = state.examTitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        val progress = if (state.totalQuestions == 0) 0f
        else (state.questionIndex + 1).toFloat() / state.totalQuestions.toFloat()
        LinearProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
                .clip(CircleShape),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceContainer,
        )
    }
}

// -------- Question bento card --------

@Composable
private fun QuestionCard(text: String, imageUrl: String?) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radius.lg))
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.30f),
                shape = RoundedCornerShape(Radius.lg),
            ),
    ) {
        Icon(
            Icons.Default.Science,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
            modifier = Modifier
                .size(80.dp)
                .align(Alignment.TopEnd)
                .padding(Spacing.md),
        )
        Column(
            modifier = Modifier.padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium,
            )
            if (!imageUrl.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(176.dp)
                        .clip(RoundedCornerShape(Radius.md))
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(Radius.md),
                        ),
                ) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }
    }
}

// -------- Option row --------

@Composable
private fun OptionRow(
    option: PracticeOption,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.outlineVariant
    val borderWidth = if (isSelected) 2.dp else 1.dp
    val container = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
    else MaterialTheme.colorScheme.surfaceContainerLowest

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radius.lg))
            .background(container)
            .border(borderWidth, borderColor, RoundedCornerShape(Radius.lg))
            .clickable(onClick = onClick)
            .padding(Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(Radius.md))
                .background(
                    if (isSelected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surfaceContainer,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = option.letter,
                style = MaterialTheme.typography.titleMedium,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.ExtraBold,
            )
        }
        Spacer(Modifier.size(Spacing.md))
        Text(
            text = option.text,
            style = MaterialTheme.typography.bodyLarge,
            color = if (isSelected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurface,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
            modifier = Modifier.weight(1f),
        )
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}

// -------- Streak nudge --------

@Composable
private fun StreakNudgeRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Icon(
            Icons.Default.Warning,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.size(16.dp),
        )
        Spacer(Modifier.size(Spacing.xs))
        Text(
            text = "Streak at risk! Submit soon to keep the fire burning.",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.tertiary,
            fontWeight = FontWeight.Bold,
        )
    }
}

// -------- Submit --------

@Composable
private fun SubmitButton(enabled: Boolean, onClick: () -> Unit) {
    val gradient = Brush.horizontalGradient(
        colors = if (enabled) listOf(
            MaterialTheme.colorScheme.secondary,
            MaterialTheme.colorScheme.secondary,
        ) else listOf(
            MaterialTheme.colorScheme.surfaceContainerHigh,
            MaterialTheme.colorScheme.surfaceContainerHigh,
        ),
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radius.lg))
            .background(gradient)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(vertical = Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Icon(
            Icons.AutoMirrored.Filled.Send,
            contentDescription = null,
            tint = if (enabled) MaterialTheme.colorScheme.onSecondary
            else MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.size(Spacing.sm))
        Text(
            text = "SUBMIT ANSWER",
            style = MaterialTheme.typography.labelLarge,
            color = if (enabled) MaterialTheme.colorScheme.onSecondary
            else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold,
        )
    }
}

// -------- Bottom Previous/Next --------

@Composable
private fun BottomActions(
    canPrevious: Boolean,
    canNext: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = Spacing.lg, vertical = Spacing.md),
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        OutlinedButton(
            onClick = onPrevious,
            enabled = canPrevious,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(Radius.md),
        ) {
            Icon(Icons.Default.ChevronLeft, contentDescription = null)
            Spacer(Modifier.size(Spacing.xs))
            Text("Previous", style = MaterialTheme.typography.labelLarge)
        }
        Button(
            onClick = onNext,
            enabled = canNext,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(Radius.md),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ),
        ) {
            Text("Next", style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.size(Spacing.xs))
            Icon(Icons.Default.ChevronRight, contentDescription = null)
        }
    }
}

// -------- Preview --------

@Preview(showBackground = true, heightDp = 1100)
@Composable
private fun ExamPracticePreview() {
    ESECTheme {
        ExamPracticeContent(
            state = ExamPracticeUiState(
                examTitle = "Biology Exam",
                questionIndex = 11,
                totalQuestions = 50,
                xpReward = 50,
                streakCount = 7,
                timerLabel = "18:45",
                questionText = "Which organelle is primarily responsible for the production of adenosine triphosphate (ATP) during aerobic respiration in eukaryotic cells?",
                imageUrl = null,
                options = listOf(
                    PracticeOption("A", "Golgi Apparatus"),
                    PracticeOption("B", "Mitochondria"),
                    PracticeOption("C", "Chloroplast"),
                    PracticeOption("D", "Lysosome"),
                ),
                selectedLetter = "B",
                nudge = StreakNudge.AtRisk,
                canSubmit = true,
                canGoPrevious = true,
                canGoNext = true,
            ),
            onMenuClick = {},
            onOptionSelect = {},
            onSubmit = {},
            onPrevious = {},
            onNext = {},
        )
    }
}

@Suppress("unused")
private val _unusedColorRef: Color = Color.Transparent
