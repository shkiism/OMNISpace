package amirparsa.shk.omnispace.feature.wiki.domain.model

import com.google.gson.annotations.SerializedName

/**
 * Domain entity representing a node in the Wiki tree hierarchy.
 */
data class WikiTreeNode(
    @SerializedName("id")
    val id: String = "",
    @SerializedName("title")
    val title: String = "",
    @SerializedName("is_folder", alternate = ["isFolder"])
    val isFolder: Boolean = false,
    @SerializedName("doc_count", alternate = ["docCount"])
    val docCount: Int = 0,
    @SerializedName("status_text", alternate = ["statusText"])
    val statusText: String? = null,
    @SerializedName("status_color", alternate = ["statusColor"])
    val statusColor: Long? = null,
    @SerializedName("children")
    val children: List<WikiTreeNode> = emptyList()
)
