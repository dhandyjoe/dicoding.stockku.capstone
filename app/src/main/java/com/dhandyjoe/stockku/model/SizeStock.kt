package com.dhandyjoe.stockku.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SizeStock(
    val id: String = "",
    val idCart: String = "",
    val nameCategory: String = "",
    val nameItemCategory: String = "",
    val nameProduct: String = "",
    val color: String = "",
    val size: String = "",
    val price: Int = 0,
    val stock: Int = 0,
    val imageUrl: String = "",
    var totalTransaction: Int = 0,
): Parcelable
