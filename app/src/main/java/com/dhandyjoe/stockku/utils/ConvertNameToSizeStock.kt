package com.dhandyjoe.stockku.utils

import com.dhandyjoe.stockku.model.Product
import com.dhandyjoe.stockku.model.SizeStock

fun convertNameToSizeStock(data: String, category: ArrayList<SizeStock>): SizeStock {
    var size = SizeStock()

    for (value in category.indices) {
        if (data == category[value].size) {
            size = category[value]
        }
    }

    return size
}