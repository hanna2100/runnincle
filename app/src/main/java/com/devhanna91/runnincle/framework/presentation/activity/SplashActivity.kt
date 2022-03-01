package com.devhanna91.runnincle.framework.presentation.activity

import android.os.Bundle
import android.os.Handler
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.devhanna91.runnincle.framework.presentation.composable.sp
import com.devhanna91.runnincle.util.startMainActivity
import com.devhanna91.runnincle.ui.theme.RunnincleTheme
import com.devhanna91.runnincle.ui.theme.TimerColorPalette

class SplashActivity: AppCompatActivity() {

    private var animKey = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RunnincleTheme {
                SplashLogo(
                    logoCircleColors = listOf(
                        TimerColorPalette[9],
                        TimerColorPalette[10],
                        TimerColorPalette[11],
                        TimerColorPalette[12]
                    ),
                    logoText = "Runnincle",
                    fraction = 0.4f,
                    animKey = animKey.value
                )
            }
        }
        val handler = Handler()
        handler.postDelayed({
            launchAnimation()
        }, 200)
        handler.postDelayed({
            startMainActivity()
        }, 1000)
    }

    override fun onPause() {
        super.onPause()
        finish()
    }

    private fun launchAnimation() {
        animKey.value = false
        animKey.value = true
    }
}

@Composable
fun SplashLogo(
    logoCircleColors: List<Color>,
    logoText: String,
    fraction: Float,
    animKey: Boolean
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val screenHeight = LocalConfiguration.current.screenHeightDp

    val minSize = if (screenWidth < screenHeight) screenWidth else screenHeight
    val logoSize = minSize.times(fraction).dp

    val strokeWidth = logoSize.times(0.07f)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.primary),
        contentAlignment = Alignment.Center,
    ) {
        Box ( modifier = Modifier
            .size(logoSize),
        ) {
            Box ( modifier = Modifier
                .fillMaxSize(0.75f)
                .align(Alignment.TopCenter)
            ) {
                LogoCircleBar(logoCircleColors, animKey, strokeWidth)
            }
            Column ( modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.25f)
                .align(Alignment.BottomCenter)
                .offset(y = -logoSize.times(0.1f))
                ,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val setTextSize = logoSize.value.times(0.2f).sp()
                Text(
                    text = logoText,
                    style = TextStyle(
                        fontSize = setTextSize,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colors.background,
                        textAlign = TextAlign.Center
                    )
                )
            }
        }
    }

}

@Composable
fun LogoCircleBar(
    logoCircleColors: List<Color>,
    animKey: Boolean,
    strokeWidth: Dp,
) {

    val workoutTimePercentages =  mutableListOf<State<Float>>()

    for (i in 1..logoCircleColors.size) {
        val drawPercentage = animateFloatAsState(
            targetValue = if (animKey) (280f.div(logoCircleColors.size)) * i else 0f,
            animationSpec = tween(
                delayMillis = 0,
                durationMillis = 600,
            )
        )
        workoutTimePercentages.add(drawPercentage)
    }

    Box(contentAlignment = Alignment.Center) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            drawArc(
                brush = SolidColor(Color.LightGray),
                startAngle = 130f,
                sweepAngle = 280f,
                useCenter = false,
                style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round)
            )
        }

        for (i in logoCircleColors.lastIndex downTo 0) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
            ) {
//                var stroke = strokeWidth
                for (i in logoCircleColors.lastIndex downTo 0) {
                    drawArc(
                        brush = SolidColor(logoCircleColors[i]),
                        startAngle = 130f,
                        sweepAngle = workoutTimePercentages[i].value,
                        useCenter = false,
                        style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round)
                    )
                }
            }
        }

        Icon(
            Icons.Filled.PlayArrow,
            contentDescription = "",
            tint = MaterialTheme.colors.background,
            modifier = Modifier.fillMaxSize(0.5f)
        )
    }
}
