package com.devhanna91.runnincle

import android.content.Context
import com.devhanna91.runnincle.business.domain.model.Workout
import com.devhanna91.runnincle.business.domain.model.Workout.Companion.getTotalWorkoutTime
import com.devhanna91.runnincle.business.util.TimeAgo
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

fun Int.toTimeLetters(context: Context): String {
    return if (this < 60) {
        "$this${context.getString(R.string.seconds)}"
    } else {
        val min: Int = this/60
        val sec: Int = this - (min * 60)
        if(sec > 0) {
            "$min${context.getString(R.string.minutes)} $sec${context.getString(R.string.seconds)}"
        } else {
            "$min${context.getString(R.string.minutes)}"
        }
    }
}

fun Int.toTimeClock(): String {
    return if (this <= 59) {
        if(this < 10) {
            "00:0$this"
        } else {
            "00:$this"
        }
    } else {
        val min: Int = this/60
        val sec: Int = this - (min * 60)
        if(sec > 0) {
            if(sec < 10) {
                if (min < 10) {
                    "0$min:0$sec"
                } else {
                    "$min:0$sec"
                }
            } else {
                if (min < 10) {
                    "0$min:$sec"
                } else {
                    "$min:$sec"
                }
            }
        } else {
            if (min < 10) {
                "0$min:00"
            } else {
                "$min:00"
            }
        }
    }
}

fun List<Workout>.getTotalWorkoutListTime(): Int {
    var totalWorkoutsTime = 0
    this.forEach {
        totalWorkoutsTime += it.getTotalWorkoutTime()
    }
    return totalWorkoutsTime
}

fun String.toTimeAgo(): String {
    // 형식검사
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        sdf.isLenient = false
        val past = sdf.parse(this)
        val now = Date()

        TimeAgo.toDuration(now.time - past.time)
    } catch (e: ParseException) {
        this
    }
}

