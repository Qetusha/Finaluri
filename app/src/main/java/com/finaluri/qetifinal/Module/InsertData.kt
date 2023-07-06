package com.finaluri.qetifinal.Module

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class InsertData(
    val productId: String? = null,
    var imageSrc: String? = null,
    val nametitle: String? = null
) : Parcelable


