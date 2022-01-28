package com.example.runnincle.di

import androidx.room.Room
import com.example.runnincle.framework.datasource.cache.database.ProgramDatabase
import com.example.runnincle.framework.datasource.cache.database.WorkoutDatabase
import com.example.runnincle.framework.presentation.BaseApplication
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Singleton
    @Provides
    fun provideProgramDB(app: BaseApplication): ProgramDatabase {
        return Room.databaseBuilder(
            app,
            ProgramDatabase::class.java,
            ProgramDatabase.DATABASE_NAME
        ).build()
    }

    @Singleton
    @Provides
    fun provideWorkoutDB(app: BaseApplication): WorkoutDatabase {
        return Room.databaseBuilder(
            app,
            WorkoutDatabase::class.java,
            WorkoutDatabase.DATABASE_NAME
        ).build()
    }
}