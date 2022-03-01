package com.devhanna91.runnincle.framework.presentation.program_list

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.devhanna91.runnincle.*
import com.devhanna91.runnincle.R
import com.devhanna91.runnincle.business.domain.model.ParcelableWorkout
import com.devhanna91.runnincle.business.domain.model.Program
import com.devhanna91.runnincle.business.domain.model.Workout
import com.devhanna91.runnincle.business.domain.model.Workout.Companion.toParcelableWorkout
import com.devhanna91.runnincle.framework.datasource.cache.model.Language
import com.devhanna91.runnincle.framework.presentation.program_list.composable.AdRemoveDialog
import com.devhanna91.runnincle.framework.presentation.program_list.composable.ProgramListWithSearchBar
import com.devhanna91.runnincle.framework.presentation.program_list.composable.SettingModalBottomSheet
import com.devhanna91.runnincle.framework.presentation.program_list.composable.deleteProgramDialog
import com.devhanna91.runnincle.ui.theme.RunnincleTheme
import com.devhanna91.runnincle.util.*
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.message
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.vanpra.composematerialdialogs.title
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate


@AndroidEntryPoint
class ProgramListFragment: Fragment() {

    private val viewModel:ProgramListViewModel by viewModels()
    private var callback: OnBackPressedCallback? = null
    private var mInterstitialAd: InterstitialAd? = null
    private var mRewardedAd: RewardedAd? = null
    private var mAdIsLoading = false
    private var mInterstitialAdErrorCode = ""
    private var mRewardedAdErrorCode = ""
    private var overlayWindowDp: Int = 100

    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val programs = viewModel.programs
        val totalTimerColor = viewModel.totalTimerColor
        var coolDownTimerColor = viewModel.coolDownTimerColor
        val isTTSUsed = viewModel.isTTSUsed
        val searchChipList = viewModel.searchChipList
        var programToBeDeleted = viewModel.programToBeDeleted
        var language = viewModel.language
        // SettingModalBottomSheet 의 defaultLanguage 에 viewModel.language 를 넣었더니 dropDownBox 선택시 텍스트가 바뀌지 않는 버그가있어 별도의 변수룰 만듦
        var defaultLanguage: Language = Language.EN

        viewModel.launch {
            viewModel.setMapOfProgram()
            viewModel.setSavedSearchWords()
            viewModel.getSettingValue()
            defaultLanguage = viewModel.getLanguage()
        }

        initializeAdMop()
        loadInterstitialAd()
        loadRewardedAd()
        return ComposeView(requireContext()).apply {
            setContent {
                RunnincleTheme(darkSystemBar = true) {
                    val scope = rememberCoroutineScope()
                    val modalBottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
                    val adRemoveDialogState = rememberMaterialDialogState()
                    val restartDialogState = rememberMaterialDialogState()
                    val deleteProgramDialogState = rememberMaterialDialogState()

                    setOnBackPressedCallback(scope, modalBottomSheetState)

                    setOverlayWindowDp()

                    ModalBottomSheetLayout(
                        modifier = Modifier.fillMaxWidth(),
                        sheetState = modalBottomSheetState,
                        sheetShape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
                        sheetContent = {
                            SettingModalBottomSheet(
                                overlaySize = viewModel.overlaySize,
                                totalTimerColor = totalTimerColor,
                                coolDownTimerColor = coolDownTimerColor,
                                isTtsUsed = isTTSUsed,
                                defaultLanguage = defaultLanguage,
                                onLanguageSelected = {
                                    language.value = it
                                },
                                onAdRemoveClick = {
                                    adRemoveDialogState.show()
                                },
                                onSaveClick = {
                                    scope.launch {
                                        if(viewModel.isLanguageChanged()) {
                                            restartDialogState.show()
                                            viewModel.saveSettingProperty()
                                        } else {
                                            viewModel.saveSettingProperty()
                                        }
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
                                scope.launch {
                                    // 다른앱위에 그리기 권한 없는 경우
                                    if (activity?.drawOverOtherAppsEnabled() == false) {
                                        activity?.startPermissionActivity()
                                        return@launch
                                    }

                                    viewModel.getSettingValue()
                                    val isAdNeeded = isAdNeeded()
                                    if (isAdNeeded && !mAdIsLoading) {
                                        mAdIsLoading = true
                                        showInterstitial {
                                            scope.launch {
                                                viewModel.saveAdRemovalPeriod(LocalDate.now())
                                                startFloatingService(
                                                    program = program,
                                                    workouts = workouts,
                                                    isTTSUsed = isTTSUsed.value,
                                                    totalTimerColor = totalTimerColor.value,
                                                    coolDownTimerColor = coolDownTimerColor.value
                                                )
                                            }
                                        }
                                    } else {
                                        startFloatingService(
                                            program = program,
                                            workouts = workouts,
                                            isTTSUsed = isTTSUsed.value,
                                            totalTimerColor = totalTimerColor.value,
                                            coolDownTimerColor = coolDownTimerColor.value
                                        )
                                    }
                                }
                            },
                            onProgramEditButtonClick = { program, workouts ->
                                viewModel.moveToEditProgram(view, program, workouts)
                            },
                            onProgramDeleteButtonClick = { program ->
                                programToBeDeleted.value = program
                                deleteProgramDialogState.show()
                            },
                            onFloatingAddButtonClick = {
                                viewModel.moveToCreateProgram(view)
                            },
                            onFloatingSettingButtonClick = {
                                scope.launch {
                                    viewModel.getSettingValue()
                                    modalBottomSheetState.animateTo(ModalBottomSheetValue.Expanded)
                                }
                            }
                        )
                    }
                    deleteProgramDialog(
                        dialogState = deleteProgramDialogState,
                        programName = programToBeDeleted.value.name,
                        onConfirmClick = {
                            viewModel.deleteProgram(programToBeDeleted.value.id)
                            viewModel.programs.remove(programToBeDeleted.value)
                            deleteProgramDialogState.hide()
                        },
                        onCancelClick = {
                            deleteProgramDialogState.hide()
                        }
                    )
                    AdRemoveDialog(
                        dialogState = adRemoveDialogState,
                        onRemoveAdFor3DaysClick = {
                            scope.launch {
                                adRemoveDialogState.hide()
                                modalBottomSheetState.hide()
                                showRewardedVideo(scope)
                            }
                        },
                        onRemoveAdForeverClick = {
                            scope.launch {
                                adRemoveDialogState.hide()
                                modalBottomSheetState.hide()
                                moveToGooglePlayStore()
                            }
                        }
                    )
                    MaterialDialog(
                        dialogState = restartDialogState,
                        buttons = {
                            positiveButton(
                                text = stringResource(id = R.string.confirm),
                                onClick = {
                                    activity?.restartApp()
                                }
                            )
                            negativeButton(
                                text = stringResource(id = R.string.cancel),
                                onClick = {
                                    restartDialogState.hide()
                                }
                            )
                        }
                    ) {
                        title(stringResource(id = R.string.restart_app))
                        message(res = R.string.restart_app_description)
                    }
                }
            }
        }
    }

    private fun moveToGooglePlayStore() {
        var appPackageName = requireContext().packageName
        appPackageName = appPackageName.replace("free", "paid")

        try {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=$appPackageName")
                )
            )
        } catch (e: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                )
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun isAdNeeded(): Boolean {
        val adRemovalPeriod = viewModel.getAdRemovalPeriod()
        val now = LocalDate.now()
        return !BuildConfig.IS_PAID && now.isAfter(adRemovalPeriod)
    }

    @Composable
    private fun setOverlayWindowDp() {
        val sw = LocalConfiguration.current.screenWidthDp
        val sh = LocalConfiguration.current.screenHeightDp
        val maxOverlaySize = if(sw > sh) sh else sw

        overlayWindowDp = if (viewModel.overlaySize.value <= 1) {
            maxOverlaySize.toFloat().times(1.5).div(10).toInt()
        } else {
            maxOverlaySize.times(viewModel.overlaySize.value).div(10)
        }
    }

    private fun startFloatingService(
        program: Program,
        workouts: List<Workout>,
        isTTSUsed: Boolean,
        totalTimerColor: Color,
        coolDownTimerColor: Color,
    ) {
        viewModel.updateUpdatedAtFieldToUpToDate(program)

        val parcelableWorkouts = ArrayList<ParcelableWorkout>()
        workouts.forEach { workout ->
            parcelableWorkouts.add(workout.toParcelableWorkout())
        }

        activity?.openOverlayWindowWithFloatingService(
            program = program,
            workouts = parcelableWorkouts,
            isTTSUsed = isTTSUsed,
            totalTimerColor = totalTimerColor,
            coolDownTimerColor = coolDownTimerColor,
            overlayDp = overlayWindowDp
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

    private fun showInterstitial(onAdDismissed: ()-> Unit) {
        if (mInterstitialAd != null) {
            mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    onAdDismissed()
                    mAdIsLoading = false
                    mInterstitialAd = null
                    loadInterstitialAd()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                    mAdIsLoading = false
                    mInterstitialAd = null
                }

                override fun onAdShowedFullScreenContent() {
                }
            }
            mInterstitialAd?.show(activity)
        } else {
            // 광고를 로드하지 못한 경우. 그냥 타이머 실행.
            val message = getString(R.string.admop_error, mInterstitialAdErrorCode)
            context?.showToastMessage(message)
            onAdDismissed()
        }
    }

    private fun initializeAdMop() {
        MobileAds.initialize(activity) {}
    }

    private fun loadInterstitialAd() {
        var adRequest = AdRequest.Builder().build()

        activity?.let {
            InterstitialAd.load(
                it, it.getString(R.string.admop_interstitial_unit_id), adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        mInterstitialAdErrorCode = adError.code.toString()
                        mAdIsLoading = false
                        mInterstitialAd = null
                    }

                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                        mInterstitialAd = interstitialAd
                    }
                }
            )
        }
    }

    private fun loadRewardedAd() {
        if (mRewardedAd == null) {
            val adRequest = AdRequest.Builder().build()
            activity?.let {
                RewardedAd.load(
                    it, it.getString(R.string.admop_rewarded_unit_id), adRequest,
                    object : RewardedAdLoadCallback() {
                        override fun onAdFailedToLoad(adError: LoadAdError) {
                            mRewardedAd = null
                            mRewardedAdErrorCode = adError.code.toString()

                        }

                        override fun onAdLoaded(rewardedAd: RewardedAd) {
                            mRewardedAd = rewardedAd
                        }
                    }
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showRewardedVideo(scope: CoroutineScope) {
        if (mRewardedAd != null) {
            mRewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    mRewardedAd = null
                    loadRewardedAd()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                    mRewardedAd = null
                }

                override fun onAdShowedFullScreenContent() {
                }
            }

            mRewardedAd?.show(
                activity
            ) { rewardItem ->
                scope.launch {
                    viewModel.saveAdRemovalPeriod(LocalDate.now().plusDays(2))
                }
            }
        } else {
            // 광고를 로드하지 못한경우
            val message = getString(R.string.admop_error, mRewardedAdErrorCode)
            context?.showToastMessage(message)
        }
    }
}

