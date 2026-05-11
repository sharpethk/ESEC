package com.esec.examprep.presentation.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.esec.examprep.R
import kotlinx.coroutines.delay

/**
 * Pure-Kotlin gating helper extracted so the splash timing logic can be
 * unit-tested without a Compose runtime. Waits for at least [durationMillis]
 * to elapse AND for [waitFor] to return true, polling every [pollMillis].
 */
internal suspend fun awaitSplashReady(
    durationMillis: Long,
    pollMillis: Long = 50L,
    waitFor: () -> Boolean,
) {
    delay(durationMillis)
    while (!waitFor()) {
        delay(pollMillis)
    }
}

/**
 * Full-bleed in-app splash that displays the [R.drawable.splash_artwork] image.
 * Stays on screen for [durationMillis] (or until [waitFor] resolves to true,
 * whichever is later), then invokes [onFinished].
 */
@Composable
fun SplashScreen(
    onFinished: () -> Unit,
    durationMillis: Long = 3_200L,
    waitFor: () -> Boolean = { true },
) {
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Fade the artwork in for a smooth handoff from the system splash.
        alpha.animateTo(1f, animationSpec = tween(durationMillis = 350))
    }

    LaunchedEffect(Unit) {
        awaitSplashReady(durationMillis, waitFor = waitFor)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            // Matches the deep-blue lower band of the artwork for any letterboxing.
            .background(Color(0xFF0A2A6B)),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.splash_artwork),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .alpha(alpha.value),
            contentScale = ContentScale.Crop,
        )
    }
}
