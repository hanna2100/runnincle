package com.example.runnincle.business.interactors.program_list

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
        difficulty: Int,
        workouts: List<Workout>
    ): Long {

        val programId = getRandomUUID()

        workouts.forEach { workout ->
            val workoutId = getRandomUUID()
            val mWorkout = workout.copy(
                id = workoutId,
                programId = programId
            )
            workoutCacheDataSource.insertWorkout(mWorkout)
        }

        val program = Program(
            id = programId,
            name = name,
            difficulty = difficulty,
            updatedAt = dateUtil.getCurrentTimestamp()
        )
        return programCacheDataSource.insertProgram(program)
    }

}