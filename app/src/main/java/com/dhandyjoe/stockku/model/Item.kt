package com.dhandyjoe.stockku.model

data class Item (
    val id: String,
    val name: String,
    val size: Int,
    val price: Int,
    val stock: Int
)