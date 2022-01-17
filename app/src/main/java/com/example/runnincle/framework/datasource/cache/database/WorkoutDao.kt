package com.example.runnincle.framework.datasource.cache.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import com.example.runnincle.framework.datasource.cache.model.WorkoutCacheEntity

@Dao
interface WorkoutDao {
    @Insert
    suspend fun insertWorkout(workout: WorkoutCacheEntity)

    @Update
    suspend fun updateWorkout(workout: WorkoutCacheEntity)

    @Delete
    suspend fun deleteWorkout(workout: WorkoutCacheEntity)
}