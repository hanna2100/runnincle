package com.example.runnincle.framework.datasource.cache.abstraction

import com.example.runnincle.business.domain.model.Workout

interface WorkoutDaoService {

    suspend fun insertWorkout(workout: Workout): Long

    suspend fun updateWorkout(
        id: String,
        name: String,
        set: Int,
        work: Int,
        rest: Int,
        order: Int
    ): Int

    suspend fun deleteWorkout(id: Int): Int

    suspend fun getWorkoutsWithProgramId(programId: Int): List<Workout>

}