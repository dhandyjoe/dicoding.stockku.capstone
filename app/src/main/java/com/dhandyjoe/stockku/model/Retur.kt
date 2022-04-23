package com.dhandyjoe.stockku.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Retur(
    val id: String = "",
    val name: String = "",
    val nameBranch: String = "",
    val totalPrice: Int = 0,
    val date: String = "",
    val note: String = "",
): Parcelable
