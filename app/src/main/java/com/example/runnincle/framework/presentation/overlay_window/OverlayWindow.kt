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
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.progressSemantics
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.fontResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.*
import androidx.savedstate.ViewTreeSavedStateRegistryOwner
import com.example.runnincle.business.domain.model.Program
import com.example.runnincle.business.domain.model.Workout
import com.example.runnincle.ui.theme.infinitySansFamily
import com.example.runnincle.util.FloatingServiceCommand
import com.example.runnincle.util.MyLifecycleOwner
import com.siddroid.holi.colors.MaterialColor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

enum class ScheduleType {
    WORK, COOL_DOWN
}

data class ScheduleData(
    val workoutName: String,
    val time: Int,
    val type: ScheduleType,
    val timerColor: Color,
)

@RequiresApi(Build.VERSION_CODES.O)
class OverlayWindow (
    private val context: Context,
    program: Program,
    workouts: List<Workout>
    ) {

    companion object {
        private var isServiceRunning = false
    }

    private lateinit var windowManager: WindowManager
    private lateinit var composeView: ComposeView
    private lateinit var viewParams: WindowManager.LayoutParams
    private lateinit var handler: Handler

    private var schedule: List<ScheduleData> = setSchedule(workouts)
    private var currentIndex = 0
    private var originalTimeOfCurrentWork by mutableStateOf(0)
    private var remainingTimeOfCurrentWork by mutableStateOf(0)
    private var originalTotalWorkTime = 0
    private var remainingTotalWorkTime by mutableStateOf(0)

    private var currentTimerColor = MaterialColor.GREY_200

    init {
        val size = 120
        initWindowManager()
        initViewParams(size)
        initVariableTimeValue()
        initComposeView(size)
        initHandler()
    }

    private fun initVariableTimeValue() {
        if (schedule.isNotEmpty()) {
            remainingTimeOfCurrentWork = schedule[currentIndex].time
            originalTimeOfCurrentWork = remainingTimeOfCurrentWork
            currentTimerColor = schedule[currentIndex].timerColor
            schedule.forEach { scheduleData ->
                originalTotalWorkTime += scheduleData.time
                println("debug totalWorkTime = $originalTotalWorkTime")
            }
        } else {
            //TODO schedule 이 비었을 경우 예외처리
        }
    }

    private fun setSchedule(workouts: List<Workout>): List<ScheduleData> {
        val schedules = mutableListOf<ScheduleData>()

        workouts.forEach { workout ->
            for (index in 1..workout.set) {
                val newWorkSchedule = ScheduleData(
                    workoutName = workout.name,
                    time = workout.work,
                    type = ScheduleType.WORK,
                    timerColor = workout.timerColor
                )
                schedules.add(newWorkSchedule)
                if (workout.coolDown > 0) {
                    if(workout.isSkipLastCoolDown && index == workout.set) {
                        break
                    }
                    val newCoolDownSchedule = ScheduleData(
                        workoutName = workout.name,
                        time = workout.coolDown,
                        type = ScheduleType.COOL_DOWN,
                        timerColor = MaterialColor.GREY_400
                    )
                    schedules.add(newCoolDownSchedule)
                }
            }
        }
        return schedules
    }

    private fun initWindowManager() {
        windowManager = context.getSystemService(Service.WINDOW_SERVICE) as WindowManager
    }

    private fun initViewParams(size: Int) {
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
        calculateSizeAndPosition(viewParams, size, size)
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

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ClickableViewAccessibility")
    private fun initComposeView(size: Int) {
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
            OverlayComposeView(
                sizeDp = size.dp,
                workoutName = schedule[currentIndex].workoutName,
                currentTimerColor = currentTimerColor,
                originalTimeOfCurrentWork = originalTimeOfCurrentWork,
                remainingTimeOfCurrentWork = remainingTimeOfCurrentWork,
                remainingTotalWorkTime = remainingTotalWorkTime,
                originalTotalWorkTime = originalTotalWorkTime,
                onCloseBtnClicked = { removeView() }
            )
        }
    }

    private fun initHandler() {
        handler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                if (!isServiceRunning) return
                if (remainingTimeOfCurrentWork <= 0) {
                    if(currentIndex < schedule.lastIndex) { // 프로그램이 끝나지 않은 경우
                        currentIndex += 1
                        remainingTimeOfCurrentWork = schedule[currentIndex].time
                        originalTimeOfCurrentWork = schedule[currentIndex].time
                        currentTimerColor = schedule[currentIndex].timerColor
                        sendEmptyMessageDelayed(0, 1000)
                    } else { // 프로그램이 완전히 끝난 경우
                        return
                    }
                } else {
                    remainingTimeOfCurrentWork -= 1
                    sendEmptyMessageDelayed(0, 1000)
                }
                remainingTotalWorkTime += 1
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
        context.startFloatingServiceWithCommand(FloatingServiceCommand.CLOSE)
    }

    fun isServiceRunning(): Boolean {
        return isServiceRunning
    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun OverlayComposeView(
    sizeDp: Dp,
    workoutName: String,
    currentTimerColor: Color,
    originalTimeOfCurrentWork: Int,
    remainingTimeOfCurrentWork: Int,
    originalTotalWorkTime: Int,
    remainingTotalWorkTime: Int,
    onCloseBtnClicked: ()-> Unit
) {
    val setTimeProgressBarColor by animateColorAsState(currentTimerColor)

    Column(
        modifier = Modifier
            .size(sizeDp),
        horizontalAlignment = CenterHorizontally
    ) {
        val animatedCurrentWorkProgress = animateFloatAsState(
            targetValue = if (remainingTimeOfCurrentWork <= 0) {
                1f
            } else {
                1 - remainingTimeOfCurrentWork.toFloat().div(originalTimeOfCurrentWork)
            },
            animationSpec = tween(durationMillis = 1000, easing = LinearEasing)
        ).value

        val animatedTotalWorkProgress = animateFloatAsState(
            targetValue = if (remainingTotalWorkTime >= originalTotalWorkTime) {
                1f
            } else {
                remainingTotalWorkTime.toFloat().div(originalTotalWorkTime)
            },
            animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
        ).value

        Box(
            contentAlignment = Center
        ) {
            val strokeWith = sizeDp.times(0.065f)
            CircularProgressIndicator(
                progress = animatedCurrentWorkProgress,
                modifier = Modifier
                    .size(sizeDp.times(0.8f))
                    .align(Center)
                    .graphicsLayer {
                        if (remainingTimeOfCurrentWork == originalTimeOfCurrentWork) {
                            rotationY = 180f
                        }
                    },
                color = setTimeProgressBarColor,
                strokeWidth = strokeWith
            )
            CircularProgressIndicator(
                progress = animatedTotalWorkProgress,
                modifier = Modifier
                    .size(sizeDp.times(0.67f))
                    .align(Center),
                color = MaterialColor.PURPLE_200,
                strokeWidth = strokeWith
            )
            OutlineText(
                modifier = Modifier
                    .width(sizeDp.times(0.5f))
                    .height(sizeDp.times(0.2f))
                    .align(Center),
                text = remainingTimeOfCurrentWork.toTimeClock(),
                fontSize = sizeDp.times(0.5f).value,
                fontWeight = FontWeight.Bold
            )
        }
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = sizeDp.times(0.05f)),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                modifier = Modifier.size(sizeDp.times(0.18f)),
                onClick = {

                },
            ) {
                Icon (
                    painter = painterResource(R.drawable.ic_baseline_play),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .size(sizeDp.times(0.18f))
                        .clip(RoundedCornerShape(sizeDp.times(0.18f)))
                        .background(Color.Black)
                )
            }
            val workNameTextSize = sizeDp.value.times(0.1f).dp()
            Text(
                text = workoutName,
                style = TextStyle(
                    color = Color.White,
                    fontSize = workNameTextSize,
                    fontFamily = infinitySansFamily,
                    shadow = Shadow(
                        color = Color.DarkGray,
                        offset = Offset(0f, 0f),
                        blurRadius = 1f
                    ),
                ),
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.width(sizeDp.times(0.5f))
            )
//            OutlineText(
//                modifier = Modifier.wrapContentSize(),
//                text = workoutName,
//                fontSize = sizeDp.times(0.3f).value,
//            )
            IconButton(
                modifier = Modifier.size(sizeDp.times(0.18f)),
                onClick = {
                    onCloseBtnClicked()
                },
            ) {
                Icon (
                    painter = painterResource(R.drawable.ic_baseline_exit),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .size(sizeDp.times(0.18f))
                        .clip(RoundedCornerShape(sizeDp.times(0.03f)))
                        .background(Color.Black)
                )
            }
        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OutlineText(
    modifier: Modifier,
    text: String,
    fontSize: Float,
    fontWeight: FontWeight = FontWeight.Normal
) {
    val typeFace = if(fontWeight == FontWeight.Bold) {
        LocalContext.current.resources.getFont(R.font.infinity_sans_cond_bold)
    } else {
        LocalContext.current.resources.getFont(R.font.infinity_sans_regular)
    }

    val textPaintStroke = Paint().asFrameworkPaint().apply {
        isAntiAlias = true
        style = android.graphics.Paint.Style.STROKE
        textSize = fontSize
        textAlign = android.graphics.Paint.Align.CENTER
        color = android.graphics.Color.BLACK
        strokeWidth = fontSize/6
        strokeMiter= 10f
        strokeJoin = android.graphics.Paint.Join.ROUND
        typeface = typeFace
    }

    val textPaint = Paint().asFrameworkPaint().apply {
        isAntiAlias = true
        textAlign = android.graphics.Paint.Align.CENTER
        style = android.graphics.Paint.Style.FILL
        textSize = fontSize
        color = android.graphics.Color.WHITE
        typeface = typeFace
    }

    Canvas(
        modifier = modifier,
        onDraw = {
            drawIntoCanvas {
                it.nativeCanvas.drawText(
                    text,
                    center.x,
                    center.y + fontSize/3,
                    textPaintStroke
                )
                it.nativeCanvas.drawText(
                    text,
                    center.x,
                    center.y + fontSize/3,
                    textPaint
                )
            }
        }
    )
}

@Composable
fun CircularProgressIndicator(
    /*@FloatRange(from = 0.0, to = 1.0)*/
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colors.primary,
    strokeWidth: Dp = ProgressIndicatorDefaults.StrokeWidth
) {
    val stroke = with(LocalDensity.current) {
        Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
    }
    Canvas(
        modifier
            .progressSemantics(progress)
            .size(40.dp)
    ) {
        // Start at 12 O'clock
        val startAngle = 270f
        val sweep = progress * 360f
        val diameterOffset = stroke.width / 2
        val arcDimen = size.width - 2 * diameterOffset
        drawArc(
            color = color,
            startAngle = startAngle,
            sweepAngle = sweep,
            useCenter = false,
            topLeft = Offset(diameterOffset, diameterOffset),
            size = Size(arcDimen, arcDimen),
            style = stroke
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
private fun UITest() {
    OverlayComposeView(
        sizeDp =200.dp,
        workoutName = "자전거자전거자전거",
        currentTimerColor = Color.Blue,
        originalTimeOfCurrentWork = 60,
        remainingTimeOfCurrentWork =10,
        originalTotalWorkTime = 100,
        remainingTotalWorkTime = 90,
        onCloseBtnClicked = {}
    )
}
