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
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.AndroidUiDispatcher
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.compositionContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.*
import androidx.savedstate.ViewTreeSavedStateRegistryOwner
import com.example.runnincle.FloatingService.Companion.INTENT_COMMAND_CLOSE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

enum class ScheduleType {
    WARMING_UP, SET_BOOST, SET_COOL_DOWN, COOL_DOWN
}

data class ScheduleData(
    val setTime: Int,
    val type: ScheduleType
)

class IntervalOverlayWindow (
    private val context: Context,
    intervalProgram: IntervalProgram
    ) {

    companion object {
        private var isServiceRunning = false
    }

    private lateinit var windowManager: WindowManager
    private lateinit var composeView: ComposeView
    private lateinit var viewParams: WindowManager.LayoutParams
    private lateinit var handler: Handler

    private var schedule: List<ScheduleData> = setSchedule(intervalProgram)
    private var currentScheduleType by mutableStateOf(ScheduleType.WARMING_UP)
    private var currentIndex = 0
    private var setTime by mutableStateOf(0)
    private var remainingSetTime by mutableStateOf(0)
    private var totalProgressTime = 0
    private var progressTime by mutableStateOf(0)

    init {
        initWindowManager()
        initViewParams()
        initVariableTimeValue()
        initComposeView()
        initHandler()
    }

    private fun initVariableTimeValue() {
        if (schedule.isNotEmpty()) {
            remainingSetTime = schedule[currentIndex].setTime
            setTime = remainingSetTime
            schedule.forEach { scheduleData ->
                totalProgressTime += scheduleData.setTime
                println("totalProgressTime = $totalProgressTime")
            }
        } else {
            //TODO schedule 이 비었을 경우 예외처리
        }
    }

    private fun setSchedule(intervalProgram: IntervalProgram): List<ScheduleData> {
        val schedule = mutableListOf<ScheduleData>()
        if (intervalProgram.warmingUp > 0) {
            schedule.add(ScheduleData(intervalProgram.warmingUp, ScheduleType.WARMING_UP))
        }
        if (intervalProgram.retryTime > 0) {
            for ( i in 1..intervalProgram.retryTime) {
                if (intervalProgram.setBoost > 0) {
                    schedule.add(ScheduleData(intervalProgram.setBoost, ScheduleType.SET_BOOST))
                }
                if (intervalProgram.setCoolDown > 0) {
                    // 마지막 쿨다운 생략일 경우 스케줄에 추가하지 않음
                    if (intervalProgram.isSkipLastSetCoolDown && i == intervalProgram.retryTime) {
                        break
                    }
                    schedule.add(ScheduleData(intervalProgram.setCoolDown, ScheduleType.SET_COOL_DOWN))
                }
            }
        }
        if (intervalProgram.coolDown > 0) {
            schedule.add(ScheduleData(intervalProgram.coolDown, ScheduleType.COOL_DOWN))
        }
        return schedule
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
        composeView.setContent {
            IntervalOverlayComposeView(
                sizeDp = 80.dp,
                currentScheduleType = currentScheduleType,
                totalSetTime = setTime,
                remainingSetTime = remainingSetTime,
                progressTime = progressTime,
                totalProgressTime = totalProgressTime,
                onCloseBtnClicked = { removeView() }
            )
        }
    }

    private fun initHandler() {
        handler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                if(!isServiceRunning) return
                println("remainingTime: $remainingSetTime")
                if (remainingSetTime <= 0) {
                    if(currentIndex < schedule.lastIndex) { // 프로그램이 끝나지 않은 경우
                        currentIndex += 1
                        remainingSetTime = schedule[currentIndex].setTime
                        setTime = schedule[currentIndex].setTime
                        currentScheduleType = schedule[currentIndex].type
                        sendEmptyMessageDelayed(0, 1000)
                    } else { // 프로그램이 완전히 끝난 경우
                        return
                    }
                } else {
                    remainingSetTime -= 1
                    sendEmptyMessageDelayed(0, 1000)
                }
                progressTime += 1
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
private fun IntervalOverlayComposeView(
    sizeDp: Dp,
    currentScheduleType: ScheduleType,
    totalSetTime: Int,
    remainingSetTime: Int,
    totalProgressTime: Int,
    progressTime: Int,
    onCloseBtnClicked: ()-> Unit
) {
    val setTimeProgressBarColor by animateColorAsState(
        when(currentScheduleType) {
            ScheduleType.WARMING_UP -> Color.LightGray
            ScheduleType.SET_BOOST -> Color.Red
            ScheduleType.SET_COOL_DOWN -> Color.Blue
            ScheduleType.COOL_DOWN -> Color.LightGray
        }
    )

    Column(
        modifier = Modifier
            .size(sizeDp)
            .wrapContentSize(Center),
        horizontalAlignment = CenterHorizontally
    ) {
        val animatedCircularProgress = animateFloatAsState(
            targetValue = if (remainingSetTime <= 0) 1f else { 1 - remainingSetTime.toFloat().div(totalSetTime) },
            animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
        ).value

        Box {
            CircularProgressIndicator(
                progress = animatedCircularProgress,
                modifier = Modifier.size(sizeDp.times(0.8f)),
                color = setTimeProgressBarColor,
                strokeWidth = 10.dp
            )
            Text(
                text = remainingSetTime.toString(),
                fontSize = 20.sp,
                modifier = Modifier.align(Center),
                style = TextStyle(
                    color = Color.White,
                    shadow = Shadow(
                        color = Color.Black,
                        offset = Offset(0f, 0f),
                        blurRadius = 2f
                    ),
                )
            )
        }

        val animatedLinearProgress = animateFloatAsState(
            targetValue = if (progressTime >= totalProgressTime) {
                1f
            } else {
                progressTime.toFloat().div(totalProgressTime)
            },
            animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
        ).value

        LinearProgressIndicator(
            progress = animatedLinearProgress,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        )
        OutlinedButton(onClick = { onCloseBtnClicked() }) {
            Text(text = "끝내기")
        }
    }
}

@Preview
@Composable
private fun UITest() {
    IntervalOverlayComposeView(
        sizeDp = 80.dp,
        currentScheduleType = ScheduleType.SET_COOL_DOWN,
        totalSetTime = 60,
        remainingSetTime = 40,
        totalProgressTime = 100,
        progressTime = 10,
        onCloseBtnClicked = {}
    )
}
