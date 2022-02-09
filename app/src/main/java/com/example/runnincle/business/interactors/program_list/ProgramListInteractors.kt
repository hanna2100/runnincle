package com.example.runnincle.business.interactors.program_list

import com.example.runnincle.business.data.cache.abstraction.ProgramCacheDataSource
import com.example.runnincle.business.data.cache.abstraction.WorkoutCacheDataSource
import com.example.runnincle.business.domain.model.Program
import com.example.runnincle.business.domain.model.Workout

class ProgramListInteractors (
    private val programCacheDataSource: ProgramCacheDataSource,
    private val workoutCacheDataSource: WorkoutCacheDataSource,
) {

    suspend fun getAllProgram(): List<Program> {
        return programCacheDataSource.getAllPrograms()
    }

    suspend fun getWorkoutsOfProgram(programId: String): List<Workout> {
        return workoutCacheDataSource.getWorkoutsOfProgram(programId)
    }

}