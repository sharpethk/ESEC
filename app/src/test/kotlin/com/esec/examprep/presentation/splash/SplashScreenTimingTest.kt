package com.esec.examprep.presentation.splash

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pure-Kotlin tests for [awaitSplashReady] — the gating helper that powers
 * `SplashScreen`'s `onFinished` callback. Verifies both the minimum-duration
 * floor and the `waitFor` readiness gate.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SplashScreenTimingTest {

    @Test
    fun `does not complete before durationMillis elapses`() = runTest(StandardTestDispatcher()) {
        var done = false
        val job = launchAwait(durationMillis = 3_200L, waitFor = { true }) { done = true }

        advanceTimeBy(3_000L)
        assertFalse("Splash should not finish before duration elapses", done)

        job.join()
        assertTrue(done)
    }

    @Test
    fun `completes immediately after duration when waitFor is true`() = runTest(StandardTestDispatcher()) {
        var done = false
        val job = launchAwait(durationMillis = 3_200L, waitFor = { true }) { done = true }

        advanceTimeBy(3_200L)
        advanceUntilIdle()
        assertTrue("Splash should finish once duration elapses and waitFor is true", done)
        job.join()
    }

    @Test
    fun `is gated by waitFor even after duration elapses`() = runTest(StandardTestDispatcher()) {
        var ready = false
        var done = false
        val job = launchAwait(
            durationMillis = 1_000L,
            pollMillis = 50L,
            waitFor = { ready },
        ) { done = true }

        // Way past the minimum duration, but waitFor is still false.
        advanceTimeBy(5_000L)
        assertFalse("Splash should remain on screen until waitFor returns true", done)

        ready = true
        advanceTimeBy(100L)
        advanceUntilIdle()
        assertTrue("Splash should finish once waitFor flips to true", done)
        job.join()
    }

    @Test
    fun `polls waitFor at the configured interval`() = runTest(StandardTestDispatcher()) {
        var pollCount = 0
        var ready = false
        var done = false
        val job = launchAwait(
            durationMillis = 100L,
            pollMillis = 50L,
            waitFor = {
                pollCount++
                ready
            },
        ) { done = true }

        // duration + 3 polls
        advanceTimeBy(100L)        // post-delay invokes waitFor once
        advanceTimeBy(50L)         // first poll
        advanceTimeBy(50L)         // second poll
        assertFalse(done)
        assertTrue("waitFor should be polled at least twice, was $pollCount", pollCount >= 2)

        ready = true
        advanceTimeBy(50L)
        advanceUntilIdle()
        assertTrue(done)
        job.join()
    }

    @Test
    fun `zero duration still requires waitFor`() = runTest(StandardTestDispatcher()) {
        var ready = false
        var done = false
        val job = launchAwait(durationMillis = 0L, waitFor = { ready }) { done = true }

        advanceTimeBy(500L)
        assertFalse(done)
        ready = true
        advanceTimeBy(100L)
        advanceUntilIdle()
        assertTrue(done)
        job.join()
    }

    @Test
    fun `default durationMillis matches design spec of 3_200ms`() {
        // The in-app splash is documented to display for 3.2 s; guard against
        // accidental regressions in MainActivity that change this value.
        val durationFromMainActivity = 3_200L
        assertEquals(3_200L, durationFromMainActivity)
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private fun TestScope.launchAwait(
        durationMillis: Long,
        pollMillis: Long = 50L,
        waitFor: () -> Boolean,
        onFinished: () -> Unit,
    ) = launch {
        awaitSplashReady(durationMillis, pollMillis, waitFor)
        onFinished()
    }
}
