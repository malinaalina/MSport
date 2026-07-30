package m.alina.msport.classdetail

import m.alina.msport.common.WorkoutError
import m.alina.msport.domain.model.BookingStatus
import m.alina.msport.mvi.UiAction
import m.alina.msport.mvi.UiEffect
import m.alina.msport.mvi.UiState

data class ClassDetailState(
    val isBooking: Boolean = false,
    val bookingStatus: BookingStatus? = null,
    val error: WorkoutError? = null,
) : UiState

sealed interface ClassDetailAction : UiAction {
    data class Book(val workoutId: String) : ClassDetailAction
    data class Cancel(val workoutId: String) : ClassDetailAction
    object DismissError : ClassDetailAction
}

sealed interface ClassDetailEffect : UiEffect {
    object Booked : ClassDetailEffect
    object Cancelled : ClassDetailEffect
}
