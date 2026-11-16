package amirparsa.shk.omnispace.feature.wiki.data.repository

import android.content.Context
import amirparsa.shk.omnispace.core.network.OmniHttpClient
import amirparsa.shk.omnispace.core.utils.NetworkResult
import amirparsa.shk.omnispace.feature.wiki.domain.model.WikiArticle
import amirparsa.shk.omnispace.feature.wiki.domain.model.WikiTreeNode
import amirparsa.shk.omnispace.feature.wiki.domain.repository.WikiRepository
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Request
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WikiRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : WikiRepository {

    private val client = OmniHttpClient.getClient(context)
    private val gson = Gson()

    override suspend fun getWikiTree(): NetworkResult<List<WikiTreeNode>> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("${OmniHttpClient.WORK_BASE}/wiki/tree")
                .get()
                .build()
            val response = client.newCall(request).execute()
            val json = response.body?.string().orEmpty()

            if (response.isSuccessful) {
                val listType = object : TypeToken<List<WikiTreeNode>>() {}.type
                val data: List<WikiTreeNode> = try {
                    val trimmed = json.trim()
                    if (trimmed.startsWith("[")) {
                        gson.fromJson(trimmed, listType) ?: emptyList()
                    } else if (trimmed.startsWith("{")) {
                        val rootObj = JsonParser.parseString(trimmed).asJsonObject
                        val arrayElement = rootObj.get("tree")
                            ?: rootObj.get("data")
                            ?: rootObj.get("nodes")
                            ?: rootObj.get("items")
                        if (arrayElement != null && arrayElement.isJsonArray) {
                            gson.fromJson(arrayElement, listType) ?: emptyList()
                        } else {
                            emptyList()
                        }
                    } else {
                        emptyList()
                    }
                } catch (e: Exception) {
                    emptyList()
                }
                NetworkResult.Success(data)
            } else {
                NetworkResult.Error(response.code, "خطا در دریافت ساختار درختی دانشنامه (${response.code})")
            }
        } catch (e: Exception) {
            NetworkResult.Error(-1, e.localizedMessage ?: "خطای ارتباط با سرور")
        }
    }

    override suspend fun getWikiArticle(id: String): NetworkResult<WikiArticle> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("${OmniHttpClient.WORK_BASE}/wiki/pages/$id")
                .get()
                .build()
            val response = client.newCall(request).execute()
            val json = response.body?.string().orEmpty()

            if (response.isSuccessful) {
                val article: WikiArticle = try {
                    val trimmed = json.trim()
                    if (trimmed.startsWith("{")) {
                        val rootObj = JsonParser.parseString(trimmed).asJsonObject
                        val articleObj = when {
                            rootObj.has("article") && rootObj.get("article").isJsonObject -> rootObj.getAsJsonObject("article")
                            rootObj.has("data") && rootObj.get("data").isJsonObject -> rootObj.getAsJsonObject("data")
                            rootObj.has("page") && rootObj.get("page").isJsonObject -> rootObj.getAsJsonObject("page")
                            else -> rootObj
                        }
                        gson.fromJson(articleObj, WikiArticle::class.java) ?: WikiArticle(id = id)
                    } else {
                        WikiArticle(id = id)
                    }
                } catch (e: Exception) {
                    WikiArticle(id = id)
                }
                NetworkResult.Success(article)
            } else {
                NetworkResult.Error(response.code, "خطا در دریافت سند دانشنامه (${response.code})")
            }
        } catch (e: Exception) {
            NetworkResult.Error(-1, e.localizedMessage ?: "خطای ارتباط با سرور")
        }
    }
}
