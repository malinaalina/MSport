package m.alina.msport.phone.viewmodel

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import m.alina.msport.domain.model.AuthResult
import m.alina.msport.domain.usecase.auth.SendSmsUseCase
import m.alina.msport.feature.auth.R
import m.alina.msport.phone.PhoneLoginAction
import m.alina.msport.phone.PhoneLoginEffect
import m.alina.msport.testutil.FakeAuthRepository
import m.alina.msport.testutil.MainDispatcherRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class PhoneLoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeAuthRepository()
    private val viewModel = PhoneLoginViewModel(SendSmsUseCase(repository))

    @Test
    fun `initial state is empty and idle`() {
        val state = viewModel.currentState
        assertEquals("", state.phoneDigits)
        assertEquals(false, state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `PhoneDigitsChanged strips non-digits and truncates to max length`() = runTest(mainDispatcherRule.testDispatcher) {
        // digits only: "79991234567" (11 digits) -> truncated to the first 10
        viewModel.onAction(PhoneLoginAction.PhoneDigitsChanged("+7 (999) 123-45-67"))
        advanceUntilIdle()

        assertEquals("7999123456", viewModel.currentState.phoneDigits)
    }

    @Test
    fun `SendSms success emits NavigateToSmsCode with e164 phone`() = runTest(mainDispatcherRule.testDispatcher) {
        repository.sendSmsResult = AuthResult.CodeSent(sessionId = "001122", retryDelaySeconds = 90)

        viewModel.onAction(PhoneLoginAction.PhoneDigitsChanged("9991234567"))
        viewModel.onAction(PhoneLoginAction.SendSms)
        advanceUntilIdle()

        assertEquals(false, viewModel.currentState.isLoading)
        assertNull(viewModel.currentState.error)
        assertEquals("+79991234567", repository.lastSentPhone)

        val effect = viewModel.effect.first() as PhoneLoginEffect.NavigateToSmsCode
        assertEquals("001122", effect.sessionId)
        assertEquals(90, effect.retryDelaySeconds)
        assertEquals("+79991234567", effect.phone)
    }

    @Test
    fun `SendSms unexpected result sets error`() = runTest(mainDispatcherRule.testDispatcher) {
        repository.sendSmsResult = AuthResult.Authenticated("token", null, null)

        viewModel.onAction(PhoneLoginAction.PhoneDigitsChanged("9991234567"))
        viewModel.onAction(PhoneLoginAction.SendSms)
        advanceUntilIdle()

        val state = viewModel.currentState
        assertEquals(false, state.isLoading)
        assertEquals(R.string.error_sms_send, state.error?.messageRes)
    }

    @Test
    fun `SendSms exception sets error with exception message`() = runTest(mainDispatcherRule.testDispatcher) {
        repository.sendSmsError = IllegalStateException("no network")

        viewModel.onAction(PhoneLoginAction.PhoneDigitsChanged("9991234567"))
        viewModel.onAction(PhoneLoginAction.SendSms)
        advanceUntilIdle()

        val state = viewModel.currentState
        assertEquals(false, state.isLoading)
        assertEquals("no network", state.error?.message)
    }

    @Test
    fun `DismissError clears error`() = runTest(mainDispatcherRule.testDispatcher) {
        repository.sendSmsError = IllegalStateException("")
        viewModel.onAction(PhoneLoginAction.PhoneDigitsChanged("9991234567"))
        viewModel.onAction(PhoneLoginAction.SendSms)
        advanceUntilIdle()
        assertTrue(viewModel.currentState.error != null)

        viewModel.onAction(PhoneLoginAction.DismissError)
        advanceUntilIdle()

        assertNull(viewModel.currentState.error)
    }
}
