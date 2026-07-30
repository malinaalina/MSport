package m.alina.msport.repository

import android.util.Log
import m.alina.msport.database.WorkoutDao
import m.alina.msport.database.WorkoutEntity
import m.alina.msport.domain.model.BookingStatus
import m.alina.msport.domain.model.WorkoutSession
import m.alina.msport.domain.repository.WorkoutRepository
import m.alina.msport.network.WorkoutApi
import m.alina.msport.network.WorkoutSessionDto
import retrofit2.HttpException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkWorkoutRepository @Inject constructor(
    private val workoutApi: WorkoutApi,
    private val workoutDao: WorkoutDao,
    private val sessionManager: SessionManager,
) : WorkoutRepository {

    private val isoFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    private fun todayIso(): String = isoFormat.format(java.util.Date())

    private fun WorkoutSessionDto.toEntity(): WorkoutEntity = WorkoutEntity(
        id = id,
        date = date,
        time = time,
        title = title,
        room = room,
        instructor = instructor,
        duration = duration,
        description = description,
        attire = attire,
        isBooked = isBooked,
    )

    private fun WorkoutEntity.toDomain(isPast: Boolean): WorkoutSession = WorkoutSession(
        id = id,
        date = date,
        time = time,
        title = title,
        room = room,
        instructor = instructor,
        duration = duration,
        description = description,
        attire = attire,
        capacity = 1,
        taken = 0,
        bookingStatus = if (isBooked) BookingStatus.BOOKED else BookingStatus.NONE,
        isPast = isPast,
    )
    private suspend fun refreshCache(forceRefresh: Boolean): List<WorkoutEntity> {
        if (!forceRefresh) {
            val cached = workoutDao.getAll()
            if (cached.isNotEmpty()) return cached
        }
        return try {
            val entities = workoutApi.getWorkouts().map { it.toEntity() }
            workoutDao.clearAll()
            workoutDao.upsertAll(entities)
            entities
        } catch (e: IOException) {
            Log.w("NetworkWorkoutRepository", "getWorkouts() failed", e)
            workoutDao.getAll()
        } catch (e: HttpException) {
            Log.w("NetworkWorkoutRepository", "getWorkouts() failed", e)
            workoutDao.getAll()
        }
    }

    override suspend fun getSchedule(isoDate: String, forceRefresh: Boolean): List<WorkoutSession> =
        refreshCache(forceRefresh)
            .filter { it.date == isoDate }
            .map { it.toDomain(isPast = false) }

    override suspend fun getPersonalWorkouts(forceRefresh: Boolean): List<WorkoutSession> {
        val today = todayIso()
        return refreshCache(forceRefresh)
            .filter { it.isBooked && it.date >= today }
            .sortedWith(compareBy({ it.date }, { it.time }))
            .map { it.toDomain(isPast = false) }
    }

    override suspend fun getPastWorkouts(forceRefresh: Boolean): List<WorkoutSession> {
        val today = todayIso()
        return refreshCache(forceRefresh)
            .filter { it.isBooked && it.date < today }
            .sortedWith(compareBy({ it.date }, { it.time }))
            .map { it.toDomain(isPast = true) }
    }

    override suspend fun bookWorkout(workoutId: String): BookingStatus {
        if (sessionManager.getAuthToken() == null) return BookingStatus.NONE
        workoutApi.bookWorkout(workoutId)
        markCached(workoutId, isBooked = true)
        return BookingStatus.BOOKED
    }

    override suspend fun cancelBooking(workoutId: String) {
        markCached(workoutId, isBooked = false)
    }

    private suspend fun markCached(workoutId: String, isBooked: Boolean) {
        workoutDao.getAll().find { it.id == workoutId }?.let {
            workoutDao.upsertAll(listOf(it.copy(isBooked = isBooked)))
        }
    }
}
