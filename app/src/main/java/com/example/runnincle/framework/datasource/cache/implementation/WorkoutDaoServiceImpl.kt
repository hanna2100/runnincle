package com.example.runnincle.framework.datasource.cache.implementation

import com.example.runnincle.business.domain.model.Workout
import com.example.runnincle.framework.datasource.cache.abstraction.WorkoutDaoService

class WorkoutDaoServiceImpl : WorkoutDaoService{
    override suspend fun insertWorkout(workout: Workout): Long {
        TODO("Not yet implemented")
    }

    override suspend fun updateWorkout(
        id: Int,
        name: String,
        set: Int,
        work: Int,
        rest: Int,
        order: Int
    ): Int {
        TODO("Not yet implemented")
    }

    override suspend fun deleteWorkout(id: Int): Int {
        TODO("Not yet implemented")
    }

    override suspend fun getWorkoutsWithProgramId(programId: Int): List<Workout> {
        TODO("Not yet implemented")
    }
}