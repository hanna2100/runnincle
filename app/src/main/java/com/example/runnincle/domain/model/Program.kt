package com.example.runnincle.domain.model

data class Program(
    var id: Int,
    var name: String,
    var difficulty: Int
)

data class Workout(
    var id: Int,
    var programId: Int,
    var name: String,
    var set: Int = 1,
    var work: Int,
    var rest: Int = 0,
    var order: Int
) {
    companion object {
        fun Workout.getTotalWorkoutTime(): Int {
            var totalWorkoutTime = 0
            for(i in 1 .. this.set) {
                totalWorkoutTime += work
                totalWorkoutTime += rest
            }
            return totalWorkoutTime
        }
    }
}