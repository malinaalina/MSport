package m.alina.msport.domain.repository

import m.alina.msport.domain.model.BookingStatus
import m.alina.msport.domain.model.WorkoutSession

interface WorkoutRepository {
    suspend fun getPersonalWorkouts(forceRefresh: Boolean = false): List<WorkoutSession>
    suspend fun getPastWorkouts(forceRefresh: Boolean = false): List<WorkoutSession>
    suspend fun getSchedule(isoDate: String, forceRefresh: Boolean = false): List<WorkoutSession>
    suspend fun bookWorkout(workoutId: String): BookingStatus
    suspend fun cancelBooking(workoutId: String)
}
