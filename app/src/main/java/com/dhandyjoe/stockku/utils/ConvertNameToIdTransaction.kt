package com.dhandyjoe.stockku.utils

import com.dhandyjoe.stockku.model.Product
import com.dhandyjoe.stockku.model.Transaction

fun convertNameToIdTransaction(data: String, category: ArrayList<Transaction>): String {
    var name = ""

    for (value in category.indices) {
        if (data == category[value].name) {
            name = category[value].id
        }
    }

    return name
}