package com.example.runnincle.business.data.cache.implementation

import com.example.runnincle.business.data.cache.abstraction.WorkoutCacheDataSource
import com.example.runnincle.business.domain.model.Workout
import com.example.runnincle.framework.datasource.cache.abstraction.WorkoutDaoService

class WorkoutCacheDataSourceImpl constructor (
    private val workoutDaoService: WorkoutDaoService
): WorkoutCacheDataSource {
    override suspend fun insertWorkout(workout: Workout): Long {
        return workoutDaoService.insertWorkout(workout)
    }

    override suspend fun updateWorkout(
        id: Int,
        name: String,
        set: Int,
        work: Int,
        rest: Int,
        order: Int
    ): Int {
        return workoutDaoService.updateWorkout(id, name, set, work, rest, order)
    }

    override suspend fun deleteWorkout(id: Int): Int {
        return workoutDaoService.deleteWorkout(id)
    }

    override suspend fun getWorkoutsWithProgramId(programId: Int): List<Workout> {
        return workoutDaoService.getWorkoutsWithProgramId(programId)
    }
}