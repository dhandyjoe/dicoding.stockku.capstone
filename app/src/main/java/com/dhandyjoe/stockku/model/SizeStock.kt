package com.dhandyjoe.stockku.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SizeStock(
    val id: String = "",
    val idCart: String = "",
    val size: String = "",
    val price: Int = 0,
    val stock: Int = 0,
    var totalTransaction: Int = 0,
): Parcelable
