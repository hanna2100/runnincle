package com.example.runnincle.framework.presentation.create_program

import android.view.View
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.example.runnincle.R
import com.example.runnincle.business.domain.model.Workout
import com.example.runnincle.business.domain.model.Workout.Companion.getMinValueAndIgnoreSecValue
import com.example.runnincle.business.domain.model.Workout.Companion.getSecValueAndIgnoreMinValue
import com.example.runnincle.business.domain.util.ResourcesProvider
import com.example.runnincle.business.interactors.create_program.CreateProgramInteractors
import com.example.runnincle.framework.presentation.create_program.CreateProgramErrorStatus.*
import com.example.runnincle.framework.presentation.create_program.composable.ShowEditProgramNameDialog
import com.example.runnincle.util.BaseViewModel
import com.vanpra.composematerialdialogs.color.ColorPalette
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.lang.NumberFormatException
import javax.inject.Inject

enum class CreateProgramErrorStatus{
    NUMBER_FORMAT, SET_FORMAT, WORK_FORMAT, WORKOUTS_EMPTY, PROGRAM_NAME_EMPTY,
}

enum class BottomSheetSaveButtonStatus {
    SAVE, EDIT
}

@HiltViewModel
class CreateProgramViewModel
@Inject
constructor(
    private val createProgramInteractors: CreateProgramInteractors,
    private val resourcesProvider: ResourcesProvider
): BaseViewModel() {

    val workouts = mutableStateOf<MutableList<Workout>>(mutableListOf())

    val programName = mutableStateOf(resourcesProvider.getString(R.string.enter_program_name))
    val isShowingEditProgramNameDialog = mutableStateOf(false)

    val errorStatus = MutableLiveData<CreateProgramErrorStatus?>()
    val bottomSheetSaveButtonStatus = mutableStateOf(BottomSheetSaveButtonStatus.SAVE)
    var editingTargetIndex: Int? = null

    val name = mutableStateOf("")
    val workMin = mutableStateOf("")
    val workSec = mutableStateOf("")
    val coolDownMin = mutableStateOf("")
    val coolDownSec = mutableStateOf("")
    val isSkipLastCoolDown = mutableStateOf(false)
    val set = mutableStateOf("")
    val timerColor = mutableStateOf(ColorPalette.Primary[0])

    private fun addWorkoutToTheList(
        name: String,
        workoutMin: String,
        workoutSec: String,
        coolDownMin: String,
        coolDownSec: String,
        isSkipLastCoolDown: Boolean,
        set: String,
        timerColor: Color
    ) {
        val wm: Int
        val ws: Int
        val cdm: Int
        val cds: Int
        val st: Int
        try {
            wm = if (workoutMin.isBlank()) {
                0
            } else {
                workoutMin.toInt()
            }
            ws = if (workoutSec.isBlank()) {
                0
            } else {
                workoutSec.toInt()
            }
            if (wm <= 0 && ws<=0) {
                errorStatus.value = WORK_FORMAT
                return
            }
            cdm = if (coolDownMin.isBlank()) {
                0
            } else {
                coolDownMin.toInt()
            }
            cds = if (coolDownSec.isBlank()) {
                0
            } else {
                coolDownSec.toInt()
            }
            if (set.isBlank()) {
                errorStatus.value = SET_FORMAT
                return
            } else {
                st = set.toInt()
            }
        } catch (e: NumberFormatException) {
            errorStatus.value = NUMBER_FORMAT
            return
        }
        val newWorkout = Workout(
            name = name,
            set = st,
            work = (wm * 60) + ws,
            coolDown = (cdm * 60) + cds,
            isSkipLastCoolDown = isSkipLastCoolDown,
            timerColor = timerColor
        )
        workouts.value.add(newWorkout)
    }

    private fun editWorkoutToTheList(
        name: String,
        workoutMin: String,
        workoutSec: String,
        coolDownMin: String,
        coolDownSec: String,
        isSkipLastCoolDown: Boolean,
        set: String,
        timerColor: Color,
        index: Int
    ) {
        val wm: Int
        val ws: Int
        val cdm: Int
        val cds: Int
        val st: Int
        try {
            wm = if (workoutMin.isBlank()) {
                0
            } else {
                workoutMin.toInt()
            }
            ws = if (workoutSec.isBlank()) {
                0
            } else {
                workoutSec.toInt()
            }
            if (wm <= 0 && ws<=0) {
                errorStatus.value = WORK_FORMAT
                return
            }
            cdm = if (coolDownMin.isBlank()) {
                0
            } else {
                coolDownMin.toInt()
            }
            cds = if (coolDownSec.isBlank()) {
                0
            } else {
                coolDownSec.toInt()
            }
            if (set.isBlank()) {
                errorStatus.value = SET_FORMAT
                return
            } else {
                st = set.toInt()
            }
        } catch (e: NumberFormatException) {
            errorStatus.value = NUMBER_FORMAT
            return
        }
        val newWorkout = Workout(
            name = name,
            set = st,
            work = (wm * 60) + ws,
            coolDown = (cdm * 60) + cds,
            isSkipLastCoolDown = isSkipLastCoolDown,
            timerColor = timerColor
        )
        workouts.value.removeAt(index)
        workouts.value.add(index, newWorkout)
    }

    fun clearBottomSheet() {
        name.value = ""
        workMin.value = ""
        workSec.value = ""
        coolDownMin.value = ""
        coolDownSec.value = ""
        isSkipLastCoolDown.value = false
        set.value = ""
        timerColor.value = ColorPalette.Primary[0]

        bottomSheetSaveButtonStatus.value = BottomSheetSaveButtonStatus.SAVE
    }

    fun setUpBottomSheetForEdit(workout: Workout) {
        name.value = workout.name
        workMin.value = workout.getMinValueAndIgnoreSecValue(workout.work).toString()
        workSec.value = workout.getSecValueAndIgnoreMinValue(workout.work).toString()
        coolDownMin.value = workout.getMinValueAndIgnoreSecValue(workout.coolDown).toString()
        coolDownSec.value = workout.getSecValueAndIgnoreMinValue(workout.coolDown).toString()
        set.value = workout.set.toString()
        isSkipLastCoolDown.value = workout.isSkipLastCoolDown
        timerColor.value = workout.timerColor

        bottomSheetSaveButtonStatus.value = BottomSheetSaveButtonStatus.EDIT
        editingTargetIndex = workouts.value.indexOf(workout)
    }

    @OptIn(ExperimentalMaterialApi::class)
    suspend fun onBottomSheetSaveButtonClick(scaffoldState: BottomSheetScaffoldState) {
        when (bottomSheetSaveButtonStatus.value) {
            BottomSheetSaveButtonStatus.SAVE -> {
                addWorkoutToTheList(
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
                clearBottomSheet()
            }
            BottomSheetSaveButtonStatus.EDIT -> {
                editWorkoutToTheList(
                    name = name.value,
                    workoutMin = workMin.value,
                    workoutSec = workSec.value,
                    coolDownMin = coolDownMin.value,
                    coolDownSec = coolDownSec.value,
                    isSkipLastCoolDown = isSkipLastCoolDown.value,
                    set = set.value,
                    timerColor = timerColor.value,
                    index = editingTargetIndex?: return
                )
                scaffoldState.bottomSheetState.collapse()
                clearBottomSheet()
            }
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    suspend fun onBottomSheetDeleteButtonClick(scaffoldState: BottomSheetScaffoldState) {
        workouts.value.removeAt(editingTargetIndex?: return)
        scaffoldState.bottomSheetState.collapse()
        clearBottomSheet()
    }

    @Composable
    fun setUpEditProgramNameDialog() {
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

    fun insertNewProgram(view: View) {
        val valid = validateProgramData()
        if (!valid) {
            return
        }
        viewModelScope.launch {
            createProgramInteractors.insertNewProgram(
                name = programName.value,
                workouts = workouts.value
            )
        }
        moveToProgramListFragment(view)
    }

    private fun validateProgramData(): Boolean {
        return if (workouts.value.isEmpty()) {
            errorStatus.value = WORKOUTS_EMPTY
            false
        } else if (programName.value.isEmpty() ||
            programName.value == resourcesProvider.getString(R.string.enter_program_name)
        ) {
            errorStatus.value = PROGRAM_NAME_EMPTY
            false
        } else {
            true
        }
    }

    fun moveToProgramListFragment(view: View) {
        val action = CreateProgramFragmentDirections.actionCreateProgramFragmentToProgramListFragment()
        view.findNavController().navigate(action)
    }
}