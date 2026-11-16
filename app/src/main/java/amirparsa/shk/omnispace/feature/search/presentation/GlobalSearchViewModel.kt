package amirparsa.shk.omnispace.feature.search.presentation

import androidx.lifecycle.viewModelScope
import amirparsa.shk.omnispace.core.base.mvi.MviViewModel
import amirparsa.shk.omnispace.core.designsystem.DarkPrimaryViolet
import amirparsa.shk.omnispace.core.designsystem.DarkStatusInfo
import amirparsa.shk.omnispace.core.utils.NetworkResult
import amirparsa.shk.omnispace.feature.search.domain.repository.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

/**
 * MVI ViewModel managing global search state, queries, filters, error handling, and AI semantic backend search.
 */
@HiltViewModel
class GlobalSearchViewModel @Inject constructor(
    private val searchRepo: SearchRepository
) : MviViewModel<GlobalSearchUiState, GlobalSearchIntent, GlobalSearchEffect>(
    initialState = GlobalSearchUiState()
) {
    private var searchJob: Job? = null

    override fun processIntent(intent: GlobalSearchIntent) {
        when (intent) {
            is GlobalSearchIntent.QueryChanged -> {
                setState { copy(searchQuery = intent.query) }
                performSemanticSearch(intent.query)
            }
            GlobalSearchIntent.ClearQuery -> {
                searchJob?.cancel()
                setState { copy(searchQuery = "", allResults = emptyList(), isLoading = false, errorMessage = null) }
            }
            is GlobalSearchIntent.SelectCategory -> setState { copy(selectedCategory = intent.category) }
            GlobalSearchIntent.ToggleFilterMine -> setState { copy(filterOnlyMine = !filterOnlyMine) }
            GlobalSearchIntent.ToggleFilterHighPriority -> setState { copy(filterOnlyHighPriority = !filterOnlyHighPriority) }
            GlobalSearchIntent.BackClicked -> sendEffect(GlobalSearchEffect.NavigateBack)
            is GlobalSearchIntent.ResultItemClicked -> handleItemClick(intent.item)
            GlobalSearchIntent.Retry -> performSemanticSearch(uiState.value.searchQuery)
        }
    }

    private fun performSemanticSearch(query: String) {
        searchJob?.cancel()
        val trimmed = query.trim()
        if (trimmed.length < 2) {
            setState { copy(isLoading = false, errorMessage = null, allResults = emptyList()) }
            return
        }

        searchJob = viewModelScope.launch {
            delay(300L) // Debounce typing
            setState { copy(isLoading = true, errorMessage = null) }
            when (val result = searchRepo.searchSemantic(trimmed)) {
                is NetworkResult.Success -> {
                    val mappedResults = result.data.map { dto ->
                        val category = when (dto.type.lowercase()) {
                            "issue", "task", "bug" -> SearchCategory.ISSUES
                            "chat", "message" -> SearchCategory.CHATS
                            "wiki", "kb", "doc", "article" -> SearchCategory.WIKI
                            "person", "people", "user" -> SearchCategory.PEOPLE
                            else -> SearchCategory.ISSUES
                        }
                        val formattedScore = String.format(Locale.US, "%.2f", dto.score)
                        val subtitleText = if (dto.score > 0.0) {
                            "امتیاز هوش مصنوعی: $formattedScore"
                        } else {
                            when (category) {
                                SearchCategory.ISSUES -> "شناسه تسک: ${dto.key ?: dto.id}"
                                SearchCategory.WIKI -> "مستند دانشنامه سازمانی"
                                SearchCategory.CHATS -> "پیام گفتگو"
                                SearchCategory.PEOPLE -> "پروفایل سازمانی همکار"
                                SearchCategory.ALL -> "نتیجه جستجو"
                            }
                        }
                        val metaTagText = when {
                            !dto.key.isNullOrBlank() -> "تسک سازمانی"
                            category == SearchCategory.WIKI -> "دانشنامه"
                            category == SearchCategory.CHATS -> "گفتگو"
                            category == SearchCategory.PEOPLE -> "همکار"
                            else -> "نتیجه"
                        }
                        SearchResultItem(
                            id = dto.id,
                            category = category,
                            title = if (!dto.key.isNullOrBlank()) "${dto.key}: ${dto.title}" else dto.title,
                            subtitle = subtitleText,
                            metaTag = metaTagText,
                            tagColor = if (category == SearchCategory.ISSUES) DarkPrimaryViolet else DarkStatusInfo,
                            targetKey = dto.key ?: dto.title,
                            isMine = dto.isMine,
                            isHighPriority = dto.isHighPriority
                        )
                    }
                    setState {
                        copy(
                            isLoading = false,
                            errorMessage = null,
                            allResults = mappedResults
                        )
                    }
                }
                is NetworkResult.Error -> {
                    setState {
                        copy(
                            isLoading = false,
                            errorMessage = result.message.ifBlank { "خطا در دریافت نتایج جستجو از سرور" },
                            allResults = emptyList()
                        )
                    }
                }
                NetworkResult.Loading -> {
                    setState { copy(isLoading = true) }
                }
            }
        }
    }

    private fun handleItemClick(item: SearchResultItem) {
        when (item.category) {
            SearchCategory.ISSUES, SearchCategory.ALL -> sendEffect(GlobalSearchEffect.OpenIssue(item.targetKey))
            SearchCategory.CHATS -> sendEffect(GlobalSearchEffect.OpenChat(item.targetKey))
            SearchCategory.WIKI -> sendEffect(GlobalSearchEffect.OpenWiki(item.targetKey))
            SearchCategory.PEOPLE -> sendEffect(GlobalSearchEffect.OpenProfile(item.targetKey))
        }
    }
}
