package m.alina.msport.testutil

import m.alina.msport.domain.model.BookingStatus
import m.alina.msport.domain.model.WorkoutSession
import m.alina.msport.domain.repository.WorkoutRepository

class FakeWorkoutRepository : WorkoutRepository {

    var personalWorkouts: List<WorkoutSession> = emptyList()
    var pastWorkouts: List<WorkoutSession> = emptyList()
    var scheduleResult: List<WorkoutSession> = emptyList()
    var bookResult: BookingStatus = BookingStatus.BOOKED
    var bookException: Exception? = null

    var lastRequestedIsoDate: String? = null
    var lastBookedWorkoutId: String? = null
    var lastCancelledWorkoutId: String? = null
    var lastScheduleForceRefresh: Boolean? = null
    var scheduleRequestCount: Int = 0

    override suspend fun getPersonalWorkouts(forceRefresh: Boolean): List<WorkoutSession> = personalWorkouts

    override suspend fun getPastWorkouts(forceRefresh: Boolean): List<WorkoutSession> = pastWorkouts

    override suspend fun getSchedule(isoDate: String, forceRefresh: Boolean): List<WorkoutSession> {
        lastRequestedIsoDate = isoDate
        lastScheduleForceRefresh = forceRefresh
        scheduleRequestCount++
        return scheduleResult
    }

    override suspend fun bookWorkout(workoutId: String): BookingStatus {
        lastBookedWorkoutId = workoutId
        bookException?.let { throw it }
        return bookResult
    }

    override suspend fun cancelBooking(workoutId: String) {
        lastCancelledWorkoutId = workoutId
    }
}

fun testWorkout(
    id: String = "1234411",
    title: String = "Йога",
    capacity: Int = 12,
    taken: Int = 0,
    bookingStatus: BookingStatus = BookingStatus.NONE,
    isPast: Boolean = false,
): WorkoutSession = WorkoutSession(
    id = id,
    date = "ПН 1 Янв",
    time = "10:00",
    title = title,
    room = "Зал 1",
    instructor = "Мария Иванова",
    duration = "60 минут",
    description = "Описание",
    attire = "Спортивная форма",
    capacity = capacity,
    taken = taken,
    bookingStatus = bookingStatus,
    isPast = isPast,
)
