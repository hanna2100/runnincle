package com.devhanna91.runnincle.business.domain.util

import android.content.Context

interface ResourcesProvider {
    fun getString(resId: Int) : String
}

class ResourcesProviderImpl(private val context: Context) : ResourcesProvider {
    override fun getString(resId: Int)= context.getString(resId)
}