package com.devhanna91.runnincle.framework.datasource.cache.implementation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import com.devhanna91.runnincle.framework.datasource.cache.abstraction.GsonSharedPreferenceService
import com.devhanna91.runnincle.framework.datasource.cache.abstraction.SharedPreferencesService
import com.devhanna91.runnincle.framework.datasource.cache.model.Language
import com.devhanna91.runnincle.framework.datasource.cache.model.PreferenceEntity
import java.time.LocalDate

class SharedPreferencesServiceImpl(
    private val gsonSharedPreferenceService: GsonSharedPreferenceService
): SharedPreferencesService {

    override suspend fun getPreferenceEntity(): PreferenceEntity {
        var preferenceEntity = gsonSharedPreferenceService.getObject(PreferenceEntity()) as PreferenceEntity?

        if (preferenceEntity == null) { // 저장된 shared preference 가 없는 경우
            preferenceEntity = PreferenceEntity()
            gsonSharedPreferenceService.saveObject(preferenceEntity)
        }
        return preferenceEntity
    }

    override suspend fun getOverlaySize(): Int {
        return getPreferenceEntity().overlaySize
    }

    override suspend fun getTotalTimerColor(): Color {
        return getPreferenceEntity().totalTimerColor
    }

    override suspend fun isTTSUsed(): Boolean {
        return getPreferenceEntity().isTTSUsed
    }

    override suspend fun getSavedSearchWords(): List<String> {
        return getPreferenceEntity().searchWords
    }

    override suspend fun saveSettingProperty(
        overlaySize: Int,
        totalTimerColor: Color,
        coolDownTimerColor: Color,
        isTTSUsed: Boolean,
        language: Language
    ) {
        val sharedPreferenceEntity = getPreferenceEntity()
        sharedPreferenceEntity.overlaySize = overlaySize
        sharedPreferenceEntity.totalTimerColor = totalTimerColor
        sharedPreferenceEntity.coolDownTimerColor = coolDownTimerColor
        sharedPreferenceEntity.isTTSUsed = isTTSUsed
        sharedPreferenceEntity.language = language

        gsonSharedPreferenceService.saveObject(sharedPreferenceEntity)
    }

    override suspend fun saveSearchWord(text: String) {
        val sharedPreferenceEntity = getPreferenceEntity()
        val originList = sharedPreferenceEntity.searchWords
        val newList = mutableListOf<String>()
        newList.addAll(originList)
        newList.add(0, text)
        sharedPreferenceEntity.searchWords = newList
        gsonSharedPreferenceService.saveObject(sharedPreferenceEntity)
    }

    override suspend fun removeSearchWord(text: String) {
        val sharedPreferenceEntity = getPreferenceEntity()
        val originList = sharedPreferenceEntity.searchWords
        val newList = mutableListOf<String>()
        newList.addAll(originList)
        newList.remove(text)
        sharedPreferenceEntity.searchWords = newList
        gsonSharedPreferenceService.saveObject(sharedPreferenceEntity)
    }

    override suspend fun getCoolDownTimerColor(): Color {
        return getPreferenceEntity().coolDownTimerColor
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getAdRemovalPeriod(): LocalDate {
        return LocalDate.parse(getPreferenceEntity().adRemovalPeriod)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun saveAdRemovalPeriod(date: LocalDate) {
        val sharedPreferenceEntity = getPreferenceEntity()
        sharedPreferenceEntity.adRemovalPeriod = date.toString()
        gsonSharedPreferenceService.saveObject(sharedPreferenceEntity)
    }

    override suspend fun isFirstRun(): Boolean {
        val sharedPreferenceEntity = getPreferenceEntity()
        return sharedPreferenceEntity.isFirstRun
    }

    override suspend fun setFirstRunFalse() {
        val sharedPreferenceEntity = getPreferenceEntity()
        sharedPreferenceEntity.isFirstRun = false
        gsonSharedPreferenceService.saveObject(sharedPreferenceEntity)
    }

    override suspend fun saveLanguage(language: Language) {
        val sharedPreferenceEntity = getPreferenceEntity()
        sharedPreferenceEntity.language = language
        gsonSharedPreferenceService.saveObject(sharedPreferenceEntity)
    }

    override suspend fun getLanguage(): Language {
        val sharedPreferenceEntity = getPreferenceEntity()
        return sharedPreferenceEntity.language
    }

}