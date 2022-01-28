package com.example.runnincle.business.data.cache.implementation

import com.example.runnincle.business.data.cache.abstraction.ProgramCacheDataSource
import com.example.runnincle.framework.datasource.cache.abstraction.ProgramDaoService
import com.example.runnincle.business.domain.model.Program
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProgramCacheDataSourceImpl
@Inject
constructor(
    private val programDaoService: ProgramDaoService
): ProgramCacheDataSource {
    override suspend fun insertProgram(program: Program): Long {
        return programDaoService.insertProgram(program)
    }

    override suspend fun updateProgram(
        id: String,
        name: String,
        difficulty: Int,
        updatedAt: String
    ): Int {
        return programDaoService.updateProgram(id, name, difficulty, updatedAt)
    }

    override suspend fun deleteProgram(primaryKey: Int): Int {
        return programDaoService.deleteProgram(primaryKey)
    }

    override suspend fun getAllPrograms(): List<Program> {
        return programDaoService.getAllPrograms()
    }

}