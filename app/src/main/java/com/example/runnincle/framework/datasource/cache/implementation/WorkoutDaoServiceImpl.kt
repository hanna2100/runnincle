package com.example.runnincle.framework.datasource.cache.implementation

import com.example.runnincle.business.domain.model.Workout
import com.example.runnincle.framework.datasource.cache.abstraction.WorkoutDaoService
import com.example.runnincle.framework.datasource.cache.database.WorkoutDao
import com.example.runnincle.framework.datasource.cache.mappers.WorkoutCacheMapper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkoutDaoServiceImpl
@Inject
constructor(
    private val workoutDao: WorkoutDao,
    private val workoutCacheMapper: WorkoutCacheMapper
): WorkoutDaoService{
    override suspend fun insertWorkout(workout: Workout): Long {
        return workoutDao.insertWorkout(workoutCacheMapper.mapFromDomainModel(workout))
    }

    override suspend fun updateWorkout(
        id: String,
        name: String,
        set: Int,
        work: Int,
        coolDown: Int,
        order: Int
    ): Int {
        return workoutDao.updateWorkout(
            id = id,
            name = name,
            set = set,
            work = work,
            coolDown = coolDown,
            order = order
        )
    }

    override suspend fun deleteWorkout(id: Int): Int {
        TODO("Not yet implemented")
    }

    override suspend fun getWorkoutsOfProgram(programId: String): List<Workout> {
        val entityWorkouts = workoutDao.getWorkoutsOfProgram(programId)
        val domainWorkouts = mutableListOf<Workout>()
        entityWorkouts.forEach { entity->
            val domain = workoutCacheMapper.mapFromEntity(entity)
            domainWorkouts.add(domain)
        }
        return domainWorkouts
    }

    override suspend fun deleteWorkoutsWithProgramId(programId: String): Int {
        return workoutDao.deleteWorkoutsWithProgramId(programId)
    }
}