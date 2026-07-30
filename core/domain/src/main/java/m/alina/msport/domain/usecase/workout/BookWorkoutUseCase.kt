package m.alina.msport.domain.usecase.workout

import m.alina.msport.domain.model.BookingStatus
import m.alina.msport.domain.repository.WorkoutRepository
import javax.inject.Inject

class BookWorkoutUseCase @Inject constructor(private val repository: WorkoutRepository) {
    suspend operator fun invoke(workoutId: String): BookingStatus = repository.bookWorkout(workoutId)
}
