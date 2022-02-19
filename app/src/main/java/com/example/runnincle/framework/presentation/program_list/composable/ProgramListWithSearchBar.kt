package com.example.runnincle.framework.presentation.program_list.composable

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.runnincle.R
import com.example.runnincle.business.domain.model.Program
import com.example.runnincle.business.domain.model.Workout


@Composable
fun ProgramListWithSearchBar(
    programs: Map<Program, List<Workout>>,
    onSearchButtonClick: (searchText: String) -> Unit,
    onChipClick: (chipText: String) -> Unit,
    onProgramCardClick: (program: Program, workouts: List<Workout>)-> Unit,
    onFloatingAddButtonClick: ()->Unit,
    onProgramEditButtonClick: (program:Program, workout: List<Workout>)->Unit,
    onProgramDeleteButtonClick: (program: Program)->Unit
) {
    var searchBarOpen by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
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
                    searchString = searchText,
                    onSearchButtonClick = {
                        if(searchBarOpen && searchText.isNotEmpty()) {
                            onSearchButtonClick(searchText)
                            chipList.add(0, SearchChip(searchText))
                            searchText = ""
                            selectedChipIndex.value = 0
                        }
                        searchBarOpen = !searchBarOpen
                    },
                    onSearchTextFieldValueChange = {
                        searchText = it
                    },
                )
                Spacer(modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp))
                ChipGroup(
                    items = chipList,
                    selectedChipIndex = selectedChipIndex,
                    onChipClick = onChipClick,
                    onChipDeleteButtonClick = { chip ->
                        var list = mutableListOf<SearchChip>()
                        list.addAll(chipList)
                        list.remove(chip)
                        chipList = list

                        if(selectedChipIndex.value != -1) {
                            selectedChipIndex.value = -1
                            onChipClick("")
                        }
                    },
                )
            }
            ProgramList(
                programs  = programs,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                ,
                onProgramCardClick = onProgramCardClick,
                onProgramEditButtonClick = onProgramEditButtonClick,
                onProgramDeleteButtonClick = onProgramDeleteButtonClick,
                selectedChipIndex = selectedChipIndex.value
            )
            // 검색결과가 없을 경우
            if (selectedChipIndex.value != -1 && programs.isEmpty()) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp, 30.dp),
                    style = MaterialTheme.typography.h6.copy(
                        color = MaterialTheme.colors.secondary,
                        textAlign = TextAlign.Center
                    ),
                    text = stringResource(id = R.string.no_search_result)
                )
            }
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