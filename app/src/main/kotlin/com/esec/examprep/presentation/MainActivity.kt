package com.esec.examprep.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.esec.examprep.data.preferences.ThemeMode
import com.esec.examprep.presentation.navigation.AppNavGraph
import com.esec.examprep.presentation.navigation.Screen
import com.esec.examprep.presentation.splash.SplashScreen
import com.esec.examprep.presentation.theme.ESECTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    // Toggled from the Compose tree as soon as the in-app splash is on
    // screen, so the system splash can dismiss without a flash.
    @Volatile private var inAppSplashReady: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
        splash.setKeepOnScreenCondition { !inAppSplashReady }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val prefs by mainViewModel.preferences.collectAsState()
            val startDest by mainViewModel.startDestination.collectAsState()
            val systemDark = isSystemInDarkTheme()
            val isDark = when (prefs.themeMode) {
                ThemeMode.LIGHT  -> false
                ThemeMode.DARK   -> true
                ThemeMode.SYSTEM -> systemDark
            }
            ESECTheme(darkTheme = isDark) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    var splashFinished by remember { mutableStateOf(false) }
                    val dest = startDest

                    LaunchedEffect(Unit) { inAppSplashReady = true }

                    if (!splashFinished) {
                        SplashScreen(
                            onFinished = { splashFinished = true },
                            durationMillis = 3_200L,
                            waitFor = { dest != null },
                        )
                    } else if (dest != null) {
                        AppNavGraph(rememberNavController(), startDestination = dest)
                    } else {
                        // Final fail-safe: if dest is still null after splash,
                        // navigate to Home so the user is never stuck on a blank screen.
                        AppNavGraph(rememberNavController(), startDestination = Screen.Home.route)
                    }
                }
            }
        }
    }
}
