package com.example.runnincle.framework.datasource.cache.implementation

import com.example.runnincle.framework.datasource.cache.abstraction.ProgramDaoService
import com.example.runnincle.business.domain.model.Program
import com.example.runnincle.business.domain.util.DateUtil
import com.example.runnincle.framework.datasource.cache.database.ProgramDao
import com.example.runnincle.framework.datasource.cache.mappers.ProgramCacheMapper

class ProgramDaoServiceImpl(
    private val programDao: ProgramDao,
    private val programCacheMapper: ProgramCacheMapper,
    private val dateUtil: DateUtil
): ProgramDaoService {
    override suspend fun insertProgram(program: Program): Long {
        return programDao.insertProgram(programCacheMapper.mapFromDomainModel(program))
    }

    override suspend fun updateProgram(
        id: String,
        name: String,
        updateAt: String?
    ): Int {
        return if (updateAt.isNullOrEmpty()) {
            programDao.updateProgram(
                id = id,
                name = name,
                updatedAt = dateUtil.getCurrentTimestamp()
            )
        } else {
            programDao.updateProgram(
                id = id,
                name = name,
                updatedAt = updateAt
            )
        }
    }

    override suspend fun deleteProgram(programId: String): Int {
        return programDao.deleteProgram(programId)
    }

    override suspend fun getAllPrograms(): List<Program> {
        val entityPrograms = programDao.getAllProgram()
        val domainPrograms = mutableListOf<Program>()
        entityPrograms.forEach { entity ->
            val domain = programCacheMapper.mapFromEntity(entity)
            domainPrograms.add(domain)
        }
        return domainPrograms
    }

    override suspend fun searchProgram(searchText: String): List<Program> {
        val entityPrograms = programDao.searchProgram(searchText)
        val domainPrograms = mutableListOf<Program>()
        entityPrograms.forEach { entity ->
            val domain = programCacheMapper.mapFromEntity(entity)
            domainPrograms.add(domain)
        }
        return domainPrograms
    }
}