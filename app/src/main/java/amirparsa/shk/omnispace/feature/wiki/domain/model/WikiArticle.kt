package amirparsa.shk.omnispace.feature.wiki.domain.model

import com.google.gson.annotations.SerializedName

/**
 * Domain entity representing a full Wiki document/article.
 */
data class WikiArticle(
    @SerializedName("id")
    val id: String = "",
    @SerializedName("title")
    val title: String = "",
    @SerializedName("space_path", alternate = ["spacePath", "space", "category"])
    val spacePath: String = "",
    @SerializedName("approved_date", alternate = ["approvedDate", "date", "created_at", "createdAt"])
    val approvedDate: String = "",
    @SerializedName("author", alternate = ["created_by", "createdBy", "authorName"])
    val author: String = "",
    @SerializedName("reading_time", alternate = ["readingTime"])
    val readingTime: String = "",
    @SerializedName("is_bookmarked", alternate = ["isBookmarked", "bookmarked"])
    val isBookmarked: Boolean = false,
    @SerializedName("sample_code_snippet", alternate = ["sampleCodeSnippet", "codeSnippet", "snippet"])
    val sampleCodeSnippet: String = "",
    @SerializedName("markdown_content", alternate = ["markdownContent", "content", "body", "text"])
    val markdownContent: String = ""
)
