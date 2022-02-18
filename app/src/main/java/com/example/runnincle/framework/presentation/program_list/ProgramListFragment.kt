package com.example.runnincle.framework.presentation.program_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.compose.runtime.*
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.runnincle.ui.theme.RunnincleTheme
import com.example.runnincle.business.domain.model.ParcelableWorkout
import com.example.runnincle.business.domain.model.Program
import com.example.runnincle.business.domain.model.Workout
import com.example.runnincle.business.domain.model.Workout.Companion.toParcelableWorkout
import com.example.runnincle.framework.presentation.program_list.composable.ProgramListWithSearchBar
import com.example.runnincle.startFloatingServiceWithCommand
import com.example.runnincle.util.FloatingServiceCommand
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope

@AndroidEntryPoint
class ProgramListFragment: Fragment() {

    private val viewModel:ProgramListViewModel by viewModels()
    private var callback: OnBackPressedCallback? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val programs = viewModel.programs

        viewModel.launch {
            val cachePrograms = viewModel.getAllPrograms()
            cachePrograms.forEach { program->
                println("program = $program")
                val workoutsOfProgram = viewModel.getWorkoutsOfProgram(program.id)
                programs.put(program, workoutsOfProgram)
            }
        }

        return ComposeView(requireContext()).apply {
            setContent {
                RunnincleTheme {
                    val scope = rememberCoroutineScope()
                    setOnBackPressedCallback(scope)
                    ProgramListWithSearchBar(
                        programs = programs,
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

    private fun setOnBackPressedCallback(scope: CoroutineScope) {
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
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