package com.example.runnincle.framework.datasource.cache.database

import androidx.room.*
import com.example.runnincle.framework.datasource.cache.model.ProgramCacheEntity

@Dao
interface ProgramDao {
    @Insert
    suspend fun insertProgram(program: ProgramCacheEntity)

    @Update
    suspend fun updateProgram(program: ProgramCacheEntity)

    @Delete
    suspend fun deleteProgram(program: ProgramCacheEntity)

}