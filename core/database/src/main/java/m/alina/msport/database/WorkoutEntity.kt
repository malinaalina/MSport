package m.alina.msport.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_sessions")
data class WorkoutEntity(
    @PrimaryKey val id: String,
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
