package com.dhandyjoe.stockku.utils

import com.dhandyjoe.stockku.model.Transaction

fun ConvertToChartData(data: ArrayList<Transaction>): Array<Float> {
    val returnData = arrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)

    for (count in data.indices) {
        if (subStringDate(data[count].name, 14, 16) == "01") {
            returnData[0] = returnData[0] + 1
        } else if (subStringDate(data[count].name, 14, 16) == "02") {
            returnData[1] = returnData[1] + 1
        } else if (subStringDate(data[count].name, 14, 16) == "03") {
            returnData[2] = returnData[2] + 1
        } else if (subStringDate(data[count].name, 14, 16) == "04") {
            returnData[3] = returnData[3] + 1
        } else if (subStringDate(data[count].name, 14, 16) == "05") {
            returnData[4] = returnData[4] + 1
        } else if (subStringDate(data[count].name, 14, 16) == "06") {
            returnData[5] = returnData[5] + 1
        } else if (subStringDate(data[count].name, 14, 16) == "07") {
            returnData[6] = returnData[6] + 1
        } else if (subStringDate(data[count].name, 14, 16) == "08") {
            returnData[7] = returnData[7] + 1
        } else if (subStringDate(data[count].name, 14, 16) == "09") {
            returnData[8] = returnData[8] + 1
        } else if (subStringDate(data[count].name, 14, 16) == "10") {
            returnData[9] = returnData[9] + 1
        } else if (subStringDate(data[count].name, 14, 16) == "11") {
            returnData[10] = returnData[10] + 1
        } else {
            returnData[11] = returnData[11] + 1
        }
    }

    return returnData
}