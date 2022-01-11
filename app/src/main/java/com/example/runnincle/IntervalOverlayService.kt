package com.example.runnincle

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import android.view.WindowManager.LayoutParams
import androidx.compose.material.Text
import androidx.compose.runtime.Recomposer
import androidx.compose.ui.platform.AndroidUiDispatcher
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.compositionContext
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.lifecycle.ViewTreeViewModelStoreOwner
import androidx.savedstate.ViewTreeSavedStateRegistryOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


class IntervalOverlayService: Service(){

    companion object {
        const val NOTIFICATION_ID = 10
    }

    val windowManager: WindowManager
        get() = getSystemService(WINDOW_SERVICE) as WindowManager

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        setNotificationChannel()
        setTheme(R.style.Theme_Runnincle)
        showOverlay()
    }

    private fun showOverlay() {
        // 오레오 이상인 경우 TYPE_APPLICATION_OVERLAY 로 설정
        val flagApplicationOverlay = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            LayoutParams.TYPE_SYSTEM_ALERT
        }
        val params = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT,
            flagApplicationOverlay,
            LayoutParams.FLAG_NOT_FOCUSABLE or LayoutParams.FLAG_NOT_TOUCH_MODAL or LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.LEFT or Gravity.CENTER_VERTICAL // 위치설정

        val composeView = ComposeView(this)
        composeView.setContent {
            Text(text = "Hello")
        }

        val viewModelStore = ViewModelStore()
        val lifecycleOwner = MyLifecycleOwner()
        lifecycleOwner.performRestore(null)
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        ViewTreeLifecycleOwner.set(composeView, lifecycleOwner)
        ViewTreeViewModelStoreOwner.set(composeView) { viewModelStore }
        ViewTreeSavedStateRegistryOwner.set(composeView, lifecycleOwner)

        windowManager.addView(composeView, params)

        //??
        val coroutineContext = AndroidUiDispatcher.CurrentThread
        val runRecomposeScope = CoroutineScope(coroutineContext)
        val recomposer = Recomposer(coroutineContext)
        composeView.compositionContext = recomposer
        runRecomposeScope.launch {
            recomposer.runRecomposeAndApplyChanges()
        }
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

    private fun stopService() {
        stopForeground(true)
        stopSelf()
    }

}