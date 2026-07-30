package m.alina.msport.network

data class WorkoutSessionDto(
    val id: String,
    val date: String,
    val time: String,
    val title: String,
    val room: String,
    val instructor: String,
    val duration: String,
    val description: String,
    val attire: String,
    val isBooked: Boolean,
)
