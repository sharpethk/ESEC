package com.esec.examprep.presentation.questiondetail

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.esec.examprep.domain.model.Question
import com.esec.examprep.presentation.components.OptionItem
import com.esec.examprep.presentation.components.StatusPill
import com.esec.examprep.presentation.theme.CorrectGreen
import com.esec.examprep.presentation.theme.Elevation
import com.esec.examprep.presentation.theme.Radius
import com.esec.examprep.presentation.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionDetailScreen(
    onBack: () -> Unit,
    viewModel: QuestionDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val q = state.question

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Question", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (q != null) {
                        IconButton(onClick = viewModel::toggleBookmark) {
                            Icon(
                                imageVector = if (q.isBookmarked) Icons.Filled.Bookmark
                                else Icons.Outlined.BookmarkBorder,
                                contentDescription =
                                    if (q.isBookmarked) "Remove bookmark" else "Add bookmark",
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                },
            )
        },
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when {
                state.isLoading -> CenteredLoading()
                state.notFound || q == null -> NotFoundState()
                else -> DetailContent(question = q, subjectName = state.subjectName)
            }
        }
    }
}

@Composable
private fun DetailContent(question: Question, subjectName: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            StatusPill(text = subjectName, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.size(Spacing.sm))
            if (question.year > 0) {
                StatusPill(
                    text = "Year ${question.year}",
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            Spacer(Modifier.size(Spacing.sm))
            StatusPill(
                text = question.difficultyLevel.name.lowercase()
                    .replaceFirstChar { it.uppercase() },
                color = MaterialTheme.colorScheme.tertiary,
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(Radius.lg),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = Elevation.xs),
        ) {
            Text(
                question.text,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(Spacing.lg),
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            question.options.forEach { option ->
                OptionItem(
                    option = option,
                    isSelected = false,
                    isCorrect = option.id == question.correctOptionId,
                    revealAnswer = true,
                    onSelected = {},
                )
            }
        }

        val correctText = question.options
            .firstOrNull { it.id == question.correctOptionId }?.text.orEmpty()
        Text(
            "Correct answer: $correctText",
            style = MaterialTheme.typography.labelMedium,
            color = CorrectGreen,
            fontWeight = FontWeight.SemiBold,
        )

        val explanation = question.explanation?.takeIf { it.isNotBlank() }
        if (explanation != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                        RoundedCornerShape(Radius.md),
                    )
                    .padding(Spacing.lg),
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(Modifier.size(Spacing.sm))
                    Column {
                        Text(
                            "Explanation",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Spacer(Modifier.height(Spacing.xs))
                        Text(
                            explanation,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CenteredLoading() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        CircularProgressIndicator(modifier = Modifier.size(48.dp), strokeWidth = 4.dp)
    }
}

@Composable
private fun NotFoundState() {
    Column(
        modifier = Modifier.fillMaxSize().padding(Spacing.xxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            "Question not found",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
        )
    }
}
