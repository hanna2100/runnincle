package com.example.runnincle.business.domain.model

data class Workout(
    var id: String,
    var programId: String,
    var name: String,
    var set: Int = 1,
    var work: Int,
    var coolDown: Int = 0,
    var order: Int
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