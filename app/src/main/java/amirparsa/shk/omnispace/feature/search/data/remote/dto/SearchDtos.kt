package amirparsa.shk.omnispace.feature.search.data.remote.dto

import com.google.gson.annotations.SerializedName

data class SemanticSearchResultDto(
    @SerializedName("type")
    val type: String = "issue",
    @SerializedName("id")
    val id: String = "",
    @SerializedName("project_id", alternate = ["projectId"])
    val projectId: String? = null,
    @SerializedName("score")
    val score: Double = 0.0,
    @SerializedName("title")
    val title: String = "",
    @SerializedName("key")
    val key: String? = null,
    @SerializedName("ref")
    val ref: String? = null,
    @SerializedName("is_mine", alternate = ["isMine", "mine"])
    val isMine: Boolean = false,
    @SerializedName("is_high_priority", alternate = ["isHighPriority", "highPriority", "urgent"])
    val isHighPriority: Boolean = false
)
