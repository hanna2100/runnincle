package com.devhanna91.runnincle.framework.presentation.activity

import com.devhanna91.runnincle.business.interactors.activity.MainActivityInteractors
import com.devhanna91.runnincle.framework.datasource.cache.model.Language
import com.devhanna91.runnincle.util.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel
@Inject
constructor(
    private val mainActivityInteractors: MainActivityInteractors
): BaseViewModel() {
    suspend fun isFirstRun(): Boolean {
        return mainActivityInteractors.isFirstRun()
    }

    suspend fun setFirstRunFalse() {
        mainActivityInteractors.setFirstRunFalse()
    }

    suspend fun saveLanguage(language: Language) {
        mainActivityInteractors.saveLanguage(language = language)
    }

    suspend fun getLanguage(): Language {
        return mainActivityInteractors.getLanguage()
    }

}