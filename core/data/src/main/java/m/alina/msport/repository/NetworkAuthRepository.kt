package m.alina.msport.repository

import m.alina.msport.domain.model.AuthResult
import m.alina.msport.domain.repository.AuthRepository
import m.alina.msport.mapper.toDomain
import m.alina.msport.model.ResendSmsRequest
import m.alina.msport.model.SendSmsRequest
import m.alina.msport.model.SetPinRequest
import m.alina.msport.model.VerifyCodeRequest
import m.alina.msport.network.AuthApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkAuthRepository @Inject constructor(
    private val authApi: AuthApi,
) : AuthRepository {

    override suspend fun sendSms(phone: String): AuthResult =
        authApi.sendSms(SendSmsRequest(phone)).toDomain()

    override suspend fun verifyCode(sessionId: String, code: String): AuthResult =
        authApi.verifyCode(VerifyCodeRequest(sessionId = sessionId, code = code)).toDomain()

    override suspend fun resendSms(sessionId: String): AuthResult =
        authApi.resendSms(ResendSmsRequest(sessionId = sessionId)).toDomain()

    override suspend fun setPin(pin: String): AuthResult =
        authApi.setPin(SetPinRequest(pinCode = pin)).toDomain()
}
