package com.devhanna91.runnincle.framework.presentation.program_list.composable

import androidx.compose.animation.Animatable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.material.ripple.LocalRippleTheme
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
import com.devhanna91.runnincle.*
import com.devhanna91.runnincle.R
import com.devhanna91.runnincle.business.domain.model.Program
import com.devhanna91.runnincle.business.domain.model.Workout
import com.devhanna91.runnincle.business.domain.model.Workout.Companion.getTotalWorkoutTime
import com.devhanna91.runnincle.ui.theme.NanumSquareFamily
import com.devhanna91.runnincle.ui.theme.DarkRippleTheme
import com.google.accompanist.flowlayout.FlowColumn
import com.google.accompanist.flowlayout.SizeMode
import com.siddroid.holi.colors.MaterialColor
import java.lang.StringBuilder


private val UpdatedAtComparator = Comparator<Program> { left, right ->
    right.updatedAt.compareTo(left.updatedAt)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProgramList(
    programs: Map<Program, List<Workout>>,
    modifier: Modifier,
    onProgramCardClick: (program: Program, workouts: List<Workout>)-> Unit,
    onProgramEditButtonClick: (program: Program, workout: List<Workout>)->Unit,
    onProgramDeleteButtonClick: (program:Program)->Unit,
    selectedChipIndex: Int
) {
    val p = programs.keys.toList().sortedWith(UpdatedAtComparator)
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(10.dp, 0.dp)
    ) {
        itemsIndexed(items = p, key = { index, item ->  item.id }) { index, item ->
            CompositionLocalProvider(LocalRippleTheme provides  DarkRippleTheme) {
                ProgramCard(
                    modifier = Modifier
                        .padding(
                            top = if (index == 0) 25.dp else 6.dp,
                            start = 10.dp,
                            end = 10.dp,
                            bottom = if (index == p.lastIndex) 60.dp else 20.dp,
                        )
                        .fillMaxWidth()
                        .heightIn(min = 130.dp)
                        .animateItemPlacement()
                    ,
                    program = item,
                    workouts = programs[item]?: emptyList(),
                    onProgramCardClick = {
                        onProgramCardClick(item, it)
                    },
                    onProgramDeleteButtonClick = onProgramDeleteButtonClick,
                    onProgramEditButtonClick = onProgramEditButtonClick,
                )
            }
        }
    }


}

@Composable
fun ProgramCard(
    modifier: Modifier,
    program: Program,
    workouts: List<Workout>,
    onProgramCardClick: (workouts: List<Workout>)->Unit,
    onProgramEditButtonClick: (program:Program, workout: List<Workout>)->Unit,
    onProgramDeleteButtonClick: (program: Program)->Unit
) {
    val backgroundColor = MaterialTheme.colors.background
    val activeBackgroundColor = MaterialTheme.colors.primary
    val textColor = MaterialTheme.colors.secondary

    var isMoreOpen by remember { mutableStateOf(false) }
    var animatableCardColor = remember { Animatable(backgroundColor) }
    var animatableTextColor = remember { Animatable(textColor) }

    LaunchedEffect(isMoreOpen) {
        animatableCardColor.animateTo(
            if (isMoreOpen) {
                activeBackgroundColor
            } else {
                backgroundColor
            },
            animationSpec = tween(300)
        )
    }
    LaunchedEffect(isMoreOpen) {
        animatableTextColor.animateTo(
            if (isMoreOpen) {
                backgroundColor
            } else {
                textColor
            },
            animationSpec = tween(300)
        )
    }
    Card(
        modifier = modifier,
        elevation = 6.dp,
        shape = RoundedCornerShape(20.dp),
        backgroundColor = animatableCardColor.value
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
                .padding(30.dp, 25.dp)
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .wrapContentHeight()
            ) {
                Text(
                    text = program.name,
                    style = MaterialTheme.typography.h6.copy(
                        fontWeight = FontWeight.Bold,
                        color = animatableTextColor.value
                    ),
                    modifier = Modifier.fillMaxWidth(0.65f)
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = program.updatedAt.toTimeAgo(),
                    style = MaterialTheme.typography.caption.copy(
                        color = animatableTextColor.value
                    )
                )
                AnimatedVisibility(visible = isMoreOpen) {
                    WorkoutsOnProgramCard(workouts = workouts, textColor = animatableTextColor.value)
                }
            }
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 15.dp, y = (-12).dp)
                ,
                horizontalArrangement = Arrangement.End
            ) {
                AnimatedVisibility(visible = isMoreOpen) {
                    Row {
                        IconButton(
                            modifier = Modifier
                                .offset(x = 25.dp),
                            onClick = {
                                onProgramEditButtonClick(program, workouts)
                            }
                        ) {
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
                                onProgramDeleteButtonClick(program)
                            }
                        ) {
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
                IconButton(
                    onClick = {
                        isMoreOpen = !isMoreOpen
                    }
                ) {
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
                    fontWeight = FontWeight.Medium,
                    color = animatableTextColor.value
                ),
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }
    }

}

@Composable
fun WorkoutsOnProgramCard(
    workouts: List<Workout>,
    textColor: Color
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
                WorkoutItem(workout = item, index, workouts.size, textColor = textColor)
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun WorkoutItem(
    workout: Workout,
    index: Int,
    size: Int,
    textColor: Color
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
                            .width(1.dp)
                            .background(textColor.copy(alpha = 0.6f))
                            .align(Alignment.BottomCenter)
                        )
                    }
                } else if(index == size -1){
                    Box(modifier = Modifier
                        .fillMaxHeight(0.5f)
                        .width(1.dp)
                        .background(textColor.copy(alpha = 0.6f))
                        .align(Alignment.TopCenter)
                    )
                } else {
                    Box(modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp)
                        .background(textColor.copy(alpha = 0.6f))
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
                    fontFamily = NanumSquareFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = textColor
                )
                Text(
                    text = getWorkAndRestTime(workout),
                    fontFamily = NanumSquareFamily,
                    fontSize = 10.sp,
                    color = textColor.copy(
                        alpha = 0.6f
                    )
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
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp,
                    textAlign = TextAlign.End,
                    color = textColor
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