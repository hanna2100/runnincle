package com.example.runnincle.framework.datasource.cache.mappers

import com.example.runnincle.framework.datasource.cache.model.WorkoutCacheEntity
import com.example.runnincle.business.domain.model.Workout
import com.example.runnincle.business.domain.util.EntityMapper

class WorkoutCacheMapper: EntityMapper<WorkoutCacheEntity, Workout>{
    override fun mapFromEntity(entity: WorkoutCacheEntity): Workout {
        return Workout(
            id = entity.id,
            programId = entity.programId,
            name = entity.name,
            set = entity.set,
            work = entity.work,
            rest = entity.rest,
            order = entity.order
        )
    }

    override fun mapFromDomainModel(domainModel: Workout): WorkoutCacheEntity {
        return WorkoutCacheEntity(
            id = domainModel.id,
            programId = domainModel.programId,
            name = domainModel.name,
            set = domainModel.set,
            work = domainModel.work,
            rest = domainModel.rest,
            order = domainModel.order
        )
    }
}