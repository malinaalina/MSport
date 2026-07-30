package m.alina.msport.classdetail.viewmodel

import m.alina.msport.classdetail.ClassDetailAction
import m.alina.msport.classdetail.ClassDetailEffect
import m.alina.msport.classdetail.ClassDetailState
import m.alina.msport.common.WorkoutError
import m.alina.msport.domain.model.BookingStatus
import m.alina.msport.domain.usecase.workout.BookWorkoutUseCase
import m.alina.msport.domain.usecase.workout.CancelBookingUseCase
import m.alina.msport.feature.workout.R
import m.alina.msport.mvi.BaseViewModel
import java.io.IOException
import javax.inject.Inject

class ClassDetailViewModel @Inject constructor(
    private val bookWorkout: BookWorkoutUseCase,
    private val cancelBooking: CancelBookingUseCase,
) : BaseViewModel<ClassDetailState, ClassDetailAction, ClassDetailEffect>(ClassDetailState()) {

    override suspend fun handleAction(action: ClassDetailAction) {
        when (action) {
            is ClassDetailAction.Book -> book(action.workoutId)
            is ClassDetailAction.Cancel -> cancel(action.workoutId)
            is ClassDetailAction.DismissError -> setState { copy(error = null) }
        }
    }

    private suspend fun book(workoutId: String) {
        setState { copy(isBooking = true, error = null) }
        try {
            val status = bookWorkout(workoutId)
            setState { copy(isBooking = false) }
            if (status != BookingStatus.NONE) {
                setState { copy(bookingStatus = status) }
                sendEffect { ClassDetailEffect.Booked }
            }
        } catch (e: IOException) {
            setState { copy(isBooking = false, error = WorkoutError(R.string.error_no_internet)) }
        } catch (e: Exception) {
            setState { copy(isBooking = false, error = WorkoutError(message = e.message)) }
        }
    }

    private suspend fun cancel(workoutId: String) {
        cancelBooking(workoutId)
        sendEffect { ClassDetailEffect.Cancelled }
    }
}
