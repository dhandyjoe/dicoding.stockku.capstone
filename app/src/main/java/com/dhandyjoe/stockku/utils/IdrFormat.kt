package com.dhandyjoe.stockku.utils

import java.text.DecimalFormat

fun idrFormat(number: Int): String {
    val decimalFormat = DecimalFormat("#,###")
    return decimalFormat.format(number)
}