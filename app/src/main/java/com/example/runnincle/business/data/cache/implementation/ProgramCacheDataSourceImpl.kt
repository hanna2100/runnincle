package com.example.runnincle.business.data.cache.implementation

import com.example.runnincle.business.data.cache.abstraction.ProgramCacheDataSource
import com.example.runnincle.framework.datasource.cache.abstraction.ProgramDaoService
import com.example.runnincle.domain.model.Program

class ProgramCacheDataSourceImpl constructor(
    private val programDaoService: ProgramDaoService
): ProgramCacheDataSource {
    override suspend fun insertProgram(program: Program): Long {
        return programDaoService.insertProgram(program)
    }

    override suspend fun updateProgram(primaryKey: Int, name: String, difficulty: Int): Int {
        return programDaoService.updateProgram(primaryKey, name, difficulty)
    }

    override suspend fun deleteProgram(primaryKey: Int): Int {
        return programDaoService.deleteProgram(primaryKey)
    }

    override suspend fun getAllPrograms(): List<Program> {
        return programDaoService.getAllPrograms()
    }

}