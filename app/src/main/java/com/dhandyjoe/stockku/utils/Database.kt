package com.dhandyjoe.stockku.utils

import com.dhandyjoe.stockku.model.Item
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class Database {
    private val firebaseDB = FirebaseFirestore.getInstance()

    // add item to firebase
    fun addItem(nameItem: String, sizeItem: String, priceItem: Int, imageUrl: String, stockItem: Int) {
        val docBarang = firebaseDB.collection(COLLECTION_ITEM).document()
        val item = Item(docBarang.id,"",nameItem, sizeItem, priceItem, imageUrl, stockItem)

        docBarang.set(item)
    }

    // edit item firebase
    fun editItem(itemId: String, nameItem: String, priceItem: Int, sizeItem: String, stockItem: Int) {
        val map = HashMap<String, Any>()
        map["name"] = nameItem
        map["price"] = priceItem
        map["size"] = sizeItem
        map["stock"] = FieldValue.increment(stockItem.toDouble())

        firebaseDB.collection(COLLECTION_ITEM)
            .document(itemId)
            .update(map)
    }

    // delete item firebase
    fun deleteItem(itemId: String) {
        firebaseDB.collection(COLLECTION_ITEM).document(itemId).delete()
    }

    // update stock item after transaction
    fun updateStockItem(item: Item) {
        firebaseDB.collection(COLLECTION_ITEM)
            .document(item.id)
            .update("stock", item.stock - item.totalTransaction)
    }

    // add transaction to firebase
    fun saveTransaction(data: Item, docTransaction: String) {
        val docItemTransaction = firebaseDB.collection(COLLECTION_TRANSACTION).document(docTransaction).
        collection("itemTransaksi").document()
        docItemTransaction.set(data)
    }

    // add item to cart
    fun addItemCart(data: Item, totalTransaction: Int) {
        val docItemCart = firebaseDB.collection(COLLECTION_CART).document()
        val item = Item(data.id, docItemCart.id, data.name, data.size, data.price, data.imageUrl, data.stock, totalTransaction)
        docItemCart.set(item)
    }

    // update totalTransaction item in cart
    fun updateItemCart(data: Item, statusIndicator: Int) {
        val docItemCart = firebaseDB.collection(COLLECTION_CART).document(data.idCart)
        docItemCart.update("totalTransaction", data.totalTransaction + statusIndicator)
    }

    // delete item in cart
    fun deleteItemCart(data: Item) {
        val docItemcart = firebaseDB.collection(COLLECTION_CART).document(data.idCart)
        docItemcart.delete()
    }
}