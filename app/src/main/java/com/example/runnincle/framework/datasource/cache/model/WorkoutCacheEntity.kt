package com.example.runnincle.framework.datasource.cache.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workouts")
data class WorkoutCacheEntity(
    @PrimaryKey(autoGenerate = false)
    var id: String,
    var programId: String,
    var name: String,
    @ColumnInfo(name = "workout_set")
    var set: Int = 1,
    var work: Int,
    var coolDown: Int = 0,
    @ColumnInfo(name = "workout_order")
    var order: Int,
    var timerColor: String
)