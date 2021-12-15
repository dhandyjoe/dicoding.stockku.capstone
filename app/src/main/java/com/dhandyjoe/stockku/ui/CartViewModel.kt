package com.dhandyjoe.stockku.ui

import androidx.lifecycle.ViewModel
import com.dhandyjoe.stockku.model.Item
import com.google.android.gms.common.util.ArrayUtils

class CartViewModel: ViewModel() {
    var data = ArrayList<Item>()

    fun addItem(dataItem: Item) {
        data.add(dataItem)
    }
}