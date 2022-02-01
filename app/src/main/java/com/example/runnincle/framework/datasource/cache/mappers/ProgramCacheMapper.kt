package com.example.runnincle.framework.datasource.cache.mappers

import com.example.runnincle.framework.datasource.cache.model.ProgramCacheEntity
import com.example.runnincle.business.domain.model.Program
import com.example.runnincle.business.domain.util.EntityMapper

class ProgramCacheMapper: EntityMapper<ProgramCacheEntity, Program> {
    override fun mapFromEntity(entity: ProgramCacheEntity): Program {
        return Program(
            id = entity.id,
            name = entity.name,
            updatedAt = entity.updated_at
        )
    }

    override fun mapFromDomainModel(domainModel: Program): ProgramCacheEntity {
        return ProgramCacheEntity(
            id = domainModel.id,
            name = domainModel.name,
            updated_at = domainModel.updatedAt
        )
    }

}