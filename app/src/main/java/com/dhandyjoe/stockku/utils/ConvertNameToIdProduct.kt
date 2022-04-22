package com.dhandyjoe.stockku.utils

import com.dhandyjoe.stockku.model.Product

fun convertNameToIdProduct(data: String, category: ArrayList<Product>): String {
    var name = ""

    for (value in category.indices) {
        if (data == category[value].name) {
            name = category[value].id
        }
    }

    return name
}