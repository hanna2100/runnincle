package com.example.runnincle.framework.presentation.create_program

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ScrollView
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.Fragment
import com.example.runnincle.R
import com.example.runnincle.business.domain.model.Workout
import com.example.runnincle.business.domain.model.Workout.Companion.getTotalWorkoutTime
import com.example.runnincle.framework.presentation.composable.*
import com.example.runnincle.toTimeClock
import com.example.runnincle.toTimeLetters
import com.example.runnincle.ui.theme.RunnincleTheme
import com.example.runnincle.ui.theme.infinitySansFamily
import com.vanpra.composematerialdialogs.*
import com.vanpra.composematerialdialogs.color.ColorPalette
import com.vanpra.composematerialdialogs.color.colorChooser
import kotlinx.coroutines.launch
import java.lang.StringBuilder

class CreateProgramFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                val workouts = mutableListOf<Workout>()
                workouts.add(Workout(
                    id = "11",
                    programId = "22",
                    name = "걷기",
                    set = 1,
                    work = 60,
                    coolDown = 0,
                    order = 0,
                ))
                workouts.add(Workout(
                    id = "12",
                    programId = "22",
                    name = "자전거",
                    set = 3,
                    work = 20,
                    coolDown = 30,
                    order = 1,
                ))
                workouts.add(Workout(
                    id = "13",
                    programId = "22",
                    name = "걷기",
                    set = 1,
                    work = 130,
                    coolDown = 0,
                    order = 2,
                ))

                RunnincleTheme {
                    BottomSheetScaffold(workouts)
                }
            }
        }
    }
}

@Composable
fun WorkoutList(workouts: List<Workout>) {
    LazyColumn(
        contentPadding = PaddingValues(10.dp, 0.dp)
    ) {
        itemsIndexed(items = workouts) { index, item ->
            WorkoutItem(workout = item, index, workouts.size)
        }
    }
}

@Composable
fun WorkoutItem(workout: Workout, index: Int, size: Int) {
    Card(
        modifier = Modifier
            .padding(0.dp)
            .fillMaxWidth()
            .height(110.dp),
        shape = RoundedCornerShape(8.dp),
        backgroundColor = Color.Transparent,
        elevation = 0.dp
    ) {
        Surface {
            Row( Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .padding(0.dp)
                        .fillMaxHeight()
                        .weight(0.2f)
                ) {
                    if (index == 0) {
                        Box(modifier = Modifier
                            .fillMaxHeight(0.5f)
                            .width(2.dp)
                            .background(MaterialTheme.colors.primaryVariant)
                            .align(Alignment.BottomCenter)
                        )
                    } else if(index == size -1){
                        Box(modifier = Modifier
                            .fillMaxHeight(0.5f)
                            .width(2.dp)
                            .background(MaterialTheme.colors.primaryVariant)
                            .align(Alignment.TopCenter)
                        )
                    } else {
                        Box(modifier = Modifier
                            .fillMaxHeight()
                            .width(2.dp)
                            .background(MaterialTheme.colors.primaryVariant)
                            .align(Alignment.BottomCenter)
                        )
                    }
                    Box(modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colors.primaryVariant)
                        .align(Alignment.Center)
                    )
                }

                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxHeight()
                        .weight(0.8f)
                ) {
                    Text(
                        text = "${workout.name} ${workout.set}set",
                        style = MaterialTheme.typography.subtitle1,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = getWorkAndRestTime(workout),
                        style = MaterialTheme.typography.body1,
                    )
                }

                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier
                        .weight(0.3f)
                        .fillMaxHeight()
                        .padding(horizontal = 20.dp)
                ) {
                    Text(
                        text = workout.getTotalWorkoutTime().toTimeClock(LocalContext.current),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        textAlign = TextAlign.End,
                        color = Color.DarkGray
                    )
                }
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

@Composable
fun TopAppBar() {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.add_new_workout),
                color = MaterialTheme.colors.onBackground,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            IconButton(onClick = {
                // 뒤로가기
            }) {
                Icon(
                    Icons.Filled.KeyboardArrowLeft,
                    contentDescription = "",
                    tint = MaterialTheme.colors.onBackground
                )
            }
        },
        actions = {
            IconButton(onClick = {
                // program 추가
            }) {
                Icon(
                    Icons.Filled.Done,
                    contentDescription = "",
                    tint = MaterialTheme.colors.primaryVariant
                )
            }
        },
        backgroundColor = Color.Transparent,
        elevation = 0.dp,
    )
}

@Composable
fun WorkoutCircle(workouts: List<Workout>) {
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
                    Text(
                        text = "40:00",
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colors.primaryVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 20.dp)
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
                    text = "자전거타기",
                    textStyle = TextStyle(
                        fontFamily = infinitySansFamily,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colors.onSurface,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 5.dp)
                )
            }
        }
    }
}

@Composable
@ExperimentalMaterialApi
fun DebugScreen(
    scaffoldState: BottomSheetScaffoldState,
    onToggle: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val fraction = scaffoldState.bottomSheetState.progress.fraction
        val targetValue = scaffoldState.bottomSheetState.targetValue
        val currentValue = scaffoldState.bottomSheetState.currentValue

        Text("fraction = $fraction")
        Text("target = $targetValue")
        Text("current = $currentValue")
        Text("New fraction = ${scaffoldState.currentFraction}")

        Button(
            modifier = Modifier.padding(20.dp),
            onClick = onToggle
        ) {
            Text(text = "Click to show/hide bottom sheet")
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BottomSheetScaffold(workouts: List<Workout>) {
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberBottomSheetState(BottomSheetValue.Collapsed)
    )
    val radius = (160 * (1f - scaffoldState.currentFraction)).dp + 40.dp
    val sheetToggle: () -> Unit = {
        scope.launch {
            if (scaffoldState.bottomSheetState.isCollapsed) {
                scaffoldState.bottomSheetState.expand()
            } else {
                scaffoldState.bottomSheetState.collapse()
            }
        }
    }
    BottomSheetScaffold(
        modifier = Modifier.fillMaxSize(),
        scaffoldState = scaffoldState,
        sheetShape = RoundedCornerShape(topStart = radius, topEnd = radius),
        topBar = {
            TopAppBar()
                 },
        sheetContent = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 150.dp, max = 460.dp)
            ) {
                SheetExpanded {
                    val scrollState = rememberScrollState()
                    LaunchedEffect(Unit) { scrollState.animateScrollTo(10000) }
                    LayoutAddWork(
                        modifier = Modifier
                            .padding(bottom = 60.dp)
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .align(Alignment.TopCenter)
                            .verticalScroll(scrollState)
                    )
                    ButtonAddWork(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .align(Alignment.BottomEnd)
                    )
                }
                SheetCollapsed(
                    isCollapsed = scaffoldState.bottomSheetState.isCollapsed,
                    currentFraction = scaffoldState.currentFraction,
                    onSheetClick = sheetToggle
                ) {
                    Image(
                        ImageVector.vectorResource(id = R.drawable.ic_baseline_library_add),
                        "총 시간",
                        colorFilter = ColorFilter.tint(MaterialTheme.colors.background),
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(top = 8.dp)
                    )
                }
            }
        },
        sheetPeekHeight = 100.dp
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .background(MaterialTheme.colors.background)
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                WorkoutCircle(workouts)
                WorkoutList(workouts)
            }
        }
//        DebugScreen(scaffoldState = scaffoldState, onToggle = sheetToggle)
    }
}

@Composable
fun LayoutAddWork(modifier: Modifier) {
    Column(
        modifier = modifier
    ) {
        var name by remember { mutableStateOf("") }
        var workMin by remember { mutableStateOf("") }
        var workSec by remember { mutableStateOf("") }
        var coolDownMin by remember { mutableStateOf("") }
        var coolDownSec by remember { mutableStateOf("") }
        var set by remember { mutableStateOf("") }

        var timerColor:Color by remember { mutableStateOf(Color.Black) }

        Text(
            text = "이름",
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 5.dp),
            textAlign = TextAlign.Start,
            color = MaterialTheme.colors.onPrimary
        )
        TextField(
            value = name,
            singleLine = true,
            onValueChange = {
                name = it
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            shape = RoundedCornerShape(8.dp),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.White.copy(alpha = 0.25f),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            textStyle = TextStyle(fontSize = 20.sp)
        )
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "운동시간",
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(bottom = 5.dp),
                    textAlign = TextAlign.Start,
                    color = MaterialTheme.colors.onPrimary
                )
                Row {
                    TextField(
                        value = workMin,
                        singleLine = true,
                        onValueChange = {
                            if (it.isDigitsOnly() && it.length <= 2 ) {
                                workMin = it
                            }
                        },
                        modifier = Modifier
                            .width(55.dp)
                            .padding(bottom = 10.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = Color.White.copy(alpha = 0.25f),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        textStyle = TextStyle(fontSize = 20.sp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    Text(
                        text = ":",
                        modifier = Modifier.padding(horizontal = 6.dp),
                        fontSize = 40.sp,
                        color = MaterialTheme.colors.onPrimary
                    )
                    TextField(
                        value = workSec,
                        singleLine = true,
                        onValueChange = {
                            if (it.isDigitsOnly() && it.length <= 2) {
                                if(it.isBlank()) {
                                    workSec = ""
                                } else if(it.toInt() <= 60) {
                                    workSec = it
                                }
                            }
                        },
                        modifier = Modifier
                            .width(55.dp)
                            .padding(bottom = 10.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = Color.White.copy(alpha = 0.25f),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        textStyle = TextStyle(fontSize = 20.sp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }
            Column {
                Text(
                    text = "쿨다운 시간",
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(bottom = 5.dp),
                    textAlign = TextAlign.Start,
                    color = MaterialTheme.colors.onPrimary
                )
                Row {
                    TextField(
                        value = coolDownMin,
                        singleLine = true,
                        onValueChange = {
                            if (it.isDigitsOnly() && it.length <= 2 ) {
                                coolDownMin = it
                            }
                        },
                        modifier = Modifier
                            .width(55.dp)
                            .padding(bottom = 10.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = Color.White.copy(alpha = 0.25f),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        textStyle = TextStyle(fontSize = 20.sp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    Text(
                        text = ":",
                        modifier = Modifier.padding(horizontal = 6.dp),
                        fontSize = 40.sp,
                        color = MaterialTheme.colors.onPrimary
                    )
                    TextField(
                        value = coolDownSec,
                        singleLine = true,
                        onValueChange = {
                            if (it.isDigitsOnly() && it.length <= 2) {
                                if(it.isBlank()) {
                                    coolDownSec = ""
                                } else if(it.toInt() <= 60) {
                                    coolDownSec = it
                                }
                            }
                        },
                        modifier = Modifier
                            .width(55.dp)
                            .padding(bottom = 10.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = Color.White.copy(alpha = 0.25f),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        textStyle = TextStyle(fontSize = 20.sp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }
        }
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
            ){
                Text(
                    text = "세트",
                    modifier = Modifier.padding(bottom = 5.dp),
                    textAlign = TextAlign.Start,
                    color = MaterialTheme.colors.onPrimary
                )
                TextField(
                    value = set,
                    singleLine = true,
                    onValueChange = {
                        if (it.isDigitsOnly() && it.length <= 2) {
                            set = it
                        }
                    },
                    modifier = Modifier
                        .width(80.dp)
                        .padding(bottom = 10.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.White.copy(alpha = 0.25f),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    textStyle = TextStyle(fontSize = 20.sp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(130.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "타이머 색상",
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(bottom = 12.dp),
                    textAlign = TextAlign.Start,
                    color = MaterialTheme.colors.onPrimary
                )
                DialogAndShowButton(
                    selectedColor = timerColor,
                    buttons = { defaultColorDialogButtons() }
                ) {
                    title("Select a Color")
                    colorChooser(colors = ColorPalette.Primary) {
                        timerColor = it
                    }
                }
            }
        }
    }
}
@Composable
private fun MaterialDialogButtons.defaultColorDialogButtons() {
    positiveButton("Select")
    negativeButton("Cancel")
}

@Composable
fun DialogAndShowButton(
    selectedColor: Color,
    buttons: @Composable MaterialDialogButtons.() -> Unit = {},
    content: @Composable MaterialDialogScope.() -> Unit
) {
    val dialogState = rememberMaterialDialogState()

    MaterialDialog(dialogState = dialogState, buttons = buttons) {
        content()
    }
    OutlinedButton(
        onClick = { dialogState.show() },
        modifier = Modifier
            .size(45.dp),
        shape = RoundedCornerShape(100),
        colors = ButtonDefaults.outlinedButtonColors(
            backgroundColor = selectedColor
        ),
        border = BorderStroke(1.dp, MaterialTheme.colors.onPrimary),
    ) {

    }
}

@Composable
fun ButtonAddWork(modifier: Modifier) {
    Button(
        onClick = {

        },
        colors = ButtonDefaults.textButtonColors(
            backgroundColor = MaterialTheme.colors.onPrimary,
            contentColor = MaterialTheme.colors.primaryVariant
        ),
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
    ) {
        Image(
            ImageVector.vectorResource(id = R.drawable.ic_baseline_add),
            "저장",
            colorFilter = ColorFilter.tint(MaterialTheme.colors.primaryVariant),
            modifier = Modifier.padding(end = 8.dp)
        )
        Text("저장")
    }
}

@Preview
@Composable
fun PreviewTest() {
    val workouts = mutableListOf<Workout>()
    workouts.add(Workout(
        id = "11",
        programId = "22",
        name = "걷기",
        set = 1,
        work = 60,
        coolDown = 0,
        order = 0,
    ))
    workouts.add(Workout(
        id = "12",
        programId = "22",
        name = "자전거",
        set = 3,
        work = 20,
        coolDown = 30,
        order = 1,
    ))
    workouts.add(Workout(
        id = "13",
        programId = "22",
        name = "걷기",
        set = 1,
        work = 130,
        coolDown = 0,
        order = 2,
    ))

    RunnincleTheme {
        BottomSheetScaffold(workouts)
    }

}