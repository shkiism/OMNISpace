package amirparsa.shk.omnispace.core.network

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    var userId: String?
        get() = prefs.getString(KEY_USER_ID, null)
        set(value) = prefs.edit().putString(KEY_USER_ID, value).apply()

    var orgId: String?
        get() = prefs.getString(KEY_ORG_ID, null)
        set(value) = prefs.edit().putString(KEY_ORG_ID, value).apply()

    var csrfToken: String?
        get() = prefs.getString(KEY_CSRF_TOKEN, null)
        set(value) = prefs.edit().putString(KEY_CSRF_TOKEN, value).apply()

    var email: String?
        get() = prefs.getString(KEY_EMAIL, null)
        set(value) = prefs.edit().putString(KEY_EMAIL, value).apply()

    var displayName: String?
        get() = prefs.getString(KEY_DISPLAY_NAME, null)
        set(value) = prefs.edit().putString(KEY_DISPLAY_NAME, value).apply()

    var isAuthenticated: Boolean
        get() = prefs.getBoolean(KEY_IS_AUTH, false)
        set(value) = prefs.edit().putBoolean(KEY_IS_AUTH, value).apply()

    fun updateSession(
        userId: String?,
        orgId: String?,
        csrfToken: String?,
        isAuthenticated: Boolean
    ) {
        prefs.edit()
            .putString(KEY_USER_ID, userId)
            .putString(KEY_ORG_ID, orgId)
            .putString(KEY_CSRF_TOKEN, csrfToken)
            .putBoolean(KEY_IS_AUTH, isAuthenticated)
            .apply()
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val PREF_NAME = "omnispace_session"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_ORG_ID = "org_id"
        private const val KEY_CSRF_TOKEN = "csrf_token"
        private const val KEY_EMAIL = "email"
        private const val KEY_DISPLAY_NAME = "display_name"
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

