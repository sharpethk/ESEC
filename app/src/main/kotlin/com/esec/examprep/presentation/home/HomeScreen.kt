package com.esec.examprep.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.esec.examprep.R
import com.esec.examprep.presentation.components.GradientHero
import com.esec.examprep.presentation.components.IconBadge
import com.esec.examprep.presentation.theme.Elevation
import com.esec.examprep.presentation.theme.Radius
import com.esec.examprep.presentation.theme.Spacing

@Composable
fun HomeScreen(
    onSubjectsClick: () -> Unit,
    onDashboardClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val error     by viewModel.error.collectAsState()

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when {
                isLoading -> LoadingState()
                error != null -> ErrorState(onRetry = viewModel::retryLoad)
                else -> HomeContent(
                    onSubjectsClick = onSubjectsClick,
                    onDashboardClick = onDashboardClick,
                )
            }
        }
    }
}

@Composable
private fun HomeContent(
    onSubjectsClick: () -> Unit,
    onDashboardClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        GradientHero(
            title = stringResource(R.string.home_title),
            subtitle = stringResource(R.string.home_tagline),
        )
        Spacer(Modifier.height(Spacing.xxl))

        Column(Modifier.padding(horizontal = Spacing.xxl)) {
            Text(
                text = stringResource(R.string.home_section_get_started),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(Spacing.md))

            PrimaryActionCard(
                title = stringResource(R.string.home_start_exam),
                description = stringResource(R.string.home_start_exam_desc),
                icon = Icons.AutoMirrored.Filled.MenuBook,
                onClick = onSubjectsClick,
            )
            Spacer(Modifier.height(Spacing.md))
            SecondaryActionCard(
                title = stringResource(R.string.home_my_progress),
                description = stringResource(R.string.home_my_progress_desc),
                icon = Icons.Default.BarChart,
                onClick = onDashboardClick,
            )

            Spacer(Modifier.height(Spacing.xxxl))
            Text(
                text = stringResource(R.string.home_section_why),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(Spacing.md))
            FeatureRow(
                icon = Icons.Default.Lock,
                title = stringResource(R.string.home_feature_offline_title),
                description = stringResource(R.string.home_feature_offline_desc),
            )
            Spacer(Modifier.height(Spacing.md))
            FeatureRow(
                icon = Icons.Default.Speed,
                title = stringResource(R.string.home_feature_timing_title),
                description = stringResource(R.string.home_feature_timing_desc),
            )
            Spacer(Modifier.height(Spacing.md))
            FeatureRow(
                icon = Icons.Default.Shield,
                title = stringResource(R.string.home_feature_review_title),
                description = stringResource(R.string.home_feature_review_desc),
            )
            Spacer(Modifier.height(Spacing.huge))
        }
    }
}

@Composable
private fun PrimaryActionCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Radius.lg),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.sm),
    ) {
        Row(
            modifier = Modifier.padding(Spacing.xl),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.16f),
                        RoundedCornerShape(Radius.md),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, null, modifier = Modifier.size(24.dp))
            }
            Spacer(Modifier.size(Spacing.lg))
            Column(Modifier.weight(1f)) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(Modifier.height(Spacing.xxs))
                Text(
                    description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f),
                )
            }
            Spacer(Modifier.size(Spacing.md))
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onPrimary,
                    contentColor = MaterialTheme.colorScheme.primary,
                ),
                shape = RoundedCornerShape(Radius.pill),
            ) {
                Icon(Icons.Default.PlayArrow, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.size(Spacing.xs))
                Text(stringResource(R.string.home_action_start), fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun SecondaryActionCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Radius.lg),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.xs),
    ) {
        Row(
            modifier = Modifier.padding(Spacing.xl),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconBadge(icon = icon, tint = MaterialTheme.colorScheme.primary, size = 48.dp)
            Spacer(Modifier.size(Spacing.lg))
            Column(Modifier.weight(1f)) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(Modifier.height(Spacing.xxs))
                Text(
                    description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(Modifier.size(Spacing.md))
            OutlinedButton(onClick = onClick, shape = RoundedCornerShape(Radius.pill)) {
                Text(stringResource(R.string.home_action_open))
            }
        }
    }
}

@Composable
private fun FeatureRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconBadge(icon = icon, tint = MaterialTheme.colorScheme.tertiary, size = 40.dp)
        Spacer(Modifier.size(Spacing.md))
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Text(
                description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun LoadingState() {
    Column(
        modifier = Modifier.fillMaxSize().padding(Spacing.xxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        CircularProgressIndicator(modifier = Modifier.size(56.dp), strokeWidth = 4.dp)
        Spacer(Modifier.height(Spacing.lg))
        Text(stringResource(R.string.home_loading), style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun ErrorState(onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(Spacing.xxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            Icons.Default.Refresh,
            null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(40.dp),
        )
        Spacer(Modifier.height(Spacing.md))
        Text(
            text = stringResource(R.string.home_load_failed),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(Spacing.sm))
        TextButton(onClick = onRetry) { Text(stringResource(R.string.home_retry)) }
    }
}
