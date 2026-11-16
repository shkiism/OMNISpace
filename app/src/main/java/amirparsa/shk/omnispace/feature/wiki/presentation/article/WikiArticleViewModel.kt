package amirparsa.shk.omnispace.feature.wiki.presentation.article

import androidx.lifecycle.viewModelScope
import amirparsa.shk.omnispace.core.base.mvi.MviViewModel
import amirparsa.shk.omnispace.core.utils.NetworkResult
import amirparsa.shk.omnispace.feature.wiki.domain.repository.WikiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * MVI ViewModel managing Wiki Article reading, bookmarks, error handling, and copy actions.
 */
@HiltViewModel
class WikiArticleViewModel @Inject constructor(
    private val wikiRepo: WikiRepository
) : MviViewModel<WikiArticleUiState, WikiArticleIntent, WikiArticleEffect>(
    initialState = WikiArticleUiState()
) {
    private var currentArticleId: String = ""

    fun loadArticle(id: String) {
        if (id.isBlank()) return
        currentArticleId = id
        viewModelScope.launch {
            setState { copy(isLoading = true, errorMessage = null) }
            when (val res = wikiRepo.getWikiArticle(id)) {
                is NetworkResult.Success -> {
                    setState {
                        copy(
                            article = res.data,
                            isBookmarked = res.data.isBookmarked,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                }
                is NetworkResult.Error -> {
                    setState {
                        copy(
                            isLoading = false,
                            errorMessage = res.message.ifBlank { "خطا در بارگذاری سند دانشنامه" }
                        )
                    }
                }
                NetworkResult.Loading -> {
                    setState { copy(isLoading = true) }
                }
            }
        }
    }

    override fun processIntent(intent: WikiArticleIntent) {
        when (intent) {
            is WikiArticleIntent.LoadArticle -> {
                loadArticle(intent.articleId)
            }
            is WikiArticleIntent.Retry -> {
                val targetId = intent.articleId.ifBlank { currentArticleId }
                loadArticle(targetId)
            }
            WikiArticleIntent.ToggleBookmark -> {
                setState { copy(isBookmarked = !isBookmarked) }
            }
            WikiArticleIntent.ShareClicked -> {
                sendEffect(WikiArticleEffect.ShowToast("لینک سند کپی شد."))
            }
            WikiArticleIntent.CopySnippetClicked -> {
                val snippet = uiState.value.article.sampleCodeSnippet
                if (snippet.isNotBlank()) {
                    sendEffect(
                        WikiArticleEffect.CopyToClipboard(
                            label = "Code snippet",
                            text = snippet
                        )
                    )
                }
            }
            WikiArticleIntent.BackClicked -> {
                sendEffect(WikiArticleEffect.NavigateBack)
            }
        }
    }
}
