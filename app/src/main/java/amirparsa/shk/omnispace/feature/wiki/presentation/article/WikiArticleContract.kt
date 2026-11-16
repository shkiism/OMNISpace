package amirparsa.shk.omnispace.feature.wiki.presentation.article

import amirparsa.shk.omnispace.core.base.mvi.UiEffect
import amirparsa.shk.omnispace.core.base.mvi.UiIntent
import amirparsa.shk.omnispace.core.base.mvi.UiState
import amirparsa.shk.omnispace.feature.wiki.domain.model.WikiArticle

/**
 * Immutable UI State for Wiki Article screen.
 */
data class WikiArticleUiState(
    val article: WikiArticle = WikiArticle(),
    val isBookmarked: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) : UiState {
    val isEmpty: Boolean
        get() = !isLoading && errorMessage == null && article.title.isBlank() && article.markdownContent.isBlank()
}

/**
 * User Intents for Wiki Article screen.
 */
sealed interface WikiArticleIntent : UiIntent {
    data class LoadArticle(val articleId: String) : WikiArticleIntent
    data class Retry(val articleId: String) : WikiArticleIntent
    data object ToggleBookmark : WikiArticleIntent
    data object ShareClicked : WikiArticleIntent
    data object CopySnippetClicked : WikiArticleIntent
    data object BackClicked : WikiArticleIntent
}

/**
 * Side Effects for Wiki Article screen.
 */
sealed interface WikiArticleEffect : UiEffect {
    data object NavigateBack : WikiArticleEffect
    data class ShowToast(val message: String) : WikiArticleEffect
    data class CopyToClipboard(val label: String, val text: String) : WikiArticleEffect
}
