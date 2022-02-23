package com.devhanna91.runnincle.framework.datasource.cache.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.devhanna91.runnincle.framework.datasource.cache.model.ProgramCacheEntity

@Database(entities = [ProgramCacheEntity::class], version = 1)
abstract class ProgramDatabase: RoomDatabase() {

    companion object {
        const val DATABASE_NAME = "program_db"
    }

    abstract fun programDao(): ProgramDao

}