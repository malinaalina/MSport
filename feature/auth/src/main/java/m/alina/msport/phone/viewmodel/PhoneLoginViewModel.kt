package m.alina.msport.phone.viewmodel

import m.alina.msport.common.AuthError
import m.alina.msport.domain.model.AuthResult
import m.alina.msport.domain.usecase.auth.SendSmsUseCase
import m.alina.msport.feature.auth.R
import m.alina.msport.mvi.BaseViewModel
import m.alina.msport.phone.PhoneLoginAction
import m.alina.msport.phone.PhoneLoginEffect
import m.alina.msport.phone.PhoneLoginState
import m.alina.msport.util.PHONE_MAX_DIGITS
import javax.inject.Inject

private fun rawDigitsToE164(digits: String): String = "+7" + digits.filter { it.isDigit() }

class PhoneLoginViewModel @Inject constructor(
    private val sendSms: SendSmsUseCase,
) : BaseViewModel<PhoneLoginState, PhoneLoginAction, PhoneLoginEffect>(PhoneLoginState()) {

    override suspend fun handleAction(action: PhoneLoginAction) {
        when (action) {
            is PhoneLoginAction.PhoneDigitsChanged -> onPhoneDigitsChanged(action.digits)
            PhoneLoginAction.SendSms -> sendSms()
            PhoneLoginAction.DismissError -> setState { copy(error = null) }
        }
    }

    private fun onPhoneDigitsChanged(digits: String) {
        setState { copy(phoneDigits = digits.filter { it.isDigit() }.take(PHONE_MAX_DIGITS)) }
    }

    private suspend fun sendSms() {
        setState { copy(isLoading = true) }
        try {
            val phone = rawDigitsToE164(currentState.phoneDigits)
            when (val result = sendSms(phone)) {
                is AuthResult.CodeSent -> {
                    setState { copy(isLoading = false) }
                    sendEffect { PhoneLoginEffect.NavigateToSmsCode(result.sessionId, result.retryDelaySeconds, phone) }
                }
                else -> setState { copy(isLoading = false, error = AuthError(R.string.error_sms_send)) }
            }
        } catch (e: Exception) {
            setState { copy(isLoading = false, error = AuthError(message = e.message)) }
        }
    }
}
