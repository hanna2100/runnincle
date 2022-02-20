package com.example.runnincle.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import androidx.core.app.NotificationCompat
import com.example.runnincle.OverlayWindow
import com.example.runnincle.R
import com.example.runnincle.business.domain.model.ParcelableWorkout
import com.example.runnincle.business.domain.model.Program
import com.example.runnincle.business.domain.model.Workout
import com.example.runnincle.business.domain.model.Workout.Companion.toWorkout
import com.example.runnincle.drawOverOtherAppsEnabled
import com.example.runnincle.startPermissionActivity

enum class FloatingServiceCommand {
    CLOSE, OPEN
}

class FloatingService: Service() {
    companion object {
        const val NOTIFICATION_ID = 10
        const val COMMAND_NAME = "COMMAND"
        const val INTENT_PROGRAM = "INTENT_PROGRAM"
        const val INTENT_OVERLAY_DP = "INTENT_OVERLAY_DP"
        const val INTENT_TOTAL_TIMER_COLOR_VALUE = "INTENT_TOTAL_TIMER_COLOR_VALUE"
        const val INTENT_COOL_DOWN_TIMER_COLOR_VALUE = "INTENT_COOL_DOWN_TIMER_COLOR_VALUE"
        const val INTENT_IS_TTS_USED = "INTENT_IS_TTS_USED"
        const val INTENT_WORKOUTS = "INTENT_WORKOUTS"
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        setNotificationChannel()
        setTheme(R.style.Theme_Runnincle)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val command = intent.getStringExtra(COMMAND_NAME)

        if (command == FloatingServiceCommand.CLOSE.name) {
            stopService()
            return START_NOT_STICKY // 강제종료시 시스템이 서비스를 재시작시키지 않음
        }

        if (command == FloatingServiceCommand.OPEN.name) {
            if (drawOverOtherAppsEnabled()) {
                val program = intent.getParcelableExtra<Program>(INTENT_PROGRAM)
                val parcelableWorkouts = intent.getParcelableArrayListExtra<ParcelableWorkout>(INTENT_WORKOUTS)
                val overlayDp = intent.getIntExtra(INTENT_OVERLAY_DP, 100)
                val totalTimerColor = intent.getStringExtra(INTENT_TOTAL_TIMER_COLOR_VALUE)
                val coolDownTimerColor = intent.getStringExtra(INTENT_COOL_DOWN_TIMER_COLOR_VALUE)
                val isTTSUsed = intent.getBooleanExtra(INTENT_IS_TTS_USED, false)

                if(program == null
                    || parcelableWorkouts == null
                    || totalTimerColor == null
                    || coolDownTimerColor == null
                ) {
                    stopService()
                    //TODO 예외처리
                    return START_STICKY
                }

                val workouts = mutableListOf<Workout>()
                parcelableWorkouts.forEach {
                    workouts.add(it.toWorkout())
                }

                val window = OverlayWindow(
                    context = this,
                    program = program,
                    workouts = workouts,
                    overlayDp = overlayDp,
                    totalTimerColor = Color(totalTimerColor.toULong()),
                    coolDownTimerColor = Color(coolDownTimerColor.toULong()),
                    isTTSUsed = isTTSUsed,
                )
                if(!window.isServiceRunning()) {
                    window.showOverlay()
                } else {
                    Toast.makeText(this, "이미 실행중이에요", Toast.LENGTH_SHORT).show()
                }
            } else {
                println("debug startPermissionActivity")
                startPermissionActivity()
                stopService()
            }
        }

        return START_STICKY
    }

    private fun stopService() {
        stopForeground(true)
        stopSelf()
    }

    private fun setNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // 오레오 이상일 경우 foreground service 실행
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channelId = getString(R.string.notification_channel_id)
            val channelTitle = getString(R.string.app_name)
            var channel = notificationManager.getNotificationChannel(channelId)
            if (channel == null) {
                channel = NotificationChannel(channelId, channelTitle, NotificationManager.IMPORTANCE_HIGH)
                notificationManager.createNotificationChannel(channel)
            }

            val notification = NotificationCompat.Builder(this, channelId).build()
            startForeground(NOTIFICATION_ID, notification)
        }
    }
}