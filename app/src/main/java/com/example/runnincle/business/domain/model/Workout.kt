package com.example.runnincle.business.domain.model

import android.os.Parcelable
import androidx.compose.ui.graphics.Color
import com.example.runnincle.business.domain.model.Workout.Companion.toParcelableWorkout
import kotlinx.android.parcel.Parcelize

data class Workout(
    var id: String? = null,
    var programId: String? = null,
    var name: String,
    var set: Int = 1,
    var work: Int,
    var coolDown: Int = 0,
    var order: Int? = null,
    var isSkipLastCoolDown: Boolean = false,
    var timerColor: Color
) {
    companion object {
        fun Workout.getTotalWorkoutTime(): Int {
            var totalWorkoutTime = 0
            for(i in 1 .. this.set) {
                totalWorkoutTime += work
                if (i == this.set && this.isSkipLastCoolDown) {
                    break
                }
                totalWorkoutTime += coolDown
            }
            return totalWorkoutTime
        }

        fun Workout.getMinValueAndIgnoreSecValue(time: Int): Int {
            return if(time < 60) {
                0
            } else {
                time / 60
            }
        }

        fun Workout.getSecValueAndIgnoreMinValue(time: Int): Int {
            return if(time < 60) {
                time
            } else {
                time % 60
            }
        }

        fun Workout.toParcelableWorkout(): ParcelableWorkout {
            return ParcelableWorkout(
                id = this.id,
                programId = this.programId,
                name = this.name ,
                set = this.set,
                work = this.work,
                coolDown = this.coolDown,
                order = this.order,
                isSkipLastCoolDown = this.isSkipLastCoolDown,
                timerColor = this.timerColor.value.toString()
            )
        }

        fun ParcelableWorkout.toWorkout(): Workout {
            return Workout(
                id = this.id,
                programId = this.programId,
                name = this.name ,
                set = this.set,
                work = this.work,
                coolDown = this.coolDown,
                order = this.order,
                isSkipLastCoolDown = this.isSkipLastCoolDown,
                timerColor = Color(this.timerColor.toULong())
            )
        }
    }
}

/*
    Workout을 직접적으로 직렬화 할 수 없어서 만듦.
    java.lang.RuntimeException: Parcel: unable to marshal value Color
 */
@Parcelize
data class ParcelableWorkout(
    var id: String? = null,
    var programId: String? = null,
    var name: String,
    var set: Int = 1,
    var work: Int,
    var coolDown: Int = 0,
    var order: Int? = null,
    var isSkipLastCoolDown: Boolean = false,
    var timerColor: String
): Parcelable
