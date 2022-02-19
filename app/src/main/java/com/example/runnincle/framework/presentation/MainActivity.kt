package com.example.runnincle.framework.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.runnincle.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint // 생명주기에 맞춰 컨테이너를 만들어 의존성 주입을 받을 수 있음.
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var app: BaseApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    private fun setSharedPreference() {

    }

}