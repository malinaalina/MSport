package m.alina.msport.navigation

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import m.alina.msport.testutil.MainDispatcherRule
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class MainScreenViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val viewModel = MainScreenViewModel()

    @Test
    fun `initial state shows the Schedule tab`() {
        val state = viewModel.currentState
        assertEquals(ScreenKey.Schedule, state.currentTab)
    }

    @Test
    fun `SelectTab switches the current tab`() = runTest(mainDispatcherRule.testDispatcher) {
        viewModel.onAction(MainScreenAction.SelectTab(ScreenKey.Profile))
        advanceUntilIdle()

        assertEquals(ScreenKey.Profile, viewModel.currentState.currentTab)
    }

    @Test
    fun `WorkoutBooked emits RefreshLists`() = runTest(mainDispatcherRule.testDispatcher) {
        viewModel.onAction(MainScreenAction.WorkoutBooked)
        advanceUntilIdle()

        assertEquals(MainScreenEffect.RefreshLists, viewModel.effect.first())
    }

    @Test
    fun `WorkoutCancelled emits RefreshLists`() = runTest(mainDispatcherRule.testDispatcher) {
        viewModel.onAction(MainScreenAction.WorkoutCancelled)
        advanceUntilIdle()

        assertEquals(MainScreenEffect.RefreshLists, viewModel.effect.first())
    }

    @Test
    fun `GoToMyClasses switches to Personal tab and emits navigation effect`() =
        runTest(mainDispatcherRule.testDispatcher) {
            viewModel.onAction(MainScreenAction.GoToMyClasses)
            advanceUntilIdle()

            assertEquals(ScreenKey.Personal, viewModel.currentState.currentTab)
            assertEquals(MainScreenEffect.NavigateToMyClassesUpcoming, viewModel.effect.first())
        }

    @Test
    fun `DismissConfirmation resets to Schedule tab`() = runTest(mainDispatcherRule.testDispatcher) {
        viewModel.onAction(MainScreenAction.SelectTab(ScreenKey.Profile))
        viewModel.onAction(MainScreenAction.DismissConfirmation)
        advanceUntilIdle()

        assertEquals(ScreenKey.Schedule, viewModel.currentState.currentTab)
    }
}
