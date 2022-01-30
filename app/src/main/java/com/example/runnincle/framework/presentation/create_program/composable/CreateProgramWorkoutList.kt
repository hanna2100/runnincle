package com.example.runnincle.framework.presentation.create_program.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.runnincle.R
import com.example.runnincle.business.domain.model.Workout
import com.example.runnincle.business.domain.model.Workout.Companion.getTotalWorkoutTime
import com.example.runnincle.framework.presentation.composable.AutoSizeText
import com.example.runnincle.toTimeClock
import com.example.runnincle.toTimeLetters
import java.lang.StringBuilder


@Composable
fun CreateProgramWorkoutList(
    workouts: List<Workout>,
    onItemClick: (workout: Workout)->Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .shadow(elevation = 60.dp)
            .clip(shape = RoundedCornerShape(topStart = 100.dp))
            .background(MaterialTheme.colors.surface)
            .padding(10.dp, 20.dp, 10.dp, 30.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent),
            contentPadding = PaddingValues(10.dp, 0.dp)
        ) {
            itemsIndexed(items = workouts) { index, item ->
                WorkoutItem(workout = item, index, workouts.size, onItemClick)
            }
        }
        Box(
            Modifier
                .fillMaxWidth()
                .height(40.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(Color.White, Color.White, Color.Transparent)
                    )
                )
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun WorkoutItem(
    workout: Workout,
    index: Int,
    size: Int,
    onItemClick: (workout: Workout)->Unit
) {
    Card(
        modifier = Modifier
            .padding(
                bottom = if (index == size - 1) {
                    40.dp
                } else {
                    0.dp
                }
            )
            .fillMaxWidth()
            .height(110.dp),
        shape = RoundedCornerShape(0.dp),
        elevation = 0.dp,
        backgroundColor = Color.Transparent,
        onClick = {
            onItemClick(workout)
        }
    ) {
        Row( Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .padding(0.dp)
                    .width(60.dp)
                    .fillMaxHeight()
            ) {
                if (index == 0) {
                    if (size > 1) {
                        Box(modifier = Modifier
                            .fillMaxHeight(0.5f)
                            .width(2.dp)
                            .background(MaterialTheme.colors.background)
                            .align(Alignment.BottomCenter)
                        )
                    }
                } else if(index == size -1){
                    Box(modifier = Modifier
                        .fillMaxHeight(0.5f)
                        .width(2.dp)
                        .background(MaterialTheme.colors.background)
                        .align(Alignment.TopCenter)
                    )
                } else {
                    Box(modifier = Modifier
                        .fillMaxHeight()
                        .width(2.dp)
                        .background(MaterialTheme.colors.background)
                        .align(Alignment.BottomCenter)
                    )
                }
                Box(modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(workout.timerColor)
                    .align(Alignment.Center)
                )
            }

            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(4.dp)
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                Text(
                    text = "${workout.name} ${workout.set}set",
                    style = MaterialTheme.typography.subtitle1,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                AutoSizeText(
                    text = getWorkAndRestTime(workout),
                    textStyle = MaterialTheme.typography.body1
                )
            }

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.End,
                modifier = Modifier
                    .width(110.dp)
                    .fillMaxHeight()
                    .padding(horizontal = 20.dp)
            ) {
                Text(
                    text = workout.getTotalWorkoutTime().toTimeClock(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    textAlign = TextAlign.End,
                    color = Color.DarkGray
                )
            }
        }
    }

}

@Composable
fun getWorkAndRestTime(workout: Workout): String {
    val workTimeBuilder = StringBuilder()
    workTimeBuilder.append(stringResource(id = R.string.work) + " ")
    workTimeBuilder.append(workout.work.toTimeLetters(LocalContext.current) + " ")
    if (workout.coolDown > 0) {
        workTimeBuilder.append(stringResource(id = R.string.cool_down) + " ")
        workTimeBuilder.append(workout.coolDown.toTimeLetters(LocalContext.current))
    }
    return workTimeBuilder.toString()
}