package com.example.runnincle.framework.presentation.create_program

import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.lang.NumberFormatException
import javax.inject.Inject

enum class CreateProgramErrorStatus{
    NUMBER_FORMAT, SET_FORMAT, WORK_FORMAT
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

    val workouts = mutableListOf<Workout>()
    val programName = mutableStateOf(resourcesProvider.getString(R.string.enter_program_name))
    val isShowingEditProgramNameDialog = mutableStateOf(false)

    val errorStatus = MutableLiveData<CreateProgramErrorStatus?>()
    val bottomSheetSaveButtonStatus = MutableLiveData(BottomSheetSaveButtonStatus.SAVE)
    var editingTarget: Workout? = null

    val name = mutableStateOf("")
    val workMin = mutableStateOf("")
    val workSec = mutableStateOf("")
    val coolDownMin = mutableStateOf("")
    val coolDownSec = mutableStateOf("")
    val isSkipLastCoolDown = mutableStateOf(false)
    val set = mutableStateOf("")
    val timerColor = mutableStateOf(ColorPalette.Primary[0])

    fun addWorkoutToTheList(
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
            order = workouts.size,
            isSkipLastCoolDown = isSkipLastCoolDown,
            timerColor = timerColor
        )
        workouts.add(newWorkout)
    }

    fun editWorkoutToTheList(
        name: String,
        workoutMin: String,
        workoutSec: String,
        coolDownMin: String,
        coolDownSec: String,
        isSkipLastCoolDown: Boolean,
        set: String,
        timerColor: Color,
        order: Int
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
            order = order,
            isSkipLastCoolDown = isSkipLastCoolDown,
            timerColor = timerColor
        )
        workouts.removeAt(order)
        workouts.add(order, newWorkout)
    }

    private fun clearBottomSheet() {
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
        editingTarget = workout
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
                    order = editingTarget?.order?: return
                )
                scaffoldState.bottomSheetState.collapse()
                clearBottomSheet()
            }
        }
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
}