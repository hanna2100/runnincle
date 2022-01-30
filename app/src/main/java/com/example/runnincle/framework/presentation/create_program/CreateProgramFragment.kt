package com.example.runnincle.framework.presentation.create_program

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.runnincle.framework.presentation.composable.*
import com.example.runnincle.framework.presentation.create_program.composable.CreateProgramTopAppBar
import com.example.runnincle.framework.presentation.create_program.composable.CreateProgramWorkoutCircle
import com.example.runnincle.framework.presentation.create_program.composable.CreateProgramWorkoutList
import com.example.runnincle.framework.presentation.create_program.composable.ShowEditProgramNameDialog
import com.example.runnincle.showToastMessage
import com.example.runnincle.ui.theme.RunnincleTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@AndroidEntryPoint
class CreateProgramFragment: Fragment() {

    private val viewModel: CreateProgramViewModel by viewModels()
    private lateinit var callback: OnBackPressedCallback
    private lateinit var scaffoldState: BottomSheetScaffoldState

    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val workouts = viewModel.workouts
        val programName = viewModel.programName
        val isShowingEditProgramNameDialog = viewModel.isShowingEditProgramNameDialog

        val name = viewModel.name
        val workMin = viewModel.workMin
        val workSec = viewModel.workSec
        val coolDownMin = viewModel.coolDownMin
        val coolDownSec = viewModel.coolDownSec
        val isSkipLastCoolDown = viewModel.isSkipLastCoolDown
        val set = viewModel.set
        val timerColor = viewModel.timerColor

        observeData()

        return ComposeView(requireContext()).apply {
            setContent {
                RunnincleTheme {
                    val scope = rememberCoroutineScope()
                    scaffoldState = rememberBottomSheetScaffoldState(
                        bottomSheetState = rememberBottomSheetState(BottomSheetValue.Collapsed)
                    )
                    val radius = (70 * (1f - scaffoldState.currentFraction)).dp + 30.dp
                    val onSaveClick: () -> Unit = {
                        scope.launch {
                            viewModel.addWorkoutToTheList(
                                name = name.value,
                                workoutMin = workMin.value,
                                workoutSec = workSec.value,
                                coolDownMin = coolDownMin.value,
                                coolDownSec = coolDownSec.value,
                                isSkipLastCoolDown = isSkipLastCoolDown.value,
                                set = set.value,
                                timerColor = timerColor.value
                            )
                            scaffoldState.bottomSheetState.collapse()
                            viewModel.clearBottomSheet()
                        }
                    }

                    val onCollapsedSheetClick: () -> Unit = {
                        scope.launch {
                            scaffoldState.bottomSheetState.expand()
                        }
                    }
                    callback = object : OnBackPressedCallback(true) {
                        override fun handleOnBackPressed() {
                            if (scaffoldState.bottomSheetState.isExpanded) {
                                scope.launch {
                                    scaffoldState.bottomSheetState.collapse()
                                }
                            } else {
                                // 프로그램 리스트 화면으로 돌아가기
                            }
                        }
                    }
                    requireActivity().onBackPressedDispatcher.addCallback(this@CreateProgramFragment.viewLifecycleOwner, callback)

                    BottomSheetScaffold(
                        modifier = Modifier.fillMaxWidth(),
                        scaffoldState = scaffoldState,
                        sheetShape = RoundedCornerShape(topStart = radius, topEnd = radius),
                        topBar = { CreateProgramTopAppBar() },
                        sheetContent = {
                            CreateProgramFragmentBottomSheet(
                                scaffoldState = scaffoldState,
                                onSaveClick = onSaveClick,
                                onCollapsedSheetClick = onCollapsedSheetClick,
                                name = name,
                                workMin = workMin,
                                workSec = workSec,
                                coolDownMin = coolDownMin,
                                coolDownSec = coolDownSec,
                                isSkipLastCoolDown = isSkipLastCoolDown,
                                set = set,
                                timerColor = timerColor
                            )
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
                                CreateProgramWorkoutCircle(
                                    workouts = workouts,
                                    programName = programName.value,
                                    onProgramNameClick = {
                                        isShowingEditProgramNameDialog.value = true
                                    }
                                )
                                CreateProgramWorkoutList(workouts)
                            }
                        }
                    }

                    if(isShowingEditProgramNameDialog.value) {
                        ShowEditProgramNameDialog(
                            onDismissRequest = {
                                isShowingEditProgramNameDialog.value = false
                            },
                            onSetProgramName = { newName ->
                                programName.value = newName
                                isShowingEditProgramNameDialog.value = false
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }

    private fun observeData() {
        viewModel.errorStatus.observe(this.viewLifecycleOwner) { status ->
            status?.let {
                when (status) {
                    CreateProgramErrorStatus.NUMBER_FORMAT -> {
                        context?.showToastMessage("운동 시간에 숫자를 입력해 주세요.")
                    }
                    CreateProgramErrorStatus.SET_FORMAT -> {
                        context?.showToastMessage("SET는 1 이상이어야 합니다.")
                    }
                    CreateProgramErrorStatus.WORK_FORMAT -> {
                        context?.showToastMessage("운동시간은 최소 1초 이상이어야 합니다.")
                    }
                }
            }
        }
    }
}