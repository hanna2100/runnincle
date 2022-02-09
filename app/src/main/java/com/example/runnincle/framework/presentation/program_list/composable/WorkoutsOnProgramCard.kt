package com.example.runnincle.framework.presentation.program_list.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.runnincle.R
import com.example.runnincle.business.domain.model.Workout
import com.example.runnincle.business.domain.model.Workout.Companion.getTotalWorkoutTime
import com.example.runnincle.framework.presentation.composable.AutoSizeText
import com.example.runnincle.toTimeClock
import com.example.runnincle.toTimeLetters
import com.example.runnincle.ui.theme.infinitySansFamily
import com.google.accompanist.flowlayout.FlowColumn
import com.google.accompanist.flowlayout.SizeMode
import java.lang.StringBuilder

@Composable
@Preview
fun test() {
    val list = listOf<Workout>(
        Workout(
            id = "a",
            name = "test",
            set = 3,
            work = 100,
            coolDown = 30,
            order = 0,
            isSkipLastCoolDown = true,
            timerColor = Color.Red
        ),
        Workout(
            id = "b",
            name = "test2",
            set = 3,
            work = 100,
            coolDown = 30,
            order = 0,
            isSkipLastCoolDown = false,
            timerColor = Color.Yellow
        ),
        Workout(
            id = "c",
            name = "test3",
            set = 3,
            work = 100,
            coolDown = 30,
            order = 0,
            isSkipLastCoolDown = true,
            timerColor = Color.Blue
        )
    )

//    WorkoutsOnProgramCard(workouts = list)
}

@Composable
fun WorkoutsOnProgramCard(
    workouts: List<Workout>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(10.dp, 20.dp, 10.dp, 30.dp)
    ) {
        FlowColumn(
            modifier = Modifier
                .fillMaxWidth()
            ,
            mainAxisSize = SizeMode.Expand
        ) {
            workouts.forEachIndexed { index, item ->
                WorkoutItem(workout = item, index, workouts.size)
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun WorkoutItem(
    workout: Workout,
    index: Int,
    size: Int
) {

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val itemWidth = screenWidth.times(0.7f)
    val leadingWith = 40.dp
    val tailWith = 80.dp

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
            .height(70.dp)
            ,
        shape = RoundedCornerShape(0.dp),
        elevation = 0.dp,
        backgroundColor = Color.Transparent,
    ) {
        Row {
            Box(
                modifier = Modifier
                    .padding(0.dp)
                    .width(leadingWith)
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
                    .size(15.dp)
                    .clip(CircleShape)
                    .background(workout.timerColor)
                    .align(Alignment.Center)
                )
            }

            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(4.dp)
                    .width(itemWidth - leadingWith - tailWith)
                    .fillMaxHeight()
            ) {
                Text(
                    text = "${workout.name} ${workout.set}set",
                    fontFamily = infinitySansFamily,
                    fontSize = 16.sp,
                )
                AutoSizeText(
                    text = getWorkAndRestTime(workout),
                    textStyle = MaterialTheme.typography.caption
                )
            }

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.End,
                modifier = Modifier
                    .width(tailWith)
                    .fillMaxHeight()
            ) {
                Text(
                    text = workout.getTotalWorkoutTime().toTimeClock(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
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