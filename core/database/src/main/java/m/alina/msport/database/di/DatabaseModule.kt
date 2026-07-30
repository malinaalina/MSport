package m.alina.msport.database.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import m.alina.msport.database.MSportDatabase
import m.alina.msport.database.WorkoutDao
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(context: Context): MSportDatabase =
        Room.databaseBuilder(context, MSportDatabase::class.java, "msport.db").build()

    @Provides
    @Singleton
    fun provideWorkoutDao(database: MSportDatabase): WorkoutDao = database.workoutDao()
}
