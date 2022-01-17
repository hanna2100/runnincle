package com.example.runnincle.framework.datasource.cache.abstraction

import com.example.runnincle.domain.model.Program

interface ProgramDaoService {

    suspend fun insertProgram(program: Program): Long

    suspend fun updateProgram(
        primaryKey: Int,
        name: String,
        difficulty: Int
    ): Int

    suspend fun deleteProgram(primaryKey: Int): Int

    suspend fun getAllPrograms(): List<Program>

}