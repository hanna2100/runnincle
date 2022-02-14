package com.example.runnincle.framework.presentation.program_list.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.runnincle.*
import com.example.runnincle.R
import com.example.runnincle.business.domain.model.Program
import com.example.runnincle.business.domain.model.Workout
import com.example.runnincle.business.domain.model.Workout.Companion.getTotalWorkoutTime
import com.example.runnincle.framework.presentation.composable.AutoSizeText
import com.example.runnincle.ui.theme.infinitySansFamily
import com.google.accompanist.flowlayout.FlowColumn
import com.google.accompanist.flowlayout.SizeMode
import com.siddroid.holi.colors.MaterialColor
import java.lang.StringBuilder



@Composable
fun ProgramList(
    programs: Map<Program, List<Workout>>,
    modifier: Modifier,
    onProgramCardClick: (program: Program, workouts: List<Workout>)-> Unit
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(10.dp, 0.dp)
    ) {
        val p = programs.keys.toList().sortedByDescending { it.updatedAt }
        itemsIndexed(items = p) { index, item ->
            ProgramCard(
                index = index,
                name = item.name,
                updatedAt = item.updatedAt,
                workouts = programs[item]?: emptyList(),
                onProgramCardClick = {
                    onProgramCardClick(item, it)
                }
            )
        }
    }

}

@Composable
fun ProgramCard(
    index: Int,
    name: String,
    updatedAt: String,
    workouts: List<Workout>,
    onProgramCardClick: (workouts: List<Workout>)->Unit
) {
    var isMoreOpen by remember { mutableStateOf(false) }

    Card(modifier = Modifier
        .padding(
            top = if (index == 0) 25.dp else 6.dp,
            start = 10.dp,
            end = 10.dp,
            bottom = 20.dp
        )
        .fillMaxWidth()
        .heightIn(min = 130.dp),
        elevation = 5.dp,
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(
            modifier = Modifier
                .clickable(
                    indication = rememberRipple(
                        bounded = true,
                        color = MaterialTheme.colors.primary
                    ),
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = {
                        onProgramCardClick(workouts)
                    }
                )
                .padding(20.dp, 15.dp)
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .wrapContentHeight()
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.h6
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = updatedAt.toTimeAgo(),
                    style = MaterialTheme.typography.caption
                )
                AnimatedVisibility(visible = isMoreOpen) {
                    WorkoutsOnProgramCard(workouts = workouts)
                }
            }
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 15.dp, y = (-5).dp)
                ,
                horizontalArrangement = Arrangement.End
            ) {
                AnimatedVisibility(visible = isMoreOpen) {
                    Row {
                        IconButton(
                            modifier = Modifier.offset(x = 25.dp),
                            onClick = {

                            }) {
                            Icon (
                                modifier = Modifier
                                    .size(20.dp),
                                imageVector = Icons.Filled.Edit,
                                contentDescription = null,
                                tint = MaterialColor.GREEN_400
                            )
                        }
                        IconButton(
                            modifier = Modifier.offset(x = 15.dp),
                            onClick = {

                            }) {
                            Icon (
                                modifier = Modifier
                                    .size(20.dp),
                                imageVector = Icons.Filled.Delete,
                                contentDescription = null,
                                tint = MaterialColor.RED_400
                            )
                        }
                    }
                }
                IconButton(onClick = {
                    isMoreOpen = !isMoreOpen
                }) {
                    Icon (
                        modifier = Modifier
                            .size(22.dp),
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = null,
                        tint = MaterialColor.GREY_400
                    )
                }
            }


            val totalWorkTime = if(workouts.isEmpty()) 0 else workouts.getTotalWorkoutListTime()
            Text(
                text = totalWorkTime.toTimeClock(),
                style = MaterialTheme.typography.h4.copy(
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }
    }

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