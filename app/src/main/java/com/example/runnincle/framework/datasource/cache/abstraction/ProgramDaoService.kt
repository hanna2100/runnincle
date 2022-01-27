package com.example.runnincle.framework.datasource.cache.abstraction

import com.example.runnincle.business.domain.model.Program

interface ProgramDaoService {

    suspend fun insertProgram(program: Program): Long

    suspend fun updateProgram(
        id: String,
        name: String,
        difficulty: Int,
        updateAt: String?
    ): Int

    suspend fun deleteProgram(primaryKey: Int): Int

    suspend fun getAllPrograms(): List<Program>

}