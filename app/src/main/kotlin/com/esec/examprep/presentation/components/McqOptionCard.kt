/**
 * Maps to the MCQ option rows in /exam_practice_gamified_variant/code.html.
 * Idle / Selected / Correct / Incorrect states all use Material 3 container
 * colors so they remain accessible in dark mode.
 * Design tokens from /stitch_erixam_exam_companion/erixam_design_system/DESIGN.md.
 */
package com.esec.examprep.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.esec.examprep.presentation.theme.ESECTheme

enum class McqState { Idle, Selected, Correct, Incorrect }

@Composable
fun McqOptionCard(
    letter: Char,
    text: String,
    state: McqState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scheme = MaterialTheme.colorScheme
    val container: Color = when (state) {
        McqState.Idle      -> scheme.surfaceContainerLowest
        McqState.Selected  -> scheme.primaryContainer.copy(alpha = 0.2f)
        McqState.Correct   -> scheme.secondaryContainer
        McqState.Incorrect -> scheme.tertiaryContainer.copy(alpha = 0.3f)
    }
    val content: Color = when (state) {
        McqState.Idle      -> scheme.onSurface
        McqState.Selected  -> scheme.onPrimaryContainer
        McqState.Correct   -> scheme.onSecondaryContainer
        McqState.Incorrect -> scheme.onTertiaryContainer
    }
    val borderColor: Color = when (state) {
        McqState.Idle      -> scheme.outlineVariant
        McqState.Selected  -> scheme.primary
        McqState.Correct   -> scheme.secondary
        McqState.Incorrect -> scheme.tertiary
    }
    val trailing: ImageVector? = when (state) {
        McqState.Correct   -> Icons.Filled.CheckCircle
        McqState.Incorrect -> Icons.Filled.Cancel
        else -> null
    }

    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale = if (pressed) 0.98f else 1f

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(MaterialTheme.shapes.large)
            .background(container)
            .border(1.dp, borderColor, MaterialTheme.shapes.large)
            .clickable(
                interactionSource = interaction,
                indication = null,
                onClick = onClick,
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(borderColor.copy(alpha = 0.18f)),
        ) {
            Text(
                text = letter.toString(),
                style = MaterialTheme.typography.labelLarge,
                color = borderColor,
            )
        }
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = content,
            modifier = Modifier.fillMaxWidth(if (trailing != null) 0.85f else 1f),
        )
        if (trailing != null) {
            Icon(
                imageVector = trailing,
                contentDescription = if (state == McqState.Correct) "Correct" else "Incorrect",
                tint = borderColor,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun McqOptionCardPreview() {
    ESECTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(16.dp),
        ) {
            McqOptionCard('A', "The mitochondria is the powerhouse of the cell.", McqState.Idle, {})
            McqOptionCard('B', "Photosynthesis happens in the chloroplast.",      McqState.Selected, {})
            McqOptionCard('C', "Ribosomes synthesise proteins.",                  McqState.Correct, {})
            McqOptionCard('D', "The nucleus stores fat.",                         McqState.Incorrect, {})
        }
    }
}
