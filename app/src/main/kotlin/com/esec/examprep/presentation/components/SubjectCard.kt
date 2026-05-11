/**
 * Maps to the subject bento tiles on /subjects_gamified_style/code.html
 * and the "Grade 12" / "Grade 8" tiles on /home_gamified_variant/code.html.
 * Design tokens from /stitch_erixam_exam_companion/erixam_design_system/DESIGN.md.
 */
package com.esec.examprep.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
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

@Immutable
data class SubjectAccent(
    val container: Color,
    val onContainer: Color,
    val ctaLabel: String,
)

@Composable
fun SubjectCard(
    name: String,
    subtitle: String,
    masteryPercent: Int,
    level: Int,
    modulesUnlocked: String,
    accent: SubjectAccent,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Filled.School,
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale = if (pressed) 0.98f else 1f

    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .scale(scale)
            .clip(MaterialTheme.shapes.large)
            .background(accent.container)
            .clickable(
                interactionSource = interaction,
                indication = null,
                onClick = onClick,
            )
            .padding(16.dp),
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accent.onContainer,
                modifier = Modifier.size(28.dp),
            )
            LevelPill(level = level, variant = PillVariant.Secondary)
        }
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleLarge,
                color = accent.onContainer,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = accent.onContainer.copy(alpha = 0.8f),
            )
            Text(
                text = "Mastery: $masteryPercent%",
                style = MaterialTheme.typography.labelSmall,
                color = accent.onContainer.copy(alpha = 0.8f),
            )
            Text(
                text = modulesUnlocked,
                style = MaterialTheme.typography.labelSmall,
                color = accent.onContainer.copy(alpha = 0.7f),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SubjectCardPreview() {
    ESECTheme {
        val scheme = MaterialTheme.colorScheme
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(16.dp),
        ) {
            SubjectCardPreviewItem(scheme, Modifier.weight(1f))
            SubjectCardPreviewItem(scheme, Modifier.weight(1f), secondary = false)
        }
    }
}

@Composable
private fun RowScope.SubjectCardPreviewItem(
    scheme: androidx.compose.material3.ColorScheme,
    modifier: Modifier,
    secondary: Boolean = true,
) {
    if (secondary) {
        SubjectCard(
            name = "Grade 12",
            subtitle = "ESLCE",
            masteryPercent = 72,
            level = 12,
            modulesUnlocked = "15/18 Modules unlocked",
            accent = SubjectAccent(scheme.secondaryContainer, scheme.onSecondaryContainer, "Continue"),
            onClick = {},
            modifier = modifier,
        )
    } else {
        SubjectCard(
            name = "Grade 8",
            subtitle = "Matriculation",
            masteryPercent = 48,
            level = 8,
            modulesUnlocked = "9/18 Modules unlocked",
            accent = SubjectAccent(scheme.surfaceContainerHighest, scheme.primary, "Continue"),
            onClick = {},
            icon = Icons.Outlined.School,
            modifier = modifier,
        )
    }
}
