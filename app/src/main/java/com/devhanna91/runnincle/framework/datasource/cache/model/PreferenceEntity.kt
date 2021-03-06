package com.devhanna91.runnincle.framework.datasource.cache.model

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import com.devhanna91.runnincle.BuildConfig
import com.devhanna91.runnincle.ui.theme.TimerColorPalette
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.time.LocalDate

class PreferenceEntity {
    @SerializedName("overlaySize")
    @Expose
    var overlaySize: Int = 3

    @SerializedName("totalTimerColor")
    @Expose
    var totalTimerColor: Color = TimerColorPalette.last()

    @SerializedName("coolDownTimerColor")
    @Expose
    var coolDownTimerColor: Color = TimerColorPalette[TimerColorPalette.lastIndex - 1]

    @SerializedName("isTTSUsed")
    @Expose
    var isTTSUsed: Boolean = false

    @SerializedName("searchWords")
    @Expose
    var searchWords: List<String> = listOf()

    @RequiresApi(Build.VERSION_CODES.O)
    @SerializedName("adRemovalPeriod")
    @Expose
    var adRemovalPeriod: String = if (BuildConfig.IS_PAID) LocalDate.MAX.toString() else LocalDate.MIN.toString()

    @SerializedName("language")
    @Expose
    var language: Language = Language.EN

    @SerializedName("isFirstRun")
    @Expose
    var isFirstRun: Boolean = true
}
