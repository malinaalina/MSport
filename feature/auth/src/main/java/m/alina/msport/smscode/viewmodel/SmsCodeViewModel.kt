package m.alina.msport.smscode.viewmodel

import m.alina.msport.common.AuthError
import m.alina.msport.domain.model.AuthResult
import m.alina.msport.domain.usecase.auth.ResendSmsUseCase
import m.alina.msport.domain.usecase.auth.VerifyCodeUseCase
import m.alina.msport.feature.auth.R
import m.alina.msport.mvi.BaseViewModel
import m.alina.msport.smscode.SMS_CODE_LENGTH
import m.alina.msport.smscode.SmsCodeAction
import m.alina.msport.smscode.SmsCodeEffect
import m.alina.msport.smscode.SmsCodeState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class SmsCodeViewModel @AssistedInject constructor(
    private val verifyCode: VerifyCodeUseCase,
    private val resendSms: ResendSmsUseCase,
    @Assisted("phone") phone: String,
    @Assisted("sessionId") initialSessionId: String,
    @Assisted initialRetryDelay: Int,
) : BaseViewModel<SmsCodeState, SmsCodeAction, SmsCodeEffect>(
    SmsCodeState(phone = phone, retryDelaySeconds = initialRetryDelay),
) {

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("phone") phone: String,
            @Assisted("sessionId") initialSessionId: String,
            initialRetryDelay: Int,
        ): SmsCodeViewModel
    }

    private var sessionId: String = initialSessionId

    override suspend fun handleAction(action: SmsCodeAction) {
        when (action) {
            is SmsCodeAction.CodeChanged -> onCodeChanged(action.code)
            SmsCodeAction.VerifyCode -> verifyCode()
            SmsCodeAction.ResendSms -> resendSms()
            SmsCodeAction.DismissError -> setState { copy(error = null) }
        }
    }

    private fun onCodeChanged(code: String) {
        if (code.length <= SMS_CODE_LENGTH && code.all(Char::isDigit)) {
            setState { copy(code = code) }
        }
    }

    private suspend fun verifyCode() {
        setState { copy(isLoading = true) }
        try {
            when (val result = verifyCode(sessionId, currentState.code)) {
                is AuthResult.PinRequired -> {
                    setState { copy(isLoading = false) }
                    sendEffect { SmsCodeEffect.NavigateToPinSetup(result.interimToken, result.isNewUser) }
                }
                else -> setState { copy(isLoading = false, error = AuthError(R.string.error_invalid_code)) }
            }
        } catch (e: Exception) {
            setState { copy(isLoading = false, error = AuthError(message = e.message)) }
        }
    }

    private suspend fun resendSms() {
        setState { copy(code = "") }
        try {
            val result = resendSms(sessionId)
            if (result is AuthResult.CodeSent) {
                sessionId = result.sessionId
                setState { copy(retryDelaySeconds = result.retryDelaySeconds) }
            }
        } catch (e: Exception) {
            // Handle silent error
        }
    }
}
