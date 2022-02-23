package com.devhanna91.runnincle.framework.datasource.cache.abstraction

import com.devhanna91.runnincle.business.domain.model.Program

interface ProgramDaoService {

    suspend fun insertProgram(program: Program): Long

    suspend fun updateProgram(
        id: String,
        name: String,
        updateAt: String?
    ): Int

    suspend fun deleteProgram(programId: String): Int

    suspend fun getAllPrograms(): List<Program>

    suspend fun searchProgram(searchText: String): List<Program>

}