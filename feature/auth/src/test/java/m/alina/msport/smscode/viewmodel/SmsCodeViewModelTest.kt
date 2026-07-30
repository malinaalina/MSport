package m.alina.msport.smscode.viewmodel

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import m.alina.msport.domain.model.AuthResult
import m.alina.msport.domain.usecase.auth.ResendSmsUseCase
import m.alina.msport.domain.usecase.auth.VerifyCodeUseCase
import m.alina.msport.feature.auth.R
import m.alina.msport.smscode.SMS_CODE_LENGTH
import m.alina.msport.smscode.SmsCodeAction
import m.alina.msport.smscode.SmsCodeEffect
import m.alina.msport.testutil.FakeAuthRepository
import m.alina.msport.testutil.MainDispatcherRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class SmsCodeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeAuthRepository()

    private fun viewModel(
        phone: String = "+79991234567",
        sessionId: String = "session-1",
        retryDelay: Int = 60,
    ) = SmsCodeViewModel(
        VerifyCodeUseCase(repository),
        ResendSmsUseCase(repository),
        phone,
        sessionId,
        retryDelay,
    )

    @Test
    fun `initial state carries phone and retry delay from constructor`() {
        val viewModel = viewModel(phone = "+79991234567", retryDelay = 42)
        val state = viewModel.currentState
        assertEquals("+79991234567", state.phone)
        assertEquals(42, state.retryDelaySeconds)
        assertEquals("", state.code)
    }

    @Test
    fun `CodeChanged accepts digits up to code length only`() = runTest(mainDispatcherRule.testDispatcher) {
        val viewModel = viewModel()
        val fullCode = "1".repeat(SMS_CODE_LENGTH)
        val tooLongCode = "1".repeat(SMS_CODE_LENGTH + 1)

        viewModel.onAction(SmsCodeAction.CodeChanged("12a"))
        advanceUntilIdle()
        assertEquals("", viewModel.currentState.code)

        viewModel.onAction(SmsCodeAction.CodeChanged(tooLongCode))
        advanceUntilIdle()
        assertEquals("", viewModel.currentState.code)

        viewModel.onAction(SmsCodeAction.CodeChanged(fullCode))
        advanceUntilIdle()
        assertEquals(fullCode, viewModel.currentState.code)
    }

    @Test
    fun `VerifyCode success emits NavigateToPinSetup`() = runTest(mainDispatcherRule.testDispatcher) {
        val viewModel = viewModel(sessionId = "session-99")
        val fullCode = "1".repeat(SMS_CODE_LENGTH)
        repository.verifyCodeResult = AuthResult.PinRequired(interimToken = "888", isNewUser = true)

        viewModel.onAction(SmsCodeAction.CodeChanged(fullCode))
        viewModel.onAction(SmsCodeAction.VerifyCode)
        advanceUntilIdle()

        assertEquals(false, viewModel.currentState.isLoading)
        assertNull(viewModel.currentState.error)
        assertEquals("session-99", repository.lastVerifiedSessionId)
        assertEquals(fullCode, repository.lastVerifiedCode)

        val effect = viewModel.effect.first() as SmsCodeEffect.NavigateToPinSetup
        assertEquals("888", effect.interimToken)
        assertEquals(true, effect.isNewUser)
    }

    @Test
    fun `VerifyCode unexpected result sets error`() = runTest(mainDispatcherRule.testDispatcher) {
        val viewModel = viewModel()
        repository.verifyCodeResult = AuthResult.CodeSent("session-2", 60)

        viewModel.onAction(SmsCodeAction.CodeChanged("1".repeat(SMS_CODE_LENGTH)))
        viewModel.onAction(SmsCodeAction.VerifyCode)
        advanceUntilIdle()

        val state = viewModel.currentState
        assertEquals(false, state.isLoading)
        assertEquals(R.string.error_invalid_code, state.error?.messageRes)
    }

    @Test
    fun `ResendSms clears code and updates session and retry delay on success`() = runTest(mainDispatcherRule.testDispatcher) {
        val viewModel = viewModel(sessionId = "session-1", retryDelay = 60)
        repository.resendSmsResult = AuthResult.CodeSent(sessionId = "session-new", retryDelaySeconds = 120)

        viewModel.onAction(SmsCodeAction.CodeChanged("1".repeat(SMS_CODE_LENGTH)))
        viewModel.onAction(SmsCodeAction.ResendSms)
        advanceUntilIdle()

        assertEquals("session-1", repository.lastResendSessionId) // resend was called with the OLD session id
        assertEquals("", viewModel.currentState.code)
        assertEquals(120, viewModel.currentState.retryDelaySeconds)

        // a subsequent verify should now use the NEW session id
        viewModel.onAction(SmsCodeAction.CodeChanged("2".repeat(SMS_CODE_LENGTH)))
        viewModel.onAction(SmsCodeAction.VerifyCode)
        advanceUntilIdle()
        assertEquals("session-new", repository.lastVerifiedSessionId)
    }

    @Test
    fun `ResendSms failure is silent and keeps state unchanged besides clearing code`() = runTest(mainDispatcherRule.testDispatcher) {
        val viewModel = viewModel(retryDelay = 60)
        repository.resendSmsError = IllegalStateException("no `network")

        viewModel.onAction(SmsCodeAction.CodeChanged("1".repeat(SMS_CODE_LENGTH)))
        viewModel.onAction(SmsCodeAction.ResendSms)
        advanceUntilIdle()

        assertNull(viewModel.currentState.error)
        assertEquals(60, viewModel.currentState.retryDelaySeconds)
        assertEquals("", viewModel.currentState.code)
    }
}
