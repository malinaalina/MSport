package m.alina.msport.network

import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface WorkoutApi {
    @GET("api/v1/workouts")
    suspend fun getWorkouts(): List<WorkoutSessionDto>

    @POST("api/v1/workouts/{id}/book")
    suspend fun bookWorkout(@Path("id") workoutId: String): Unit
}
