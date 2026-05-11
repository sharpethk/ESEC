/**
 * Maps to the bento grid tiles on /home_gamified_variant/code.html
 * (`bento-grid`, `bento-wide`, `bento-tall`).
 * Design tokens from /stitch_erixam_exam_companion/erixam_design_system/DESIGN.md.
 */
package com.esec.examprep.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.esec.examprep.presentation.theme.ESECTheme

/**
 * Visual span hint — useful for documentation but layout is owned by the
 * parent grid. `Single` and `Square` clamp to 1:1, `Wide` and `Tall` let
 * the caller dictate width/height via the modifier they pass in.
 */
enum class BentoSpan { Single, Wide, Tall, Square }

@Composable
fun BentoCard(
    span: BentoSpan = BentoSpan.Single,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerLowest,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    contentPadding: androidx.compose.ui.unit.Dp = 16.dp,
    content: @Composable BoxScope.() -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale = if (pressed && onClick != null) 0.98f else 1f

    val aspectModifier = when (span) {
        BentoSpan.Square, BentoSpan.Single -> Modifier.aspectRatio(1f)
        else -> Modifier
    }

    Box(
        modifier = modifier
            .then(aspectModifier)
            .scale(scale)
            .clip(MaterialTheme.shapes.large)
            .background(containerColor)
            .then(
                if (onClick != null) Modifier.clickable(
                    interactionSource = interaction,
                    indication = null,
                    onClick = onClick,
                ) else Modifier
            )
            .padding(contentPadding),
        content = content,
    )
}

@Preview(showBackground = true)
@Composable
private fun BentoCardPreview() {
    ESECTheme {
        val scheme = MaterialTheme.colorScheme
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(16.dp),
        ) {
            BentoCard(
                span = BentoSpan.Wide,
                containerColor = scheme.tertiary,
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = "5 Day Learning Streak",
                    style = MaterialTheme.typography.titleLarge,
                    color = scheme.onTertiary,
                    modifier = Modifier.align(Alignment.BottomStart),
                )
            }
            BentoCard(
                containerColor = scheme.secondaryContainer,
                onClick = {},
                modifier = Modifier.fillMaxWidth(0.5f),
            ) {
                Text(
                    text = "Grade 12",
                    style = MaterialTheme.typography.titleLarge,
                    color = scheme.onSecondaryContainer,
                    modifier = Modifier.align(Alignment.BottomStart),
                )
            }
        }
    }
}
