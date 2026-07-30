package m.alina.msport.domain.usecase.workout

import m.alina.msport.domain.model.WorkoutSession
import m.alina.msport.domain.repository.WorkoutRepository
import javax.inject.Inject

class GetPastWorkoutsUseCase @Inject constructor(private val repository: WorkoutRepository) {
    suspend operator fun invoke(forceRefresh: Boolean = false): List<WorkoutSession> =
        repository.getPastWorkouts(forceRefresh)
}
