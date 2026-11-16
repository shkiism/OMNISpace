package amirparsa.shk.omnispace.feature.wiki.domain.repository

import amirparsa.shk.omnispace.core.utils.NetworkResult
import amirparsa.shk.omnispace.feature.wiki.domain.model.WikiArticle
import amirparsa.shk.omnispace.feature.wiki.domain.model.WikiTreeNode

interface WikiRepository {
    suspend fun getWikiTree(): NetworkResult<List<WikiTreeNode>>
    suspend fun getWikiArticle(id: String): NetworkResult<WikiArticle>
}

