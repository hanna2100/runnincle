package com.example.runnincle.framework.datasource.cache.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.runnincle.framework.datasource.cache.model.WorkoutCacheEntity

@Database(entities = [WorkoutCacheEntity::class], version = 1)
abstract class WorkoutDatabase: RoomDatabase() {

    companion object {
        private const val DATABASE_NAME = "workout_db"
        private var instance: WorkoutDatabase? = null

        @Synchronized
        fun getInstance(context: Context): WorkoutDatabase? {
            if (instance == null) {
                synchronized(ProgramDatabase::class) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        WorkoutDatabase::class.java,
                        DATABASE_NAME
                    ).build()
                }
            }
            return instance
        }
    }

    abstract fun workoutDao(): WorkoutDao

}