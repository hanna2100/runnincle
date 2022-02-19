package com.example.runnincle.framework.presentation.program_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.runnincle.ui.theme.RunnincleTheme
import com.example.runnincle.business.domain.model.ParcelableWorkout
import com.example.runnincle.business.domain.model.Program
import com.example.runnincle.business.domain.model.Workout
import com.example.runnincle.business.domain.model.Workout.Companion.toParcelableWorkout
import com.example.runnincle.framework.presentation.program_list.composable.AdRemoveDialog
import com.example.runnincle.framework.presentation.program_list.composable.ProgramListWithSearchBar
import com.example.runnincle.framework.presentation.program_list.composable.SettingModalBottomSheet
import com.example.runnincle.startFloatingServiceWithCommand
import com.example.runnincle.util.FloatingServiceCommand
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProgramListFragment: Fragment() {

    private val viewModel:ProgramListViewModel by viewModels()
    private var callback: OnBackPressedCallback? = null

    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val programs = viewModel.programs
        val overlaySize = viewModel.overlaySize
        val totalTimerColor = viewModel.totalTimerColor
        val isTTSUsed = viewModel.isTTSUsed
        val searchChipList = viewModel.searchChipList

        viewModel.setMapOfProgram()
        viewModel.setSavedSearchWords()

        return ComposeView(requireContext()).apply {
            setContent {
                RunnincleTheme(darkSystemBar = true) {
                    val scope = rememberCoroutineScope()
                    val modalBottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
                    val dialogState = rememberMaterialDialogState()
                    setOnBackPressedCallback(scope, modalBottomSheetState)

                    ModalBottomSheetLayout(
                        modifier = Modifier.fillMaxWidth(),
                        sheetState = modalBottomSheetState,
                        sheetShape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
                        sheetContent = {
                            SettingModalBottomSheet(
                                overlaySize = overlaySize,
                                totalTimerColor = totalTimerColor,
                                isTtsUsed = isTTSUsed,
                                onAdRemoveClick = {
                                    dialogState.show()
                                },
                                onSaveClick = {
                                    scope.launch {
                                        viewModel.saveSettingProperty()
                                        modalBottomSheetState.hide()
                                    }
                                }
                            )
                        }
                    ) {
                        ProgramListWithSearchBar(
                            programs = programs,
                            chipList = searchChipList,
                            onSearchButtonClick = { searchText ->
                                scope.launch {
                                    viewModel.searchProgram(searchText)
                                    viewModel.saveSearchWordToSharedPreference(searchText)
                                }
                            },
                            onChipClick = { chipText ->
                                viewModel.searchProgram(chipText)
                            },
                            onChipDeleteButtonClick = { chipText ->
                              scope.launch {
                                  viewModel.removeSearchWorldToSharedPreference(chipText)
                              }
                            },
                            onProgramCardClick = { program, workouts ->
                                startFloatingService(program, workouts)
                            },
                            onProgramEditButtonClick = { program, workouts ->
                                viewModel.moveToEditProgram(view, program, workouts)
                            },
                            onProgramDeleteButtonClick = { program ->
                                viewModel.deleteProgram(program.id)
                                viewModel.programs.remove(program)
                            },
                            onFloatingAddButtonClick = {
                                viewModel.moveToCreateProgram(view)
                            },
                            onFloatingSettingButtonClick = {
                                scope.launch {
                                    viewModel.getSettingValue()
                                    modalBottomSheetState.show()
                                }
                            }
                        )
                    }
                    AdRemoveDialog(
                        dialogState = dialogState,
                        onRemoveAdFor3DaysClick = {

                        },
                        onRemoveAdForeverClick = {

                        }
                    )
                }
            }
        }
    }

    private fun startFloatingService(
        program: Program,
        workouts: List<Workout>
    ) {
        val parcelableWorkouts = ArrayList<ParcelableWorkout>()
        workouts.forEach { workout ->
            parcelableWorkouts.add(workout.toParcelableWorkout())
        }

        activity?.startFloatingServiceWithCommand(
            command = FloatingServiceCommand.OPEN,
            program = program,
            workouts = parcelableWorkouts
        )
    }

    @OptIn(ExperimentalMaterialApi::class)
    private fun setOnBackPressedCallback(scope: CoroutineScope, modalBottomSheetState: ModalBottomSheetState) {
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if(modalBottomSheetState.isVisible) {
                    scope.launch {
                        modalBottomSheetState.hide()
                    }
                } else {
                    requireActivity().finish()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            this.viewLifecycleOwner,
            callback!!
        )
    }

    override fun onDetach() {
        super.onDetach()
        callback?.remove()
    }
}

