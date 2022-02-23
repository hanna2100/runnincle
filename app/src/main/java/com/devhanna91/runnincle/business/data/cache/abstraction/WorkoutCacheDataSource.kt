package com.devhanna91.runnincle.business.data.cache.abstraction

import com.devhanna91.runnincle.business.domain.model.Workout

interface WorkoutCacheDataSource {
    suspend fun insertWorkout(workout: Workout): Long

    suspend fun updateWorkout (
        id: String,
        name: String,
        set: Int,
        work: Int,
        rest: Int,
        order: Int
    ): Int

    suspend fun deleteWorkout(id: Int): Int

    suspend fun getWorkoutsOfProgram(programId: String): List<Workout>

    suspend fun deleteWorkoutsWithProgramId(programId: String): Int
}