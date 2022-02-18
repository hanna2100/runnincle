package com.example.runnincle.business.data.cache.implementation

import com.example.runnincle.business.data.cache.abstraction.WorkoutCacheDataSource
import com.example.runnincle.business.domain.model.Workout
import com.example.runnincle.framework.datasource.cache.abstraction.WorkoutDaoService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkoutCacheDataSourceImpl
@Inject
constructor (
    private val workoutDaoService: WorkoutDaoService
): WorkoutCacheDataSource {
    override suspend fun insertWorkout(workout: Workout): Long {
        return workoutDaoService.insertWorkout(workout)
    }

    override suspend fun updateWorkout(
        id: String,
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

    override suspend fun getWorkoutsOfProgram(programId: String): List<Workout> {
        return workoutDaoService.getWorkoutsOfProgram(programId)
    }

    override suspend fun deleteWorkoutsWithProgramId(programId: String): Int {
        return workoutDaoService.deleteWorkoutsWithProgramId(programId)
    }
}