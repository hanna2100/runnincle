package com.devhanna91.runnincle.framework.presentation

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.devhanna91.runnincle.R
import com.devhanna91.runnincle.framework.datasource.cache.model.Language
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint // 생명주기에 맞춰 컨테이너를 만들어 의존성 주입을 받을 수 있음.
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    @Inject
    lateinit var app: BaseApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setLocalLanguage()
        setContentView(R.layout.activity_main)
    }

    private fun setLocalLanguage() {
        viewModel.launch {
            if(viewModel.isFirstRun()) {
                val config = Configuration()
                val userLocale = Locale.getDefault()
                if (userLocale == Locale.KOREA) {
                    viewModel.saveLanguage(Language.KO)
                    config.locale = Language.KO.locale
                } else {
                    viewModel.saveLanguage(Language.EN)
                    config.locale =  Language.EN.locale
                }
                viewModel.setFirstRunFalse()
                resources.updateConfiguration(config, resources.displayMetrics)
            } else {
                val savedLocale = viewModel.getLanguage()
                val config = Configuration()
                config.locale = savedLocale.locale
                resources.updateConfiguration(config, resources.displayMetrics)
            }


        }
    }
}