/**
 * Maps to /stitch_erixam_exam_companion/global_leaderboard_gamified_style/code.html
 * Design tokens from /stitch_erixam_exam_companion/erixam_design_system/DESIGN.md.
 *
 * National podium with 1st/2nd/3rd plinths, top-students list,
 * and a sticky "You" rank bar fixed at the bottom.
 */
package com.esec.examprep.presentation.leaderboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.esec.examprep.presentation.theme.ESECTheme
import com.esec.examprep.presentation.theme.Radius
import com.esec.examprep.presentation.theme.Spacing

private val GoldColor = Color(0xFFFFCC00)
private val BronzeColor = Color(0xFFCD7F32)

// -------- UI Models --------

@Immutable
data class PodiumEntry(
    val rank: Int,
    val name: String,
    val xp: Int,
    val level: Int? = null,
    val avatarUrl: String? = null,
)

@Immutable
data class LeaderboardRow(
    val rank: Int,
    val name: String,
    val level: Int,
    val xp: Int,
    val avatarUrl: String? = null,
)

@Immutable
data class CurrentUserRank(
    val rank: Int,
    val level: Int,
    val xp: Int,
    val avatarUrl: String? = null,
)

@Immutable
data class GlobalLeaderboardUiState(
    val seasonLabel: String,
    val first: PodiumEntry,
    val second: PodiumEntry,
    val third: PodiumEntry,
    val topRows: List<LeaderboardRow>,
    val nearbyRows: List<LeaderboardRow>,
    val totalStudents: Int,
    val currentUser: CurrentUserRank,
)

// -------- Stateful entry --------

@Composable
fun GlobalLeaderboardScreen(
    state: GlobalLeaderboardUiState,
    onMenuClick: () -> Unit,
    onSearchClick: () -> Unit,
    onRowClick: (LeaderboardRow) -> Unit,
    onMyRankClick: () -> Unit,
) {
    GlobalLeaderboardContent(
        state = state,
        onMenuClick = onMenuClick,
        onSearchClick = onSearchClick,
        onRowClick = onRowClick,
        onMyRankClick = onMyRankClick,
    )
}

// -------- Stateless content --------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GlobalLeaderboardContent(
    state: GlobalLeaderboardUiState,
    onMenuClick: () -> Unit,
    onSearchClick: () -> Unit,
    onRowClick: (LeaderboardRow) -> Unit,
    onMyRankClick: () -> Unit,
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
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 128.dp),
            ) {
                item("hero") { PodiumHero(state = state) }
                item("list-header") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = Spacing.lg,
                                end = Spacing.lg,
                                top = Spacing.lg,
                                bottom = Spacing.md,
                            ),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Top Students",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = "Total: %,d".format(state.totalStudents),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                items(state.topRows) { row ->
                    Box(modifier = Modifier.padding(horizontal = Spacing.lg, vertical = 4.dp)) {
                        LeaderRowItem(row = row, onClick = { onRowClick(row) })
                    }
                }
                if (state.nearbyRows.isNotEmpty()) {
                    item("divider-dots") {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = Spacing.sm),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(2.dp),
                        ) {
                            repeat(3) {
                                Box(
                                    modifier = Modifier
                                        .size(4.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.outlineVariant),
                                )
                            }
                        }
                    }
                    items(state.nearbyRows) { row ->
                        Box(modifier = Modifier.padding(horizontal = Spacing.lg, vertical = 4.dp)) {
                            LeaderRowItem(row = row, onClick = { onRowClick(row) })
                        }
                    }
                }
            }

            // Sticky "You" rank bar
            MyRankBar(
                user = state.currentUser,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(Spacing.lg),
                onClick = onMyRankClick,
            )
        }
    }
}

// -------- Podium hero --------

@Composable
private fun PodiumHero(state: GlobalLeaderboardUiState) {
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
        ),
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
            .background(gradientBrush)
            .padding(horizontal = Spacing.lg, vertical = Spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "National Leaderboard",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(Modifier.size(Spacing.xs))
        Row(
            modifier = Modifier
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .padding(horizontal = Spacing.md, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Icon(
                Icons.Default.WorkspacePremium,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.size(18.dp),
            )
            Text(
                text = state.seasonLabel,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                fontWeight = FontWeight.SemiBold,
            )
        }
        Spacer(Modifier.size(Spacing.xl))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(256.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            PodiumColumn(
                modifier = Modifier.weight(1f),
                entry = state.second,
                plinthHeight = 96.dp,
                plinthColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                plinthShape = RoundedCornerShape(topStart = Radius.lg, topEnd = Radius.lg),
                avatarSize = 64.dp,
                avatarBorder = MaterialTheme.colorScheme.outlineVariant,
                rankBadgeColor = MaterialTheme.colorScheme.outlineVariant,
                rankBadgeContent = Color.White,
                nameStyle = MaterialTheme.typography.labelLarge,
                xpColor = MaterialTheme.colorScheme.primary,
                avatarLift = 0.dp,
            )
            PodiumColumn(
                modifier = Modifier.weight(1f),
                entry = state.first,
                plinthHeight = 144.dp,
                plinthColor = MaterialTheme.colorScheme.primaryContainer,
                plinthShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                avatarSize = 80.dp,
                avatarBorder = GoldColor,
                rankBadgeColor = GoldColor,
                rankBadgeContent = MaterialTheme.colorScheme.onSurface,
                nameStyle = MaterialTheme.typography.titleLarge,
                xpColor = Color.White,
                avatarLift = 16.dp,
                isChampion = true,
            )
            PodiumColumn(
                modifier = Modifier.weight(1f),
                entry = state.third,
                plinthHeight = 80.dp,
                plinthColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                plinthShape = RoundedCornerShape(topStart = Radius.lg, topEnd = Radius.lg),
                avatarSize = 64.dp,
                avatarBorder = BronzeColor.copy(alpha = 0.4f),
                rankBadgeColor = BronzeColor,
                rankBadgeContent = Color.White,
                nameStyle = MaterialTheme.typography.labelLarge,
                xpColor = MaterialTheme.colorScheme.primary,
                avatarLift = 0.dp,
            )
        }
    }
}

@Composable
private fun PodiumColumn(
    modifier: Modifier = Modifier,
    entry: PodiumEntry,
    plinthHeight: Dp,
    plinthColor: Color,
    plinthShape: androidx.compose.ui.graphics.Shape,
    avatarSize: Dp,
    avatarBorder: Color,
    rankBadgeColor: Color,
    rankBadgeContent: Color,
    nameStyle: androidx.compose.ui.text.TextStyle,
    xpColor: Color,
    avatarLift: Dp,
    isChampion: Boolean = false,
) {
    val onPlinthColor = if (isChampion) Color.White else MaterialTheme.colorScheme.onSurface
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .offset(y = -avatarLift)
                .padding(bottom = 4.dp),
            contentAlignment = Alignment.Center,
        ) {
            if (isChampion) {
                Icon(
                    Icons.Default.Stars,
                    contentDescription = null,
                    tint = GoldColor,
                    modifier = Modifier
                        .size(28.dp)
                        .align(Alignment.TopCenter)
                        .offset(y = (-24).dp),
                )
            }
            Box(
                modifier = Modifier
                    .size(avatarSize)
                    .clip(CircleShape)
                    .border(4.dp, avatarBorder, CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainer),
                contentAlignment = Alignment.Center,
            ) {
                if (entry.avatarUrl != null) {
                    AsyncImage(
                        model = entry.avatarUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                    )
                } else {
                    Text(
                        text = entry.name.take(1).uppercase(),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            // rank badge
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 4.dp, y = 4.dp)
                    .size(if (isChampion) 32.dp else 24.dp)
                    .clip(CircleShape)
                    .background(rankBadgeColor),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = entry.rank.toString(),
                    style = if (isChampion) MaterialTheme.typography.labelLarge else MaterialTheme.typography.labelSmall,
                    color = rankBadgeContent,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(plinthHeight)
                .clip(plinthShape)
                .background(plinthColor)
                .padding(horizontal = 4.dp, vertical = Spacing.xs),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = entry.name,
                style = nameStyle,
                color = onPlinthColor,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "${formatXp(entry.xp)} XP",
                style = MaterialTheme.typography.labelLarge,
                color = xpColor,
                fontWeight = if (isChampion) FontWeight.ExtraBold else FontWeight.Bold,
            )
            if (isChampion && entry.level != null) {
                Spacer(Modifier.size(4.dp))
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.20f))
                        .padding(horizontal = Spacing.sm, vertical = 1.dp),
                ) {
                    Text(
                        text = "LVL ${entry.level}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

private fun formatXp(xp: Int): String {
    if (xp >= 1000) {
        val k = xp / 1000.0
        val rounded = (k * 10).toInt() / 10.0
        return if (rounded % 1.0 == 0.0) "${rounded.toInt()}k" else "${rounded}k"
    }
    return xp.toString()
}

// -------- Leaderboard row --------

@Composable
private fun LeaderRowItem(row: LeaderboardRow, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radius.md))
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.30f),
                shape = RoundedCornerShape(Radius.md),
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
            Text(
                text = row.rank.toString(),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.widthIn(min = 24.dp),
            )
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainer),
                contentAlignment = Alignment.Center,
            ) {
                if (row.avatarUrl != null) {
                    AsyncImage(
                        model = row.avatarUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                    )
                } else {
                    Text(
                        text = row.name.take(1).uppercase(),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = row.name,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "LEVEL ${row.level}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "%,d".format(row.xp),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "XP",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

// -------- My-rank sticky bar --------

@Composable
private fun MyRankBar(
    user: CurrentUserRank,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radius.md))
            .background(MaterialTheme.colorScheme.primary)
            .border(
                width = 4.dp,
                color = MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(Radius.md),
            )
            .padding(Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.White.copy(alpha = 0.40f), CircleShape)
                    .background(Color.White.copy(alpha = 0.10f)),
                contentAlignment = Alignment.Center,
            ) {
                if (user.avatarUrl != null) {
                    AsyncImage(
                        model = user.avatarUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                    )
                } else {
                    Text(
                        text = "Y",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            Column {
                Text(
                    text = "You",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "Level ${user.level} • %,d XP".format(user.xp),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.80f),
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        Row(
            modifier = Modifier
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.20f))
                .padding(horizontal = Spacing.md, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = "Rank ${user.rank}",
                style = MaterialTheme.typography.labelLarge,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
            )
            IconButton(
                onClick = onClick,
                modifier = Modifier.size(20.dp),
            ) {
                Icon(
                    Icons.Default.ExpandLess,
                    contentDescription = "Expand",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}

// -------- Preview --------

@Preview(showBackground = true, heightDp = 1400)
@Composable
private fun GlobalLeaderboardPreview() {
    ESECTheme {
        GlobalLeaderboardContent(
            state = GlobalLeaderboardUiState(
                seasonLabel = "Season 4 • 12 Days Left",
                first = PodiumEntry(rank = 1, name = "Dawit H.", xp = 16800, level = 42),
                second = PodiumEntry(rank = 2, name = "Senai B.", xp = 14200),
                third = PodiumEntry(rank = 3, name = "Meron T.", xp = 12900),
                topRows = listOf(
                    LeaderboardRow(4, "Yonas K.", 38, 11240),
                    LeaderboardRow(5, "Saba G.", 37, 10890),
                    LeaderboardRow(6, "Filmon W.", 35, 9420),
                ),
                nearbyRows = listOf(
                    LeaderboardRow(452, "Ruth S.", 15, 2150),
                ),
                totalStudents = 12402,
                currentUser = CurrentUserRank(rank = 453, level = 14, xp = 1980),
            ),
            onMenuClick = {},
            onSearchClick = {},
            onRowClick = {},
            onMyRankClick = {},
        )
    }
}
