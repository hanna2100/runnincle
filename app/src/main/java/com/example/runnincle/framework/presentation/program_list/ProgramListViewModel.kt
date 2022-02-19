package com.example.runnincle.framework.presentation.program_list

import android.content.Context
import android.view.View
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.navigation.findNavController
import com.example.runnincle.business.domain.model.ParcelableWorkout
import com.example.runnincle.business.domain.model.Program
import com.example.runnincle.business.domain.model.Workout
import com.example.runnincle.business.domain.model.Workout.Companion.toParcelableWorkout
import com.example.runnincle.business.domain.util.DateUtil
import com.example.runnincle.business.interactors.program_list.ProgramListInteractors
import com.example.runnincle.util.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class ProgramListViewModel
@Inject
constructor(
    private val programListInteractors: ProgramListInteractors,
): BaseViewModel() {

    var programs = mutableStateMapOf<Program, List<Workout>>()
        private set

    fun setMapOfProgram() {
        launch {
            val cachePrograms = getAllPrograms()
            cachePrograms.forEach { program->
                val workoutsOfProgram = getWorkoutsOfProgram(program.id)
                programs[program] = workoutsOfProgram
            }
        }
    }

    suspend fun getAllPrograms(): List<Program> = withContext(Dispatchers.IO) {
        programListInteractors.getAllProgram()
    }

    suspend fun getWorkoutsOfProgram(programId: String): List<Workout> {
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

}