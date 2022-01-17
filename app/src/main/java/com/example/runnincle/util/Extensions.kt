package com.example.runnincle

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import com.example.runnincle.util.FloatingService.Companion.INTENT_COMMAND
import com.example.runnincle.util.FloatingService.Companion.INTENT_COMMAND_OPEN
import com.example.runnincle.util.FloatingService.Companion.INTENT_INTERVAL_PROGRAM
import com.example.runnincle.domain.model.IntervalProgram
import com.example.runnincle.framework.presentation.PermissionActivity
import com.example.runnincle.util.FloatingService
import kotlin.reflect.KClass

fun Context.startFloatingServiceWithCommand(
    command: String = "",
    intervalProgram: IntervalProgram? = null
) {
    val intent = Intent(this, FloatingService::class.java)
    when (command) {
        "" -> return
        INTENT_COMMAND_OPEN -> {
            if (intervalProgram != null) {
                intent.putExtra(INTENT_INTERVAL_PROGRAM, intervalProgram)
                intent.putExtra(INTENT_COMMAND, command)
            } else {
                Toast.makeText(this, "인터벌 시간 설정이 올바르지 않습니다.", Toast.LENGTH_SHORT).show()
            }
        }
        else -> {
            intent.putExtra(INTENT_COMMAND, command)
        }
    }
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