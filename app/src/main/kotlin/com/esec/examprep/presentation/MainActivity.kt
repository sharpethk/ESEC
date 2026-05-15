package com.esec.examprep.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.esec.examprep.data.preferences.ThemeMode
import com.esec.examprep.presentation.navigation.AppNavGraph
import com.esec.examprep.presentation.navigation.Screen
import com.esec.examprep.presentation.splash.SplashScreen
import com.esec.examprep.presentation.theme.ESECTheme
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "ESEC.MainActivity"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG, "onCreate: installing splash")
        installSplashScreen()
        super.onCreate(savedInstanceState)
        Log.i(TAG, "onCreate: super done, enableEdgeToEdge")
        enableEdgeToEdge()
        Log.i(TAG, "onCreate: calling setContent")
        setContent {
            Log.i(TAG, "setContent: composition started")
            val prefs by mainViewModel.preferences.collectAsState()
            val startDest by mainViewModel.startDestination.collectAsState()
            val systemDark = isSystemInDarkTheme()
            val isDark = when (prefs.themeMode) {
                ThemeMode.LIGHT  -> false
                ThemeMode.DARK   -> true
                ThemeMode.SYSTEM -> systemDark
            }
            Log.i(TAG, "setContent: dark=$isDark, startDest=$startDest")
            ESECTheme(darkTheme = isDark) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    var splashFinished by remember { mutableStateOf(false) }
                    var hardTimeoutFired by remember { mutableStateOf(false) }
                    val dest = startDest

                    // Absolute ceiling: if ViewModel init never produces a start
                    // destination within 6s, force-dismiss the splash and route
                    // to Home so the user is never stuck on a blank screen.
                    LaunchedEffect(Unit) {
                        kotlinx.coroutines.delay(6_000L)
                        if (!splashFinished) {
                            Log.w(TAG, "Hard timeout: forcing splash dismiss")
                            hardTimeoutFired = true
                            splashFinished = true
                        }
                    }

                    val effectiveDest = dest ?: if (hardTimeoutFired) Screen.Home.route else null

                    when {
                        !splashFinished -> SplashScreen(
                            onFinished = {
                                Log.i(TAG, "Splash finished, dest=$dest")
                                splashFinished = true
                            },
                            durationMillis = 3_200L,
                            waitFor = { dest != null },
                        )
                        effectiveDest != null -> {
                            Log.i(TAG, "Mounting AppNavGraph at $effectiveDest")
                            AppNavGraph(rememberNavController(), startDestination = effectiveDest)
                        }
                        else -> {
                            Log.w(TAG, "dest still null after splash — falling back to Home")
                            AppNavGraph(rememberNavController(), startDestination = Screen.Home.route)
                        }
                    }
                }
            }
        }
        Log.i(TAG, "onCreate: setContent returned")
    }
}

/** Visible last-resort error surface so a composition crash never shows a blank screen. */
@Suppress("unused")
@Composable
private fun FatalErrorBanner(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFB00020))
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Init failed:\n$message",
            color = Color.White,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}
