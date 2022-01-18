package com.example.runnincle.framework.datasource.cache.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ProgramCacheEntity(
    @PrimaryKey(autoGenerate = false)
    var id: String,
    var name: String,
    var difficulty: Int,
    var updated_at: String
)