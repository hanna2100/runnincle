package com.devhanna91.runnincle.framework.datasource.cache.abstraction

import androidx.compose.ui.graphics.Color
import com.devhanna91.runnincle.framework.datasource.cache.model.PreferenceEntity
import java.time.LocalDate

interface SharedPreferencesService {

    suspend fun getPreferenceEntity(): PreferenceEntity

    suspend fun getOverlaySize(): Int

    suspend fun getTotalTimerColor(): Color

    suspend fun isTTSUsed(): Boolean

    suspend fun getSavedSearchWords(): List<String>

    suspend fun saveSettingProperty(
        overlaySize: Int,
        totalTimerColor: Color,
        coolDownTimerColor: Color,
        isTTSUsed: Boolean
    )

    suspend fun saveSearchWord(text: String)

    suspend fun removeSearchWord(text: String)

    suspend fun getCoolDownTimerColor(): Color

    suspend fun getAdRemovalPeriod(): LocalDate

    suspend fun saveAdRemovalPeriod(date: LocalDate)
}