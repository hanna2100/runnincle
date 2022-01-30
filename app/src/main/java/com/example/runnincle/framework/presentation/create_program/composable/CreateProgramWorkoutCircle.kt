package com.example.runnincle.framework.presentation.create_program.composable

import android.content.Context
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.runnincle.R
import com.example.runnincle.business.domain.model.Workout
import com.example.runnincle.business.domain.model.Workout.Companion.getTotalWorkoutTime
import com.example.runnincle.framework.presentation.composable.AutoSizeText
import com.example.runnincle.toTimeClock


@Composable
fun CreateProgramWorkoutCircle(
    workouts: List<Workout>,
    programName: String,
    onProgramNameClick: ()-> Unit,
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
                        text = getTotalWorkoutTime(LocalContext.current, workouts),
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

fun getTotalWorkoutTime(
    context: Context,
    workouts: List<Workout>
): String {
    var totalTime = 0
    workouts.forEach {
        totalTime += it.getTotalWorkoutTime()
    }
    return totalTime.toTimeClock(context)
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