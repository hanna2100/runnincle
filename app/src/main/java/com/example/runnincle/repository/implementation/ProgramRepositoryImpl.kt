package com.example.runnincle.repository.implementation

import com.example.runnincle.framework.datasource.cache.mappers.ProgramCacheMapper
import com.example.runnincle.domain.model.Program
import com.example.runnincle.repository.abstraction.ProgramRepository

class ProgramRepositoryImpl(
    private val mapper: ProgramCacheMapper
): ProgramRepository {
    override suspend fun getAllPrograms(): List<Program> {
        TODO("Not yet implemented")
    }

    override suspend fun get(id: Int): Program {
        TODO("Not yet implemented")
    }
}