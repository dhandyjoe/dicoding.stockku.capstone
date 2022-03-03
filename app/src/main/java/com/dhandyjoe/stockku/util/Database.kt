package com.dhandyjoe.stockku.util

import com.dhandyjoe.stockku.model.Item
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class Database {
    private val firebaseDB = FirebaseFirestore.getInstance()

    // CREATE
    fun addItem(nameItem: String, sizeItem: String, priceItem: String, imageUrl: String, stockItem: Int) {
        val docBarang = firebaseDB.collection(COLLECTION_ITEM).document()
        val item = Item(docBarang.id,"",nameItem, sizeItem, priceItem, imageUrl, stockItem)

        docBarang.set(item)
    }

    // UPDATE
    fun editItem(itemId: String, nameItem: String, priceItem: String, sizeItem: String, stockItem: String) {
        val map = HashMap<String, Any>()
        map["name"] = nameItem
        map["price"] = priceItem
        map["size"] = sizeItem
        map["stock"] = FieldValue.increment(stockItem.toDouble())

        firebaseDB.collection(COLLECTION_ITEM)
            .document(itemId)
            .update(map)
    }

    // DELETE
    fun deleteItem(itemId: String) {
        firebaseDB.collection(COLLECTION_ITEM).document(itemId).delete()
    }

    // UPDATE
    fun updateStockItem(item: Item) {
        firebaseDB.collection(COLLECTION_ITEM)
            .document(item.id)
            .update("stock", item.stock - item.totalTransaction)

        firebaseDB.collection(COLLECTION_ITEM)
            .document(item.id)
            .update("totalTransaction", 0)
    }

    fun addItemTransaction(data: Item, docTransaction: String) {
        val docItemTransaction = firebaseDB.collection(COLLECTION_TRANSACTION).document(docTransaction).
        collection("itemTransaksi").document()
        docItemTransaction.set(data)
    }

    fun addItemCart(data: Item) {
        val docItemCart = firebaseDB.collection(COLLECTION_CART).document()
        val item = Item(data.id, docItemCart.id, data.name, data.size, data.price, data.imageUrl, data.stock, data.totalTransaction)
        docItemCart.set(item)
    }

    fun updateItemCart(data: Item, statusIndicator: Int) {
        val docItemCart = firebaseDB.collection(COLLECTION_CART).document(data.idCart)
        docItemCart.update("totalTransaction", data.totalTransaction + statusIndicator)
    }
}