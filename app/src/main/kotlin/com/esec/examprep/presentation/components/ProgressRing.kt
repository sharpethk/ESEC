/**
 * Maps to the "Overall Mastery 65%" ring in the tall bento tile of
 * /home_gamified_variant/code.html and the score ring on
 * /exam_results_gamified_style/code.html.
 * Design tokens from /stitch_erixam_exam_companion/erixam_design_system/DESIGN.md.
 */
package com.esec.examprep.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.esec.examprep.presentation.theme.ESECTheme

@Composable
fun ProgressRing(
    progress: Float,
    modifier: Modifier = Modifier,
    size: Dp = 96.dp,
    strokeWidth: Dp = 8.dp,
    centerLabel: String? = null,
    trackColor: Color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f),
    progressColor: Color = MaterialTheme.colorScheme.onPrimary,
) {
    val clamped = progress.coerceIn(0f, 1f)
    val animated by animateFloatAsState(
        targetValue = clamped,
        animationSpec = tween(durationMillis = 600),
        label = "ProgressRing",
    )
    val percentText = "${(clamped * 100).toInt()}%"
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size)
            .semantics {
                contentDescription = centerLabel?.let { "$it $percentText" } ?: "Progress $percentText"
            },
    ) {
        Canvas(modifier = Modifier.size(size).clearAndSetSemantics { }) {
            val stroke = strokeWidth.toPx()
            val arcSize = Size(this.size.width - stroke, this.size.height - stroke)
            val topLeft = Offset(stroke / 2f, stroke / 2f)
            drawArc(
                color = trackColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke),
            )
            drawArc(
                color = progressColor,
                startAngle = -90f,
                sweepAngle = 360f * animated,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke),
            )
        }
        if (centerLabel != null) {
            Text(
                text = centerLabel,
                style = MaterialTheme.typography.headlineMedium,
                color = progressColor,
            )
        } else {
            Text(
                text = percentText,
                style = MaterialTheme.typography.headlineMedium,
                color = progressColor,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProgressRingPreview() {
    ESECTheme {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(16.dp),
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.shapes.large)
                    .padding(16.dp),
            ) {
                ProgressRing(progress = 0.65f)
            }
            ProgressRing(progress = 0.92f, centerLabel = "92%")
            ProgressRing(progress = 0.15f, size = 64.dp, strokeWidth = 6.dp)
        }
    }
}
