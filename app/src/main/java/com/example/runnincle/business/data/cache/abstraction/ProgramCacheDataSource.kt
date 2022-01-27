package com.example.runnincle.business.data.cache.abstraction

import com.example.runnincle.business.domain.model.Program

interface ProgramCacheDataSource {
    suspend fun insertProgram(program: Program): Long

    suspend fun updateProgram(
        id: String,
        name: String,
        difficulty: Int,
        updatedAt: String
    ): Int

    suspend fun deleteProgram(primaryKey: Int): Int

    suspend fun getAllPrograms(): List<Program>
}