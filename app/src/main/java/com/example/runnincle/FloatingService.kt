package com.example.runnincle

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.core.app.NotificationCompat


class FloatingService: Service(){
    companion object {
        const val NOTIFICATION_ID = 10
        const val INTENT_COMMAND = "com.example.runnincle.COMMAND"
        const val INTENT_COMMAND_CLOSE = "CLOSE"
        const val INTENT_COMMAND_OPEN = "OPEN"
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        setNotificationChannel()
        setTheme(R.style.Theme_Runnincle)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val command = intent.getStringExtra(INTENT_COMMAND)

        if (command == INTENT_COMMAND_CLOSE) {
            stopService()
            return START_NOT_STICKY // 강제종료시 시스템이 서비스를 재시작시키지 않음
        }

        if (command == INTENT_COMMAND_OPEN) {
            if (drawOverOtherAppsEnabled()) {
                val window = IntervalOverlayWindow(this)
                if(!window.isServiceRunning()) {
                    window.showOverlay()
                } else {
                    Toast.makeText(this, "이미 실행중이에요", Toast.LENGTH_SHORT).show()
                }
            } else {
                startPermissionActivity()
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