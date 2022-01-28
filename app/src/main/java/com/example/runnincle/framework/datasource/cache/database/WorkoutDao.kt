package com.example.runnincle.framework.datasource.cache.database

import androidx.room.*
import com.example.runnincle.framework.datasource.cache.model.WorkoutCacheEntity

@Dao
interface WorkoutDao {
    @Insert
    suspend fun insertWorkout(workout: WorkoutCacheEntity): Long

    @Query("""
        UPDATE workouts 
        SET 
        name = :name,
        workout_set = :set,
        work = :work,
        coolDown = :coolDown,
        workout_order = :order
        WHERE id = :id
    """)
    suspend fun updateWorkout(
        id: String,
        name: String,
        set: Int,
        work: Int,
        coolDown: Int,
        order: Int,
    ): Int

    @Delete
    suspend fun deleteWorkout(workout: WorkoutCacheEntity)
}