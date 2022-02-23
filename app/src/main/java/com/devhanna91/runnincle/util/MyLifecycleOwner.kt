package com.devhanna91.runnincle.util

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleRegistry
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner

internal class MyLifecycleOwner: SavedStateRegistryOwner {
    private var mLifecycleRegistry = LifecycleRegistry(this)
    private var mSavedStateRegistryController = SavedStateRegistryController.create(this)

    val isInitialized: Boolean
        get() = true

    override fun getLifecycle(): Lifecycle {
        return mLifecycleRegistry
    }

    override fun getSavedStateRegistry(): SavedStateRegistry {
        return mSavedStateRegistryController.savedStateRegistry
    }

    fun setCurrentState(state: Lifecycle.State) {
        mLifecycleRegistry.currentState = state
    }

    fun handleLifecycleEvent(event: Lifecycle.Event) {
        mLifecycleRegistry.handleLifecycleEvent(event)
    }

    fun performRestore(savedState: Bundle?) {
        mSavedStateRegistryController.performRestore(savedState)
    }

    fun performSave(outBundle: Bundle) {
        mSavedStateRegistryController.performSave(outBundle)
    }

}