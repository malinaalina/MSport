package m.alina.msport.model

import com.google.gson.annotations.SerializedName

data class SendSmsRequest(val phone: String)
data class VerifyCodeRequest(@SerializedName("session_id") val sessionId: String, val code: String)
data class ResendSmsRequest(@SerializedName("session_id") val sessionId: String)
data class SetPinRequest(@SerializedName("pin_code") val pinCode: String)
data class RefreshTokenRequest(@SerializedName("refresh_token") val refreshToken: String)

data class AuthResponseDto(
    val status: String,
    @SerializedName("session_id") val sessionId: String? = null,
    @SerializedName("retry_delay_seconds") val retryDelaySeconds: Int? = null,
    @SerializedName("interim_token") val interimToken: String? = null,
    @SerializedName("is_new_user") val isNewUser: Boolean? = null,
    @SerializedName("access_token") val accessToken: String? = null,
    @SerializedName("refresh_token") val refreshToken: String? = null,
    @SerializedName("expires_in") val expiresIn: Int? = null,
)
