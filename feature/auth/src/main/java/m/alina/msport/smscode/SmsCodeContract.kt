package m.alina.msport.smscode

import m.alina.msport.common.AuthError
import m.alina.msport.mvi.UiAction
import m.alina.msport.mvi.UiEffect
import m.alina.msport.mvi.UiState

const val SMS_CODE_LENGTH = 4

data class SmsCodeState(
    val phone: String = "",
    val code: String = "",
    val retryDelaySeconds: Int = 0,
    val isLoading: Boolean = false,
    val error: AuthError? = null,
) : UiState

sealed interface SmsCodeAction : UiAction {
    data class CodeChanged(val code: String) : SmsCodeAction
    object VerifyCode : SmsCodeAction
    object ResendSms : SmsCodeAction
    object DismissError : SmsCodeAction
}

sealed interface SmsCodeEffect : UiEffect {
    data class NavigateToPinSetup(val interimToken: String, val isNewUser: Boolean) : SmsCodeEffect
}
