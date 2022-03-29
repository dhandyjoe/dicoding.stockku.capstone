package com.dhandyjoe.stockku.utils

import com.dhandyjoe.stockku.model.Transaction

fun subStringDate(data: String, startChar: Int, endChar: Int): String {
    return data.substring(startChar, endChar)
}