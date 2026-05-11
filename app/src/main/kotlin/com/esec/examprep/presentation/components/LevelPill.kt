/**
 * Maps to the "LEVEL 12" / "LEVEL 8" pills on the subject bento cards in
 * /home_gamified_variant/code.html and /subjects_gamified_style/code.html.
 * Design tokens from /stitch_erixam_exam_companion/erixam_design_system/DESIGN.md.
 */
package com.esec.examprep.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.esec.examprep.presentation.theme.ESECTheme

enum class PillVariant { Primary, Secondary, Outlined }

@Composable
fun LevelPill(
    level: Int,
    variant: PillVariant,
    modifier: Modifier = Modifier,
) {
    val scheme = MaterialTheme.colorScheme
    val container: Color
    val content: Color
    val border: BorderStroke?
    when (variant) {
        PillVariant.Primary -> {
            container = scheme.primary
            content = scheme.onPrimary
            border = null
        }
        PillVariant.Secondary -> {
            container = scheme.onSecondaryContainer
            content = scheme.secondaryContainer
            border = null
        }
        PillVariant.Outlined -> {
            container = Color.Transparent
            content = scheme.primary
            border = BorderStroke(1.dp, scheme.outlineVariant)
        }
    }
    Row(
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .clip(CircleShape)
            .then(if (border != null) Modifier.border(border, CircleShape) else Modifier)
            .background(container)
            .padding(horizontal = 8.dp, vertical = 2.dp),
    ) {
        Text(
            text = "LEVEL $level",
            style = MaterialTheme.typography.labelSmall,
            color = content,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LevelPillPreview() {
    ESECTheme {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(16.dp),
        ) {
            LevelPill(level = 12, variant = PillVariant.Primary)
            LevelPill(level = 8, variant = PillVariant.Secondary)
            LevelPill(level = 3, variant = PillVariant.Outlined)
        }
    }
}
