package amirparsa.shk.omnispace.feature.search.domain.repository

import amirparsa.shk.omnispace.core.utils.NetworkResult
import amirparsa.shk.omnispace.feature.search.data.remote.dto.SemanticSearchResultDto

interface SearchRepository {
    suspend fun searchSemantic(query: String, limit: Int = 20): NetworkResult<List<SemanticSearchResultDto>>
}

