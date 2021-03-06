package com.devhanna91.runnincle.framework.presentation.composable

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.devhanna91.runnincle.R
import com.devhanna91.runnincle.framework.presentation.create_program.BottomSheetSaveButtonStatus
import com.devhanna91.runnincle.framework.presentation.create_program.composable.AddWorkoutLayout
import com.devhanna91.runnincle.framework.presentation.create_program.composable.ButtonAddWork

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddWorkoutBottomSheet(
    scaffoldState: BottomSheetScaffoldState,
    name: MutableState<String>,
    workMin: MutableState<Int>,
    workSec: MutableState<Int>,
    coolDownMin: MutableState<Int>,
    coolDownSec: MutableState<Int>,
    isSkipLastCoolDown: MutableState<Boolean>,
    set: MutableState<Int>,
    timerColor: MutableState<Color>,
    buttonStatus: BottomSheetSaveButtonStatus,
    onSaveClick: ()->Unit,
    onDeleteClick: ()->Unit,
    onCollapsedSheetClick: ()->Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 150.dp, max = 570.dp)
    ) {
        SheetExpanded {
            val scrollState = rememberScrollState()
            LaunchedEffect(Unit) { scrollState.animateScrollTo(10000) }
            AddWorkoutLayout(
                modifier = Modifier
                    .padding(top = 20.dp, bottom = 70.dp)
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .align(Alignment.TopCenter)
                    .verticalScroll(scrollState),
                name = name,
                workMin = workMin,
                workSec = workSec,
                coolDownMin = coolDownMin,
                coolDownSec = coolDownSec,
                isSkipLastCoolDown= isSkipLastCoolDown,
                set = set,
                timerColor = timerColor
            )
            ButtonAddWork(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
                    .align(Alignment.BottomEnd)
                    .padding(0.dp),
                onSaveClick = onSaveClick,
                onDeleteClick = onDeleteClick,
                buttonStatus = buttonStatus
            )
        }
        SheetCollapsed(
            currentFraction = scaffoldState.currentFraction,
            onSheetClick = {
                onCollapsedSheetClick()
            }
        ) {
            Image(
                ImageVector.vectorResource(id = R.drawable.ic_baseline_library_add),
                stringResource(id = R.string.add_new_workout),
                colorFilter = ColorFilter.tint(MaterialTheme.colors.background),
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
fun SheetExpanded(
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colors.primary,
                        MaterialTheme.colors.primaryVariant
                    )
                ),
            )
            .padding(top = 30.dp, start = 40.dp, end = 40.dp, bottom = 20.dp)
    ) {
        content()
    }
}

@Composable
fun SheetCollapsed(
    onSheetClick: () -> Unit,
    currentFraction: Float,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height((70 * (1f - currentFraction)).dp)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colors.primary,
                        MaterialTheme.colors.primaryVariant
                    )
                )
            )
            .graphicsLayer(alpha = 1f - currentFraction)
            .clickable {
                onSheetClick()
            }
    ) {
        content()
    }
}

