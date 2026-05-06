package com.esec.examprep.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.esec.examprep.domain.model.Option
import com.esec.examprep.presentation.theme.CorrectGreen
import com.esec.examprep.presentation.theme.CorrectGreenLight
import com.esec.examprep.presentation.theme.WrongRed
import com.esec.examprep.presentation.theme.WrongRedLight

@Composable
fun OptionItem(
    option: Option,
    isSelected: Boolean,
    isCorrect: Boolean,
    revealAnswer: Boolean,
    onSelected: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val targetBg by animateColorAsState(
        targetValue = when {
            revealAnswer && isCorrect   -> CorrectGreenLight
            revealAnswer && isSelected  -> WrongRedLight
            isSelected                  -> MaterialTheme.colorScheme.primaryContainer
            else                        -> MaterialTheme.colorScheme.surface
        },
        animationSpec = tween(300),
        label = "optionBg",
    )
    val targetBorder by animateColorAsState(
        targetValue = when {
            revealAnswer && isCorrect   -> CorrectGreen
            revealAnswer && isSelected  -> WrongRed
            isSelected                  -> MaterialTheme.colorScheme.primary
            else                        -> MaterialTheme.colorScheme.outline
        },
        animationSpec = tween(300),
        label = "optionBorder",
    )

    Card(
        colors  = CardDefaults.cardColors(containerColor = targetBg),
        border  = BorderStroke(1.5.dp, targetBorder),
        shape   = RoundedCornerShape(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = !revealAnswer) { onSelected() },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
            Text(
                text  = option.text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}
