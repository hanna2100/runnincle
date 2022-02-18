package com.example.runnincle.framework.presentation.create_program

import android.view.View
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import com.example.runnincle.R
import com.example.runnincle.business.domain.model.Workout
import com.example.runnincle.business.domain.model.Workout.Companion.getMinValueAndIgnoreSecValue
import com.example.runnincle.business.domain.model.Workout.Companion.getSecValueAndIgnoreMinValue
import com.example.runnincle.business.domain.util.ResourcesProvider
import com.example.runnincle.business.interactors.create_program.CreateProgramInteractors
import com.example.runnincle.framework.presentation.create_program.CreateProgramErrorStatus.*
import com.example.runnincle.framework.presentation.create_program.composable.ShowEditProgramNameDialog
import com.example.runnincle.ui.theme.TimerColorPalette
import com.example.runnincle.util.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import javax.inject.Inject

enum class CreateProgramErrorStatus{
    WORK_NAME_EMPTY, SET_FORMAT, WORK_FORMAT, WORKOUTS_EMPTY, PROGRAM_NAME_EMPTY,
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

    val programName = mutableStateOf(resourcesProvider.getString(R.string.enter_name))
    val isShowingEditProgramNameDialog = mutableStateOf(false)

    val errorStatus = MutableLiveData<CreateProgramErrorStatus?>()
    val bottomSheetSaveButtonStatus = mutableStateOf(BottomSheetSaveButtonStatus.SAVE)
    var editingTargetIndex: Int? = null

    val name = mutableStateOf("")
    val workMin = mutableStateOf(0)
    val workSec = mutableStateOf(0)
    val coolDownMin = mutableStateOf(0)
    val coolDownSec = mutableStateOf(0)
    val isSkipLastCoolDown = mutableStateOf(false)
    val set = mutableStateOf(1)
    val timerColor = mutableStateOf(TimerColorPalette[0])
    var onAddAnimKey = mutableStateOf(false)
    var onEditAnimKey = mutableStateOf(false)

    private fun validateWorkoutFormat(
        name: String,
        workoutMin: Int,
        workoutSec: Int,
        set: Int,
    ): Boolean {
        if (workoutMin <= 0 && workoutSec<=0) {
            errorStatus.value = WORK_FORMAT
            return false
        }
        if (name.isBlank()) {
            errorStatus.value = WORK_NAME_EMPTY
            return false
        }
        if (set <= 0) {
            errorStatus.value = SET_FORMAT
            return false
        }
        return true
    }

    private fun addWorkoutToTheList(
        name: String,
        workoutMin: Int,
        workoutSec: Int,
        coolDownMin: Int,
        coolDownSec: Int,
        isSkipLastCoolDown: Boolean,
        set: Int,
        timerColor: Color
    ): Boolean {
        val isValid = validateWorkoutFormat(name, workoutMin, workoutSec, set)
        if (!isValid) return false

        val newWorkout = Workout(
            name = name,
            set = set,
            work = (workoutMin * 60) + workoutSec,
            coolDown = (coolDownMin * 60) + coolDownSec,
            isSkipLastCoolDown = isSkipLastCoolDown,
            timerColor = timerColor
        )
        workouts.value.add(newWorkout)
        return true
    }

    private fun editWorkoutToTheList(
        name: String,
        workoutMin: Int,
        workoutSec: Int,
        coolDownMin: Int,
        coolDownSec: Int,
        isSkipLastCoolDown: Boolean,
        set: Int,
        timerColor: Color,
        index: Int
    ): Boolean {
        val isValid = validateWorkoutFormat(name, workoutMin, workoutSec, set)
        if (!isValid) return false
        val newWorkout = Workout(
            name = name,
            set = set,
            work = (workoutMin * 60) + workoutSec,
            coolDown = (coolDownMin * 60) + coolDownSec,
            isSkipLastCoolDown = isSkipLastCoolDown,
            timerColor = timerColor
        )
        workouts.value.removeAt(index)
        workouts.value.add(index, newWorkout)
        return true
    }

    fun clearBottomSheet() {
        name.value = ""
        workMin.value = 0
        workSec.value = 0
        coolDownMin.value = 0
        coolDownSec.value = 0
        isSkipLastCoolDown.value = false
        set.value = 1
        timerColor.value = TimerColorPalette[0]

        bottomSheetSaveButtonStatus.value = BottomSheetSaveButtonStatus.SAVE
    }

    fun setUpBottomSheetForEdit(workout: Workout) {
        name.value = workout.name
        workMin.value = workout.getMinValueAndIgnoreSecValue(workout.work)
        workSec.value = workout.getSecValueAndIgnoreMinValue(workout.work)
        coolDownMin.value = workout.getMinValueAndIgnoreSecValue(workout.coolDown)
        coolDownSec.value = workout.getSecValueAndIgnoreMinValue(workout.coolDown)
        set.value = workout.set
        isSkipLastCoolDown.value = workout.isSkipLastCoolDown
        timerColor.value = workout.timerColor

        bottomSheetSaveButtonStatus.value = BottomSheetSaveButtonStatus.EDIT
        editingTargetIndex = workouts.value.indexOf(workout)
    }

    @OptIn(ExperimentalMaterialApi::class)
    suspend fun onBottomSheetSaveButtonClick(scaffoldState: BottomSheetScaffoldState): Boolean {
        val isSuccess = addWorkoutToTheList(
            name = name.value,
            workoutMin = workMin.value,
            workoutSec = workSec.value,
            coolDownMin = coolDownMin.value,
            coolDownSec = coolDownSec.value,
            isSkipLastCoolDown = isSkipLastCoolDown.value,
            set = set.value,
            timerColor = timerColor.value
        )
        if (isSuccess) {
            scaffoldState.bottomSheetState.collapse()
            clearBottomSheet()
        }
        return isSuccess
    }

    @OptIn(ExperimentalMaterialApi::class)
    suspend fun onBottomSheetEditButtonClick(scaffoldState: BottomSheetScaffoldState): Boolean {
        val isSuccess = editWorkoutToTheList(
            name = name.value,
            workoutMin = workMin.value,
            workoutSec = workSec.value,
            coolDownMin = coolDownMin.value,
            coolDownSec = coolDownSec.value,
            isSkipLastCoolDown = isSkipLastCoolDown.value,
            set = set.value,
            timerColor = timerColor.value,
            index = editingTargetIndex?: return false
        )
        if (isSuccess) {
            scaffoldState.bottomSheetState.collapse()
            clearBottomSheet()
        }
        return isSuccess
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

    suspend fun insertNewProgram() {
        createProgramInteractors.insertNewProgram(
            name = programName.value,
            workouts = workouts.value
        )
    }

    suspend fun updateProgram(programId: String) {
        createProgramInteractors.updateProgram(
            programId = programId,
            name = programName.value,
            workouts = workouts.value
        )
    }

    fun validateProgramData(): Boolean {
        return if (workouts.value.isEmpty()) {
            errorStatus.value = WORKOUTS_EMPTY
            false
        } else if (programName.value.isEmpty() || programName.value == resourcesProvider.getString(R.string.enter_name)) {
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

    fun launchAddAnimation() {
        launch {
            onAddAnimKey.value = false
            onAddAnimKey.value = true
        }
    }

    fun launchEditAnimation() {
        launch {
            onEditAnimKey.value = true
            delay(200)
            onEditAnimKey.value = false
        }
    }

}