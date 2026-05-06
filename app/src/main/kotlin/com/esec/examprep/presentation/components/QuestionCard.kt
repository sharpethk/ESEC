package com.esec.examprep.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.esec.examprep.domain.model.Question

@Composable
fun QuestionCard(
    question: Question,
    questionIndex: Int,
    totalQuestions: Int,
    selectedOptionId: String?,
    revealAnswer: Boolean,
    onOptionSelected: (String) -> Unit,
    onToggleBookmark: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text  = "Question ${questionIndex + 1} of $totalQuestions",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
            )
            IconButton(onClick = onToggleBookmark, modifier = Modifier.size(32.dp)) {
                Icon(
                    imageVector = if (question.isBookmarked) Icons.Filled.Bookmark
                                  else Icons.Outlined.BookmarkBorder,
                    contentDescription = if (question.isBookmarked) "Remove bookmark" else "Bookmark question",
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text  = question.text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(Modifier.height(16.dp))
        question.options.forEach { option ->
            OptionItem(
                option           = option,
                isSelected       = option.id == selectedOptionId,
                isCorrect        = option.id == question.correctOptionId,
                revealAnswer     = revealAnswer,
                onSelected       = { onOptionSelected(option.id) },
                modifier         = Modifier.padding(vertical = 4.dp),
            )
        }
        if (revealAnswer && question.explanation != null) {
            Spacer(Modifier.height(12.dp))
            ExplanationCard(text = question.explanation)
        }
    }
}

@Composable
private fun ExplanationCard(text: String) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text      = "Explanation: $text",
            style     = MaterialTheme.typography.bodySmall,
            color     = MaterialTheme.colorScheme.onSurface,
            modifier  = Modifier.padding(12.dp),
        )
    }
}
