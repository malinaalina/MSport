package m.alina.msport.schedule.viewmodel

import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import m.alina.msport.domain.usecase.workout.GetScheduleUseCase
import m.alina.msport.schedule.ScheduleAction
import m.alina.msport.testutil.FakeWorkoutRepository
import m.alina.msport.testutil.MainDispatcherRule
import m.alina.msport.testutil.testWorkout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import java.util.Calendar
import java.util.Date

class ScheduleViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeWorkoutRepository()

    @Test
    fun `initial state loads a 7-day visible week around today`() = runTest(mainDispatcherRule.testDispatcher) {
        repository.scheduleResult = listOf(testWorkout(id = "w1"))
        val viewModel = ScheduleViewModel(GetScheduleUseCase(repository))
        advanceUntilIdle()
        assertEquals(7, viewModel.currentState.visibleDates.size)
        assertNull(viewModel.currentState.activeTypeFilter)
        assertEquals(listOf(testWorkout(id = "w1")), viewModel.currentState.classes)
    }

    @Test
    fun `SelectDate loads schedule for that day`() = runTest(mainDispatcherRule.testDispatcher) {
        val viewModel = ScheduleViewModel(GetScheduleUseCase(repository))
        advanceUntilIdle()
        repository.scheduleResult = listOf(testWorkout(id = "w2", title = "Бокс-фитнес"))
        val someDate = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 3) }.time
        viewModel.onAction(ScheduleAction.SelectDate(someDate))
        advanceUntilIdle()
        assertEquals(listOf(testWorkout(id = "w2", title = "Бокс-фитнес")), viewModel.currentState.classes)
        assertEquals(someDate.toIsoDateOnly(), viewModel.currentState.selectedDate.toIsoDateOnly())
    }

    @Test
    fun `SetFilter narrows classes down to the matching title`() = runTest(mainDispatcherRule.testDispatcher) {
        repository.scheduleResult = listOf(
            testWorkout(id = "yoga", title = "Йога"),
            testWorkout(id = "boxing", title = "Бокс-фитнес"),
        )
        val viewModel = ScheduleViewModel(GetScheduleUseCase(repository))
        advanceUntilIdle()
        viewModel.onAction(ScheduleAction.SetFilter("Йога"))
        advanceUntilIdle()
        assertEquals("Йога", viewModel.currentState.activeTypeFilter)
        assertEquals(listOf("yoga"), viewModel.currentState.classes.map { it.id })
        viewModel.onAction(ScheduleAction.SetFilter(null))
        advanceUntilIdle()
        assertEquals(listOf("yoga", "boxing"), viewModel.currentState.classes.map { it.id })
    }

    @Test
    fun `NextWeek and PreviousWeek shift the visible week`() = runTest(mainDispatcherRule.testDispatcher) {
        val viewModel = ScheduleViewModel(GetScheduleUseCase(repository))
        advanceUntilIdle()
        val initialFirstDay = viewModel.currentState.visibleDates.first()
        viewModel.onAction(ScheduleAction.NextWeek)
        advanceUntilIdle()
        val nextWeekFirstDay = viewModel.currentState.visibleDates.first()
        val diffDays = ((nextWeekFirstDay.time - initialFirstDay.time) / (24 * 60 * 60 * 1000))
        assertEquals(7, diffDays)
        viewModel.onAction(ScheduleAction.PreviousWeek)
        advanceUntilIdle()
        assertEquals(initialFirstDay.toIsoDateOnly(), viewModel.currentState.visibleDates.first().toIsoDateOnly())
    }

    @Test
    fun `Refresh reloads the schedule for the currently selected date`() = runTest(mainDispatcherRule.testDispatcher) {
        val viewModel = ScheduleViewModel(GetScheduleUseCase(repository))
        advanceUntilIdle()
        repository.scheduleResult = listOf(testWorkout(id = "refreshed"))
        viewModel.onAction(ScheduleAction.Refresh)
        advanceUntilIdle()
        assertEquals(listOf("refreshed"), viewModel.currentState.classes.map { it.id })
    }

    @Test
    fun `SelectDate reads from cache while explicit Refresh forces a network fetch`() = runTest(mainDispatcherRule.testDispatcher) {
        val viewModel = ScheduleViewModel(GetScheduleUseCase(repository))
        advanceUntilIdle()
        val someDate = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 3) }.time
        viewModel.onAction(ScheduleAction.SelectDate(someDate))
        advanceUntilIdle()
        assertEquals(false, repository.lastScheduleForceRefresh)
        viewModel.onAction(ScheduleAction.Refresh)
        advanceUntilIdle()
        assertEquals(true, repository.lastScheduleForceRefresh)
    }
}

private fun Date.toIsoDateOnly(): String {
    val cal = Calendar.getInstance().apply { time = this@toIsoDateOnly }
    return "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.DAY_OF_YEAR)}"
}
