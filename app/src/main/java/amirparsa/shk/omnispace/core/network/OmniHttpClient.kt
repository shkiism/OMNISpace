package amirparsa.shk.omnispace.core.network

import android.content.Context
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

object OmniHttpClient {

    const val BASE_URL = "https://omnispace.ir"
    const val WORK_BASE = "$BASE_URL/api/v1"
    const val PRIVATE_BASE = "$BASE_URL/private/v1"

    private val cookieStore = ConcurrentHashMap<String, MutableList<Cookie>>()

    private val cookieJar = object : CookieJar {
        override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
            val host = url.host
            val current = cookieStore.getOrPut(host) { mutableListOf() }
            synchronized(current) {
                for (cookie in cookies) {
                    current.removeAll { it.name == cookie.name }
                    current.add(cookie)
                }
            }
        }

        override fun loadForRequest(url: HttpUrl): List<Cookie> {
            val host = url.host
            return cookieStore[host]?.toList() ?: emptyList()
        }
    }

    fun createClient(context: Context): OkHttpClient {
        val sessionManager = SessionManager.getInstance(context)

        val authInterceptor = Interceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
                .header("User-Agent", "OmniSpace-Android/1.0 (Linux; Android)")
                .header("Accept", "application/json")

            sessionManager.csrfToken?.let { token ->
                if (token.isNotBlank()) {
                    requestBuilder.header("x-csrf-token", token)
                }
            }

            chain.proceed(requestBuilder.build())
        }

        return OkHttpClient.Builder()
            .cookieJar(cookieJar)
            .addInterceptor(authInterceptor)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .followRedirects(true)
            .followSslRedirects(true)
            .build()
    }
}

