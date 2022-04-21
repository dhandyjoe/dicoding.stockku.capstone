package com.dhandyjoe.stockku.utils

fun resultDiscount (discount: Int, price: Int): Int {
    val value = price * discount / 100
    return price - value
}