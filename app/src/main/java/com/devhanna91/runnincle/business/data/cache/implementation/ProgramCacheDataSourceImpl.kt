package com.devhanna91.runnincle.business.data.cache.implementation

import com.devhanna91.runnincle.business.data.cache.abstraction.ProgramCacheDataSource
import com.devhanna91.runnincle.framework.datasource.cache.abstraction.ProgramDaoService
import com.devhanna91.runnincle.business.domain.model.Program
import com.devhanna91.runnincle.business.domain.util.DateUtil
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProgramCacheDataSourceImpl
@Inject
constructor(
    private val programDaoService: ProgramDaoService,
    private val dateUtil: DateUtil
): ProgramCacheDataSource {
    override suspend fun insertProgram(program: Program): Long {
        return programDaoService.insertProgram(program)
    }

    override suspend fun updateProgram(
        id: String,
        name: String,
        updatedAt: String
    ): Int {
        return programDaoService.updateProgram(id, name, updatedAt)
    }

    override suspend fun deleteProgram(programId: String): Int {
        return programDaoService.deleteProgram(programId)
    }

    override suspend fun getAllPrograms(): List<Program> {
        return programDaoService.getAllPrograms()
    }

    override suspend fun searchProgram(searchText: String): List<Program> {
        return programDaoService.searchProgram(searchText)
    }

    override suspend fun updateUpdatedAtFieldToUpToDate(program: Program) {
        programDaoService.updateProgram(
            id = program.id,
            name = program.name,
            updateAt = dateUtil.getCurrentTimestamp()
        )
    }

}