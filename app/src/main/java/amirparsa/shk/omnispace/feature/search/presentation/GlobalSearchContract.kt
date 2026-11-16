package amirparsa.shk.omnispace.feature.search.presentation

import androidx.compose.ui.graphics.Color
import amirparsa.shk.omnispace.core.base.mvi.UiEffect
import amirparsa.shk.omnispace.core.base.mvi.UiIntent
import amirparsa.shk.omnispace.core.base.mvi.UiState

/**
 * Filter categories for global search.
 * Belonging to Presentation Layer (NOT Domain).
 */
enum class SearchCategory(val titleFa: String) {
    ALL("همه"),
    ISSUES("تسک‌ها و باگ‌ها"),
    CHATS("گفتگوها و پیام‌ها"),
    WIKI("اسناد دانشنامه"),
    PEOPLE("همکاران")
}

/**
 * UI presentation item for Search results with styling & badges.
 */
data class SearchResultItem(
    val id: String,
    val category: SearchCategory,
    val title: String,
    val subtitle: String,
    val metaTag: String,
    val tagColor: Color,
    val targetKey: String,
    val isMine: Boolean = false,
    val isHighPriority: Boolean = false
)

/**
 * Immutable UI State for Global Search screen.
 */
data class GlobalSearchUiState(
    val searchQuery: String = "",
    val selectedCategory: SearchCategory = SearchCategory.ALL,
    val filterOnlyMine: Boolean = false,
    val filterOnlyHighPriority: Boolean = false,
    val allResults: List<SearchResultItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) : UiState {
    val filteredResults: List<SearchResultItem>
        get() = allResults.filter { item ->
            val matchesCategory = selectedCategory == SearchCategory.ALL || item.category == selectedCategory
            val matchesQuery = searchQuery.isBlank() ||
                    item.title.contains(searchQuery, ignoreCase = true) ||
                    item.subtitle.contains(searchQuery, ignoreCase = true)
            val matchesPriority = !filterOnlyHighPriority || item.isHighPriority || item.metaTag.contains("فوری") || item.metaTag.contains("بحرانی")
            val matchesMine = !filterOnlyMine || item.isMine || item.title.contains("من", ignoreCase = true) || item.metaTag.contains("من")
            matchesCategory && matchesQuery && matchesPriority && matchesMine
        }
}

/**
 * User Intents for Global Search.
 */
sealed interface GlobalSearchIntent : UiIntent {
    data class QueryChanged(val query: String) : GlobalSearchIntent
    data object ClearQuery : GlobalSearchIntent
    data class SelectCategory(val category: SearchCategory) : GlobalSearchIntent
    data object ToggleFilterMine : GlobalSearchIntent
    data object ToggleFilterHighPriority : GlobalSearchIntent
    data object BackClicked : GlobalSearchIntent
    data class ResultItemClicked(val item: SearchResultItem) : GlobalSearchIntent
    data object Retry : GlobalSearchIntent
}

/**
 * Side Effects for Global Search.
 */
sealed interface GlobalSearchEffect : UiEffect {
    data object NavigateBack : GlobalSearchEffect
    data class OpenIssue(val key: String) : GlobalSearchEffect
    data class OpenChat(val key: String) : GlobalSearchEffect
    data class OpenWiki(val key: String) : GlobalSearchEffect
    data class OpenProfile(val key: String) : GlobalSearchEffect
}
