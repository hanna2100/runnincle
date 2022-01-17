package com.example.runnincle.framework.datasource.cache.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.runnincle.framework.datasource.cache.model.ProgramCacheEntity

@Database(entities = [ProgramCacheEntity::class], version = 1)
abstract class ProgramDatabase: RoomDatabase() {

    companion object {
        private const val DATABASE_NAME = "program_db"
        private var instance: ProgramDatabase? = null

        @Synchronized
        fun getInstance(context: Context): ProgramDatabase? {
            if (instance == null) {
                synchronized(ProgramDatabase::class) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        ProgramDatabase::class.java,
                        DATABASE_NAME
                    ).build()
                }
            }
            return instance
        }
    }

    abstract fun programDao(): ProgramDao

}