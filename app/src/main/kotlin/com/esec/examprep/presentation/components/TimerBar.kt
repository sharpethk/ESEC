package com.esec.examprep.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import com.esec.examprep.presentation.theme.TimerCritical
import com.esec.examprep.presentation.theme.TimerSafe
import com.esec.examprep.presentation.theme.TimerWarning

@Composable
fun TimerBar(
    remainingSeconds: Int,
    totalSeconds: Int,
    modifier: Modifier = Modifier,
) {
    val fraction = (remainingSeconds.toFloat() / totalSeconds).coerceIn(0f, 1f)
    val timerColor by animateColorAsState(
        targetValue = when {
            fraction > 0.5f -> TimerSafe
            fraction > 0.2f -> TimerWarning
            else            -> TimerCritical
        },
        animationSpec = tween(500),
        label = "timerColor",
    )

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text  = formatTime(remainingSeconds),
            style = MaterialTheme.typography.labelLarge,
            color = timerColor,
        )
        LinearProgressIndicator(
            progress       = { fraction },
            modifier       = Modifier.fillMaxWidth(),
            color          = timerColor,
            trackColor     = MaterialTheme.colorScheme.surfaceVariant,
            strokeCap      = StrokeCap.Round,
        )
    }
}

private fun formatTime(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return "%02d:%02d".format(m, s)
}

private val Int.dp get() = androidx.compose.ui.unit.dp * this
