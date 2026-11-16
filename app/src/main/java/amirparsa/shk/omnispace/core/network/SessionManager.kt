package amirparsa.shk.omnispace.core.network

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class SessionManager(context: Context) {

    private val prefs: SharedPreferences = createEncryptedPreferences(context)

    private fun createEncryptedPreferences(context: Context): SharedPreferences {
        return try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            EncryptedSharedPreferences.create(
                context,
                PREF_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize EncryptedSharedPreferences, falling back to standard prefs", e)
            context.getSharedPreferences(FALLBACK_PREF_NAME, Context.MODE_PRIVATE)
        }
    }

    private val _sessionExpiredEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val sessionExpiredEvent: SharedFlow<Unit> = _sessionExpiredEvent.asSharedFlow()

    var userId: String?
        get() = prefs.getString(KEY_USER_ID, null)
        set(value) = prefs.edit { putString(KEY_USER_ID, value) }

    var orgId: String?
        get() = prefs.getString(KEY_ORG_ID, null)
        set(value) = prefs.edit { putString(KEY_ORG_ID, value) }

    var csrfToken: String?
        get() = prefs.getString(KEY_CSRF_TOKEN, null)
        set(value) = prefs.edit { putString(KEY_CSRF_TOKEN, value) }

    var authToken: String?
        get() = prefs.getString(KEY_AUTH_TOKEN, null)
        set(value) = prefs.edit { putString(KEY_AUTH_TOKEN, value) }

    var email: String?
        get() = prefs.getString(KEY_EMAIL, null)
        set(value) = prefs.edit { putString(KEY_EMAIL, value) }

    var displayName: String?
        get() = prefs.getString(KEY_DISPLAY_NAME, null)
        set(value) = prefs.edit { putString(KEY_DISPLAY_NAME, value) }

    var organizationName: String?
        get() = prefs.getString(KEY_ORGANIZATION_NAME, null)
        set(value) = prefs.edit { putString(KEY_ORGANIZATION_NAME, value) }

    var orgRole: String?
        get() = prefs.getString(KEY_ORG_ROLE, null)
        set(value) = prefs.edit { putString(KEY_ORG_ROLE, value) }

    var isAuthenticated: Boolean
        get() = prefs.getBoolean(KEY_IS_AUTH, false)
        set(value) = prefs.edit { putBoolean(KEY_IS_AUTH, value) }

    fun updateSession(
        userId: String?,
        orgId: String?,
        csrfToken: String?,
        isAuthenticated: Boolean,
        authToken: String? = null
    ) {
        prefs.edit {
            putString(KEY_USER_ID, userId)
                .putString(KEY_ORG_ID, orgId)
                .putString(KEY_CSRF_TOKEN, csrfToken)
                .putBoolean(KEY_IS_AUTH, isAuthenticated)
            if (authToken != null) {
                putString(KEY_AUTH_TOKEN, authToken)
            } else {
                remove(KEY_AUTH_TOKEN)
            }
        }
    }

    fun clearSession() {
        prefs.edit { clear() }
    }

    fun notifySessionExpired() {
        clearSession()
        _sessionExpiredEvent.tryEmit(Unit)
    }

    companion object {
        private const val TAG = "SessionManager"
        private const val PREF_NAME = "omnispace_secure_session"
        private const val FALLBACK_PREF_NAME = "omnispace_session"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_ORG_ID = "org_id"
        private const val KEY_CSRF_TOKEN = "csrf_token"
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_EMAIL = "email"
        private const val KEY_DISPLAY_NAME = "display_name"
        private const val KEY_ORGANIZATION_NAME = "organization_name"
        private const val KEY_ORG_ROLE = "org_role"
        private const val KEY_IS_AUTH = "is_authenticated"

        @Volatile
        private var instance: SessionManager? = null

        fun getInstance(context: Context): SessionManager {
            return instance ?: synchronized(this) {
                instance ?: SessionManager(context.applicationContext).also { instance = it }
            }
        }
    }
}
