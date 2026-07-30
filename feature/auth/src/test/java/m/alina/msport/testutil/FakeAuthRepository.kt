package m.alina.msport.testutil

import m.alina.msport.domain.model.AuthResult
import m.alina.msport.domain.repository.AuthRepository

class FakeAuthRepository : AuthRepository {

    var sendSmsResult: AuthResult = AuthResult.CodeSent(sessionId = "session-1", retryDelaySeconds = 60)
    var verifyCodeResult: AuthResult = AuthResult.PinRequired(interimToken = "interim-token", isNewUser = false)
    var resendSmsResult: AuthResult = AuthResult.CodeSent(sessionId = "session-2", retryDelaySeconds = 60)
    var setPinResult: AuthResult =
        AuthResult.Authenticated(accessToken = "access-token", refreshToken = "refresh-token", expiresIn = 3600)

    var sendSmsError: Throwable? = null
    var verifyCodeError: Throwable? = null
    var resendSmsError: Throwable? = null
    var setPinError: Throwable? = null

    var lastSentPhone: String? = null
    var lastVerifiedSessionId: String? = null
    var lastVerifiedCode: String? = null
    var lastResendSessionId: String? = null
    var lastSetPin: String? = null

    override suspend fun sendSms(phone: String): AuthResult {
        lastSentPhone = phone
        sendSmsError?.let { throw it }
        return sendSmsResult
    }

    override suspend fun verifyCode(sessionId: String, code: String): AuthResult {
        lastVerifiedSessionId = sessionId
        lastVerifiedCode = code
        verifyCodeError?.let { throw it }
        return verifyCodeResult
    }

    override suspend fun resendSms(sessionId: String): AuthResult {
        lastResendSessionId = sessionId
        resendSmsError?.let { throw it }
        return resendSmsResult
    }

    override suspend fun setPin(pin: String): AuthResult {
        lastSetPin = pin
        setPinError?.let { throw it }
        return setPinResult
    }
}
