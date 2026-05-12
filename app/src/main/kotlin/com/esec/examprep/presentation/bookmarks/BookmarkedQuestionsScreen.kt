/**
 * Maps to /stitch_erixam_exam_companion/bookmarked_questions/code.html
 * Design tokens from /stitch_erixam_exam_companion/erixam_design_system/DESIGN.md.
 *
 * "My Collection" of bookmarked questions: search + tabs +
 * subject filter chips + quest cards (subject/difficulty badges,
 * faded subject glyph), an inset image promo bento, and a
 * "REVIEW NOW" extended FAB.
 */
package com.esec.examprep.presentation.bookmarks

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.BookmarkRemove
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.esec.examprep.presentation.theme.ESECTheme
import com.esec.examprep.presentation.theme.Radius
import com.esec.examprep.presentation.theme.Spacing

// -------- UI Models --------

enum class BookmarkTab { Bookmarked, Notes }
enum class BookmarkDifficulty { Easy, Medium, Hard }

@Immutable
data class BookmarkChip(val id: String, val label: String)

@Immutable
data class BookmarkedQuestion(
    val id: String,
    val subject: String,
    val difficulty: BookmarkDifficulty,
    val text: String,
    val savedAgo: String,
    val icon: ImageVector,
)

@Immutable
data class BookmarkPromo(
    val title: String,
    val subtitle: String,
    val imageUrl: String? = null,
)

@Immutable
data class BookmarksUiState(
    val level: Int,
    val intro: String,
    val searchQuery: String,
    val activeTab: BookmarkTab,
    val filters: List<BookmarkChip>,
    val activeFilterId: String,
    val questions: List<BookmarkedQuestion>,
    val promo: BookmarkPromo? = null,
)

// -------- Stateful entry --------

@Composable
fun BookmarkedQuestionsScreen(
    state: BookmarksUiState,
    onMenuClick: () -> Unit,
    onSearchClick: () -> Unit,
    onQueryChange: (String) -> Unit,
    onTabChange: (BookmarkTab) -> Unit,
    onFilterClick: (BookmarkChip) -> Unit,
    onQuestionClick: (BookmarkedQuestion) -> Unit,
    onRemoveBookmark: (BookmarkedQuestion) -> Unit,
    onReviewNow: () -> Unit,
) {
    BookmarkedQuestionsContent(
        state = state,
        onMenuClick = onMenuClick,
        onSearchClick = onSearchClick,
        onQueryChange = onQueryChange,
        onTabChange = onTabChange,
        onFilterClick = onFilterClick,
        onQuestionClick = onQuestionClick,
        onRemoveBookmark = onRemoveBookmark,
        onReviewNow = onReviewNow,
    )
}

// -------- Stateless content --------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BookmarkedQuestionsContent(
    state: BookmarksUiState,
    onMenuClick: () -> Unit,
    onSearchClick: () -> Unit,
    onQueryChange: (String) -> Unit,
    onTabChange: (BookmarkTab) -> Unit,
    onFilterClick: (BookmarkChip) -> Unit,
    onQuestionClick: (BookmarkedQuestion) -> Unit,
    onRemoveBookmark: (BookmarkedQuestion) -> Unit,
    onReviewNow: () -> Unit,
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
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onReviewNow,
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary,
                icon = {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = null,
                    )
                },
                text = {
                    Text(
                        text = "REVIEW NOW",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                    )
                },
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
                bottom = Spacing.xxxl + Spacing.xxl,
            ),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            item("title") {
                CollectionTitle(level = state.level, intro = state.intro)
            }
            item("search") {
                SearchField(
                    query = state.searchQuery,
                    onQueryChange = onQueryChange,
                )
            }
            item("tabs") {
                BookmarkTabs(active = state.activeTab, onTabChange = onTabChange)
            }
            item("filters") {
                FilterChipsRow(
                    chips = state.filters,
                    activeId = state.activeFilterId,
                    onClick = onFilterClick,
                )
            }
            items(state.questions, key = { it.id }) { q ->
                QuestionBentoCard(
                    question = q,
                    onClick = { onQuestionClick(q) },
                    onRemove = { onRemoveBookmark(q) },
                )
            }
            state.promo?.let { promo ->
                item("promo") { PromoImageCard(promo = promo) }
            }
        }
    }
}

// -------- Title --------

@Composable
private fun CollectionTitle(level: Int, intro: String) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "My Collection",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Row(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .padding(horizontal = Spacing.sm, vertical = 4.dp),
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
                    text = "Level $level",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
        Text(
            text = intro,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

// -------- Search field --------

@Composable
private fun SearchField(query: String, onQueryChange: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radius.md))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(horizontal = Spacing.md, vertical = Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Icon(
            Icons.Default.Search,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline,
        )
        Box(modifier = Modifier.weight(1f)) {
            if (query.isEmpty()) {
                Text(
                    text = "Search saved questions...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outlineVariant,
                )
            }
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                ),
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

// -------- Tab segmented control --------

@Composable
private fun BookmarkTabs(active: BookmarkTab, onTabChange: (BookmarkTab) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radius.md))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        BookmarkTab.values().forEach { tab ->
            val selected = tab == active
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(Radius.sm))
                    .background(
                        if (selected) MaterialTheme.colorScheme.primary
                        else Color.Transparent,
                    )
                    .clickable { onTabChange(tab) }
                    .padding(vertical = Spacing.sm),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = when (tab) {
                        BookmarkTab.Bookmarked -> "Bookmarked"
                        BookmarkTab.Notes -> "Notes"
                    },
                    style = MaterialTheme.typography.labelLarge,
                    color = if (selected) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

// -------- Filter chips --------

@Composable
private fun FilterChipsRow(
    chips: List<BookmarkChip>,
    activeId: String,
    onClick: (BookmarkChip) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        chips.forEachIndexed { idx, chip ->
            val selected = chip.id == activeId
            Row(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(
                        if (selected) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surfaceContainerLowest,
                    )
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant,
                        shape = CircleShape,
                    )
                    .clickable { onClick(chip) }
                    .padding(horizontal = Spacing.md, vertical = Spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                if (idx == 0) {
                    Icon(
                        Icons.Default.FilterList,
                        contentDescription = null,
                        tint = if (selected) MaterialTheme.colorScheme.onPrimaryContainer
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp),
                    )
                }
                Text(
                    text = chip.label,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

// -------- Question bento card --------

@Composable
private fun QuestionBentoCard(
    question: BookmarkedQuestion,
    onClick: () -> Unit,
    onRemove: () -> Unit,
) {
    val gradient = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.surfaceContainerLowest,
            MaterialTheme.colorScheme.primary.copy(alpha = 0.06f),
        ),
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radius.md))
            .background(gradient)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(Radius.md),
            )
            .clickable(onClick = onClick)
            .padding(Spacing.md),
    ) {
        // Faded subject glyph
        Icon(
            question.icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(36.dp),
        )
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    DifficultyBadge(
                        text = question.subject,
                        bg = MaterialTheme.colorScheme.primaryContainer,
                        fg = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                    val (db, df) = when (question.difficulty) {
                        BookmarkDifficulty.Easy ->
                            MaterialTheme.colorScheme.secondaryContainer to
                                MaterialTheme.colorScheme.onSecondaryContainer
                        BookmarkDifficulty.Medium ->
                            MaterialTheme.colorScheme.secondaryContainer to
                                MaterialTheme.colorScheme.onSecondaryContainer
                        BookmarkDifficulty.Hard ->
                            MaterialTheme.colorScheme.tertiaryContainer to
                                MaterialTheme.colorScheme.onTertiaryContainer
                    }
                    DifficultyBadge(
                        text = when (question.difficulty) {
                            BookmarkDifficulty.Easy -> "Easy"
                            BookmarkDifficulty.Medium -> "Medium"
                            BookmarkDifficulty.Hard -> "Hard"
                        },
                        bg = db,
                        fg = df,
                    )
                }
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(24.dp),
                ) {
                    Icon(
                        Icons.Default.BookmarkRemove,
                        contentDescription = "Remove bookmark",
                        tint = MaterialTheme.colorScheme.error,
                    )
                }
            }
            Text(
                text = question.text,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "Saved ${question.savedAgo}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = "View Solution",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun DifficultyBadge(text: String, bg: Color, fg: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(Radius.sm))
            .background(bg)
            .padding(horizontal = Spacing.sm, vertical = 2.dp),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = fg,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

// -------- Promo image card --------

@Composable
private fun PromoImageCard(promo: BookmarkPromo) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(192.dp)
            .clip(RoundedCornerShape(Radius.md))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(Radius.md),
            )
            .background(MaterialTheme.colorScheme.surfaceContainerHigh),
    ) {
        if (promo.imageUrl != null) {
            AsyncImage(
                model = promo.imageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
            )
        }
        // Dark gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.6f),
                        ),
                    ),
                ),
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(Spacing.md),
        ) {
            Text(
                text = promo.title,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.size(2.dp))
            Text(
                text = promo.subtitle,
                style = MaterialTheme.typography.labelLarge,
                color = Color.White,
            )
        }
    }
}

// -------- Preview --------

@Preview(showBackground = true, heightDp = 1500)
@Composable
private fun BookmarkedQuestionsPreview() {
    ESECTheme {
        BookmarkedQuestionsContent(
            state = BookmarksUiState(
                level = 12,
                intro = "Manage your quest items and special notes for final battle preparation.",
                searchQuery = "",
                activeTab = BookmarkTab.Bookmarked,
                filters = listOf(
                    BookmarkChip("all", "All Subjects"),
                    BookmarkChip("math", "Math"),
                    BookmarkChip("bio", "Biology"),
                    BookmarkChip("hist", "History"),
                    BookmarkChip("hard", "Hard"),
                ),
                activeFilterId = "all",
                questions = listOf(
                    BookmarkedQuestion(
                        id = "bio-1",
                        subject = "Biology",
                        difficulty = BookmarkDifficulty.Hard,
                        text = "Explain the process of photosynthesis and the specific role of chlorophyll in capturing sunlight.",
                        savedAgo = "2 days ago",
                        icon = Icons.Default.Eco,
                    ),
                    BookmarkedQuestion(
                        id = "math-1",
                        subject = "Mathematics",
                        difficulty = BookmarkDifficulty.Medium,
                        text = "Find the derivative of f(x) = 3x^2 + 5x - 2 using the first principles of calculus.",
                        savedAgo = "5 days ago",
                        icon = Icons.Default.Calculate,
                    ),
                    BookmarkedQuestion(
                        id = "hist-1",
                        subject = "History",
                        difficulty = BookmarkDifficulty.Easy,
                        text = "What were the primary outcomes of the Battle of Dogali in 1887?",
                        savedAgo = "1 week ago",
                        icon = Icons.Default.HistoryEdu,
                    ),
                ),
                promo = BookmarkPromo(
                    title = "Quest Streak: 7 Days",
                    subtitle = "Continue your mastery journey",
                ),
            ),
            onMenuClick = {},
            onSearchClick = {},
            onQueryChange = {},
            onTabChange = {},
            onFilterClick = {},
            onQuestionClick = {},
            onRemoveBookmark = {},
            onReviewNow = {},
        )
    }
}
