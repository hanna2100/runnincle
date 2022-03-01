package com.devhanna91.runnincle.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import com.devhanna91.runnincle.business.domain.model.ParcelableWorkout
import com.devhanna91.runnincle.business.domain.model.Program
import com.devhanna91.runnincle.framework.presentation.activity.MainActivity
import com.devhanna91.runnincle.framework.presentation.activity.PermissionActivity
import java.util.ArrayList
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
    intent.putExtra(FloatingService.COMMAND_NAME, FloatingServiceCommand.OPEN.name)
    intent.putExtra(FloatingService.ARGUMENT_OVERLAY_DP, overlayDp)
    intent.putExtra(FloatingService.ARGUMENT_PROGRAM, program)
    intent.putExtra(FloatingService.ARGUMENT_WORKOUTS, workouts)
    intent.putExtra(FloatingService.ARGUMENT_IS_TTS_USED, isTTSUsed)
    intent.putExtra(FloatingService.ARGUMENT_TOTAL_TIMER_COLOR_VALUE, totalTimerColor.value.toString())
    intent.putExtra(FloatingService.ARGUMENT_COOL_DOWN_TIMER_COLOR_VALUE, coolDownTimerColor.value.toString())

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        this.startForegroundService(intent)
    } else {
        this.startService(intent)
    }
}

fun Context.closeOverlayWindowWithFloatingService() {
    val intent = Intent(this, FloatingService::class.java)
    intent.putExtra(FloatingService.COMMAND_NAME, FloatingServiceCommand.CLOSE.name)
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

fun Context.startMainActivity() {
    startActivity(MainActivity::class)
}

fun Context.startActivity(kclass: KClass<out Activity>) {
    startActivity(
        Intent(this, kclass.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    )
}

fun Context.showToastMessage(message:String){
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Activity.restartApp() {
    val intent = this.packageManager.getLaunchIntentForPackage(this.packageName)!!
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    this.finish()
    startActivity(intent)
}