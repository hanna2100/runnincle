package com.example.runnincle.framework.presentation.program_list

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import com.example.runnincle.business.domain.model.Program
import com.example.runnincle.business.domain.model.Workout
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

    suspend fun getAllPrograms(): List<Program> = withContext(Dispatchers.IO) {
        programListInteractors.getAllProgram()
    }

    suspend fun getWorkoutsOfProgram(programId: String): List<Workout> {
        return programListInteractors.getWorkoutsOfProgram(programId)
    }

}