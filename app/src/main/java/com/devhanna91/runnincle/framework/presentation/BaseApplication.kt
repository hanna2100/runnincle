package com.devhanna91.runnincle.framework.presentation

import android.content.Context
import com.akexorcist.localizationactivity.ui.LocalizationApplication
import com.devhanna91.runnincle.framework.datasource.cache.model.Language
import dagger.hilt.android.HiltAndroidApp
import java.util.*

@HiltAndroidApp // 컴파일 시 컴포넌트 빌딩에 필요한 클래스들을 초기화 해줌
class BaseApplication: LocalizationApplication() {
    override fun getDefaultLanguage(context: Context): Locale = Language.KO.locale

}