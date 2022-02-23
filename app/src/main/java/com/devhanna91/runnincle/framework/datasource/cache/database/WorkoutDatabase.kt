package com.devhanna91.runnincle.framework.datasource.cache.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.devhanna91.runnincle.framework.datasource.cache.model.WorkoutCacheEntity

@Database(entities = [WorkoutCacheEntity::class], version = 1)
abstract class WorkoutDatabase: RoomDatabase() {

    companion object {
        const val DATABASE_NAME = "workout_db"
    }

    abstract fun workoutDao(): WorkoutDao

}