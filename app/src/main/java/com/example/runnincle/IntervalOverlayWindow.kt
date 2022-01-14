package com.example.runnincle

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.WindowManager
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.AndroidUiDispatcher
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.compositionContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.*
import androidx.savedstate.ViewTreeSavedStateRegistryOwner
import com.example.runnincle.FloatingService.Companion.INTENT_COMMAND_CLOSE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class IntervalOverlayWindow (private val context: Context){

    companion object {
        private var isServiceRunning = false
    }

    private lateinit var windowManager: WindowManager
    private lateinit var composeView: ComposeView
    private lateinit var viewParams: WindowManager.LayoutParams
    private lateinit var handler: Handler

    private var remainingTime by mutableStateOf(20)

    init {
        initWindowManager()
        initViewParams()
        initComposeView()
        initHandler()
    }

    private fun initWindowManager() {
        windowManager = context.getSystemService(Service.WINDOW_SERVICE) as WindowManager
    }

    private fun initViewParams() {
        viewParams = WindowManager.LayoutParams(
            0,
            0,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // 오레오 이상인 경우 TYPE_APPLICATION_OVERLAY 로 설정
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
            },
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
            PixelFormat.TRANSLUCENT
        )
        calculateSizeAndPosition(viewParams, 100, 100)
    }

    private fun calculateSizeAndPosition(
        params: WindowManager.LayoutParams,
        widthInDp: Int,
        heightInDp: Int
    ) {
        val dm = getCurrentDisplayMetrics()
        params.gravity = Gravity.TOP or Gravity.RIGHT
        params.width = (widthInDp * dm.density).toInt()
        params.height = (heightInDp * dm.density).toInt()
        println(params.width)
        println(params.height)
    }

    private fun getCurrentDisplayMetrics(): DisplayMetrics {
        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)
        return dm
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initComposeView() {
        composeView = ComposeView(context)
        composeView.filterTouchesWhenObscured = true
        composeView.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> Log.d("test", "touch DOWN ")
                MotionEvent.ACTION_UP -> Log.d("test", "touch UP")
                MotionEvent.ACTION_MOVE -> Log.d("test", "touch move ")
            }
            false
        }
        val setTime = remainingTime
        composeView.setContent {
            SetComposeViewContent(80.dp, setTime, remainingTime, onCloseBtnClicked = { removeView() })
        }
    }

    private fun initHandler() {
        handler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                if(!isServiceRunning) return
                println("remainingTime: $remainingTime")
                if (remainingTime < 0) {
                    println("[타이머 종료]")
                    return
                } else {
                    sendEmptyMessageDelayed(0, 1000)
                }
                remainingTime -= 1
                windowManager.updateViewLayout(composeView, viewParams)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun showOverlay() {
        isServiceRunning = true
        setLifecycleOwner()
        setRecomposer()
        windowManager.addView(composeView, viewParams)
        handler.sendEmptyMessageDelayed(0, 1000)
    }

    private fun setLifecycleOwner() {
        val viewModelStore = ViewModelStore()
        val lifecycleOwner = MyLifecycleOwner()
        lifecycleOwner.performRestore(null)
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        ViewTreeLifecycleOwner.set(composeView, lifecycleOwner)
        ViewTreeViewModelStoreOwner.set(composeView) { viewModelStore }
        ViewTreeSavedStateRegistryOwner.set(composeView, lifecycleOwner)
    }

    // Compose UI 업데이트를 위한 Recomposer 설정
    private fun setRecomposer() {
        val coroutineContext = AndroidUiDispatcher.CurrentThread
        val runRecomposeScope = CoroutineScope(coroutineContext)
        val recomposer = Recomposer(coroutineContext)
        composeView.compositionContext = recomposer
        runRecomposeScope.launch {
            recomposer.runRecomposeAndApplyChanges()
        }
    }

    private fun removeView() {
        isServiceRunning = false
        windowManager.removeView(composeView)
        context.startFloatingServiceWithCommand(INTENT_COMMAND_CLOSE)
    }

    fun isServiceRunning(): Boolean {
        return isServiceRunning
    }

}

@Composable
private fun SetComposeViewContent(
    sizeDp: Dp,
    totalTime: Int,
    remainingTime: Int,
    onCloseBtnClicked: ()-> Unit
) {
    Column(
        modifier = Modifier.size(sizeDp).wrapContentSize(Center),
        horizontalAlignment = CenterHorizontally
    ) {
        val remainingText = if (remainingTime < 0) {
            "FIN"
        } else {
            remainingTime.toString()
        }
        val animatedProgress = animateFloatAsState(
            targetValue = if (remainingTime <= 0) 1f else { 1 - remainingTime.toFloat().div(totalTime) },
            animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
        ).value

        Box {
            CircularProgressIndicator(
                progress = animatedProgress,
                modifier = Modifier.size(sizeDp.times(0.8f)),
                strokeWidth = 10.dp
            )
            Text(text = remainingText, fontSize = 20.sp, modifier = Modifier.align(Center))
        }
        LinearProgressIndicator(progress = 0.5f, modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp))
        OutlinedButton(onClick = { onCloseBtnClicked() }) {
            Text(text = "끝내기")
        }
    }
}

@Preview
@Composable
private fun UITest() {
    SetComposeViewContent(80.dp, totalTime = 60, remainingTime = 40, onCloseBtnClicked = {})
}
