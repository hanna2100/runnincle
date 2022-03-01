package com.devhanna91.runnincle.business.interactors.activity

import com.devhanna91.runnincle.framework.datasource.cache.abstraction.SharedPreferencesService
import com.devhanna91.runnincle.framework.datasource.cache.model.Language

class MainActivityInteractors(
    private val sharedPreferencesService: SharedPreferencesService
) {
    suspend fun isFirstRun(): Boolean {
        return sharedPreferencesService.isFirstRun()
    }

    suspend fun setFirstRunFalse() {
        sharedPreferencesService.setFirstRunFalse()
    }

    suspend fun saveLanguage(language: Language) {
        sharedPreferencesService.saveLanguage(language = language)
    }

    suspend fun getLanguage(): Language {
        return sharedPreferencesService.getLanguage()
    }
}