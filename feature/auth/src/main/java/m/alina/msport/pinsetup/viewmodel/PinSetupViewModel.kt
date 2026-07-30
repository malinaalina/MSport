package m.alina.msport.pinsetup.viewmodel

import m.alina.msport.common.AuthError
import m.alina.msport.domain.model.AuthResult
import m.alina.msport.domain.usecase.auth.SetPinUseCase
import m.alina.msport.feature.auth.R
import m.alina.msport.mvi.BaseViewModel
import m.alina.msport.pinsetup.PIN_LENGTH
import m.alina.msport.pinsetup.PinSetupAction
import m.alina.msport.pinsetup.PinSetupEffect
import m.alina.msport.pinsetup.PinSetupState
import m.alina.msport.repository.SessionManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class PinSetupViewModel @AssistedInject constructor(
    private val setPin: SetPinUseCase,
    private val sessionManager: SessionManager,
    @Assisted isReentry: Boolean,
) : BaseViewModel<PinSetupState, PinSetupAction, PinSetupEffect>(PinSetupState(isReentry = isReentry)) {

    @AssistedFactory
    interface Factory {
        fun create(isReentry: Boolean): PinSetupViewModel
    }

    override suspend fun handleAction(action: PinSetupAction) {
        when (action) {
            is PinSetupAction.PinChanged -> onPinChanged(action.pin)
            PinSetupAction.Submit -> submit()
            PinSetupAction.DismissError -> setState { copy(error = null) }
        }
    }

    private suspend fun onPinChanged(pin: String) {
        if (pin.length <= PIN_LENGTH && pin.all(Char::isDigit)) {
            setState { copy(pin = pin) }
            if (pin.length == PIN_LENGTH) {
                submit()
            }
        }
    }

    private suspend fun submit() {
        if (currentState.pin.length != PIN_LENGTH) return
        setState { copy(isLoading = true) }
        try {
            when (val result = setPin(currentState.pin)) {
                is AuthResult.Authenticated -> {
                    sessionManager.saveAuthToken(result.accessToken, result.refreshToken)
                    setState { copy(isLoading = false) }
                    sendEffect { PinSetupEffect.Authorized(result.accessToken) }
                }
                else -> setState { copy(isLoading = false, error = AuthError(R.string.error_pin)) }
            }
        } catch (e: Exception) {
            setState { copy(isLoading = false, error = AuthError(message = e.message)) }
        }
    }
}
