/**
 * Maps to /stitch_erixam_exam_companion/subject_detail_gamified_style/code.html
 * Design tokens from /stitch_erixam_exam_companion/erixam_design_system/DESIGN.md.
 *
 * Stateless presentational screen — wires to a future SubjectDetailViewModel
 * once curriculum/module data is modeled in the domain layer.
 */
package com.esec.examprep.presentation.subject

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChangeHistory
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.esec.examprep.presentation.theme.ESECTheme
import com.esec.examprep.presentation.theme.Elevation
import com.esec.examprep.presentation.theme.Radius
import com.esec.examprep.presentation.theme.Spacing

// -------- UI Models --------

enum class ModuleStatus { Completed, InProgress, Locked }

enum class ModuleTileSpan { Tall, Single, Wide }

@Immutable
data class CurriculumModule(
    val id: String,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val status: ModuleStatus,
    val progressLabel: String? = null, // e.g. "4/12" or "100% Done" or "Requires Level 15"
    val progress: Float = 0f,           // 0f..1f
    val span: ModuleTileSpan = ModuleTileSpan.Single,
)

@Immutable
data class SubjectDetailUiState(
    val subjectName: String,
    val masteryPercent: Int,
    val level: Int,
    val xpEarned: Int,
    val xpToNextLevel: Int,
    val ctaLabel: String,
    val modules: List<CurriculumModule>,
    val streakDays: Int,
    val streakMessage: String,
)

// -------- Stateful entry --------

@Composable
fun SubjectDetailScreen(
    state: SubjectDetailUiState,
    onBack: () -> Unit,
    onShare: () -> Unit,
    onResumeMission: () -> Unit,
    onModuleClick: (CurriculumModule) -> Unit,
    onViewAllModules: () -> Unit,
) {
    SubjectDetailContent(
        state = state,
        onBack = onBack,
        onShare = onShare,
        onResumeMission = onResumeMission,
        onModuleClick = onModuleClick,
        onViewAllModules = onViewAllModules,
    )
}

// -------- Stateless content --------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SubjectDetailContent(
    state: SubjectDetailUiState,
    onBack: () -> Unit,
    onShare: () -> Unit,
    onResumeMission: () -> Unit,
    onModuleClick: (CurriculumModule) -> Unit,
    onViewAllModules: () -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = state.subjectName,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onShare) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = "Share",
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
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(
                start = Spacing.lg,
                end = Spacing.lg,
                top = Spacing.sm,
                bottom = Spacing.xxxl,
            ),
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            item(span = { GridItemSpan(2) }) { MasteryHero(state) }
            item(span = { GridItemSpan(2) }) {
                Button(
                    onClick = onResumeMission,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(Modifier.size(Spacing.sm))
                    Text(
                        text = state.ctaLabel,
                        style = MaterialTheme.typography.titleLarge,
                    )
                }
            }
            item(span = { GridItemSpan(2) }) { CurriculumHeader(onViewAll = onViewAllModules) }

            state.modules.forEach { module ->
                val span = when (module.span) {
                    ModuleTileSpan.Single -> 1
                    ModuleTileSpan.Tall -> 1
                    ModuleTileSpan.Wide -> 2
                }
                item(span = { GridItemSpan(span) }, key = module.id) {
                    ModuleTile(
                        module = module,
                        onClick = { onModuleClick(module) },
                    )
                }
            }

            item(span = { GridItemSpan(2) }) {
                StreakBanner(days = state.streakDays, message = state.streakMessage)
            }
        }
    }
}

@Composable
private fun MasteryHero(state: SubjectDetailUiState) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radius.lg))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(Spacing.lg),
    ) {
        Icon(
            imageVector = Icons.Default.GridView,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.10f),
            modifier = Modifier
                .size(120.dp)
                .align(Alignment.BottomEnd),
        )
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column {
                    Text(
                        text = "Subject Mastery",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f),
                    )
                    Spacer(Modifier.height(Spacing.xs))
                    Text(
                        text = "${state.masteryPercent}%",
                        style = MaterialTheme.typography.displayLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
                LevelBadge(level = state.level)
            }
            Spacer(Modifier.height(Spacing.md))
            LinearProgressIndicator(
                progress = { state.masteryPercent / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(50)),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                trackColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f),
            )
            Spacer(Modifier.height(Spacing.sm))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "${state.xpEarned} XP Earned",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f),
                )
                Text(
                    text = "${state.xpToNextLevel} XP to Level ${state.level + 1}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f),
                )
            }
        }
    }
}

@Composable
private fun LevelBadge(level: Int) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.20f))
            .padding(horizontal = Spacing.md, vertical = Spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        Icon(
            imageVector = Icons.Default.MilitaryTech,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.size(18.dp),
        )
        Text(
            text = "Level $level",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
    }
}

@Composable
private fun CurriculumHeader(onViewAll: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Curriculum",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = "View All",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .clickable(onClick = onViewAll)
                .padding(horizontal = Spacing.sm, vertical = Spacing.xs),
        )
    }
}

@Composable
private fun ModuleTile(
    module: CurriculumModule,
    onClick: () -> Unit,
) {
    val isLocked = module.status == ModuleStatus.Locked

    val modifier = when (module.span) {
        ModuleTileSpan.Tall -> Modifier.fillMaxWidth().aspectRatio(0.62f)
        ModuleTileSpan.Single -> Modifier.fillMaxWidth().aspectRatio(1f)
        ModuleTileSpan.Wide -> Modifier.fillMaxWidth().height(96.dp)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(Radius.lg))
            .background(
                if (isLocked) MaterialTheme.colorScheme.surfaceContainerLow
                else MaterialTheme.colorScheme.surfaceContainerLowest,
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(Radius.lg),
            )
            .clickable(enabled = !isLocked, onClick = onClick)
            .padding(Spacing.md),
    ) {
        when (module.span) {
            ModuleTileSpan.Wide -> WideModuleContent(module)
            else -> StandardModuleContent(module)
        }
        if (isLocked) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Locked",
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(32.dp),
            )
        }
    }
}

@Composable
private fun StandardModuleContent(module: CurriculumModule) {
    val iconContainer = when (module.status) {
        ModuleStatus.Completed -> MaterialTheme.colorScheme.secondaryContainer
        ModuleStatus.InProgress -> MaterialTheme.colorScheme.primaryContainer
        ModuleStatus.Locked -> MaterialTheme.colorScheme.surfaceVariant
    }
    val iconTint = when (module.status) {
        ModuleStatus.Completed -> MaterialTheme.colorScheme.onSecondaryContainer
        ModuleStatus.InProgress -> MaterialTheme.colorScheme.onPrimaryContainer
        ModuleStatus.Locked -> MaterialTheme.colorScheme.outline
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(Radius.sm))
                        .background(iconContainer),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = module.icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(22.dp),
                    )
                }
                if (module.status == ModuleStatus.InProgress && module.progressLabel != null) {
                    Text(
                        text = module.progressLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                            .padding(horizontal = Spacing.sm, vertical = 2.dp),
                    )
                }
            }
            Spacer(Modifier.height(Spacing.md))
            Text(
                text = module.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
            )
            if (module.span == ModuleTileSpan.Tall) {
                Spacer(Modifier.height(Spacing.xs))
                Text(
                    text = module.subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        when (module.status) {
            ModuleStatus.Completed -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(16.dp),
                    )
                    Text(
                        text = module.progressLabel ?: "100% Done",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.secondary,
                    )
                }
            }
            ModuleStatus.InProgress -> {
                LinearProgressIndicator(
                    progress = { module.progress.coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(50)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                )
            }
            ModuleStatus.Locked -> {
                Text(
                    text = module.progressLabel ?: "Locked",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline,
                )
            }
        }
    }
}

@Composable
private fun WideModuleContent(module: CurriculumModule) {
    val isLocked = module.status == ModuleStatus.Locked
    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(Radius.sm))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = module.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = module.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = module.progressLabel ?: module.subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline,
            )
        }
        if (isLocked) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
            )
        }
    }
}

@Composable
private fun StreakBanner(days: Int, message: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radius.lg))
            .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.30f))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(Radius.lg),
            )
            .padding(Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Icon(
            imageVector = Icons.Default.LocalFireDepartment,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(32.dp),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "$days Day Study Streak!",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = message,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.85f),
            )
        }
    }
}

// -------- Preview --------

@Preview(showBackground = true, heightDp = 1400)
@Composable
private fun SubjectDetailPreview() {
    ESECTheme {
        SubjectDetailContent(
            state = SubjectDetailUiState(
                subjectName = "Mathematics",
                masteryPercent = 68,
                level = 12,
                xpEarned = 1240,
                xpToNextLevel = 360,
                ctaLabel = "Resume Mission",
                streakDays = 5,
                streakMessage = "Complete one more lesson today to earn a Golden Wreath badge.",
                modules = listOf(
                    CurriculumModule(
                        id = "algebra",
                        title = "Algebra Fundamentals",
                        subtitle = "Linear equations, polynomials, and factoring.",
                        icon = Icons.Default.Functions,
                        status = ModuleStatus.Completed,
                        progressLabel = "100% Done",
                        progress = 1f,
                        span = ModuleTileSpan.Tall,
                    ),
                    CurriculumModule(
                        id = "geometry",
                        title = "Geometry",
                        subtitle = "Shapes, angles, proofs.",
                        icon = Icons.Default.ChangeHistory,
                        status = ModuleStatus.InProgress,
                        progressLabel = "4/12",
                        progress = 0.33f,
                    ),
                    CurriculumModule(
                        id = "calculus",
                        title = "Calculus I",
                        subtitle = "Limits and derivatives.",
                        icon = Icons.Default.Timeline,
                        status = ModuleStatus.Locked,
                        progressLabel = "Requires Level 15",
                    ),
                    CurriculumModule(
                        id = "stats",
                        title = "Statistics & Probability",
                        subtitle = "",
                        icon = Icons.Default.Leaderboard,
                        status = ModuleStatus.Locked,
                        progressLabel = "Unlocks after Geometry",
                        span = ModuleTileSpan.Wide,
                    ),
                ),
            ),
            onBack = {},
            onShare = {},
            onResumeMission = {},
            onModuleClick = {},
            onViewAllModules = {},
        )
    }
}