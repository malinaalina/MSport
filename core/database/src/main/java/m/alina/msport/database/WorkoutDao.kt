package m.alina.msport.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface WorkoutDao {
    @Query("SELECT * FROM workout_sessions")
    suspend fun getAll(): List<WorkoutEntity>

    @Upsert
    suspend fun upsertAll(entities: List<WorkoutEntity>)

    @Query("DELETE FROM workout_sessions")
    suspend fun clearAll()
}
