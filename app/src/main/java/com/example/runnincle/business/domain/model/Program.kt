package com.example.runnincle.business.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Program(
    var id: String,
    var name: String,
    var updatedAt: String
): Parcelable