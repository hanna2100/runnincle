package com.example.runnincle.framework.presentation.create_program.composable

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import com.example.runnincle.R
import com.example.runnincle.business.domain.model.Workout
import com.example.runnincle.business.domain.model.Workout.Companion.getTotalWorkoutTime
import com.example.runnincle.framework.presentation.composable.AutoSizeText
import com.example.runnincle.getTotalWorkoutListTime
import com.example.runnincle.toTimeClock


@Composable
fun CreateProgramWorkoutCircle(
    workouts: List<Workout>,
    programName: String,
    onProgramNameClick: ()-> Unit,
    isFired: Boolean
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
                WorkoutScheduleBar(workouts, isFired)
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
                        textStyle = MaterialTheme.typography.h3.plus(
                            TextStyle(
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colors.primary,
                                textAlign = TextAlign.Center
                            )
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
                .align(Alignment.BottomCenter),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AutoSizeText(
                    text = programName,
                    textStyle = MaterialTheme.typography.h4.plus(
                        TextStyle(
                            fontWeight = FontWeight.Medium,
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
fun WorkoutScheduleBar(workouts: List<Workout>, isFired: Boolean) {
    val totalWorkoutListTime = workouts.getTotalWorkoutListTime()
    val workoutTimePercentages =  mutableListOf<State<Float>>()
    var workTimeFloatValue = 0f

    workouts.forEach {
        workTimeFloatValue += it.getTotalWorkoutTime().toFloat().div(totalWorkoutListTime)
        val drawPercentage = animateFloatAsState(
            targetValue = if (isFired) workTimeFloatValue else 0f,
            animationSpec = tween(
                delayMillis = 0,
                durationMillis = 1000
            )
        )
        workoutTimePercentages.add(drawPercentage)
    }


//    LaunchedEffect(isFired) {
//        stateList.clear()
//
//        workouts.forEach {
//            workTimeFloatValue += it.getTotalWorkoutTime().toFloat().div(totalWorkoutListTime)
//            val drawPercentage = animateFloatAsState(
//                targetValue = if()workTimeFloatValue,
//                animationSpec = tween(
//                    delayMillis = 0,
//                    durationMillis = 1000
//                )
//            )
//            stateList.add(drawPercentage)
//        }
//        println("stateList size = ${stateList.size}")
//        onComplete()
//    }

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
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            var stroke = 35f
            for (i in workouts.lastIndex downTo 0) {
                drawArc(
                    brush = SolidColor(workouts[i].timerColor),
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

@Composable
fun ShowEditProgramNameDialog(
    onSetProgramName: (String) -> Unit,
    onDismissRequest: ()-> Unit,
) {
    var newName: String by remember { mutableStateOf("")}

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
            OutlinedTextField(
                value = newName,
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text(stringResource(id = R.string.program_name))
                },
                onValueChange = {
                    newName = it
                }
            )
        }
    )
}