package m.alina.msport.di

import dagger.Binds
import dagger.Module
import m.alina.msport.domain.repository.AuthRepository
import m.alina.msport.domain.repository.WorkoutRepository
import m.alina.msport.repository.MockAuthRepository
import m.alina.msport.repository.MockWorkoutRepository
import javax.inject.Singleton

@Module
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindWorkoutRepository(impl: MockWorkoutRepository): WorkoutRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: MockAuthRepository): AuthRepository
}
