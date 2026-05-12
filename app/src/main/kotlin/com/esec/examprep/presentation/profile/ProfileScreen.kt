/**
 * Maps to /stitch_erixam_exam_companion/profile_gamified_style/code.html
 * Design tokens from /stitch_erixam_exam_companion/erixam_design_system/DESIGN.md.
 *
 * Profile hub: gradient-ring avatar with Level badge, school + grade,
 * National Rank row tile, square stat tiles (Quests Completed +
 * Studied This Month), Settings list (Account / Notifications /
 * Download / Logout).
 */
package com.esec.examprep.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.esec.examprep.presentation.theme.ESECTheme
import com.esec.examprep.presentation.theme.Radius
import com.esec.examprep.presentation.theme.Spacing

// -------- UI Models --------

@Immutable
data class ProfileUiState(
    val displayName: String,
    val avatarUrl: String?,
    val level: Int,
    val schoolName: String,
    val grade: String,                  // e.g. "Grade 12"
    val nationalRank: Int,
    val questsCompleted: Int,
    val studiedHoursThisMonth: Int,
)

// -------- Stateful entry --------

@Composable
fun ProfileScreen(
    state: ProfileUiState,
    onMenuClick: () -> Unit,
    onSearchClick: () -> Unit,
    onRankClick: () -> Unit,
    onAccountSettings: () -> Unit,
    onNotificationPrefs: () -> Unit,
    onDownloadPapers: () -> Unit,
    onLogout: () -> Unit,
) {
    ProfileContent(
        state = state,
        onMenuClick = onMenuClick,
        onSearchClick = onSearchClick,
        onRankClick = onRankClick,
        onAccountSettings = onAccountSettings,
        onNotificationPrefs = onNotificationPrefs,
        onDownloadPapers = onDownloadPapers,
        onLogout = onLogout,
    )
}

// -------- Stateless content --------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileContent(
    state: ProfileUiState,
    onMenuClick: () -> Unit,
    onSearchClick: () -> Unit,
    onRankClick: () -> Unit,
    onAccountSettings: () -> Unit,
    onNotificationPrefs: () -> Unit,
    onDownloadPapers: () -> Unit,
    onLogout: () -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "EriXam",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(
                            Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onSearchClick) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(
                start = Spacing.lg,
                end = Spacing.lg,
                top = Spacing.md,
                bottom = Spacing.xxxl,
            ),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg),
        ) {
            item("header") { ProfileHeader(state = state) }
            item("rank") { NationalRankRow(rank = state.nationalRank, onClick = onRankClick) }
            item("stats") {
                StatTiles(
                    questsCompleted = state.questsCompleted,
                    studiedHours = state.studiedHoursThisMonth,
                )
            }
            item("settings-header") {
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 4.dp),
                )
            }
            item("settings") {
                SettingsList(
                    onAccount = onAccountSettings,
                    onNotifications = onNotificationPrefs,
                    onDownload = onDownloadPapers,
                    onLogout = onLogout,
                )
            }
        }
    }
}

// -------- Header --------

@Composable
private fun ProfileHeader(state: ProfileUiState) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Box(contentAlignment = Alignment.BottomEnd) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondaryContainer,
                            ),
                        ),
                    )
                    .padding(4.dp),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(2.dp),
                ) {
                    if (!state.avatarUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = state.avatarUrl,
                            contentDescription = "Avatar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(40.dp),
                            )
                        }
                    }
                }
            }
            Text(
                text = "Level ${state.level}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onTertiary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.tertiary)
                    .padding(horizontal = Spacing.md, vertical = 4.dp),
            )
        }
        Spacer(Modifier.size(Spacing.xs))
        Text(
            text = state.displayName,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = "${state.schoolName} • ${state.grade}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

// -------- National rank --------

@Composable
private fun NationalRankRow(rank: Int, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radius.lg))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(Radius.lg),
            )
            .clickable(onClick = onClick)
            .padding(Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(Radius.md))
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .padding(Spacing.sm),
        ) {
            Icon(
                Icons.Default.Leaderboard,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
            )
        }
        Spacer(Modifier.size(Spacing.md))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "National Rank",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "#$rank",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
            )
        }
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline,
        )
    }
}

// -------- Stat tiles --------

@Composable
private fun StatTiles(questsCompleted: Int, studiedHours: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
        Box(modifier = Modifier.weight(1f)) {
            StatTile(
                icon = Icons.Default.TaskAlt,
                value = questsCompleted.toString(),
                label = "Quests Completed",
                container = MaterialTheme.colorScheme.primaryContainer,
                content = MaterialTheme.colorScheme.onPrimaryContainer,
                iconTint = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
        Box(modifier = Modifier.weight(1f)) {
            StatTile(
                icon = Icons.Default.Schedule,
                value = "${studiedHours}h",
                label = "Studied This Month",
                container = MaterialTheme.colorScheme.surfaceContainerHighest,
                content = MaterialTheme.colorScheme.onSurface,
                iconTint = MaterialTheme.colorScheme.secondary,
                outlined = true,
            )
        }
    }
}

@Composable
private fun StatTile(
    icon: ImageVector,
    value: String,
    label: String,
    container: Color,
    content: Color,
    iconTint: Color,
    outlined: Boolean = false,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(Radius.lg))
            .background(container)
            .let {
                if (outlined) it.border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(Radius.lg),
                ) else it
            }
            .padding(Spacing.md),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(32.dp),
        )
        Column {
            Text(
                text = value,
                style = MaterialTheme.typography.displayLarge,
                color = content,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = content.copy(alpha = 0.90f),
            )
        }
    }
}

// -------- Settings list --------

@Composable
private fun SettingsList(
    onAccount: () -> Unit,
    onNotifications: () -> Unit,
    onDownload: () -> Unit,
    onLogout: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radius.lg))
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(Radius.lg),
            ),
    ) {
        SettingsRow(icon = Icons.Default.ManageAccounts, label = "Account Settings", onClick = onAccount)
        SettingsDivider()
        SettingsRow(icon = Icons.Default.Notifications, label = "Notification Preferences", onClick = onNotifications)
        SettingsDivider()
        SettingsRow(icon = Icons.Default.Download, label = "Download Offline Papers", onClick = onDownload)
        SettingsDivider()
        SettingsRow(
            icon = Icons.AutoMirrored.Filled.Logout,
            label = "Logout",
            onClick = onLogout,
            destructive = true,
        )
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    destructive: Boolean = false,
) {
    val iconTint = if (destructive) MaterialTheme.colorScheme.error
    else MaterialTheme.colorScheme.onSurfaceVariant
    val labelColor = if (destructive) MaterialTheme.colorScheme.error
    else MaterialTheme.colorScheme.onSurface
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = null, tint = iconTint)
        Spacer(Modifier.size(Spacing.md))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = labelColor,
            fontWeight = if (destructive) FontWeight.SemiBold else FontWeight.Normal,
            modifier = Modifier.weight(1f),
        )
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = if (destructive) MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
            else MaterialTheme.colorScheme.outline,
        )
    }
}

@Composable
private fun SettingsDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = Spacing.md),
        color = MaterialTheme.colorScheme.surfaceVariant,
    )
}

// -------- Preview --------

@Preview(showBackground = true, heightDp = 1200)
@Composable
private fun ProfilePreview() {
    ESECTheme {
        ProfileContent(
            state = ProfileUiState(
                displayName = "Amanuel",
                avatarUrl = null,
                level = 12,
                schoolName = "Warsay Yikealo Secondary School",
                grade = "Grade 12",
                nationalRank = 42,
                questsCompleted = 128,
                studiedHoursThisMonth = 45,
            ),
            onMenuClick = {},
            onSearchClick = {},
            onRankClick = {},
            onAccountSettings = {},
            onNotificationPrefs = {},
            onDownloadPapers = {},
            onLogout = {},
        )
    }
}
