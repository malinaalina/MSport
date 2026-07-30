package m.alina.msport.myclasses.viewmodel

import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import m.alina.msport.domain.usecase.workout.CancelBookingUseCase
import m.alina.msport.domain.usecase.workout.GetPastWorkoutsUseCase
import m.alina.msport.domain.usecase.workout.GetPersonalWorkoutsUseCase
import m.alina.msport.myclasses.MyClassesAction
import m.alina.msport.myclasses.MyClassesSubTab
import m.alina.msport.testutil.FakeWorkoutRepository
import m.alina.msport.testutil.MainDispatcherRule
import m.alina.msport.testutil.testWorkout
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class MyClassesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeWorkoutRepository()

    private fun viewModel() = MyClassesViewModel(
        GetPersonalWorkoutsUseCase(repository),
        GetPastWorkoutsUseCase(repository),
        CancelBookingUseCase(repository),
    )

    @Test
    fun `init loads upcoming and past workouts`() = runTest(mainDispatcherRule.testDispatcher) {
        repository.personalWorkouts = listOf(testWorkout(id = "up1"))
        repository.pastWorkouts = listOf(testWorkout(id = "past1"))

        val viewModel = viewModel()
        advanceUntilIdle()

        assertEquals(MyClassesSubTab.UPCOMING, viewModel.currentState.subTab)
        assertEquals(listOf("up1"), viewModel.currentState.upcoming.map { it.id })
        assertEquals(listOf("past1"), viewModel.currentState.past.map { it.id })
    }

    @Test
    fun `SelectUpcoming and SelectPast switch the sub-tab`() = runTest(mainDispatcherRule.testDispatcher) {
        val viewModel = viewModel()
        advanceUntilIdle()

        viewModel.onAction(MyClassesAction.SelectPast)
        advanceUntilIdle()
        assertEquals(MyClassesSubTab.PAST, viewModel.currentState.subTab)

        viewModel.onAction(MyClassesAction.SelectUpcoming)
        advanceUntilIdle()
        assertEquals(MyClassesSubTab.UPCOMING, viewModel.currentState.subTab)
    }

    @Test
    fun `Cancel cancels the booking and refreshes upcoming only`() = runTest(mainDispatcherRule.testDispatcher) {
        repository.personalWorkouts = listOf(testWorkout(id = "up1"))
        repository.pastWorkouts = listOf(testWorkout(id = "past1"))
        val viewModel = viewModel()
        advanceUntilIdle()

        repository.personalWorkouts = emptyList()
        viewModel.onAction(MyClassesAction.Cancel("up1"))
        advanceUntilIdle()

        assertEquals("up1", repository.lastCancelledWorkoutId)
        assertEquals(emptyList<String>(), viewModel.currentState.upcoming.map { it.id })
        assertEquals(listOf("past1"), viewModel.currentState.past.map { it.id })
    }

    @Test
    fun `RefreshAll reloads both lists`() = runTest(mainDispatcherRule.testDispatcher) {
        val viewModel = viewModel()
        advanceUntilIdle()

        repository.personalWorkouts = listOf(testWorkout(id = "new-up"))
        repository.pastWorkouts = listOf(testWorkout(id = "new-past"))
        viewModel.onAction(MyClassesAction.RefreshAll)
        advanceUntilIdle()

        assertEquals(listOf("new-up"), viewModel.currentState.upcoming.map { it.id })
        assertEquals(listOf("new-past"), viewModel.currentState.past.map { it.id })
    }
}
