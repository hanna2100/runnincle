package com.example.runnincle.framework.datasource.cache.database

import androidx.room.*
import com.example.runnincle.business.domain.model.Workout
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

    @Query("""
        SELECT * 
        FROM workouts 
        WHERE programId = :programId 
        ORDER BY workout_order ASC
    """)
    suspend fun getWorkoutsOfProgram(programId: String): Array<WorkoutCacheEntity>

    @Query("""
        DELETE 
        FROM workouts 
        WHERE programId = :programId
    """)
    suspend fun deleteWorkoutsWithProgramId(programId: String): Int
}