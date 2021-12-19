package com.dhandyjoe.stockku.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Cart (
    var id: String = "",
    var name: String = ""
): Parcelable