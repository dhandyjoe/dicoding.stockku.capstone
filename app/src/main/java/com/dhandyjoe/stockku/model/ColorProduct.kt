package com.dhandyjoe.stockku.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ColorProduct(
    val id: String = "",
    val name: String = ""
): Parcelable
