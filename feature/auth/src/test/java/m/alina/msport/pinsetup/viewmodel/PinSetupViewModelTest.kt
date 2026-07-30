package m.alina.msport.pinsetup.viewmodel

import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import m.alina.msport.domain.model.AuthResult
import m.alina.msport.domain.usecase.auth.SetPinUseCase
import m.alina.msport.feature.auth.R
import m.alina.msport.pinsetup.PinSetupAction
import m.alina.msport.pinsetup.PinSetupEffect
import m.alina.msport.repository.SessionManager
import m.alina.msport.testutil.FakeAuthRepository
import m.alina.msport.testutil.MainDispatcherRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class PinSetupViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeAuthRepository()
    private val sessionManager = mockk<SessionManager>(relaxed = true)

    private fun viewModel(isReentry: Boolean = false) =
        PinSetupViewModel(SetPinUseCase(repository), sessionManager, isReentry)

    @Test
    fun `initial state carries isReentry from constructor`() {
        assertEquals(true, viewModel(isReentry = true).currentState.isReentry)
        assertEquals(false, viewModel(isReentry = false).currentState.isReentry)
    }

    @Test
    fun `PinChanged auto-submits once pin reaches required length`() = runTest(mainDispatcherRule.testDispatcher) {
        val viewModel = viewModel()
        repository.setPinResult = AuthResult.Authenticated("access-token", "refresh-token", 3600)

        viewModel.onAction(PinSetupAction.PinChanged("1"))
        viewModel.onAction(PinSetupAction.PinChanged("12"))
        viewModel.onAction(PinSetupAction.PinChanged("123"))
        advanceUntilIdle()
        assertNull(repository.lastSetPin)

        viewModel.onAction(PinSetupAction.PinChanged("1234"))
        advanceUntilIdle()

        assertEquals("1234", repository.lastSetPin)
        verify { sessionManager.saveAuthToken("access-token", "refresh-token") }

        val effect = viewModel.effect.first() as PinSetupEffect.Authorized
        assertEquals("access-token", effect.accessToken)
    }

    @Test
    fun `PinChanged rejects non-digit input`() = runTest(mainDispatcherRule.testDispatcher) {
        val viewModel = viewModel()

        viewModel.onAction(PinSetupAction.PinChanged("12a4"))
        advanceUntilIdle()

        assertEquals("", viewModel.currentState.pin)
    }

    @Test
    fun `Submit does nothing when pin is incomplete`() = runTest(mainDispatcherRule.testDispatcher) {
        val viewModel = viewModel()

        viewModel.onAction(PinSetupAction.PinChanged("12"))
        viewModel.onAction(PinSetupAction.Submit)
        advanceUntilIdle()

        assertNull(repository.lastSetPin)
        assertEquals(false, viewModel.currentState.isLoading)
    }

    @Test
    fun `setPin unexpected result sets error`() = runTest(mainDispatcherRule.testDispatcher) {
        val viewModel = viewModel()
        repository.setPinResult = AuthResult.CodeSent("session-1", 60)

        viewModel.onAction(PinSetupAction.PinChanged("1234"))
        advanceUntilIdle()

        val state = viewModel.currentState
        assertEquals(false, state.isLoading)
        assertEquals(R.string.error_pin, state.error?.messageRes)
    }

    @Test
    fun `setPin exception sets error with exception message`() = runTest(mainDispatcherRule.testDispatcher) {
        val viewModel = viewModel()
        repository.setPinError = IllegalStateException("no network")

        viewModel.onAction(PinSetupAction.PinChanged("1234"))
        advanceUntilIdle()

        assertEquals("no network", viewModel.currentState.error?.message)
    }

    @Test
    fun `DismissError clears error`() = runTest(mainDispatcherRule.testDispatcher) {
        val viewModel = viewModel()
        repository.setPinError = IllegalStateException("")

        viewModel.onAction(PinSetupAction.PinChanged("1234"))
        advanceUntilIdle()
        assertEquals(false, viewModel.currentState.error == null)

        viewModel.onAction(PinSetupAction.DismissError)
        advanceUntilIdle()

        assertNull(viewModel.currentState.error)
    }
}
