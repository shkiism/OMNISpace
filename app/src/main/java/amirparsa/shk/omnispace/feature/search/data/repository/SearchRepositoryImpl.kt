package amirparsa.shk.omnispace.feature.search.data.repository

import android.content.Context
import amirparsa.shk.omnispace.core.network.OmniHttpClient
import amirparsa.shk.omnispace.core.utils.NetworkResult
import amirparsa.shk.omnispace.feature.search.data.remote.dto.SemanticSearchResultDto
import amirparsa.shk.omnispace.feature.search.domain.repository.SearchRepository
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Request
import java.net.URLEncoder
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SearchRepository {

    private val client = OmniHttpClient.getClient(context)
    private val gson = Gson()

    override suspend fun searchSemantic(query: String, limit: Int): NetworkResult<List<SemanticSearchResultDto>> =
        withContext(Dispatchers.IO) {
            try {
                val encodedQuery = URLEncoder.encode(query.trim(), "UTF-8")
                val request = Request.Builder()
                    .url("${OmniHttpClient.WORK_BASE}/search?q=$encodedQuery&limit=$limit")
                    .get()
                    .build()
                val response = client.newCall(request).execute()
                val json = response.body?.string().orEmpty()

                if (response.isSuccessful) {
                    val listType = object : TypeToken<List<SemanticSearchResultDto>>() {}.type
                    val data: List<SemanticSearchResultDto> = try {
                        val trimmed = json.trim()
                        if (trimmed.startsWith("[")) {
                            gson.fromJson(trimmed, listType) ?: emptyList()
                        } else if (trimmed.startsWith("{")) {
                            val rootObj = JsonParser.parseString(trimmed).asJsonObject
                            val arrayElement = rootObj.get("results")
                                ?: rootObj.get("data")
                                ?: rootObj.get("items")
                                ?: rootObj.get("hits")
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
                    NetworkResult.Error(response.code, "خطا در جستجوی سازمانی (${response.code})")
                }
            } catch (e: Exception) {
                NetworkResult.Error(-1, e.localizedMessage ?: "خطای ارتباط با سرور جستجو")
            }
        }
}
