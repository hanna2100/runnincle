package com.example.runnincle.framework.presentation.program_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.runnincle.ui.theme.RunnincleTheme
import com.example.runnincle.ui.theme.infinitySansFamily
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.SizeMode
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.findNavController
import com.example.runnincle.business.domain.model.Program
import com.example.runnincle.business.domain.model.Workout
import com.example.runnincle.framework.presentation.program_list.composable.WorkoutsOnProgramCard
import com.example.runnincle.getTotalWorkoutListTime
import com.example.runnincle.toTimeAgo
import com.example.runnincle.toTimeClock
import com.siddroid.holi.colors.MaterialColor
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProgramListFragment: Fragment() {

    private val viewModel:ProgramListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val programs = viewModel.programs

        viewModel.launch {
            val cachePrograms = viewModel.getAllPrograms()
            cachePrograms.forEach { program->
                println("program = $program")
                val workoutsOfProgram = viewModel.getWorkoutsOfProgram(program.id)
                programs.put(program, workoutsOfProgram)
            }
        }

        return ComposeView(requireContext()).apply {
            setContent {
                RunnincleTheme {
                    ProgramListWithSearchBar(programs)
                }
            }
        }
    }
}


@Composable
fun ProgramListWithSearchBar(programs: Map<Program, List<Workout>>) {
    RunnincleTheme {
        var searchBarOpen by remember { mutableStateOf(false) }
        var searchString by remember { mutableStateOf("") }
        var chipList by remember { mutableStateOf(
            mutableListOf(
                SearchChip( "자전거"),
                SearchChip( "크로스핏"),
                SearchChip( "루마니안데드리프트")
            )
        )}
        val selectedChipIndex = remember { mutableStateOf(-1) }

        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
        ) {
            val (floatingButtonRef) = createRefs()

            Column(modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
            ) {
                Column (modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .clip(RoundedCornerShape(bottomStart = 50.dp, bottomEnd = 50.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colors.primary,
                                MaterialTheme.colors.primary,
                                MaterialTheme.colors.primaryVariant
                            ),
                            end = Offset(0f, Float.POSITIVE_INFINITY),
                            start = Offset(Float.POSITIVE_INFINITY, 0f)
                        ),
                    )
                    .padding(30.dp)
                    .animateContentSize()
                ) {
                    ProgramSearchBar(
                        searchButtonState = searchBarOpen,
                        searchString = searchString,
                        onSearchButtonClick = {
                            if(searchBarOpen && searchString.isNotEmpty()) {
                                chipList.add(0, SearchChip(searchString))
                                searchString = ""
                                selectedChipIndex.value = 0
                            }
                            searchBarOpen = !searchBarOpen
                        },
                        onSearchTextFieldValueChange = {
                            searchString = it
                        }
                    )
                    Spacer(modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp))
                    ChipGroup(
                        items = chipList,
                        selectIndex = selectedChipIndex,
                        onDeleteChipClick = { chip ->
                            var list = mutableListOf<SearchChip>()
                            list.addAll(chipList)
                            list.remove(chip)
                            chipList = list
                            println(chipList.toString())
                        },
                    )
                }
                ProgramList(
                    programs = programs,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                )
            }

            FloatingButtonToCreateProgram(
                view = LocalView.current,
                modifier = Modifier.constrainAs(floatingButtonRef) {
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }
            )
        }
    }

}

@Composable
fun ProgramList(
    programs: Map<Program, List<Workout>>,
    modifier: Modifier,
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
                workouts = programs[item]?: emptyList())
        }
    }

}

@Composable
fun ProgramCard(
    index: Int,
    name: String,
    updatedAt: String,
    workouts: List<Workout>,
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
                        // 타이머 오픈
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
fun FloatingButtonToCreateProgram(view: View, modifier: Modifier) {
    FloatingActionButton(
        modifier = modifier.padding(20.dp),
        backgroundColor = MaterialTheme.colors.primary,
        onClick = {
            val action = ProgramListFragmentDirections.actionProgramListFragmentToCreateProgramFragment()
            view.findNavController().navigate(action)
        }
    ) {
        Icon(Icons.Filled.Add,"")
    }
}

@Composable
fun ProgramSearchBar(
    searchButtonState: Boolean,
    searchString: String,
    onSearchTextFieldValueChange: (String)->Unit,
    onSearchButtonClick: ()->Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        AnimatedVisibility(visible = !searchButtonState) {
            Column(
                modifier = Modifier.fillMaxHeight()
            ) {
                Text(
                    text = "Search",
                    style = MaterialTheme.typography.h4.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    ),
                )
                Text(
                    text = "workouts",
                    style = MaterialTheme.typography.h6.copy(
                        color = Color.White,
                    ),
                    modifier = Modifier.offset(y = -10.dp)
                )
            }
        }
        Row (
            modifier = Modifier
                .height(46.dp)
                .fillMaxWidth()
                .offset(y = 10.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .border(
                        border = BorderStroke(1.dp, Color.White),
                        shape = RoundedCornerShape(40.dp)
                    )
            ) {
                AnimatedVisibility(searchButtonState) {
                    val configuration = LocalConfiguration.current
                    val textFieldWidth = configuration.screenWidthDp.dp - 150.dp
                    BasicTextField(
                        value = searchString,
                        onValueChange = {
                            onSearchTextFieldValueChange(it)
                        },
                        modifier = Modifier
                            .padding(20.dp, 10.dp, 20.dp, 4.dp)
                            .fillMaxHeight()
                            .width(textFieldWidth)
                        ,
                        textStyle = MaterialTheme.typography.h6.copy(
                            color = Color.White,
                            lineHeight = 24.sp,
                            textAlign = TextAlign.Start
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        cursorBrush = SolidColor(Color.White),
                        singleLine = true,
                        maxLines = 1
                    )
                }
                IconButton(onClick = onSearchButtonClick) {
                    Icon (
                        modifier = Modifier
                            .size(30.dp),
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
        }
    }
}

data class SearchChip(
    val text: String
)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ChipGroup(
    items: List<SearchChip>,
    selectIndex: MutableState<Int>,
    modifier: Modifier = Modifier,
    onDeleteChipClick: (SearchChip) -> Unit
) {
    FlowRow(
        modifier = modifier,
        mainAxisSize = SizeMode.Expand,
    ) {
        items.forEachIndexed { index, chip ->
            ChipItem(
                item = chip,
                itemIndex = index,
                selectIndex = selectIndex,
                onDeleteChipClick = onDeleteChipClick
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ChipItem(
    item: SearchChip,
    itemIndex: Int,
    selectIndex: MutableState<Int>,
    onDeleteChipClick: (SearchChip)->Unit
) {
    val backgroundColor = if (selectIndex.value == itemIndex) {
        MaterialTheme.colors.background
    } else {
        Color.Transparent
    }
    val textColor = if (selectIndex.value == itemIndex) {
        MaterialTheme.colors.primary
    } else {
        Color.White
    }
    val borderColor = if (selectIndex.value == itemIndex) {
        MaterialTheme.colors.primary
    } else {
        Color.White
    }
    Box(
        modifier = Modifier
            .padding(6.dp)
            .background(backgroundColor, CircleShape)
            .clip(CircleShape)
            .border(
                border = BorderStroke(1.dp, borderColor),
                shape = CircleShape
            )
            .clickable {
                selectIndex.value = itemIndex
            }
    ) {
        Row(
            modifier = Modifier
                .wrapContentWidth()
                .padding(end = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item.text,
                color = textColor,
                style = TextStyle(
                    fontFamily = infinitySansFamily,
                    fontSize = 12.sp
                ),
                modifier = Modifier.padding(8.dp, 5.dp),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
            IconButton(
                onClick = { onDeleteChipClick(item) },
                modifier = Modifier
                    .size(12.dp)
                    .offset(10.dp)
            ) {
                Icon (
                    modifier = Modifier
                        .size(12.dp)
                        .offset((-10).dp),
                    imageVector = Icons.Filled.Close,
                    contentDescription = null,
                    tint = textColor
                )
            }
        }
    }
}