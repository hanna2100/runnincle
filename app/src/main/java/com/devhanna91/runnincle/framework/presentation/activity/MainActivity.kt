package com.devhanna91.runnincle.framework.presentation.activity

import android.os.Bundle
import androidx.activity.viewModels
import com.akexorcist.localizationactivity.ui.LocalizationActivity
import com.devhanna91.runnincle.R
import com.devhanna91.runnincle.framework.datasource.cache.model.Language
import com.devhanna91.runnincle.framework.presentation.BaseApplication
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint // 생명주기에 맞춰 컨테이너를 만들어 의존성 주입을 받을 수 있음.
class MainActivity : LocalizationActivity() {

    private val viewModel: MainViewModel by viewModels()

    @Inject
    lateinit var app: BaseApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLocalLanguage()
        setContentView(R.layout.activity_main)
    }

    private fun setLocalLanguage() {
        viewModel.launch {
            if(viewModel.isFirstRun()) {
                var userLanguage = when(Locale.getDefault()) {
                    Locale.KOREA -> Language.KO
                    else -> Language.EN
                }
                viewModel.setFirstRunFalse()
                viewModel.saveLanguage(userLanguage)
                setLanguage(userLanguage.locale)
            } else {
                val savedLanguage = viewModel.getLanguage()
                setLanguage(savedLanguage.locale)
            }
        }
    }

}