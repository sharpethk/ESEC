package com.esec.examprep.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.esec.examprep.presentation.theme.CorrectGreen
import com.esec.examprep.presentation.theme.WrongRed

@Composable
fun ScoreRing(
    scorePercent: Float,
    size: Dp = 160.dp,
    strokeWidth: Dp = 12.dp,
    modifier: Modifier = Modifier,
) {
    val animatedAngle = remember { Animatable(0f) }
    LaunchedEffect(scorePercent) {
        animatedAngle.animateTo(
            targetValue    = scorePercent / 100f * 360f,
            animationSpec  = tween(durationMillis = 1200),
        )
    }

    val trackColor = MaterialTheme.colorScheme.surfaceVariant
    val arcColor   = if (scorePercent >= 50f) CorrectGreen else WrongRed

    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(size)) {
            val stroke = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            val inset  = strokeWidth.toPx() / 2
            val arcRect = Size(this.size.width - inset * 2, this.size.height - inset * 2)
            val topLeft = Offset(inset, inset)
            // Track
            drawArc(color = trackColor, startAngle = -90f, sweepAngle = 360f,
                useCenter = false, topLeft = topLeft, size = arcRect, style = stroke)
            // Progress
            drawArc(color = arcColor, startAngle = -90f, sweepAngle = animatedAngle.value,
                useCenter = false, topLeft = topLeft, size = arcRect, style = stroke)
        }
        Text(
            text  = "${scorePercent.toInt()}%",
            style = MaterialTheme.typography.headlineMedium,
            color = arcColor,
        )
    }
}
