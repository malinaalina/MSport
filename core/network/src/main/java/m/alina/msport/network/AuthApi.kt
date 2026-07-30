package m.alina.msport.network

import m.alina.msport.model.AuthResponseDto
import m.alina.msport.model.RefreshTokenRequest
import m.alina.msport.model.ResendSmsRequest
import m.alina.msport.model.SendSmsRequest
import m.alina.msport.model.SetPinRequest
import m.alina.msport.model.VerifyCodeRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("api/v1/auth/send-sms")
    suspend fun sendSms(@Body request: SendSmsRequest): AuthResponseDto

    @POST("api/v1/auth/verify-code")
    suspend fun verifyCode(@Body request: VerifyCodeRequest): AuthResponseDto

    @POST("api/v1/auth/resend-sms")
    suspend fun resendSms(@Body request: ResendSmsRequest): AuthResponseDto

    @POST("api/v1/auth/set-pin")
    suspend fun setPin(@Body request: SetPinRequest): AuthResponseDto

    @POST("api/v1/auth/refresh-token")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): AuthResponseDto
}
