package com.example.runnincle.framework.datasource.cache.model

import androidx.compose.ui.graphics.Color
import com.example.runnincle.ui.theme.TimerColorPalette
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PreferenceEntity {
    @SerializedName("overlaySize")
    @Expose
    var overlaySize: Int = 3

    @SerializedName("totalTimerColor")
    @Expose
    var totalTimerColor: Color = TimerColorPalette.last()

    @SerializedName("coolDownTimerColor")
    @Expose
    var coolDownTimerColor: Color = TimerColorPalette.last()

    @SerializedName("isTTSUsed")
    @Expose
    var isTTSUsed: Boolean = false

    @SerializedName("searchWords")
    @Expose
    var searchWords: List<String> = listOf()
}
