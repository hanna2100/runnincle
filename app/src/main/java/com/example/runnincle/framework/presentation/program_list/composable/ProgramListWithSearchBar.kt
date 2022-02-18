package com.example.runnincle.framework.presentation.program_list.composable

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.runnincle.business.domain.model.Program
import com.example.runnincle.business.domain.model.Workout
import com.example.runnincle.ui.theme.RunnincleTheme


@Composable
fun ProgramListWithSearchBar(
    programs: Map<Program, List<Workout>>,
    onProgramCardClick: (program: Program, workouts: List<Workout>)-> Unit,
    onFloatingAddButtonClick: ()->Unit,
    onProgramEditButtonClick: (program:Program, workout: List<Workout>)->Unit,
    onProgramDeleteButtonClick: (program: Program)->Unit
) {
    RunnincleTheme {
        var searchBarOpen by remember { mutableStateOf(false) }
        var searchString by remember { mutableStateOf("") }
        var chipList by remember { mutableStateOf(mutableListOf<SearchChip>()) }
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
                    programs  = programs,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    onProgramCardClick = onProgramCardClick,
                    onProgramEditButtonClick = onProgramEditButtonClick,
                    onProgramDeleteButtonClick = onProgramDeleteButtonClick
                )
            }

            FloatingButtonToCreateProgram(
                modifier = Modifier.constrainAs(floatingButtonRef) {
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                },
                onClick = {
                    onFloatingAddButtonClick()
                }
            )
        }
    }

}