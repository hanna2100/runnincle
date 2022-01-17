package com.example.runnincle.framework.datasource.cache.mappers

import com.example.runnincle.framework.datasource.cache.model.ProgramCacheEntity
import com.example.runnincle.domain.model.Program
import com.example.runnincle.domain.util.EntityMapper

class ProgramCacheMapper: EntityMapper<ProgramCacheEntity, Program> {
    override fun mapFromEntity(entity: ProgramCacheEntity): Program {
        return Program(
            id = entity.id,
            name = entity.name,
            difficulty = entity.difficulty
        )
    }

    override fun mapFromDomainModel(domainModel: Program): ProgramCacheEntity {
        return ProgramCacheEntity(
            id = domainModel.id,
            name = domainModel.name,
            difficulty = domainModel.difficulty
        )
    }

}