package com.devhanna91.runnincle

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.DisplayMetrics
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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.progressSemantics
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.TopEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.lifecycle.ViewTreeViewModelStoreOwner
import androidx.savedstate.ViewTreeSavedStateRegistryOwner
import com.devhanna91.runnincle.business.domain.model.Program
import com.devhanna91.runnincle.business.domain.model.Workout
import com.devhanna91.runnincle.framework.presentation.composable.sp
import com.devhanna91.runnincle.ui.theme.NanumSquareFamily
import com.devhanna91.runnincle.ui.theme.TimerColorPalette
import com.devhanna91.runnincle.util.MyLifecycleOwner
import com.devhanna91.runnincle.util.SoundPlayer
import com.devhanna91.runnincle.util.closeOverlayWindowWithFloatingService
import com.siddroid.holi.colors.MaterialColor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


enum class ScheduleType {
    WORK, COOL_DOWN
}

enum class OverlayWindowStatus {
    PLAY, PAUSE, END
}

data class ScheduleData(
    val workoutName: String,
    val set: Int,
    val time: Int,
    val type: ScheduleType,
    val timerColor: Color,
)

@RequiresApi(Build.VERSION_CODES.O)
class OverlayWindow (
    private val context: Context,
    program: Program,
    workouts: List<Workout>,
    overlayDp: Int,
    totalTimerColor: Color,
    coolDownTimerColor: Color,
    isTTSUsed: Boolean
    ) {

    companion object {
        private var isServiceRunning = false
    }

    private lateinit var windowManager: WindowManager
    private lateinit var composeView: ComposeView
    private lateinit var viewParams: WindowManager.LayoutParams
    private var prevX = 0f
    private var prevY = 0f
    private lateinit var handler: Handler
    private var overlayStatus by mutableStateOf(OverlayWindowStatus.PAUSE)

    private var schedule: List<ScheduleData> = setSchedule(workouts, coolDownTimerColor)
    private var currentIndex = 0
    private var originalTotalWorkTime = 0
    private var currentSetIndex by mutableStateOf(1)
    private var remainingTimeOfCurrentWork by mutableStateOf(0)
    private var progressedTotalWorkTime by mutableStateOf(0)

    init {
        initWindowManager()
        initViewParams(overlayDp)
        initVariableTimeValue()
        initSoundPlayer(context)
        initComposeView(overlayDp, totalTimerColor)
        initHandler()
    }

    private fun initSoundPlayer(context: Context) {
        SoundPlayer.initSounds(context)
    }

    private fun initVariableTimeValue() {
        if (schedule.isNotEmpty()) {
            remainingTimeOfCurrentWork = schedule[currentIndex].time
            schedule.forEach { scheduleData ->
                originalTotalWorkTime += scheduleData.time
            }
        }
    }

    private fun setSchedule(workouts: List<Workout>, coolDownTimerColor: Color): List<ScheduleData> {
        val schedules = mutableListOf<ScheduleData>()

        workouts.forEach { workout ->
            for (index in 1..workout.set) {
                val newWorkSchedule = ScheduleData(
                    workoutName = workout.name,
                    time = workout.work,
                    type = ScheduleType.WORK,
                    timerColor = workout.timerColor,
                    set = workout.set
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
                        timerColor = coolDownTimerColor,
                        set = workout.set
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

    private fun initViewParams(overlaySize: Int) {
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
        calculateSizeAndPosition(viewParams, overlaySize)
    }

    private fun calculateSizeAndPosition(
        params: WindowManager.LayoutParams,
        overlaySize: Int
    ) {
        val dm = getCurrentDisplayMetrics()
        params.gravity = Gravity.TOP or Gravity.RIGHT
        params.width = (overlaySize * dm.density).toInt()
        params.height = (overlaySize * dm.density).toInt()
    }

    private fun getCurrentDisplayMetrics(): DisplayMetrics {
        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)
        return dm
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ClickableViewAccessibility")
    private fun initComposeView(
        overlayDp: Int,
        totalTimerColor: Color
    ) {
        composeView = ComposeView(context)
        composeView.filterTouchesWhenObscured = true
        composeView.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN ->{
                    prevX = motionEvent.rawX
                    prevY = motionEvent.rawY
                }
                MotionEvent.ACTION_MOVE -> {
                    val rawX = motionEvent.rawX
                    val rawY = motionEvent.rawY

                    val x = prevX - rawX
                    val y = rawY - prevY

                    viewParams.x += x.toInt()
                    viewParams.y += y.toInt()

                    prevX = rawX
                    prevY = rawY

                    windowManager.updateViewLayout(view, viewParams)

                }
            }
            false
        }
        composeView.setContent {
            OverlayComposeView(
                sizeDp = overlayDp.dp,
                scheduleData = schedule[currentIndex],
                totalTimerColor = totalTimerColor,
                currentSetIndex = currentSetIndex,
                remainingTimeOfCurrentWork = remainingTimeOfCurrentWork,
                progressedTotalWorkTime = progressedTotalWorkTime,
                originalTotalWorkTime = originalTotalWorkTime,
                overlayStatus = overlayStatus,
                onCloseBtnClicked = {
                    removeView()
                },
                onPlayBtnClick = { overlayWindowStatus ->
                    handler.removeMessages(0)
                    overlayStatus = overlayWindowStatus
                    if (overlayStatus == OverlayWindowStatus.PLAY) {
                        handler.sendEmptyMessageDelayed(0, 1000)
                    }
                }
            )
        }
    }

    private fun initHandler() {
        handler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                if (!isServiceRunning || overlayStatus != OverlayWindowStatus.PLAY) return

                // 현재 진행중인 work 의 시간이 남은경우
                if (remainingTimeOfCurrentWork > 0) {
                    remainingTimeOfCurrentWork -= 1
                    progressedTotalWorkTime += 1

                    // 2초 전일 경우 사운드음향 재생
                    if (remainingTimeOfCurrentWork <= 2) {
                       SoundPlayer.play(R.raw.count_sound)
                    }

                    sendEmptyMessageDelayed(0, 1000)

                } else { //  현재 진행중인 work 의 시간이 끝난 경우
                    if (currentIndex < schedule.lastIndex) { // 남은 work 가 있을 경우
                        // 시작 사운드음향 재생
                        SoundPlayer.play(R.raw.start_sound)

                        // set index 계산
                        val totalSet = schedule[currentIndex].set
                        val isNextScheduleWork = schedule[currentIndex + 1].type == ScheduleType.WORK
                        if (currentSetIndex == totalSet && isNextScheduleWork) {
                            currentSetIndex = 1
                        } else if (currentSetIndex < totalSet && isNextScheduleWork) {
                            currentSetIndex += 1
                        }
                        // schedule index 업데이트
                        currentIndex += 1
                        remainingTimeOfCurrentWork = schedule[currentIndex].time

                        sendEmptyMessageDelayed(0, 1000)
                    } else { // 남은 work 가 없을 경우 (프로그램 종료)
                        SoundPlayer.play(R.raw.end_sound)
                        overlayStatus = OverlayWindowStatus.END
                        return
                    }
                }
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
        handler.removeMessages(0)
        isServiceRunning = false
        context.closeOverlayWindowWithFloatingService()
        composeView.setContent {  } // composeView를 비워주지 않으면 돌아가던 애니메이션이 remove 된 view 를 찾지 못해서 크래쉬가 남
        windowManager.removeView(composeView)
    }

    fun isServiceRunning(): Boolean {
        return isServiceRunning
    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun OverlayComposeView(
    sizeDp: Dp,
    scheduleData: ScheduleData,
    currentSetIndex: Int,
    totalTimerColor: Color,
    remainingTimeOfCurrentWork: Int,
    originalTotalWorkTime: Int,
    progressedTotalWorkTime: Int,
    overlayStatus: OverlayWindowStatus,
    onCloseBtnClicked: ()-> Unit,
    onPlayBtnClick: (overlayStatus: OverlayWindowStatus) ->Unit
) {
    val animatedCurrentTimerColor by animateColorAsState(scheduleData.timerColor)

    Column(
        modifier = Modifier
            .width(sizeDp.times(1.05f))
            .height(sizeDp)
        ,
        horizontalAlignment = CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            contentAlignment = Center
        ) {
            IconButton(
                modifier = Modifier
                    .size(sizeDp.times(0.15f))
                    .align(TopEnd),
                onClick = {
                    onCloseBtnClicked()
                },
            ) {
                Icon (
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .size(sizeDp.times(0.15f))
                        .clip(RoundedCornerShape(sizeDp.times(0.06f)))
                        .background(Color.Black)
                )
            }
            TimerCircle(
                modifier = Modifier.align(BottomCenter),
                scheduleData = scheduleData,
                currentSetIndex = currentSetIndex,
                animatedCurrentTimerColor = animatedCurrentTimerColor,
                remainingTimeOfCurrentWork = remainingTimeOfCurrentWork,
                originalTotalWorkTime = originalTotalWorkTime,
                progressedTotalWorkTime = progressedTotalWorkTime,
                sizeDp = sizeDp.times(0.95f),
                overlayStatus = overlayStatus,
                totalTimerColor = totalTimerColor,
                onPlayBtnClick = onPlayBtnClick
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TimerCircle(
    modifier: Modifier,
    scheduleData: ScheduleData,
    currentSetIndex: Int,
    animatedCurrentTimerColor: Color,
    remainingTimeOfCurrentWork: Int,
    progressedTotalWorkTime: Int,
    originalTotalWorkTime: Int,
    sizeDp: Dp,
    totalTimerColor: Color,
    overlayStatus: OverlayWindowStatus,
    onPlayBtnClick: (overlayStatus: OverlayWindowStatus)->Unit,
) {
    var isPaused by remember { mutableStateOf(true) }

    val animatedCurrentWorkProgress = animateFloatAsState(
        targetValue = if (remainingTimeOfCurrentWork <= 0) {
            1f
        } else {
            1 - remainingTimeOfCurrentWork.toFloat().div(scheduleData.time)
        },
        animationSpec = tween(durationMillis = 1000, easing = LinearEasing)
    )

    val animatedTotalWorkProgress = animateFloatAsState(
        targetValue = if (progressedTotalWorkTime >= originalTotalWorkTime) {
            1f
        } else {
            progressedTotalWorkTime.toFloat().div(originalTotalWorkTime)
        },
        animationSpec = tween(durationMillis = 1000, easing = LinearEasing)
    )

    Box(modifier = modifier) {
        val strokeWith = sizeDp.times(0.07f)

        CircularProgressIndicator(
            progress = animatedCurrentWorkProgress.value,
            modifier = Modifier
                .size(sizeDp)
                .align(Center)
                .graphicsLayer {
                    if (remainingTimeOfCurrentWork == scheduleData.time) {
                        rotationY = 180f
                    }
                },
            color = animatedCurrentTimerColor,
            strokeWidth = strokeWith
        )

        CircularProgressIndicator(
            progress = animatedTotalWorkProgress.value,
            modifier = Modifier
                .size(sizeDp.times(0.86f))
                .align(Center),
            color = totalTimerColor,
            strokeWidth = strokeWith
        )

        Spacer(
            modifier = Modifier
                .size(sizeDp.times(0.72f))
                .align(Center)
                .clip(RoundedCornerShape(sizeDp.times(0.72f)))
                .background(Color.Black.copy(alpha = 0.1f))
            ,
        )

        val setTextSize = sizeDp.value.times(0.08f).sp()
        Text(
            text = if (overlayStatus != OverlayWindowStatus.END) {
                "$currentSetIndex / ${scheduleData.set}"
            } else {
                ""
            },
            style = TextStyle(
                color = Color.White,
                fontSize = setTextSize,
                fontFamily = NanumSquareFamily,
                shadow = Shadow(
                    color = Color.DarkGray,
                    offset = Offset(0f, 0f),
                    blurRadius = 1f
                ),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier
                .align(Center)
                .offset(y = -(sizeDp.times(0.18f)))
                .alpha(if (isPaused) 0f else 1f)
            ,
        )

        val timeTextSize = sizeDp.value.times(0.21f).sp()
        Text(
            text = if (overlayStatus == OverlayWindowStatus.END) {
                "END"
            } else {
                remainingTimeOfCurrentWork.toTimeClock()
            },
            style = TextStyle(
                color = Color.White,
                fontSize = timeTextSize,
                fontFamily = NanumSquareFamily,
                shadow = Shadow(
                    color = Color.DarkGray,
                    offset = Offset(0f, 0f),
                    blurRadius = 1f
                ),
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier
                .align(Center)
                .alpha(if (isPaused) 0f else 1f)
            ,
        )

        IconButton(
            modifier = Modifier
                .size(sizeDp.times(0.5f))
                .align(Center)
                .alpha(if (isPaused) 1f else 0f),
            onClick = {
                if (overlayStatus != OverlayWindowStatus.END) {
                    isPaused = !isPaused
                    val newStatus = if (isPaused) {
                        OverlayWindowStatus.PAUSE
                    } else {
                        OverlayWindowStatus.PLAY
                    }
                    onPlayBtnClick(newStatus)
                }
            },
        ) {
            Box(
                contentAlignment = Center
            ) {
                Spacer(modifier = Modifier
                    .size(sizeDp.times(0.45f))
                    .clip(RoundedCornerShape(sizeDp.times(0.5f)))
                    .background(
                        brush = Brush.linearGradient(listOf(Color.Black, MaterialColor.GREY_800)),
                        alpha = 0.3f
                    )
                )
                Icon (
                    painter = painterResource(R.drawable.ic_baseline_play),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .size(sizeDp.times(0.5f))
                )
            }
        }

        val workNameTextSize = sizeDp.value.times(0.08f).sp()

        Text(
            text = if (overlayStatus != OverlayWindowStatus.END) scheduleData.workoutName else "",
            style = TextStyle(
                color = Color.White,
                fontSize = workNameTextSize,
                fontFamily = NanumSquareFamily,
                shadow = Shadow(
                    color = Color.DarkGray,
                    offset = Offset(0f, 0f),
                    blurRadius = 1f
                ),
                fontWeight = FontWeight.Bold
            ),
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .width(sizeDp.times(0.55f))
                .align(Center)
                .offset(y = sizeDp.times(0.18f))
                .alpha(if (isPaused) 0f else 1f),
        )
    }
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
        currentSetIndex = 1,
        scheduleData = ScheduleData(
            workoutName = "Runnincle",
            time = 60,
            type = ScheduleType.WORK,
            timerColor = TimerColorPalette[10],
            set = 3,
        ),
        totalTimerColor = TimerColorPalette[6],
        remainingTimeOfCurrentWork = 20,
        originalTotalWorkTime = 100,
        progressedTotalWorkTime = 30,
        overlayStatus = OverlayWindowStatus.PLAY,
        onCloseBtnClicked = {},
        onPlayBtnClick = {}
    )
}
