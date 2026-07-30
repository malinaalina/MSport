package m.alina.msport.domain.model

sealed interface AuthResult {
    data class CodeSent(val sessionId: String, val retryDelaySeconds: Int) : AuthResult
    data class PinRequired(val interimToken: String, val isNewUser: Boolean) : AuthResult
    data class Authenticated(
        val accessToken: String,
        val refreshToken: String?,
        val expiresIn: Int?,
    ) : AuthResult
}
