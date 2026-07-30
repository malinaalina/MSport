package m.alina.msport.repository

import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("msport_prefs", Context.MODE_PRIVATE)
    private val mainHandler = Handler(Looper.getMainLooper())
    private val sessionExpiredListeners = mutableListOf<() -> Unit>()

    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_IS_AUTHORIZED = "is_authorized"
    }

    fun saveAuthToken(accessToken: String, refreshToken: String? = null) {
        prefs.edit().apply {
            putString(KEY_ACCESS_TOKEN, accessToken)
            if (refreshToken != null) putString(KEY_REFRESH_TOKEN, refreshToken)
            putBoolean(KEY_IS_AUTHORIZED, true)
        }.apply()
    }

    fun getAuthToken(): String? {
        return prefs.getString(KEY_ACCESS_TOKEN, null)
    }

    fun getRefreshToken(): String? {
        return prefs.getString(KEY_REFRESH_TOKEN, null)
    }

    fun isAuthorized(): Boolean {
        return prefs.getBoolean(KEY_IS_AUTHORIZED, false)
    }

    fun logout() {
        prefs.edit().clear().apply()
    }

    fun forceLogout() {
        logout()
        mainHandler.post {
            synchronized(sessionExpiredListeners) { sessionExpiredListeners.toList() }.forEach { it() }
        }
    }

    fun addSessionExpiredListener(listener: () -> Unit) {
        synchronized(sessionExpiredListeners) { sessionExpiredListeners += listener }
    }

    fun removeSessionExpiredListener(listener: () -> Unit) {
        synchronized(sessionExpiredListeners) { sessionExpiredListeners -= listener }
    }
}
