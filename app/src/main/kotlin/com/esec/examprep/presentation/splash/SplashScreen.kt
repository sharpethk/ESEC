package com.esec.examprep.presentation.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
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
    // Capture latest waitFor so the polling LaunchedEffect below never reads a
    // stale closure (LaunchedEffect(Unit) keeps the original lambda forever).
    val currentWaitFor by rememberUpdatedState(waitFor)

    LaunchedEffect(Unit) {
        // Fade the artwork in for a smooth handoff from the system splash.
        alpha.animateTo(1f, animationSpec = tween(durationMillis = 350))
    }

    LaunchedEffect(Unit) {
        awaitSplashReady(durationMillis, waitFor = { currentWaitFor() })
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            // Vertical gradient approximates the original splash_artwork layer-list.
            // Drawn directly with Compose to avoid painterResource() crashing on
            // <layer-list> XML drawables (only VectorDrawables / rasters supported).
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF4A300),
                        Color(0xFFD62828),
                        Color(0xFF0A2A6B),
                    ),
                )
            ),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.ic_splash_logo),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .fillMaxHeight(0.4f)
                .alpha(alpha.value),
            contentScale = ContentScale.Fit,
        )
    }
}
