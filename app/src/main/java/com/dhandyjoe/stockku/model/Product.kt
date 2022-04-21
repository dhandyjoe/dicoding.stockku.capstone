package com.dhandyjoe.stockku.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    val id: String = "",
    val name: String = "",
    val imageUrl: String = "",
): Parcelable