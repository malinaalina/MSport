package m.alina.msport.domain.usecase.workout

import m.alina.msport.domain.repository.WorkoutRepository
import javax.inject.Inject

class CancelBookingUseCase @Inject constructor(private val repository: WorkoutRepository) {
    suspend operator fun invoke(workoutId: String) = repository.cancelBooking(workoutId)
}
