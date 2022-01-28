package com.example.runnincle.framework.presentation.create_program

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.runnincle.business.domain.model.Workout
import com.example.runnincle.framework.presentation.composable.*
import com.example.runnincle.framework.presentation.create_program.composable.CreateProgramTopAppBar
import com.example.runnincle.framework.presentation.create_program.composable.CreateProgramWorkoutCircle
import com.example.runnincle.framework.presentation.create_program.composable.CreateProgramWorkoutList
import com.example.runnincle.ui.theme.RunnincleTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreateProgramFragment: Fragment() {

    private val createProgramViewModel: CreateProgramViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        println("test $createProgramViewModel")
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
                workouts.add(Workout(
                    id = "14",
                    programId = "22",
                    name = "걷기",
                    set = 1,
                    work = 130,
                    coolDown = 0,
                    order = 2,
                ))
                RunnincleTheme {
                    CreateProgramComposeView(workouts)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CreateProgramComposeView(workouts: List<Workout>) {
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberBottomSheetState(BottomSheetValue.Collapsed)
    )
    val radius = (70 * (1f - scaffoldState.currentFraction)).dp + 30.dp
    val onSheetClick: () -> Unit = {
        scope.launch {
            if (scaffoldState.bottomSheetState.isCollapsed) {
                scaffoldState.bottomSheetState.expand()
            } else {
                scaffoldState.bottomSheetState.collapse()
            }
        }
    }
    BottomSheetScaffold(
        modifier = Modifier.fillMaxWidth(),
        scaffoldState = scaffoldState,
        sheetShape = RoundedCornerShape(topStart = radius, topEnd = radius),
        topBar = { CreateProgramTopAppBar() },
        sheetContent = {
            CreateProgramFragmentBottomSheet(scaffoldState, onSheetClick)
        },
        sheetPeekHeight = 70.dp
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
                CreateProgramWorkoutCircle(workouts)
                CreateProgramWorkoutList(workouts)
            }
        }
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
        CreateProgramWorkoutList(workouts)
    }

}