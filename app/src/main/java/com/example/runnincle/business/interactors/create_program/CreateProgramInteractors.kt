package com.example.runnincle.business.interactors.create_program

import com.example.runnincle.business.data.cache.abstraction.ProgramCacheDataSource
import com.example.runnincle.business.data.cache.abstraction.WorkoutCacheDataSource
import com.example.runnincle.business.data.util.getRandomUUID
import com.example.runnincle.business.domain.model.Program
import com.example.runnincle.business.domain.model.Workout
import com.example.runnincle.business.domain.util.DateUtil

// use cases 정의
class CreateProgramInteractors(
    private val programCacheDataSource: ProgramCacheDataSource,
    private val workoutCacheDataSource: WorkoutCacheDataSource,
    private val dateUtil: DateUtil
) {
    suspend fun insertNewProgram (
        name: String,
        workouts: List<Workout>
    ): Long {

        val programId = getRandomUUID()

        val program = Program(
            id = programId,
            name = name,
            updatedAt = dateUtil.getCurrentTimestamp()
        )

        for(i in 0..workouts.lastIndex) {
            val workoutId = getRandomUUID()
            val w = workouts[i].copy(
                id = workoutId,
                programId = programId,
                order = i
            )
            workoutCacheDataSource.insertWorkout(w)
        }

        return programCacheDataSource.insertProgram(program)
    }
}