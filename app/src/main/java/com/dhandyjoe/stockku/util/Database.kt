package com.dhandyjoe.stockku.util

import com.dhandyjoe.stockku.model.Cart
import com.dhandyjoe.stockku.model.Item
import com.google.firebase.firestore.FirebaseFirestore

class Database {
    private val firebaseDB = FirebaseFirestore.getInstance()

    fun saveItemTransaction(data: Item, docTransaction: String) {
        var docItemTransaction = firebaseDB.collection("transaksi").document(docTransaction).
        collection("itemTransaksi").document()
        docItemTransaction.set(data)

    }
}