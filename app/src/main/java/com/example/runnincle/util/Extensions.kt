package com.example.runnincle

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import com.example.runnincle.business.domain.model.ParcelableWorkout
import com.example.runnincle.util.FloatingService.Companion.COMMAND_NAME
import com.example.runnincle.util.FloatingService.Companion.ARGUMENT_PROGRAM
import com.example.runnincle.business.domain.model.Program
import com.example.runnincle.business.domain.model.Workout
import com.example.runnincle.business.domain.model.Workout.Companion.getTotalWorkoutTime
import com.example.runnincle.business.domain.util.TimeAgo
import com.example.runnincle.framework.presentation.PermissionActivity
import com.example.runnincle.util.FloatingService
import com.example.runnincle.util.FloatingService.Companion.ARGUMENT_COOL_DOWN_TIMER_COLOR_VALUE
import com.example.runnincle.util.FloatingService.Companion.ARGUMENT_IS_TTS_USED
import com.example.runnincle.util.FloatingService.Companion.ARGUMENT_OVERLAY_DP
import com.example.runnincle.util.FloatingService.Companion.ARGUMENT_TOTAL_TIMER_COLOR_VALUE
import com.example.runnincle.util.FloatingService.Companion.ARGUMENT_WORKOUTS
import com.example.runnincle.util.FloatingServiceCommand
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.reflect.KClass

fun Context.openOverlayWindowWithFloatingService(
    program: Program,
    workouts: ArrayList<ParcelableWorkout>,
    overlayDp: Int,
    isTTSUsed: Boolean,
    totalTimerColor: Color,
    coolDownTimerColor: Color,
) {
    val intent = Intent(this, FloatingService::class.java)
    intent.putExtra(COMMAND_NAME, FloatingServiceCommand.OPEN.name)
    intent.putExtra(ARGUMENT_OVERLAY_DP, overlayDp)
    intent.putExtra(ARGUMENT_PROGRAM, program)
    intent.putExtra(ARGUMENT_WORKOUTS, workouts)
    intent.putExtra(ARGUMENT_IS_TTS_USED, isTTSUsed)
    intent.putExtra(ARGUMENT_TOTAL_TIMER_COLOR_VALUE, totalTimerColor.value.toString())
    intent.putExtra(ARGUMENT_COOL_DOWN_TIMER_COLOR_VALUE, coolDownTimerColor.value.toString())

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        this.startForegroundService(intent)
    } else {
        this.startService(intent)
    }
}

fun Context.closeOverlayWindowWithFloatingService() {
    val intent = Intent(this, FloatingService::class.java)
    intent.putExtra(COMMAND_NAME, FloatingServiceCommand.CLOSE.name)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        this.startForegroundService(intent)
    } else {
        this.startService(intent)
    }
}

fun Context.drawOverOtherAppsEnabled(): Boolean {
    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
        true
    } else {
        Settings.canDrawOverlays(this)
    }
}

fun Context.startPermissionActivity() {
    startActivity(PermissionActivity::class)
}

fun Context.startActivity(kclass: KClass<out Activity>) {
    startActivity(
        Intent(this, kclass.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    )
}

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

fun Context.showToastMessage(message:String){
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
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
@Composable
fun Float.dp() = with(LocalDensity.current) {  Dp(this@dp).toSp() }


//fun NavController.navigateSafe(
//    @IdRes resId: Int,
//    args: Bundle? = null,
//    navOptions: NavOptions? = null,
//    navExtras: Navigator.Extras? = null
//) {
//    val action = currentDestination?.getAction(resId) ?: graph.getAction(resId)
//    if (action != null && currentDestination?.id != action.destinationId) {
//        navigate(resId, args, navOptions, navExtras)
//    }
//}
//
//fun NavController.navigateSafeUp(@IdRes currentFragmentResId: Int) {
//    if (currentDestination?.id == currentFragmentResId) {
//        navigateUp()
//    }
//}
