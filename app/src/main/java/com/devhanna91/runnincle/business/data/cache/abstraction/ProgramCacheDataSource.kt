package com.devhanna91.runnincle.business.data.cache.abstraction

import com.devhanna91.runnincle.business.domain.model.Program

interface ProgramCacheDataSource {
    suspend fun insertProgram(program: Program): Long

    suspend fun updateProgram(
        id: String,
        name: String,
        updatedAt: String
    ): Int

    suspend fun deleteProgram(programId: String): Int

    suspend fun getAllPrograms(): List<Program>

    suspend fun searchProgram(searchText: String): List<Program>

    suspend fun updateUpdatedAtFieldToUpToDate(program: Program)
}