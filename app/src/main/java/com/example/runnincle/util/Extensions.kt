package com.example.runnincle

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import com.example.runnincle.util.FloatingService.Companion.INTENT_COMMAND
import com.example.runnincle.util.FloatingService.Companion.INTENT_COMMAND_OPEN
import com.example.runnincle.util.FloatingService.Companion.INTENT_INTERVAL_PROGRAM
import com.example.runnincle.business.domain.model.IntervalProgram
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

fun Int.toTimeClock(context: Context): String {
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
                "$min:0$sec"
            } else {
                "$min:$sec"
            }
        } else {
            "$min:00"
        }
    }
}

fun Context.showToastMessage(message:String){
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}