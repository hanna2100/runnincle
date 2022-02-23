package com.devhanna91.runnincle.framework.presentation.create_program

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.devhanna91.runnincle.R
import com.devhanna91.runnincle.business.domain.model.Workout.Companion.toWorkout
import com.devhanna91.runnincle.framework.presentation.composable.*
import com.devhanna91.runnincle.framework.presentation.create_program.composable.CreateProgramTopAppBar
import com.devhanna91.runnincle.framework.presentation.create_program.composable.CreateProgramWorkoutCircle
import com.devhanna91.runnincle.framework.presentation.create_program.composable.CreateProgramWorkoutList
import com.devhanna91.runnincle.showToastMessage
import com.devhanna91.runnincle.ui.theme.RunnincleTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class MODE {
    CREATE, EDIT
}

@ExperimentalMaterialApi
@AndroidEntryPoint
class CreateProgramFragment: Fragment() {

    private val viewModel: CreateProgramViewModel by viewModels()
    private var callback: OnBackPressedCallback? = null
    private lateinit var scaffoldState: BottomSheetScaffoldState
    private var mode = MODE.CREATE

    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val workouts = viewModel.workouts.value
        val programName = viewModel.programName

        val name = viewModel.name
        val workMin = viewModel.workMin
        val workSec = viewModel.workSec
        val coolDownMin = viewModel.coolDownMin
        val coolDownSec = viewModel.coolDownSec
        val isSkipLastCoolDown = viewModel.isSkipLastCoolDown
        val set = viewModel.set
        val timerColor = viewModel.timerColor
        val onAddAnimKey = viewModel.onAddAnimKey
        val onEditAnimKey = viewModel.onEditAnimKey

        observeData()

        return ComposeView(requireContext()).apply {
            setContent {
                RunnincleTheme {
                    val scope = rememberCoroutineScope()

                    scaffoldState = setBottomSheetScaffoldState()
                    val radius = setDefaultBottomSheetCornerRadius()
                    setOnBackPressedCallback(scope)
                    viewModel.setUpEditProgramNameDialog()

                    BottomSheetScaffold(
                        modifier = Modifier.fillMaxWidth(),
                        scaffoldState = scaffoldState,
                        sheetShape = RoundedCornerShape(topStart = radius, topEnd = radius),
                        topBar = { CreateProgramTopAppBar(
                            title = if (mode == MODE.CREATE) {
                                stringResource(id = R.string.add_new_workout)
                            } else {
                                stringResource(id = R.string.edit_workout)
                           },
                            onBackClick = {
                                viewModel.moveToProgramListFragment(this)
                            },
                            onProgramSaveClick = {
                                saveProgram()
                            }
                        ) },
                        sheetContent = {
                            CreateProgramFragmentBottomSheet(
                                scaffoldState = scaffoldState,
                                buttonStatus = viewModel.bottomSheetSaveButtonStatus.value,
                                onSaveClick = {
                                    scope.launch {
                                        saveWorkout()
                                    }
                                },
                                onDeleteClick = {
                                    scope.launch {
                                        deleteWorkout()
                                    }
                                },
                                onCollapsedSheetClick = {
                                    onCollapsedSheetClick(scope)
                                },
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
                                        viewModel.isShowingEditProgramNameDialog.value = true
                                    },
                                    onAddAnimKey = onAddAnimKey.value,
                                    onEditAnimKey = onEditAnimKey.value
                                )
                                CreateProgramWorkoutList(
                                    workouts = workouts,
                                    onItemClick = { workout ->
                                        viewModel.setUpBottomSheetForEdit(workout)
                                        scope.launch {
                                            scaffoldState.bottomSheetState.expand()
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private suspend fun deleteWorkout() {
        viewModel.onBottomSheetDeleteButtonClick(scaffoldState)
        viewModel.launchEditAnimation()
    }

    private suspend fun saveWorkout() {
        when(viewModel.bottomSheetSaveButtonStatus.value) {
            BottomSheetSaveButtonStatus.SAVE -> {
                val isSuccess = viewModel.onBottomSheetSaveButtonClick(scaffoldState)
                if (isSuccess) {
                    viewModel.launchAddAnimation()
                }
            }
            BottomSheetSaveButtonStatus.EDIT -> {
                val isSuccess = viewModel.onBottomSheetEditButtonClick(scaffoldState)
                if (isSuccess) {
                    viewModel.launchEditAnimation()
                }
            }
        }
    }

    private fun saveProgram() {
        val view = this@CreateProgramFragment.requireView()
        val isValid = viewModel.validateProgramData()
        if (!isValid) {
            return
        }
        viewModel.launch {
            if (mode == MODE.CREATE) {
                viewModel.insertNewProgram()
                delay(100)
                viewModel.moveToProgramListFragment(view)
            } else if (mode == MODE.EDIT){
                val args: CreateProgramFragmentArgs by navArgs()
                val programId = args.program?.id
                if (programId != null) {
                    viewModel.updateProgram(programId = programId)
                    delay(100)
                    viewModel.moveToProgramListFragment(view)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args: CreateProgramFragmentArgs by navArgs()
        val parcelableWorkouts = args.workouts?.toCollection(ArrayList())
        parcelableWorkouts?.forEach {
            viewModel.workouts.value.add(it.toWorkout())
        }
        val program = args.program
        if (program != null) {
            viewModel.programName.value = program.name
            mode = MODE.EDIT
            viewModel.launch {
                delay(500)
                viewModel.launchAddAnimation()
            }
        }
    }

    private fun onCollapsedSheetClick(scope: CoroutineScope) {
        scope.launch {
            viewModel.clearBottomSheet()
            viewModel.bottomSheetSaveButtonStatus.value = BottomSheetSaveButtonStatus.SAVE
            scaffoldState.bottomSheetState.expand()
        }
    }

    private fun setDefaultBottomSheetCornerRadius(): Dp {
       return (70 * (1f - scaffoldState.currentFraction)).dp + 30.dp
    }

    @Composable
    private fun setBottomSheetScaffoldState(): BottomSheetScaffoldState {
        return rememberBottomSheetScaffoldState(
            bottomSheetState = rememberBottomSheetState(BottomSheetValue.Collapsed)
        )
    }

    private fun setOnBackPressedCallback(scope: CoroutineScope) {
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (scaffoldState.bottomSheetState.isExpanded) {
                    scope.launch {
                        scaffoldState.bottomSheetState.collapse()
                    }
                } else {
                    viewModel.moveToProgramListFragment(this@CreateProgramFragment.requireView())
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            this@CreateProgramFragment.viewLifecycleOwner,
            callback!!
        )
    }

    override fun onDetach() {
        super.onDetach()
        callback?.remove()
    }

    private fun observeData() {
        viewModel.errorStatus.observe(this.viewLifecycleOwner) { status ->
            status?.let {
                when (status) {
                    CreateProgramErrorStatus.WORK_NAME_EMPTY -> {
                        context?.showToastMessage(getString(R.string.enter_name))
                    }
                    CreateProgramErrorStatus.SET_FORMAT -> {
                        context?.showToastMessage(getString(R.string.set_must_be_at_least_1))
                    }
                    CreateProgramErrorStatus.WORK_FORMAT -> {
                        context?.showToastMessage(getString(R.string.work_time_must_be_at_least_1))
                    }
                    CreateProgramErrorStatus.PROGRAM_NAME_EMPTY -> {
                        context?.showToastMessage(getString(R.string.program_name_must_be_entered))
                    }
                    CreateProgramErrorStatus.WORKOUTS_EMPTY -> {
                        context?.showToastMessage(getString(R.string.workout_must_be_exist))
                    }
                }
            }
        }
    }
}