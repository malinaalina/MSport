package m.alina.msport.domain.model

enum class BookingStatus {
    NONE, BOOKED, WAITLISTED
}

data class WorkoutSession(
    val id: String,
    val date: String,
    val time: String,
    val title: String,
    val room: String,
    val instructor: String,
    val duration: String,
    val description: String,
    val attire: String,
    val capacity: Int = 12,
    val taken: Int = 0,
    val bookingStatus: BookingStatus = BookingStatus.NONE,
    val isPast: Boolean = false,
)
