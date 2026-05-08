package com.esec.examprep.presentation.settings

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.ui.platform.LocalContext
import com.esec.examprep.BuildConfig
import com.esec.examprep.R
import com.esec.examprep.data.preferences.AppLanguage
import com.esec.examprep.data.preferences.ThemeMode
import com.esec.examprep.presentation.theme.Elevation
import com.esec.examprep.presentation.theme.Radius
import com.esec.examprep.presentation.theme.Spacing

private const val ALL_QUESTIONS = 0

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onManageProfiles: () -> Unit = {},
    onPracticeBuilder: () -> Unit = {},
    onAchievements: () -> Unit = {},
    onParentView: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val prefs = state.preferences
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.reloadBankMessage) {
        val msg = state.reloadBankMessage
        if (msg != null) {
            snackbarHostState.showSnackbar(msg)
            viewModel.consumeReloadBankMessage()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.lg, vertical = Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg),
        ) {
            SettingsHero()

            SectionCard(title = stringResource(R.string.settings_profile_section), icon = Icons.Default.ManageAccounts) {
                Button(
                    onClick = onManageProfiles,
                    shape = RoundedCornerShape(Radius.md),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(Icons.Default.ManageAccounts, contentDescription = null)
                    Spacer(Modifier.size(Spacing.sm))
                    Text(stringResource(R.string.settings_manage_profiles), fontWeight = FontWeight.SemiBold)
                }
                Spacer(Modifier.height(Spacing.sm))
                HelperText(stringResource(R.string.settings_profile_helper))
            }

            SectionCard(title = "Custom practice", icon = Icons.Default.Tune) {
                Button(
                    onClick = onPracticeBuilder,
                    shape = RoundedCornerShape(Radius.md),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(Icons.Default.Tune, contentDescription = null)
                    Spacer(Modifier.size(Spacing.sm))
                    Text("Build a practice exam", fontWeight = FontWeight.SemiBold)
                }
                Spacer(Modifier.height(Spacing.sm))
                HelperText("Pick subjects and difficulty mix to focus your study session.")
            }

            SectionCard(title = "Achievements", icon = Icons.Default.AutoAwesome) {
                Button(
                    onClick = onAchievements,
                    shape = RoundedCornerShape(Radius.md),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null)
                    Spacer(Modifier.size(Spacing.sm))
                    Text("View achievements", fontWeight = FontWeight.SemiBold)
                }
                Spacer(Modifier.height(Spacing.sm))
                HelperText("Unlock badges as you study and master subjects.")
            }

            SectionCard(title = "Parent view", icon = Icons.Default.FamilyRestroom) {
                Button(
                    onClick = onParentView,
                    shape = RoundedCornerShape(Radius.md),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(Icons.Default.FamilyRestroom, contentDescription = null)
                    Spacer(Modifier.size(Spacing.sm))
                    Text("Open parent view", fontWeight = FontWeight.SemiBold)
                }
                Spacer(Modifier.height(Spacing.sm))
                HelperText("PIN-protected read-only summary of all profiles.")
            }

            SectionCard(title = "Language", icon = Icons.Default.Language) {
                LanguageSegmented(
                    selected = prefs.language,
                    onChange = viewModel::onLanguageChanged,
                )
                Spacer(Modifier.height(Spacing.sm))
                HelperText("Changes apply on next screen change.")
            }

            SectionCard(title = "Appearance", icon = Icons.Default.Palette) {
                ThemeModeSegmented(
                    selected = prefs.themeMode,
                    onChange = viewModel::onThemeModeChanged,
                )
                Spacer(Modifier.height(Spacing.sm))
                HelperText("Light, dark, or follow your device.")
            }

            SectionCard(title = "Exam preferences", icon = Icons.Default.Tune) {
                StepperRow(
                    label = "Default exam length",
                    options = listOf(10, 20, 40, 60, ALL_QUESTIONS),
                    selected = prefs.defaultExamLength,
                    onChange = viewModel::onExamLengthChanged,
                    formatter = { if (it == ALL_QUESTIONS) "All" else "$it" },
                )
                Spacer(Modifier.height(Spacing.xs))
                HelperText(
                    if (prefs.defaultExamLength == ALL_QUESTIONS)
                        "Every available question for the subject. Timer scales automatically."
                    else "Number of questions sampled per exam.",
                )

                Spacer(Modifier.height(Spacing.md))
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                Spacer(Modifier.height(Spacing.md))

                StepperRow(
                    label = "Timer duration",
                    options = listOf(15, 30, 45, 60),
                    selected = prefs.defaultTimerMinutes,
                    onChange = viewModel::onTimerMinutesChanged,
                    formatter = { "$it min" },
                    leadingIcon = Icons.Default.Schedule,
                )
                Spacer(Modifier.height(Spacing.xs))
                HelperText("Used for timed-mode exams.")
            }

            SectionCard(title = "Reminders", icon = Icons.Default.Notifications) {
                val ctx = LocalContext.current
                val permLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission(),
                ) { granted ->
                    if (granted) viewModel.onRemindersEnabledChanged(true)
                }
                ReminderRow(
                    enabled = prefs.remindersEnabled,
                    hour = prefs.reminderHour,
                    minute = prefs.reminderMinute,
                    onEnabledChange = { wantEnabled ->
                        if (wantEnabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            val granted = ContextCompat.checkSelfPermission(
                                ctx, Manifest.permission.POST_NOTIFICATIONS,
                            ) == PackageManager.PERMISSION_GRANTED
                            if (granted) viewModel.onRemindersEnabledChanged(true)
                            else permLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            viewModel.onRemindersEnabledChanged(wantEnabled)
                        }
                    },
                    onTimeChange = viewModel::onReminderTimeChanged,
                )
                Spacer(Modifier.height(Spacing.sm))
                HelperText("Daily challenge reminder at your chosen time. Skipped if you've already completed today.")
            }

            SectionCard(title = "Data", icon = Icons.Default.DeleteSweep) {
                Button(
                    onClick = { viewModel.showClearHistoryDialog(true) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    ),
                    shape = RoundedCornerShape(Radius.md),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(Icons.Default.DeleteSweep, contentDescription = null)
                    Spacer(Modifier.size(Spacing.sm))
                    Text("Clear exam history", fontWeight = FontWeight.SemiBold)
                }
                Spacer(Modifier.height(Spacing.sm))
                HelperText("Removes all results, attempts, and weak-topic stats. Bookmarks are kept.")
            }

            if (BuildConfig.DEBUG) {
                SectionCard(title = "Debug", icon = Icons.Default.BugReport) {
                    OutlinedButton(
                        onClick = viewModel::reloadQuestionBank,
                        enabled = !state.isReloadingBank,
                        shape = RoundedCornerShape(Radius.md),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        if (state.isReloadingBank) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                            )
                        } else {
                            Icon(Icons.Default.BugReport, contentDescription = null)
                        }
                        Spacer(Modifier.size(Spacing.sm))
                        Text(
                            if (state.isReloadingBank) "Reloading\u2026" else "Reload question bank",
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                    Spacer(Modifier.height(Spacing.sm))
                    HelperText(
                        "Re-decrypts and re-seeds the bundled question bank. " +
                            "Removed questions are deleted; user data is preserved.",
                    )
                }
            }

            FooterCredit()
            Spacer(Modifier.height(Spacing.sm))
        }
    }

    if (state.showClearHistoryDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.showClearHistoryDialog(false) },
            icon = { Icon(Icons.Default.DeleteSweep, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Clear all exam history?") },
            text = { Text("This will permanently delete all exam results and per-question attempt history. This cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = viewModel::clearHistoryConfirmed,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                ) { Text("Clear") }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.showClearHistoryDialog(false) }) { Text("Cancel") }
            },
        )
    }
}

@Composable
private fun SettingsHero() {
    val brush = Brush.linearGradient(
        listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.tertiary,
        ),
    )
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Radius.lg),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.sm),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(brush)
                .padding(Spacing.xl),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.18f),
                            shape = RoundedCornerShape(Radius.md),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Default.School,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                    )
                }
                Spacer(Modifier.size(Spacing.md))
                Column(Modifier.weight(1f)) {
                    Text(
                        "ESEC",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                    Text(
                        "Tune your exam experience",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f),
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Radius.lg),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.xs),
    ) {
        Column(Modifier.padding(Spacing.lg)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(Modifier.size(Spacing.sm))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            Spacer(Modifier.height(Spacing.md))
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LanguageSegmented(
    selected: AppLanguage,
    onChange: (AppLanguage) -> Unit,
) {
    val items = AppLanguage.entries
    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
        items.forEachIndexed { index, lang ->
            SegmentedButton(
                selected = selected == lang,
                onClick = { onChange(lang) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = items.size),
                label = { Text(lang.label) },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThemeModeSegmented(
    selected: ThemeMode,
    onChange: (ThemeMode) -> Unit,
) {
    val modes = ThemeMode.entries
    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
        modes.forEachIndexed { index, mode ->
            SegmentedButton(
                selected = selected == mode,
                onClick = { onChange(mode) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = modes.size),
                icon = {
                    Icon(
                        imageVector = when (mode) {
                            ThemeMode.LIGHT -> Icons.Default.LightMode
                            ThemeMode.DARK -> Icons.Default.DarkMode
                            ThemeMode.SYSTEM -> Icons.Default.AutoAwesome
                        },
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                },
                label = {
                    Text(mode.name.lowercase().replaceFirstChar { it.uppercase() })
                },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StepperRow(
    label: String,
    options: List<Int>,
    selected: Int,
    onChange: (Int) -> Unit,
    formatter: (Int) -> String,
    leadingIcon: ImageVector? = null,
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (leadingIcon != null) {
                Icon(
                    leadingIcon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(Modifier.size(Spacing.xs))
            }
            Text(label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
        }
        Spacer(Modifier.height(Spacing.sm))
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            options.forEach { value ->
                FilterChip(
                    selected = selected == value,
                    onClick = { onChange(value) },
                    label = { Text(formatter(value)) },
                )
            }
        }
    }
}

@Composable
private fun HelperText(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReminderRow(
    enabled: Boolean,
    hour: Int,
    minute: Int,
    onEnabledChange: (Boolean) -> Unit,
    onTimeChange: (Int, Int) -> Unit,
) {
    var showPicker by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                "Daily reminder",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
            )
            Text(
                "%02d:%02d".format(hour, minute),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        AssistChip(
            onClick = { showPicker = true },
            enabled = enabled,
            label = { Text("Change time") },
            leadingIcon = {
                Icon(
                    Icons.Default.Schedule,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                )
            },
        )
        Spacer(Modifier.size(Spacing.sm))
        Switch(checked = enabled, onCheckedChange = onEnabledChange)
    }

    if (showPicker) {
        val pickerState = rememberTimePickerState(
            initialHour = hour,
            initialMinute = minute,
            is24Hour = true,
        )
        AlertDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    onTimeChange(pickerState.hour, pickerState.minute)
                    showPicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) { Text("Cancel") }
            },
            title = { Text("Reminder time") },
            text = { TimePicker(state = pickerState) },
        )
    }
}

@Composable
private fun FooterCredit() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        AssistChip(
            onClick = {},
            enabled = false,
            label = { Text("Made for Eritrean Grade 8 students") },
            leadingIcon = {
                Icon(
                    Icons.Default.School,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                )
            },
            colors = AssistChipDefaults.assistChipColors(
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledLeadingIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
        )
    }
}
