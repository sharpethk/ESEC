package com.esec.examprep.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun HomeScreen(
    onSubjectsClick: () -> Unit,
    onDashboardClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val error     by viewModel.error.collectAsState()

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.size(56.dp))
                    Spacer(Modifier.height(16.dp))
                    Text("Preparing question bank…", style = MaterialTheme.typography.bodyMedium)
                }
                error != null -> {
                    Text(
                        text      = "Failed to load questions",
                        style     = MaterialTheme.typography.titleMedium,
                        color     = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(8.dp))
                    TextButton(onClick = viewModel::retryLoad) { Text("Retry") }
                }
                else -> {
                    Text(
                        text  = "ESEC Exam Prep",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text      = "Practice past papers. Offline. Anytime.",
                        style     = MaterialTheme.typography.bodyLarge,
                        color     = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(48.dp))
                    Button(
                        onClick  = onSubjectsClick,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Icon(Icons.Default.MenuBook, contentDescription = null)
                        Spacer(Modifier.size(8.dp))
                        Text("Start Exam")
                    }
                    Spacer(Modifier.height(12.dp))
                    OutlinedButton(
                        onClick  = onDashboardClick,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Icon(Icons.Default.BarChart, contentDescription = null)
                        Spacer(Modifier.size(8.dp))
                        Text("My Progress")
                    }
                }
            }
        }
    }
}
