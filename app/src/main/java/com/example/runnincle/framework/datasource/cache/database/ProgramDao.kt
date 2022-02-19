package com.example.runnincle.framework.datasource.cache.database

import androidx.room.*
import com.example.runnincle.business.domain.model.Program
import com.example.runnincle.framework.datasource.cache.model.ProgramCacheEntity

@Dao
interface ProgramDao {
    @Insert
    suspend fun insertProgram(program: ProgramCacheEntity): Long

    @Query("""
        UPDATE programs 
        SET 
        name = :name, 
        updated_at = :updatedAt
        WHERE id = :id
        """)
    suspend fun updateProgram(
        id: String,
        name: String,
        updatedAt: String
    ): Int

    @Query("DELETE FROM programs WHERE id = :programId")
    suspend fun deleteProgram(programId: String): Int

    @Query("""
        SELECT * FROM programs ORDER BY updated_at DESC
        """)
    suspend fun getAllProgram(): Array<ProgramCacheEntity>

    @Query("""
        SELECT *
        FROM programs 
        WHERE name LIKE '%' || :searchText || '%'
        ORDER BY updated_at DESC
        """)
    suspend fun searchProgram(searchText: String): Array<ProgramCacheEntity>

}