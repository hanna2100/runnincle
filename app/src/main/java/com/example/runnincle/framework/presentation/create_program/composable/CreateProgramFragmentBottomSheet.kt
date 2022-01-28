package com.example.runnincle.framework.presentation.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.example.runnincle.R
import com.example.runnincle.framework.presentation.create_program.composable.AddWorkLayout
import com.example.runnincle.framework.presentation.create_program.composable.ButtonAddWork

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CreateProgramFragmentBottomSheet(
    scaffoldState: BottomSheetScaffoldState,
    onSheetClick: ()->Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 150.dp, max = 470.dp)
    ) {
        SheetExpanded {
            val scrollState = rememberScrollState()
            LaunchedEffect(Unit) { scrollState.animateScrollTo(10000) }
            AddWorkLayout(
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
                    .height(55.dp)
                    .align(Alignment.BottomEnd)
            )
        }
        SheetCollapsed(
            isCollapsed = scaffoldState.bottomSheetState.isCollapsed,
            currentFraction = scaffoldState.currentFraction,
            onSheetClick = onSheetClick
        ) {
            Image(
                ImageVector.vectorResource(id = R.drawable.ic_baseline_library_add),
                "추가하기",
                colorFilter = ColorFilter.tint(MaterialTheme.colors.background),
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

