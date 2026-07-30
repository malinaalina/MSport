package m.alina.msport.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [WorkoutEntity::class], version = 1, exportSchema = false)
abstract class MSportDatabase : RoomDatabase() {
    abstract fun workoutDao(): WorkoutDao
}
