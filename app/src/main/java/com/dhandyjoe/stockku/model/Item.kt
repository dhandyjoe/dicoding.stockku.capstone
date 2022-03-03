package com.dhandyjoe.stockku.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Item(
    val id: String = "",
    val idCart: String = "",
    val name: String = "",
    val size: String = "",
    val price: String = "",
    val imageUrl: String = "",
    val stock: Int = 0,
    var totalTransaction: Int = 0
): Parcelable