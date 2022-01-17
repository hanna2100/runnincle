package com.example.runnincle.framework.datasource.cache.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ProgramCacheEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var name: String,
    var difficulty: Int
)