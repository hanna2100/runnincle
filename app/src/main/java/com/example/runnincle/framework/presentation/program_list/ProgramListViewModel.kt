package com.example.runnincle.framework.presentation.program_list

import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.navigation.findNavController
import com.example.runnincle.business.domain.model.ParcelableWorkout
import com.example.runnincle.business.domain.model.Program
import com.example.runnincle.business.domain.model.Workout
import com.example.runnincle.business.domain.model.Workout.Companion.toParcelableWorkout
import com.example.runnincle.business.interactors.program_list.ProgramListInteractors
import com.example.runnincle.ui.theme.TimerColorPalette
import com.example.runnincle.util.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject


@HiltViewModel
class ProgramListViewModel
@Inject
constructor(
    private val programListInteractors: ProgramListInteractors,
): BaseViewModel() {

    var programs = mutableStateMapOf<Program, List<Workout>>()
        private set

    var programToBeDeleted = mutableStateOf(Program("","",""))

    // Setting
    var overlaySize = mutableStateOf(3)
    var totalTimerColor = mutableStateOf(TimerColorPalette.last())
    var coolDownTimerColor = mutableStateOf(TimerColorPalette.last())
    var isTTSUsed = mutableStateOf(false)
    var searchChipList = mutableStateOf(mutableListOf<String>())

    fun setMapOfProgram() {
        launch {
            val cachePrograms = getAllPrograms()
            cachePrograms.forEach { program->
                val workoutsOfProgram = getWorkoutsOfProgram(program.id)
                programs[program] = workoutsOfProgram
            }
        }
    }

    private suspend fun getAllPrograms(): List<Program> = withContext(Dispatchers.IO) {
        programListInteractors.getAllProgram()
    }

    private suspend fun getWorkoutsOfProgram(programId: String): List<Workout> {
        return programListInteractors.getWorkoutsOfProgram(programId)
    }

    fun moveToCreateProgram(view: View?) {
        val action = ProgramListFragmentDirections
            .actionProgramListFragmentToCreateProgramFragment(null, null)
        view?.findNavController()?.navigate(action)
    }

    fun moveToEditProgram(
        view: View?,
        program: Program,
        workouts: List<Workout>
    ) {
        val parcelableWorkouts = mutableListOf<ParcelableWorkout>()
        workouts.forEach {
            parcelableWorkouts.add(it.toParcelableWorkout())
        }
        val action = ProgramListFragmentDirections
            .actionProgramListFragmentToCreateProgramFragment(
                program,
                parcelableWorkouts.toTypedArray()
            )
        view?.findNavController()?.navigate(action)
    }

    fun deleteProgram(programId: String) {
        launch {
            programListInteractors.deleteProgram(programId)
        }
    }

    fun searchProgram(searchText: String) {
        launch {
            programs.clear()
            val searchedProgram = programListInteractors.searchProgram(searchText)
            searchedProgram.forEach { program->
                val workoutsOfProgram = getWorkoutsOfProgram(program.id)
                programs[program] = workoutsOfProgram
            }
        }
    }

    suspend fun getSettingValue() {
        overlaySize.value = programListInteractors.getOverlaySize()
        totalTimerColor.value = programListInteractors.getTotalTimerColor()
        coolDownTimerColor.value = programListInteractors.getCoolDownTimerColor()
        isTTSUsed.value = programListInteractors.isTTSUsed()
    }

    fun setSavedSearchWords() {
        launch {
            val savedList = programListInteractors.getSavedSearchWords()
            searchChipList.value.clear()
            searchChipList.value.addAll(savedList)
        }
    }

    suspend fun saveSettingProperty() {
        programListInteractors.saveSettingProperty(
            overlaySize.value,
            totalTimerColor.value,
            coolDownTimerColor.value,
            isTTSUsed.value
        )
    }

    suspend fun saveSearchWordToSharedPreference(text: String) {
        programListInteractors.saveSearchWord(text)
    }

    suspend fun removeSearchWorldToSharedPreference(text: String) {
        programListInteractors.removeSearchWord(text)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun saveAdRemovalPeriod(localDate: LocalDate) {
        programListInteractors.saveAdRemovalPeriod(localDate)
    }

    suspend fun getAdRemovalPeriod(): LocalDate {
        return programListInteractors.getAdRemovalPeriod()
    }
}