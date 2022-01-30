package com.example.runnincle.business.domain.model

import androidx.compose.ui.graphics.Color

data class Workout(
    var id: String? = null,
    var programId: String? = null,
    var name: String,
    var set: Int = 1,
    var work: Int,
    var coolDown: Int = 0,
    var order: Int,
    var isSkipLastCoolDown: Boolean = false,
    var timerColor: Color
) {
    companion object {
        fun Workout.getTotalWorkoutTime(): Int {
            var totalWorkoutTime = 0
            for(i in 1 .. this.set) {
                totalWorkoutTime += work
                totalWorkoutTime += coolDown
            }
            return totalWorkoutTime
        }
    }
}