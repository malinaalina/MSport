package m.alina.msport.pinsetup

import m.alina.msport.common.AuthError
import m.alina.msport.mvi.UiAction
import m.alina.msport.mvi.UiEffect
import m.alina.msport.mvi.UiState

const val PIN_LENGTH = 4

data class PinSetupState(
    val isReentry: Boolean = false,
    val pin: String = "",
    val isLoading: Boolean = false,
    val error: AuthError? = null,
) : UiState

sealed interface PinSetupAction : UiAction {
    data class PinChanged(val pin: String) : PinSetupAction
    object Submit : PinSetupAction
    object DismissError : PinSetupAction
}

sealed interface PinSetupEffect : UiEffect {
    data class Authorized(val accessToken: String) : PinSetupEffect
}
