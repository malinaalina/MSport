package m.alina.msport.repository

import android.content.Context
import kotlinx.coroutines.delay
import m.alina.msport.core.data.R
import m.alina.msport.domain.model.BookingStatus
import m.alina.msport.domain.model.WorkoutSession
import m.alina.msport.domain.repository.WorkoutRepository
import m.alina.msport.util.russianDayAbbrev
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

private data class ClassSlot(
    val time: String,
    val type: String,
    val trainer: String,
    val hall: String,
    val capacity: Int,
    val taken: Int,
)

private val WEEKLY_TEMPLATE: List<List<ClassSlot>> = listOf(
    listOf( // Пн
        ClassSlot("08:00", "Йога", "Анна Смирнова", "Студия Yoga", 12, 12),
        ClassSlot("12:00", "Функциональный тренинг", "Мария Иванова", "Зал 1", 14, 6),
        ClassSlot("19:00", "Стретчинг", "Екатерина Орлова", "Зал 2", 10, 4),
    ),
    listOf( // Вт
        ClassSlot("09:00", "Пилатес", "Мария Иванова", "Зал 1", 10, 9),
        ClassSlot("18:30", "Бокс-фитнес", "Екатерина Орлова", "Зал 2", 12, 5),
        ClassSlot("20:00", "Йога", "Анна Смирнова", "Студия Yoga", 12, 3),
    ),
    listOf( // Ср
        ClassSlot("08:00", "Стретчинг", "Екатерина Орлова", "Зал 2", 10, 2),
        ClassSlot("13:00", "Функциональный тренинг", "Мария Иванова", "Зал 1", 14, 14),
        ClassSlot("19:30", "Пилатес", "Мария Иванова", "Зал 1", 10, 7),
    ),
    listOf( // Чт
        ClassSlot("08:30", "Йога", "Анна Смирнова", "Студия Yoga", 12, 8),
        ClassSlot("12:30", "Бокс-фитнес", "Екатерина Орлова", "Зал 2", 12, 9),
        ClassSlot("18:00", "Стретчинг", "Екатерина Орлова", "Зал 2", 10, 1),
    ),
    listOf( // Пт
        ClassSlot("09:00", "Пилатес", "Мария Иванова", "Зал 1", 10, 4),
        ClassSlot("17:00", "Функциональный тренинг", "Мария Иванова", "Зал 1", 14, 11),
        ClassSlot("19:00", "Йога", "Анна Смирнова", "Студия Yoga", 12, 12),
    ),
    listOf( // Сб
        ClassSlot("10:00", "Стретчинг", "Екатерина Орлова", "Зал 2", 10, 3),
        ClassSlot("12:00", "Бокс-фитнес", "Екатерина Орлова", "Зал 2", 12, 6),
        ClassSlot("18:30", "Пилатес", "Мария Иванова", "Зал 1", 10, 5),
    ),
    listOf( // Вс
        ClassSlot("09:30", "Йога", "Анна Смирнова", "Студия Yoga", 12, 5),
        ClassSlot("11:30", "Функциональный тренинг", "Мария Иванова", "Зал 1", 14, 2),
        ClassSlot("17:30", "Стретчинг", "Екатерина Орлова", "Зал 2", 10, 6),
    )
)

@Singleton
class MockWorkoutRepository @Inject constructor(
    private val sessionManager: SessionManager,
    context: Context,
) : WorkoutRepository {

    private val isoFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    private val defaultDuration = context.getString(R.string.default_workout_duration)
    private val defaultDescription = context.getString(R.string.default_workout_description)
    private val defaultAttire = context.getString(R.string.default_workout_attire)

    private val bookings = mutableMapOf<String, BookingStatus>()

    private val pastWorkout = WorkoutSession(
        id = "past1",
        date = "ПТ 3 Июл",
        time = "07:30",
        title = "Пилатес",
        room = "Зал 1",
        instructor = "Мария Иванова",
        duration = defaultDuration,
        description = defaultDescription,
        attire = defaultAttire,
        isPast = true,
    )

    init {
        val today = Calendar.getInstance()
        bookings[isoDateFor(today, 0)] = BookingStatus.BOOKED
        val inTwoDays = (today.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, 2) }
        bookings[isoDateFor(inTwoDays, 1)] = BookingStatus.WAITLISTED
    }

    private fun isoDateFor(calendar: Calendar, slotIndex: Int): String =
        "${isoFormat.format(calendar.time)}#$slotIndex"

    private fun mondayIndex(dayOfWeek: Int): Int = (dayOfWeek + 5) % 7

    private fun buildSession(isoDate: String, slotIndex: Int, calendar: Calendar, slot: ClassSlot): WorkoutSession {
        val id = "$isoDate#$slotIndex"
        val dow = russianDayAbbrev(calendar.get(Calendar.DAY_OF_WEEK))
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        return WorkoutSession(
            id = id,
            date = "$dow $dayOfMonth",
            time = slot.time,
            title = slot.type,
            room = slot.hall,
            instructor = slot.trainer,
            duration = defaultDuration,
            description = defaultDescription,
            attire = defaultAttire,
            capacity = slot.capacity,
            taken = slot.taken,
            bookingStatus = bookings[id] ?: BookingStatus.NONE,
        )
    }

    private fun sessionFromId(id: String): WorkoutSession? {
        val parts = id.split("#")
        if (parts.size != 2) return null
        val isoDate = parts[0]
        return parts[1].toIntOrNull()?.let { slotIndex ->
            isoFormat.parse(isoDate)?.let { date ->
                val calendar = Calendar.getInstance().apply { time = date }
                val slots = WEEKLY_TEMPLATE[mondayIndex(calendar.get(Calendar.DAY_OF_WEEK))]
                slots.getOrNull(slotIndex)?.let { slot ->
                    buildSession(isoDate, slotIndex, calendar, slot)
                }
            }
        }
    }

    override suspend fun getPersonalWorkouts(forceRefresh: Boolean): List<WorkoutSession> {
        return bookings.keys
            .mapNotNull { sessionFromId(it) }
            .sortedWith(compareBy({ it.date }, { it.time }))
    }

    override suspend fun getPastWorkouts(forceRefresh: Boolean): List<WorkoutSession> {
        return listOf(pastWorkout)
    }

    override suspend fun getSchedule(isoDate: String, forceRefresh: Boolean): List<WorkoutSession> {
        val date = isoFormat.parse(isoDate) ?: return emptyList()
        val calendar = Calendar.getInstance().apply { time = date }
        val slots = WEEKLY_TEMPLATE[mondayIndex(calendar.get(Calendar.DAY_OF_WEEK))]
        return slots.mapIndexed { index, slot -> buildSession(isoDate, index, calendar, slot) }
    }

    override suspend fun bookWorkout(workoutId: String): BookingStatus {
        val token = sessionManager.getAuthToken()
        if (token == null) return BookingStatus.NONE
        delay(600)
        val session = sessionFromId(workoutId) ?: return BookingStatus.NONE
        val status = if (session.taken >= session.capacity) BookingStatus.WAITLISTED else BookingStatus.BOOKED
        bookings[workoutId] = status
        return status
    }

    override suspend fun cancelBooking(workoutId: String) {
        delay(300)
        bookings.remove(workoutId)
    }
}
