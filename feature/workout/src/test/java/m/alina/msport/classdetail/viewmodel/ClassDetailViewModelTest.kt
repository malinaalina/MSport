package m.alina.msport.classdetail.viewmodel

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import m.alina.msport.classdetail.ClassDetailAction
import m.alina.msport.classdetail.ClassDetailEffect
import m.alina.msport.domain.model.BookingStatus
import m.alina.msport.domain.usecase.workout.BookWorkoutUseCase
import m.alina.msport.domain.usecase.workout.CancelBookingUseCase
import m.alina.msport.feature.workout.R
import m.alina.msport.testutil.FakeWorkoutRepository
import m.alina.msport.testutil.MainDispatcherRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class ClassDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeWorkoutRepository()
    private val viewModel = ClassDetailViewModel(BookWorkoutUseCase(repository), CancelBookingUseCase(repository))

    @Test
    fun `Book success updates bookingStatus and emits Booked`() = runTest(mainDispatcherRule.testDispatcher) {
        repository.bookResult = BookingStatus.BOOKED
        viewModel.onAction(ClassDetailAction.Book("1234411"))
        advanceUntilIdle()
        assertEquals("1234411", repository.lastBookedWorkoutId)
        assertEquals(false, viewModel.currentState.isBooking)
        assertEquals(BookingStatus.BOOKED, viewModel.currentState.bookingStatus)
        assertEquals(ClassDetailEffect.Booked, viewModel.effect.first())
    }

    @Test
    fun `Book that results in waitlist still emits Booked with WAITLISTED status`() = runTest(mainDispatcherRule.testDispatcher) {
        repository.bookResult = BookingStatus.WAITLISTED

        viewModel.onAction(ClassDetailAction.Book("1234411"))
        advanceUntilIdle()
        assertEquals(BookingStatus.WAITLISTED, viewModel.currentState.bookingStatus)
        assertEquals(ClassDetailEffect.Booked, viewModel.effect.first())
    }

    @Test
    fun `Book failure (NONE) does not emit an effect or set a status`() = runTest(mainDispatcherRule.testDispatcher) {
        repository.bookResult = BookingStatus.NONE
        viewModel.onAction(ClassDetailAction.Book("1234411"))
        advanceUntilIdle()
        assertEquals(false, viewModel.currentState.isBooking)
        assertNull(viewModel.currentState.bookingStatus)
    }

    @Test
    fun `Book with no internet sets error and does not emit Booked`() = runTest(mainDispatcherRule.testDispatcher) {
        repository.bookException = IOException("no network")
        viewModel.onAction(ClassDetailAction.Book("1234411"))
        advanceUntilIdle()
        assertEquals(false, viewModel.currentState.isBooking)
        assertNull(viewModel.currentState.bookingStatus)
        assertNotNull(viewModel.currentState.error)
        assertEquals(R.string.error_no_internet, viewModel.currentState.error?.messageRes)
    }

    @Test
    fun `DismissError clears the error state`() = runTest(mainDispatcherRule.testDispatcher) {
        repository.bookException = IOException("no network")
        viewModel.onAction(ClassDetailAction.Book("1234411"))
        advanceUntilIdle()
        viewModel.onAction(ClassDetailAction.DismissError)
        advanceUntilIdle()
        assertNull(viewModel.currentState.error)
    }

    @Test
    fun `Cancel cancels booking and emits Cancelled`() = runTest(mainDispatcherRule.testDispatcher) {
        viewModel.onAction(ClassDetailAction.Cancel("1234411"))
        advanceUntilIdle()
        assertEquals("1234411", repository.lastCancelledWorkoutId)
        assertEquals(ClassDetailEffect.Cancelled, viewModel.effect.first())
    }
}
