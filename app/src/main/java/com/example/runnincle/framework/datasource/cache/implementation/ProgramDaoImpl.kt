package com.example.runnincle.framework.datasource.cache.implementation

import com.example.runnincle.framework.datasource.cache.abstraction.ProgramDaoService
import com.example.runnincle.domain.model.Program

class ProgramDaoImpl: ProgramDaoService {
    override suspend fun insertProgram(program: Program): Long {
        TODO("Not yet implemented")
    }

    override suspend fun updateProgram(primaryKey: Int, name: String, difficulty: Int): Int {
        TODO("Not yet implemented")
    }

    override suspend fun deleteProgram(primaryKey: Int): Int {
        TODO("Not yet implemented")
    }

    override suspend fun getAllPrograms(): List<Program> {
        TODO("Not yet implemented")
    }
}