package m.alina.msport.di

import dagger.Lazy
import kotlinx.coroutines.runBlocking
import m.alina.msport.model.RefreshTokenRequest
import m.alina.msport.network.AuthApi
import m.alina.msport.repository.SessionManager
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

private const val MAX_RETRIES = 2

class TokenAuthenticator @Inject constructor(
    private val authApi: Lazy<AuthApi>,
    private val sessionManager: SessionManager,
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= MAX_RETRIES) return null
        synchronized(this) {
            val failedToken = response.request.header("Authorization")?.removePrefix("Bearer ")
            val currentToken = sessionManager.getAuthToken()
            if (currentToken != null && failedToken != currentToken) {
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $currentToken")
                    .build()
            }
            val refreshToken = sessionManager.getRefreshToken() ?: run {
                sessionManager.forceLogout()
                return null
            }
            val refreshed = runCatching {
                runBlocking { authApi.get().refreshToken(RefreshTokenRequest(refreshToken)) }
            }.getOrNull()
            val newAccessToken = refreshed?.accessToken
            if (newAccessToken == null) {
                sessionManager.forceLogout()
                return null
            }
            sessionManager.saveAuthToken(newAccessToken, refreshed.refreshToken ?: refreshToken)
            return response.request.newBuilder()
                .header("Authorization", "Bearer $newAccessToken")
                .build()
        }
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }
}
