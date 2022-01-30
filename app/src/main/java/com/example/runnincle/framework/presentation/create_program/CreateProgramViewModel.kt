package com.example.runnincle.framework.presentation.create_program

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.runnincle.business.domain.model.Workout
import com.example.runnincle.business.interactors.create_program.CreateProgramInteractors
import com.example.runnincle.framework.presentation.create_program.CreateProgramErrorStatus.*
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

@HiltViewModel
class CreateProgramViewModel
@Inject
constructor(
    private val createProgramInteractors: CreateProgramInteractors
): BaseViewModel() {

    val workouts = mutableListOf<Workout>()
    val programName = mutableStateOf("프로그램 이름")
    val isShowingEditProgramNameDialog = mutableStateOf(false)

    val errorStatus = MutableLiveData<CreateProgramErrorStatus?>()
    
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

    fun clearBottomSheet() {
        name.value = ""
        workMin.value = ""
        workSec.value = ""
        coolDownMin.value = ""
        coolDownSec.value = ""
        isSkipLastCoolDown.value = false
        set.value = ""
        timerColor.value = ColorPalette.Primary[0]
    }
}