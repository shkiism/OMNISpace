package amirparsa.shk.omnispace.core.network

import android.content.Context
import android.util.Log
import okhttp3.Authenticator
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

object OmniHttpClient {

    private const val TAG = "OmniHttpClient"

    const val BASE_URL = "https://omnispace.ir"
    const val WORK_BASE = "$BASE_URL/api/v1"
    const val PRIVATE_BASE = "$BASE_URL/private/v1"
    const val REWARDS_BASE = "$BASE_URL/rewards/v1"
    const val ASSISTANT_BASE = "$BASE_URL/assistant/v1"

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

    fun clearCookies() {
        cookieStore.clear()
    }

    private val renewalClient by lazy {
        OkHttpClient.Builder()
            .cookieJar(cookieJar)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()
    }

    @Volatile
    private var clientInstance: OkHttpClient? = null

    fun getClient(context: Context = amirparsa.shk.omnispace.OmniApp.instance): OkHttpClient {
        return clientInstance ?: synchronized(this) {
            clientInstance ?: buildClient(context.applicationContext).also { clientInstance = it }
        }
    }

    // For backwards compatibility
    fun createClient(context: Context = amirparsa.shk.omnispace.OmniApp.instance): OkHttpClient = getClient(context)

    private fun buildClient(context: Context): OkHttpClient {
        val sessionManager = SessionManager.getInstance(context)

        val authInterceptor = Interceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
                .header("User-Agent", "OmniSpace-Android/1.0 (Linux; Android)")
                .header("Accept", "application/json")

            // Standard Bearer token authentication (PAT / Keycloak JWT)
            sessionManager.authToken?.let { token ->
                if (token.isNotBlank()) {
                    requestBuilder.header("Authorization", "Bearer $token")
                }
            }

            // CSRF double-submit token for cookie/BFF session writes
            sessionManager.csrfToken?.let { token ->
                if (token.isNotBlank()) {
                    requestBuilder.header("x-csrf-token", token)
                }
            }

            chain.proceed(requestBuilder.build())
        }

        val tokenAuthenticator = Authenticator { _: Route?, response: Response ->
            // Prevent infinite retry loops
            if (responseCount(response) >= 2) {
                Log.w(TAG, "Request to ${response.request.url} failed with 401 after retry. Session expired.")
                sessionManager.notifySessionExpired()
                clearCookies()
                return@Authenticator null
            }

            val path = response.request.url.encodedPath
            // Do not intercept auth flows themselves
            if (path.contains("/auth/start") || path.contains("/auth/forgot") || path.contains("/auth/reset") || path.contains("/auth/logout")) {
                return@Authenticator null
            }

            // Case 1: If using Personal Access Token / Bearer token, a 401 means the token is invalid or revoked
            val bearerToken = sessionManager.authToken
            if (!bearerToken.isNullOrBlank()) {
                Log.w(TAG, "Bearer token returned 401 Unauthorized. Notifying session expiration.")
                sessionManager.notifySessionExpired()
                clearCookies()
                return@Authenticator null
            }

            // Case 2: If /auth/session itself failed with 401, session is invalid
            if (path.endsWith("/auth/session")) {
                Log.w(TAG, "Session check failed with 401. Notifying session expiration.")
                sessionManager.notifySessionExpired()
                clearCookies()
                return@Authenticator null
            }

            // Case 3: Cookie-based session. Per OmniSpace docs, requesting /api/v1/auth/session renews the cookie
            synchronized(this) {
                if (!sessionManager.isAuthenticated) {
                    return@Authenticator null
                }

                try {
                    val sessionRequest = Request.Builder()
                        .url("$WORK_BASE/auth/session")
                        .header("User-Agent", "OmniSpace-Android/1.0 (Linux; Android)")
                        .header("Accept", "application/json")
                        .build()

                    val sessionResponse = renewalClient.newCall(sessionRequest).execute()
                    val bodyString = sessionResponse.body?.string().orEmpty()

                    if (sessionResponse.isSuccessful && bodyString.contains("\"authenticated\":true")) {
                        try {
                            val jsonObject = com.google.gson.JsonParser.parseString(bodyString).asJsonObject
                            if (jsonObject.has("csrfToken")) {
                                sessionManager.csrfToken = jsonObject.get("csrfToken").asString
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Error parsing renewed csrfToken", e)
                        }

                        // Retry original request with renewed headers
                        val newRequestBuilder = response.request.newBuilder()
                        sessionManager.csrfToken?.let { token ->
                            if (token.isNotBlank()) {
                                newRequestBuilder.header("x-csrf-token", token)
                            }
                        }
                        return@Authenticator newRequestBuilder.build()
                    } else {
                        Log.w(TAG, "Session renewal failed. Notifying session expiration.")
                        sessionManager.notifySessionExpired()
                        clearCookies()
                        return@Authenticator null
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Exception during session check: ${e.message}")
                    sessionManager.notifySessionExpired()
                    clearCookies()
                    return@Authenticator null
                }
            }
        }

        val loggingInterceptor = HttpLoggingInterceptor { message ->
            // Android Logcat truncates messages exceeding ~4000 characters.
            val maxLogSize = 3500
            if (message.length > maxLogSize) {
                var start = 0
                while (start < message.length) {
                    val end = (start + maxLogSize).coerceAtMost(message.length)
                    Log.d(TAG, message.substring(start, end))
                    start = end
                }
            } else {
                Log.d(TAG, message)
            }
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .cookieJar(cookieJar)
            .authenticator(tokenAuthenticator)
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(25, TimeUnit.SECONDS)
            .writeTimeout(25, TimeUnit.SECONDS)
            .followRedirects(true)
            .followSslRedirects(true)
            .build()
    }

    private fun responseCount(response: Response): Int {
        var result = 1
        var prior = response.priorResponse
        while (prior != null) {
            result++
            prior = prior.priorResponse
        }
        return result
    }
}
