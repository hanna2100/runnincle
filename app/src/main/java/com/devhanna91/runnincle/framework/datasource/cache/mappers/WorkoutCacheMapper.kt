package com.devhanna91.runnincle.framework.datasource.cache.mappers

import androidx.compose.ui.graphics.Color
import com.devhanna91.runnincle.framework.datasource.cache.model.WorkoutCacheEntity
import com.devhanna91.runnincle.business.domain.model.Workout
import com.devhanna91.runnincle.business.util.EntityMapper

class WorkoutCacheMapper: EntityMapper<WorkoutCacheEntity, Workout> {
    override fun mapFromEntity(entity: WorkoutCacheEntity): Workout {
        return Workout(
            id = entity.id,
            programId = entity.programId,
            name = entity.name,
            set = entity.set,
            work = entity.work,
            coolDown = entity.coolDown,
            isSkipLastCoolDown = entity.isSkipLastCoolDown,
            order = entity.order,
            timerColor = Color(entity.timerColor.toULong())
        )
    }

    override fun mapFromDomainModel(domainModel: Workout): WorkoutCacheEntity {
        return WorkoutCacheEntity(
            id = domainModel.id!!,
            programId = domainModel.programId!!,
            name = domainModel.name,
            set = domainModel.set,
            work = domainModel.work,
            coolDown = domainModel.coolDown,
            isSkipLastCoolDown = domainModel.isSkipLastCoolDown,
            order = domainModel.order!!,
            timerColor = domainModel.timerColor.value.toString()
        )
    }
}