package amirparsa.shk.omnispace.core.network

import android.content.Context
import amirparsa.shk.omnispace.core.database.dao.UserProfileDao
import amirparsa.shk.omnispace.core.database.entity.toEntity
import amirparsa.shk.omnispace.core.database.entity.toResponse
import amirparsa.shk.omnispace.core.network.model.AuthSessionResponse
import amirparsa.shk.omnispace.core.network.model.UserProfileResponse
import amirparsa.shk.omnispace.core.utils.NetworkResult
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.Request
import java.net.URI
import java.util.regex.Pattern
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userProfileDao: UserProfileDao
) : AuthRepository {

    private val client = OmniHttpClient.getClient(context)
    private val sessionManager = SessionManager.getInstance(context)
    private val gson = Gson()

    override suspend fun login(username: String, password: String): NetworkResult<UserProfileResponse> =
        withContext(Dispatchers.IO) {
            try {
                // 1. Initiate auth flow and fetch Keycloak login page
                val startRequest = Request.Builder()
                    .url("${OmniHttpClient.BASE_URL}/api/v1/auth/start")
                    .build()

                val startResponse = client.newCall(startRequest).execute()
                val html = startResponse.body?.string().orEmpty()
                val currentUrl = startResponse.request.url.toString()

                if (!startResponse.isSuccessful) {
                    return@withContext NetworkResult.Error(
                        code = startResponse.code,
                        message = "خطا در اتصال به سرور احراز هویت (${startResponse.code})"
                    )
                }

                // 2. Extract form action URL from Keycloak HTML
                val actionMatcher = Pattern.compile("<form[^>]*action=[\"']([^\"']+)[\"']", Pattern.CASE_INSENSITIVE)
                    .matcher(html)

                if (!actionMatcher.find()) {
                    return@withContext NetworkResult.Error(
                        code = -1,
                        message = "فرم ورود در صفحه یافت نشد."
                    )
                }

                var actionUrl = actionMatcher.group(1).orEmpty()
                if (actionUrl.startsWith("/")) {
                    val uri = URI(currentUrl)
                    actionUrl = "${uri.scheme}://${uri.host}:${uri.port.takeIf { it != -1 } ?: 443}$actionUrl"
                }

                // 3. Submit credentials to Keycloak
                val formBody = FormBody.Builder()
                    .add("username", username)
                    .add("password", password)
                    .add("credentialId", "")
                    .build()

                val loginRequest = Request.Builder()
                    .url(actionUrl)
                    .post(formBody)
                    .build()

                val loginResponse = client.newCall(loginRequest).execute()

                if (!loginResponse.isSuccessful) {
                    return@withContext NetworkResult.Error(
                        code = loginResponse.code,
                        message = "اطلاعات ورود نادرست است."
                    )
                }

                // 4. Verify session with BFF
                val sessionRequest = Request.Builder()
                    .url("${OmniHttpClient.BASE_URL}/api/v1/auth/session")
                    .build()

                val sessionResponse = client.newCall(sessionRequest).execute()
                val sessionJson = sessionResponse.body?.string().orEmpty()
                val sessionData = gson.fromJson(sessionJson, AuthSessionResponse::class.java)

                if (!sessionData.authenticated || sessionData.csrfToken.isNullOrBlank()) {
                    return@withContext NetworkResult.Error(
                        code = 401,
                        message = "احراز هویت موفقیت‌آمیز نبود. لطفاً نام کاربری و رمز عبور را بررسی کنید."
                    )
                }

                // Save session tokens
                sessionManager.updateSession(
                    userId = sessionData.userId,
                    orgId = sessionData.orgId,
                    csrfToken = sessionData.csrfToken,
                    isAuthenticated = true,
                    authToken = null
                )

                // 5. Fetch user profile
                val meRequest = Request.Builder()
                    .url("${OmniHttpClient.BASE_URL}/api/v1/me")
                    .build()

                val meResponse = client.newCall(meRequest).execute()
                val meJson = meResponse.body?.string().orEmpty()

                if (!meResponse.isSuccessful) {
                    return@withContext NetworkResult.Error(
                        code = meResponse.code,
                        message = "خطا در دریافت پروفایل کاربری."
                    )
                }

                val userProfile = gson.fromJson(meJson, UserProfileResponse::class.java)
                sessionManager.email = userProfile.email
                sessionManager.displayName = userProfile.displayName
                sessionManager.organizationName = userProfile.org?.name
                sessionManager.orgRole = userProfile.orgRole

                // Cache in Room Database
                userProfileDao.insertOrUpdate(userProfile.toEntity())

                NetworkResult.Success(userProfile)
            } catch (e: Exception) {
                NetworkResult.Error(
                    code = -1,
                    message = e.localizedMessage ?: "خطای ناشناخته در برقراری ارتباط با سرور"
                )
            }
        }

    override suspend fun checkSession(): NetworkResult<AuthSessionResponse> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("${OmniHttpClient.BASE_URL}/api/v1/auth/session")
                .build()

            val response = client.newCall(request).execute()
            val json = response.body?.string().orEmpty()
            val session = gson.fromJson(json, AuthSessionResponse::class.java)

            if (session.authenticated) {
                sessionManager.updateSession(
                    userId = session.userId,
                    orgId = session.orgId,
                    csrfToken = session.csrfToken,
                    isAuthenticated = true
                )
                NetworkResult.Success(session)
            } else {
                sessionManager.isAuthenticated = false
                NetworkResult.Error(401, "نشست کاربر منقضی شده است.")
            }
        } catch (e: Exception) {
            NetworkResult.Error(-1, e.localizedMessage ?: "خطای بررسی نشست")
        }
    }

    override suspend fun getProfile(): NetworkResult<UserProfileResponse> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("${OmniHttpClient.BASE_URL}/api/v1/me")
                .build()

            val response = client.newCall(request).execute()
            val json = response.body?.string().orEmpty()

            if (response.isSuccessful && json.isNotBlank()) {
                val profile = gson.fromJson(json, UserProfileResponse::class.java)
                sessionManager.displayName = profile.displayName
                sessionManager.email = profile.email
                // Cache to Room Database
                userProfileDao.insertOrUpdate(profile.toEntity())
                NetworkResult.Success(profile)
            } else {
                // Room cache fallback
                val cached = userProfileDao.getCurrentUserProfile()?.toResponse()
                if (cached != null) {
                    NetworkResult.Success(cached)
                } else {
                    NetworkResult.Error(response.code, "خطا در دریافت مشخصات کاربر")
                }
            }
        } catch (e: Exception) {
            // Room cache fallback
            val cached = userProfileDao.getCurrentUserProfile()?.toResponse()
            if (cached != null) {
                NetworkResult.Success(cached)
            } else {
                NetworkResult.Error(-1, e.localizedMessage ?: "خطای ارتباط با سرور")
            }
        }
    }

    override suspend fun logout(): NetworkResult<Boolean> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("${OmniHttpClient.BASE_URL}/api/v1/auth/logout")
                .post(FormBody.Builder().build())
                .build()

            client.newCall(request).execute()
        } catch (_: Exception) {
        } finally {
            sessionManager.clearSession()
            OmniHttpClient.clearCookies()
            userProfileDao.clearAll()
        }
        NetworkResult.Success(true)
    }
}
