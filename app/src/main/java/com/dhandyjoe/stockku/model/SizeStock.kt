package com.dhandyjoe.stockku.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SizeStock(
    val id: String = "",
    val idCart: String = "",
    val idRetur: String = "",
    val category: Category = Category("", ""),
    val itemCategory: Category = Category("", ""),
    val product: Product = Product("", "", ""),
    val color: Category = Category("", ""),
    val size: String = "",
    val price: Int = 0,
    val stock: Int = 0,
    val imageUrl: String = "",
    var totalTransaction: Int = 0,
    val discount: Int = 0
): Parcelable
