package com.example.runnincle.framework.datasource.cache.database

import androidx.room.*
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

    @Query("DELETE FROM programs WHERE id = :id")
    suspend fun deleteProgram(id: String): Int

}