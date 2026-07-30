package m.alina.msport.domain.repository

import m.alina.msport.domain.model.AuthResult

interface AuthRepository {
    suspend fun sendSms(phone: String): AuthResult
    suspend fun verifyCode(sessionId: String, code: String): AuthResult
    suspend fun resendSms(sessionId: String): AuthResult
    suspend fun setPin(pin: String): AuthResult
}
