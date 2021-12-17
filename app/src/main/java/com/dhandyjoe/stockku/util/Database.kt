package com.dhandyjoe.stockku.util

import android.widget.Toast
import com.dhandyjoe.stockku.model.Cart
import com.dhandyjoe.stockku.model.Item
import com.google.firebase.firestore.FirebaseFirestore

class Database {
    private val firebaseDB = FirebaseFirestore.getInstance()

    fun addItem(nameItem: String, sizeItem: String, priceItem: String, stockItem: Int) {
        val docBarang = firebaseDB.collection(COLLECTION_ITEM).document()
        val item = Item(docBarang.id, nameItem, sizeItem, priceItem, stockItem)

        docBarang.set(item)
    }

    fun editItem(itemId: String, nameItem: String, priceItem: String, sizeItem: String) {
        val map = HashMap<String, Any>()
        map["name"] = nameItem
        map["price"] = priceItem
        map["size"] = sizeItem

        firebaseDB.collection(COLLECTION_ITEM)
            .document(itemId)
            .update(map)
    }

    fun deleteItem(itemId: String) {
        firebaseDB.collection(COLLECTION_ITEM).document(itemId).delete()
    }



    fun addItemTransaction(data: Item, docTransaction: String) {
        val docItemTransaction = firebaseDB.collection(COLLECTION_TRANSACTION).document(docTransaction).
        collection("itemTransaksi").document()
        docItemTransaction.set(data)

    }
}