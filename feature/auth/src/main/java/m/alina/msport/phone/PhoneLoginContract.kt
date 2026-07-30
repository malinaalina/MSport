package m.alina.msport.phone

import m.alina.msport.common.AuthError
import m.alina.msport.mvi.UiAction
import m.alina.msport.mvi.UiEffect
import m.alina.msport.mvi.UiState

data class PhoneLoginState(
    val phoneDigits: String = "",
    val isLoading: Boolean = false,
    val error: AuthError? = null,
) : UiState

sealed interface PhoneLoginAction : UiAction {
    data class PhoneDigitsChanged(val digits: String) : PhoneLoginAction
    object SendSms : PhoneLoginAction
    object DismissError : PhoneLoginAction
}

sealed interface PhoneLoginEffect : UiEffect {
    data class NavigateToSmsCode(val sessionId: String, val retryDelaySeconds: Int, val phone: String) : PhoneLoginEffect
}
