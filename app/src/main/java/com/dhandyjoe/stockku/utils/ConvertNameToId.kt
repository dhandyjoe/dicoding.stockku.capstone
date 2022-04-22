package com.dhandyjoe.stockku.utils

import com.dhandyjoe.stockku.model.Category

fun convertNameToId(data: String, category: ArrayList<Category>): String {
    var name = ""

    for (value in category.indices) {
        if (data == category[value].name) {
            name = category[value].id
        }
    }

    return name
}