package com.example.runnincle.framework.datasource.cache.abstraction

import androidx.compose.ui.graphics.Color
import com.example.runnincle.framework.datasource.cache.model.PreferenceEntity

interface SharedPreferencesService {

    suspend fun getPreferenceEntity(): PreferenceEntity

    suspend fun getOverlaySize(): Int

    suspend fun getTotalTimerColor(): Color

    suspend fun isTTSUsed(): Boolean

    suspend fun getSavedSearchWords(): List<String>

    suspend fun saveSettingProperty(
        overlaySize: Int,
        totalTimerColor: Color,
        isTTSUsed: Boolean
    )

    suspend fun saveSearchWord(text: String)

    suspend fun removeSearchWord(text: String)
}