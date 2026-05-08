package com.esec.examprep.presentation.achievements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.esec.examprep.domain.model.Achievement
import com.esec.examprep.domain.model.AchievementCode
import com.esec.examprep.presentation.theme.Elevation
import com.esec.examprep.presentation.theme.Radius
import com.esec.examprep.presentation.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementsScreen(
    onBack: () -> Unit,
    viewModel: AchievementsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Achievements", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when {
                state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                else -> AchievementsContent(state.items, padding = PaddingValues(Spacing.lg))
            }
        }
    }
}

@Composable
private fun AchievementsContent(items: List<Achievement>, padding: PaddingValues) {
    val unlocked = items.count { it.isUnlocked }
    val total = items.size
    val progress = if (total == 0) 0f else unlocked.toFloat() / total

    LazyColumn(
        contentPadding = padding,
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        item {
            Card(
                shape = androidx.compose.foundation.shape.RoundedCornerShape(Radius.lg),
                elevation = CardDefaults.cardElevation(Elevation.sm),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(Modifier.padding(Spacing.lg)) {
                    Text(
                        "$unlocked of $total unlocked",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth().padding(top = Spacing.sm),
                    )
                }
            }
        }
        items(items, key = { it.code.name }) { achievement ->
            AchievementRow(achievement)
        }
    }
}

@Composable
private fun AchievementRow(achievement: Achievement) {
    val unlocked = achievement.isUnlocked
    val container = if (unlocked) MaterialTheme.colorScheme.tertiaryContainer
                    else MaterialTheme.colorScheme.surfaceVariant
    val onContainer = if (unlocked) MaterialTheme.colorScheme.onTertiaryContainer
                      else MaterialTheme.colorScheme.onSurfaceVariant

    Card(
        shape = androidx.compose.foundation.shape.RoundedCornerShape(Radius.lg),
        elevation = CardDefaults.cardElevation(Elevation.xs),
        colors = CardDefaults.cardColors(containerColor = container),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(Spacing.lg),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = if (unlocked) iconFor(achievement.code) else Icons.Filled.Lock,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = onContainer,
            )
            Column(Modifier.padding(start = Spacing.lg).fillMaxWidth()) {
                Text(
                    achievement.code.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = onContainer,
                )
                Text(
                    achievement.code.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = onContainer.copy(alpha = 0.8f),
                )
            }
        }
    }
}

private fun iconFor(code: AchievementCode): ImageVector = when (code) {
    AchievementCode.FIRST_EXAM -> Icons.Filled.School
    AchievementCode.HUNDRED_QUESTIONS -> Icons.Filled.Numbers
    AchievementCode.TEN_IN_A_ROW -> Icons.AutoMirrored.Filled.TrendingUp
    AchievementCode.GPA_3_5 -> Icons.Filled.Star
    AchievementCode.ALL_SUBJECTS -> Icons.Filled.AutoAwesome
    AchievementCode.STREAK_7 -> Icons.Filled.LocalFireDepartment
    AchievementCode.NOTEBOOK_CLEARED -> Icons.Filled.CheckCircle
}
