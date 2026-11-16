package amirparsa.shk.omnispace.feature.wiki.presentation.tree

import amirparsa.shk.omnispace.core.base.mvi.UiEffect
import amirparsa.shk.omnispace.core.base.mvi.UiIntent
import amirparsa.shk.omnispace.core.base.mvi.UiState
import amirparsa.shk.omnispace.feature.wiki.domain.model.WikiTreeNode

/**
 * Immutable UI State for Wiki Tree Navigation screen.
 */
data class WikiTreeUiState(
    val spaces: List<String> = listOf("فنی و معماری", "محصول و دیزاین", "منابع انسانی", "DevOps و زیرساخت"),
    val selectedSpace: String = "فنی و معماری",
    val searchQuery: String = "",
    val treeData: List<WikiTreeNode> = emptyList(),
    val expandedFolderIds: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) : UiState {
    val totalDocCount: Int
        get() = treeData.sumOf { it.docCount }

    val filteredTreeData: List<WikiTreeNode>
        get() {
            if (searchQuery.isBlank()) return treeData
            return treeData.mapNotNull { folder ->
                val matchingChildren = folder.children.filter {
                    it.title.contains(searchQuery, ignoreCase = true)
                }
                if (folder.title.contains(searchQuery, ignoreCase = true) || matchingChildren.isNotEmpty()) {
                    folder.copy(children = if (matchingChildren.isNotEmpty()) matchingChildren else folder.children)
                } else {
                    null
                }
            }
        }

    val isEmpty: Boolean
        get() = !isLoading && errorMessage == null && treeData.isEmpty()
}

/**
 * User Intents for Wiki Tree Navigation screen.
 */
sealed interface WikiTreeIntent : UiIntent {
    data class SelectSpace(val space: String) : WikiTreeIntent
    data class SearchQueryChanged(val query: String) : WikiTreeIntent
    data class ToggleFolder(val folderId: String) : WikiTreeIntent
    data class DocumentClicked(val docId: String, val title: String) : WikiTreeIntent
    data object BackClicked : WikiTreeIntent
    data object Retry : WikiTreeIntent
}

/**
 * Side Effects for Wiki Tree Navigation screen.
 */
sealed interface WikiTreeEffect : UiEffect {
    data object NavigateBack : WikiTreeEffect
    data class OpenDocument(val docId: String, val title: String) : WikiTreeEffect
}
