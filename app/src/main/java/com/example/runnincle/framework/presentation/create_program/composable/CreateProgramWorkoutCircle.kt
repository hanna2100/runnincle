package com.example.runnincle.framework.presentation.create_program.composable

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.runnincle.R
import com.example.runnincle.business.domain.model.Workout
import com.example.runnincle.business.domain.model.Workout.Companion.getTotalWorkoutTime
import com.example.runnincle.framework.presentation.composable.AutoSizeText
import com.example.runnincle.getTotalWorkoutListTime
import com.example.runnincle.toTimeClock
import com.example.runnincle.ui.theme.MinSansFamily


@Composable
fun CreateProgramWorkoutCircle(
    workouts: List<Workout>,
    programName: String,
    onProgramNameClick: ()-> Unit,
    onAddAnimKey: Boolean,
    onEditAnimKey: Boolean
) {

    BoxWithConstraints (
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.35f)
    ) {
        val boxWithConstraintsScope = this
        Box ( modifier = Modifier
            .size(boxWithConstraintsScope.maxHeight)
            .align(Alignment.Center)
        ) {
            Box ( modifier = Modifier
                .fillMaxSize(0.75f)
                .align(Alignment.TopCenter)
            ) {
                WorkoutScheduleBar(workouts, onAddAnimKey, onEditAnimKey)
            }
            Box ( modifier = Modifier
                .fillMaxSize(0.8f)
                .align(Alignment.TopCenter)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AutoSizeText(
                        text = workouts.getTotalWorkoutListTime().toTimeClock(),
                        textStyle = TextStyle(
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colors.primary,
                            textAlign = TextAlign.Center,
                            fontFamily = MinSansFamily,
                            fontSize = 48.sp
                            ),
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .padding(bottom = 20.dp)
                    )
                }
            }
            Column ( modifier = Modifier
                .fillMaxWidth(0.7f)
                .fillMaxHeight(0.33f)
                .align(Alignment.BottomCenter)
                .offset(y = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AutoSizeText(
                    text = programName,
                    textStyle = MaterialTheme.typography.h4.plus(
                        TextStyle(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colors.onSurface,
                            textAlign = TextAlign.Center
                        )
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 5.dp)
                        .clickable { onProgramNameClick() }
                )
            }
        }
    }
}

@Composable
fun WorkoutScheduleBar(
    workouts: List<Workout>,
    onAddAnimKey: Boolean,
    onEditAnimKey: Boolean
) {
    val totalWorkoutListTime = workouts.getTotalWorkoutListTime()
    val workoutTimePercentages =  mutableListOf<State<Float>>()
    var workTimeFloatValue = 0f
    val animatableTimerColorList = remember { mutableListOf<Animatable<Color, AnimationVector4D>>() }

    workouts.forEach {
        workTimeFloatValue += it.getTotalWorkoutTime().toFloat().div(totalWorkoutListTime)
        val drawPercentage = animateFloatAsState(
            targetValue = if (onAddAnimKey) workTimeFloatValue else 0f,
            animationSpec = tween(
                delayMillis = 0,
                durationMillis = 500,
            )
        )
        workoutTimePercentages.add(drawPercentage)

        animatableTimerColorList.add(
            Animatable(it.timerColor)
        )
    }

    for(i in 0..workouts.lastIndex) {
        LaunchedEffect(onEditAnimKey) {
            animatableTimerColorList[i].animateTo(
                workouts[i].timerColor,
                animationSpec = tween(500)
            )
        }
    }


    Box {
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
                style = Stroke(35f, cap = StrokeCap.Round)
            )
        }

        for (i in workouts.lastIndex downTo 0) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
            ) {
                var stroke = 35f
                for (i in workouts.lastIndex downTo 0) {
                    drawArc(
                        brush = SolidColor(animatableTimerColorList[i].value),
                        startAngle = 130f,
                        sweepAngle = 280f * workoutTimePercentages[i].value,
                        useCenter = false,
                        style = Stroke(stroke, cap = StrokeCap.Round)
                    )
                    stroke += 1f
                }
            }
        }

    }

}

@Composable
fun ShowEditProgramNameDialog(
    onSetProgramName: (String) -> Unit,
    onDismissRequest: ()-> Unit,
) {
    var newName: String by remember { mutableStateOf("")}
    val maxChar = 20

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = {
                onSetProgramName(newName)
            })
            { Text(text = stringResource(id = R.string.confirm)) }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest)
            { Text(text = stringResource(id = R.string.cancel)) }
        },
        title = { },
        text = {
            Column {
                OutlinedTextField(
                    value = newName,
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text(stringResource(id = R.string.program_name))
                    },
                    onValueChange = {
                        if(it.length <= 20) {
                            newName = it
                        }
                    },
                    maxLines = 1,
                    singleLine = true,
                )
                Text(
                    text = "${newName.length} / $maxChar",
                    textAlign = TextAlign.End,
                    color = MaterialTheme.colors.primary,
                    style = MaterialTheme.typography.button,
                    modifier = Modifier.fillMaxWidth().padding(10.dp)
                )
            }
        }
    )
}