package amirparsa.shk.omnispace.feature.wiki.presentation.tree

import androidx.lifecycle.viewModelScope
import amirparsa.shk.omnispace.core.base.mvi.MviViewModel
import amirparsa.shk.omnispace.core.utils.NetworkResult
import amirparsa.shk.omnispace.feature.wiki.domain.repository.WikiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * MVI ViewModel managing Wiki Tree hierarchy, spaces, search filtering, error handling, and expanded folders.
 */
@HiltViewModel
class WikiTreeViewModel @Inject constructor(
    private val wikiRepo: WikiRepository
) : MviViewModel<WikiTreeUiState, WikiTreeIntent, WikiTreeEffect>(
    initialState = WikiTreeUiState()
) {

    init {
        loadWikiTree()
    }

    fun loadWikiTree() {
        viewModelScope.launch {
            setState { copy(isLoading = true, errorMessage = null) }
            when (val result = wikiRepo.getWikiTree()) {
                is NetworkResult.Success -> {
                    val nodes = result.data
                    val folderIds = nodes.filter { it.isFolder }.map { it.id }.toSet()
                    setState {
                        copy(
                            isLoading = false,
                            errorMessage = null,
                            treeData = nodes,
                            expandedFolderIds = folderIds
                        )
                    }
                }
                is NetworkResult.Error -> {
                    setState {
                        copy(
                            isLoading = false,
                            errorMessage = result.message.ifBlank { "خطا در دریافت ساختار درختی دانشنامه" },
                            treeData = emptyList()
                        )
                    }
                }
                NetworkResult.Loading -> {
                    setState { copy(isLoading = true) }
                }
            }
        }
    }

    override fun processIntent(intent: WikiTreeIntent) {
        when (intent) {
            is WikiTreeIntent.SelectSpace -> setState { copy(selectedSpace = intent.space) }
            is WikiTreeIntent.SearchQueryChanged -> setState { copy(searchQuery = intent.query) }
            is WikiTreeIntent.ToggleFolder -> toggleFolder(intent.folderId)
            is WikiTreeIntent.DocumentClicked -> sendEffect(WikiTreeEffect.OpenDocument(intent.docId, intent.title))
            WikiTreeIntent.BackClicked -> sendEffect(WikiTreeEffect.NavigateBack)
            WikiTreeIntent.Retry -> loadWikiTree()
        }
    }

    private fun toggleFolder(folderId: String) {
        val currentSet = uiState.value.expandedFolderIds
        val newSet = if (currentSet.contains(folderId)) {
            currentSet - folderId
        } else {
            currentSet + folderId
        }
        setState { copy(expandedFolderIds = newSet) }
    }
}
