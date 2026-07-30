package m.alina.msport.repository

import kotlinx.coroutines.delay
import m.alina.msport.domain.model.AuthResult
import m.alina.msport.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockAuthRepository @Inject constructor() : AuthRepository {

    override suspend fun sendSms(phone: String): AuthResult {
        delay(1000)
        return AuthResult.CodeSent(
            sessionId = "mock-session-id-${System.currentTimeMillis()}",
            retryDelaySeconds = 60,
        )
    }

    override suspend fun verifyCode(sessionId: String, code: String): AuthResult {
        delay(1000)
        return AuthResult.PinRequired(
            interimToken = "mock-interim-token",
            isNewUser = true,
        )
    }

    override suspend fun resendSms(sessionId: String): AuthResult {
        delay(1000)
        return AuthResult.CodeSent(
            sessionId = "mock-new-session-id",
            retryDelaySeconds = 120,
        )
    }

    override suspend fun setPin(pin: String): AuthResult {
        delay(1000)
        return AuthResult.Authenticated(
            accessToken = "mock-access-token",
            refreshToken = "mock-refresh-token",
            expiresIn = 3600,
        )
    }
}
