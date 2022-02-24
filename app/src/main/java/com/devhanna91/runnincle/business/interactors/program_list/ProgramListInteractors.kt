package com.devhanna91.runnincle.business.interactors.program_list

import androidx.compose.ui.graphics.Color
import com.devhanna91.runnincle.business.data.cache.abstraction.ProgramCacheDataSource
import com.devhanna91.runnincle.business.data.cache.abstraction.WorkoutCacheDataSource
import com.devhanna91.runnincle.business.domain.model.Program
import com.devhanna91.runnincle.business.domain.model.Workout
import com.devhanna91.runnincle.framework.datasource.cache.abstraction.SharedPreferencesService
import com.devhanna91.runnincle.framework.datasource.cache.model.Language
import java.time.LocalDate

class ProgramListInteractors (
    private val programCacheDataSource: ProgramCacheDataSource,
    private val workoutCacheDataSource: WorkoutCacheDataSource,
    private val sharedPreferencesService: SharedPreferencesService
) {

    suspend fun getAllProgram(): List<Program> {
        return programCacheDataSource.getAllPrograms()
    }

    suspend fun getWorkoutsOfProgram(programId: String): List<Workout> {
        return workoutCacheDataSource.getWorkoutsOfProgram(programId)
    }

    suspend fun deleteProgram(programId: String) {
        programCacheDataSource.deleteProgram(programId)
        workoutCacheDataSource.deleteWorkoutsWithProgramId(programId)
    }

    suspend fun searchProgram(searchText: String): List<Program> {
        return programCacheDataSource.searchProgram(searchText)
    }

    suspend fun getOverlaySize(): Int {
        return sharedPreferencesService.getOverlaySize()
    }

    suspend fun getTotalTimerColor(): Color {
        return sharedPreferencesService.getTotalTimerColor()
    }

    suspend fun isTTSUsed(): Boolean {
        return sharedPreferencesService.isTTSUsed()
    }

    suspend fun getSavedSearchWords(): List<String> {
        return sharedPreferencesService.getSavedSearchWords()
    }

    suspend fun saveSettingProperty(
        overlaySize: Int,
        totalTimerColor: Color,
        coolDownTimerColor: Color,
        isTTSUsed: Boolean,
        language: Language
    ) {
        sharedPreferencesService.saveSettingProperty(
            overlaySize,
            totalTimerColor,
            coolDownTimerColor,
            isTTSUsed,
            language
        )
    }

    suspend fun saveSearchWord(text: String) {
        sharedPreferencesService.saveSearchWord(text)
    }

    suspend fun removeSearchWord(text: String) {
        sharedPreferencesService.removeSearchWord(text)
    }

    suspend fun getCoolDownTimerColor(): Color {
        return sharedPreferencesService.getCoolDownTimerColor()
    }

    suspend fun saveAdRemovalPeriod(localDate: LocalDate) {
        sharedPreferencesService.saveAdRemovalPeriod(localDate)
    }

    suspend fun getAdRemovalPeriod(): LocalDate {
        return sharedPreferencesService.getAdRemovalPeriod()
    }

    suspend fun getLanguage():Language {
        return sharedPreferencesService.getLanguage()
    }
}