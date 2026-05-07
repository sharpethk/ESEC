package com.esec.examprep.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.esec.examprep.data.preferences.ThemeMode
import com.esec.examprep.presentation.navigation.AppNavGraph
import com.esec.examprep.presentation.theme.ESECTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
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
                    val dest = startDest
                    if (dest != null) {
                        AppNavGraph(rememberNavController(), startDestination = dest)
                    }
                }
            }
        }
    }
}
