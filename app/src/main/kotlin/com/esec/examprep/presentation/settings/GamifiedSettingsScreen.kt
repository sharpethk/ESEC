/**
 * Maps to /stitch_erixam_exam_companion/settings/code.html
 * Design tokens from /stitch_erixam_exam_companion/erixam_design_system/DESIGN.md.
 *
 * Gamified settings: profile header bento (avatar + LVL chip + XP
 * progress + streak) followed by 4 grouped lists (Account /
 * Notifications / Learning / Support) with hairline dividers and a
 * pill "Switch to Parent View" CTA + version footer.
 *
 * Lives next to the production [SettingsScreen]; mock-mapped variant.
 */
package com.esec.examprep.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.esec.examprep.presentation.theme.ESECTheme
import com.esec.examprep.presentation.theme.Radius
import com.esec.examprep.presentation.theme.Spacing

// -------- UI Models --------

@Immutable
data class GamifiedSettingsProfile(
    val displayName: String,
    val tagline: String,
    val level: Int,
    val avatarUrl: String? = null,
    val xpCurrent: Int,
    val xpTarget: Int,
    val streakDays: Int,
)

sealed interface GamifiedSettingsRow {
    val id: String
    val icon: ImageVector
    val label: String

    data class Navigation(
        override val id: String,
        override val icon: ImageVector,
        override val label: String,
    ) : GamifiedSettingsRow

    data class Toggle(
        override val id: String,
        override val icon: ImageVector,
        override val label: String,
        val checked: Boolean,
    ) : GamifiedSettingsRow

    data class Trailing(
        override val id: String,
        override val icon: ImageVector,
        override val label: String,
        val trailingText: String,
        val trailingHighlighted: Boolean = false,
        val showChevron: Boolean = false,
    ) : GamifiedSettingsRow
}

@Immutable
data class GamifiedSettingsGroup(val title: String, val rows: List<GamifiedSettingsRow>)

@Immutable
data class GamifiedSettingsUiState(
    val profile: GamifiedSettingsProfile,
    val groups: List<GamifiedSettingsGroup>,
    val versionLabel: String,
)

// -------- Stateful entry --------

@Composable
fun GamifiedSettingsScreen(
    state: GamifiedSettingsUiState,
    onMenuClick: () -> Unit,
    onSearchClick: () -> Unit,
    onRowClick: (GamifiedSettingsRow) -> Unit,
    onToggleChange: (GamifiedSettingsRow.Toggle, Boolean) -> Unit,
    onSwitchToParentView: () -> Unit,
) {
    GamifiedSettingsContent(
        state = state,
        onMenuClick = onMenuClick,
        onSearchClick = onSearchClick,
        onRowClick = onRowClick,
        onToggleChange = onToggleChange,
        onSwitchToParentView = onSwitchToParentView,
    )
}

// -------- Stateless content --------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GamifiedSettingsContent(
    state: GamifiedSettingsUiState,
    onMenuClick: () -> Unit,
    onSearchClick: () -> Unit,
    onRowClick: (GamifiedSettingsRow) -> Unit,
    onToggleChange: (GamifiedSettingsRow.Toggle, Boolean) -> Unit,
    onSwitchToParentView: () -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
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
            item("profile") { ProfileHeaderCard(profile = state.profile) }
            state.groups.forEach { group ->
                item("group-${group.title}") {
                    SettingsGroupBlock(
                        group = group,
                        onRowClick = onRowClick,
                        onToggleChange = onToggleChange,
                    )
                }
            }
            item("action") {
                SwitchToParentButton(onClick = onSwitchToParentView)
            }
            item("footer") {
                Text(
                    text = state.versionLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Spacing.sm),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

// -------- Profile header --------

@Composable
private fun ProfileHeaderCard(profile: GamifiedSettingsProfile) {
    val fraction = if (profile.xpTarget == 0) 0f
    else profile.xpCurrent.toFloat() / profile.xpTarget.toFloat()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radius.md))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.30f),
                shape = RoundedCornerShape(Radius.md),
            )
            .padding(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            Box(modifier = Modifier.size(72.dp), contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .border(2.dp, MaterialTheme.colorScheme.primaryContainer, CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .align(Alignment.TopStart),
                    contentAlignment = Alignment.Center,
                ) {
                    if (profile.avatarUrl != null) {
                        AsyncImage(
                            model = profile.avatarUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                        )
                    } else {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(40.dp),
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .border(1.dp, MaterialTheme.colorScheme.surface, CircleShape)
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                ) {
                    Text(
                        text = "LVL ${profile.level}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = profile.displayName,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = profile.tagline,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "XP Progress",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = "%,d / %,d".format(profile.xpCurrent, profile.xpTarget),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                )
            }
            LinearProgressIndicator(
                progress = { fraction.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(CircleShape),
                color = MaterialTheme.colorScheme.primaryContainer,
                trackColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Icon(
                    Icons.Default.LocalFireDepartment,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(16.dp),
                )
                Text(
                    text = "${profile.streakDays} Day Streak",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

// -------- Settings group --------

@Composable
private fun SettingsGroupBlock(
    group: GamifiedSettingsGroup,
    onRowClick: (GamifiedSettingsRow) -> Unit,
    onToggleChange: (GamifiedSettingsRow.Toggle, Boolean) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        Text(
            text = group.title.uppercase(),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(start = 4.dp),
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(Radius.md))
                .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.30f),
                    shape = RoundedCornerShape(Radius.md),
                ),
        ) {
            group.rows.forEachIndexed { index, row ->
                SettingsRowItem(
                    row = row,
                    onClick = { onRowClick(row) },
                    onToggleChange = onToggleChange,
                )
                if (index < group.rows.size - 1) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = Spacing.md),
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.20f),
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsRowItem(
    row: GamifiedSettingsRow,
    onClick: () -> Unit,
    onToggleChange: (GamifiedSettingsRow.Toggle, Boolean) -> Unit,
) {
    val isInteractive = row !is GamifiedSettingsRow.Toggle
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (isInteractive) Modifier.clickable(onClick = onClick)
                else Modifier,
            )
            .padding(Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            modifier = Modifier.weight(1f),
        ) {
            Icon(
                row.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = row.label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
        when (row) {
            is GamifiedSettingsRow.Navigation -> {
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outlineVariant,
                )
            }
            is GamifiedSettingsRow.Toggle -> {
                Switch(
                    checked = row.checked,
                    onCheckedChange = { onToggleChange(row, it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.surface,
                        checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                        uncheckedThumbColor = MaterialTheme.colorScheme.surface,
                        uncheckedTrackColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    ),
                )
            }
            is GamifiedSettingsRow.Trailing -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = row.trailingText,
                        style = MaterialTheme.typography.labelLarge,
                        color = if (row.trailingHighlighted)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold,
                    )
                    if (row.showChevron) {
                        Icon(
                            Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outlineVariant,
                        )
                    }
                }
            }
        }
    }
}

// -------- Switch-to-parent button --------

@Composable
private fun SwitchToParentButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary,
        ),
    ) {
        Icon(Icons.Default.SupervisorAccount, contentDescription = null)
        Spacer(Modifier.size(Spacing.sm))
        Text(
            text = "Switch to Parent View",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

// -------- Preview --------

@Preview(showBackground = true, heightDp = 1700)
@Composable
private fun GamifiedSettingsPreview() {
    ESECTheme {
        GamifiedSettingsContent(
            state = GamifiedSettingsUiState(
                profile = GamifiedSettingsProfile(
                    displayName = "Abraham Tekle",
                    tagline = "Aspiring Medical Student",
                    level = 12,
                    xpCurrent = 2450,
                    xpTarget = 3000,
                    streakDays = 14,
                ),
                groups = listOf(
                    GamifiedSettingsGroup(
                        title = "Account",
                        rows = listOf(
                            GamifiedSettingsRow.Navigation("personal", Icons.Default.PersonOutline, "Personal Info"),
                            GamifiedSettingsRow.Navigation("security", Icons.Default.Security, "Security"),
                            GamifiedSettingsRow.Navigation("plan", Icons.Default.CalendarMonth, "Study Plan"),
                        ),
                    ),
                    GamifiedSettingsGroup(
                        title = "Notifications",
                        rows = listOf(
                            GamifiedSettingsRow.Toggle("quest", Icons.Default.NotificationsActive, "Quest Alerts", true),
                            GamifiedSettingsRow.Toggle("reminders", Icons.Default.Schedule, "Reminders", true),
                            GamifiedSettingsRow.Toggle("sounds", Icons.Default.VolumeUp, "Sound Effects", false),
                        ),
                    ),
                    GamifiedSettingsGroup(
                        title = "Learning",
                        rows = listOf(
                            GamifiedSettingsRow.Trailing(
                                id = "offline",
                                icon = Icons.Default.CloudDownload,
                                label = "Offline Mode",
                                trailingText = "Downloaded (2.4GB)",
                            ),
                            GamifiedSettingsRow.Trailing(
                                id = "language",
                                icon = Icons.Default.Translate,
                                label = "Language",
                                trailingText = "English",
                                trailingHighlighted = true,
                                showChevron = true,
                            ),
                        ),
                    ),
                    GamifiedSettingsGroup(
                        title = "Support",
                        rows = listOf(
                            GamifiedSettingsRow.Navigation("help", Icons.Default.HelpOutline, "Help Center"),
                            GamifiedSettingsRow.Navigation("feedback", Icons.Default.RateReview, "Feedback"),
                        ),
                    ),
                ),
                versionLabel = "EriXam v2.4.1 • Made with ❤ in Eritrea",
            ),
            onMenuClick = {},
            onSearchClick = {},
            onRowClick = {},
            onToggleChange = { _, _ -> },
            onSwitchToParentView = {},
        )
    }
}
