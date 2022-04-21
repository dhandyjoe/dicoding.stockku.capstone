package com.dhandyjoe.stockku.utils

import com.dhandyjoe.stockku.model.Category
import com.dhandyjoe.stockku.model.ColorProduct
import com.dhandyjoe.stockku.model.Product
import com.dhandyjoe.stockku.model.SizeStock
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class Database {
    private val firebaseDB = FirebaseFirestore.getInstance()

    // add category to firebase
    fun addCategory (userId: String, nameCategory: String) {
        val docCategory = firebaseDB.collection(COLLECTION_USERS).document(userId)
            .collection(COLLECTION_CATEGORY).document()
        docCategory.set(Category(docCategory.id, nameCategory))
    }

    // delete category from firebase
    fun deleteCategory(userId: String, docId: String) {
        firebaseDB.collection(COLLECTION_USERS).document(userId)
            .collection(COLLECTION_CATEGORY).document(docId)
            .delete()
    }

    fun deleteItemCategory(userId: String, categoryId: String) {
        firebaseDB.collection(COLLECTION_USERS).document(userId)
            .collection(COLLECTION_CATEGORY).document(categoryId)
            .collection(COLLECTION_ITEM_CATEGORY)
            .get()
            .addOnSuccessListener {
                it.forEach { query ->
                    query.reference.delete()
                }
            }
    }

    // add item category to firebase
    fun addItemCategory (userId: String, categoryId: String, nameItemCategory: String) {
        val docItemCategory = firebaseDB.collection(COLLECTION_USERS).document(userId)
            .collection(COLLECTION_CATEGORY).document(categoryId)
            .collection(COLLECTION_ITEM_CATEGORY).document()
        docItemCategory.set(Category(docItemCategory.id, nameItemCategory))
    }







    // add product
    fun addProduct(userId: String, categoryId: String, itemCategoryId: String, product: Product) {
        val doc = firebaseDB.collection(COLLECTION_USERS).document(userId)
            .collection(COLLECTION_CATEGORY).document(categoryId)
            .collection(COLLECTION_ITEM_CATEGORY).document(itemCategoryId)
            .collection(COLLECTION_PRODUCT).document()
        doc.set(Product(doc.id, product.name, product.imageUrl))
    }

    // add color product
    fun addColorProduct(userId: String, categoryId: String, itemCategoryId: String, productId: String, colorName: String) {
        val doc = firebaseDB.collection(COLLECTION_USERS).document(userId)
            .collection(COLLECTION_CATEGORY).document(categoryId)
            .collection(COLLECTION_ITEM_CATEGORY).document(itemCategoryId)
            .collection(COLLECTION_PRODUCT).document(productId)
            .collection(COLLECTION_COLOR_PRODUCT).document()
        doc.set(ColorProduct(doc.id, colorName))
    }

    fun addSizeStockProduct(userId: String, categoryId: String, itemCategoryId: String, productId: String, colorId: String, sizeStock: SizeStock) {
        val doc = firebaseDB.collection(COLLECTION_USERS).document(userId)
            .collection(COLLECTION_CATEGORY).document(categoryId)
            .collection(COLLECTION_ITEM_CATEGORY).document(itemCategoryId)
            .collection(COLLECTION_PRODUCT).document(productId)
            .collection(COLLECTION_COLOR_PRODUCT).document(colorId)
            .collection(COLLECTION_SIZE_STOCK_PRODUCT).document()
        doc.set(
            SizeStock(
                doc.id,
                "",
                sizeStock.nameCategory,
                sizeStock.nameItemCategory,
                sizeStock.nameProduct,
                sizeStock.color,
                sizeStock.size,
                sizeStock.price,
                sizeStock.stock,
                sizeStock.imageUrl,
                sizeStock.totalTransaction
            )
        )
    }

    fun editSizeStockProduct(userId: String, categoryId: String, itemCategoryId: String, productId: String, colorId: String, sizeStock: SizeStock) {
        val map = HashMap<String, Any>()
        map["size"] = sizeStock.size
        map["price"] = sizeStock.price
        map["stock"] = sizeStock.stock

        val doc = firebaseDB.collection(COLLECTION_USERS).document(userId)
            .collection(COLLECTION_CATEGORY).document(categoryId)
            .collection(COLLECTION_ITEM_CATEGORY).document(itemCategoryId)
            .collection(COLLECTION_PRODUCT).document(productId)
            .collection(COLLECTION_COLOR_PRODUCT).document(colorId)
            .collection(COLLECTION_SIZE_STOCK_PRODUCT).document(sizeStock.id)
        doc.update(map)
    }






    // edit item firebase
    fun editItem(userId: String, itemId: String, nameItem: String, priceItem: Int, sizeItem: String, stockItem: Int) {
        val map = HashMap<String, Any>()
        map["name"] = nameItem
        map["price"] = priceItem
        map["size"] = sizeItem
        map["stock"] = FieldValue.increment(stockItem.toDouble())

        firebaseDB.collection(COLLECTION_USERS).document(userId).collection(COLLECTION_PRODUCT)
            .document(itemId)
            .update(map)
    }

    // delete item firebase
    fun deleteItem(userId: String, itemId: String) {
        firebaseDB.collection(COLLECTION_USERS).document(userId)
            .collection(COLLECTION_PRODUCT).document(itemId)
            .delete()
    }

//    // update stock item after transaction
//    fun updateStockItem(userId: String, item: Product) {
//        firebaseDB.collection(COLLECTION_USERS).document(userId)
//            .collection(COLLECTION_PRODUCT).document(item.id)
//            .update("stock", item.stock - item.totalTransaction)
//    }

    // add transaction item to firebase
    fun saveTransactionItem(userId: String, data: Product, docTransaction: String) {
        firebaseDB.collection(COLLECTION_USERS).document(userId)
            .collection(COLLECTION_TRANSACTION).document(docTransaction)
            .collection(COLLECTION_TRANSACTION_ITEM).document()
            .set(data)
    }







    // add item to cart
    fun addItemCart(userId: String, sizeStock: SizeStock, totalTransaction: Int) {
        val docItemCart = firebaseDB.collection(COLLECTION_USERS).document(userId)
            .collection(COLLECTION_CART).document()
        docItemCart.set(
            SizeStock(
                sizeStock.id,
                docItemCart.id,
                sizeStock.nameCategory,
                sizeStock.nameItemCategory,
                sizeStock.nameProduct,
                sizeStock.color,
                sizeStock.size,
                sizeStock.price,
                sizeStock.stock,
                sizeStock.imageUrl,
                totalTransaction
            )
        )
    }

    // update totalTransaction item in cart
    fun updateItemCart(userId: String, data: SizeStock, statusIndicator: Int) {
        firebaseDB.collection(COLLECTION_USERS).document(userId)
            .collection(COLLECTION_CART).document(data.idCart)
            .update("totalTransaction", data.totalTransaction + statusIndicator)
    }

    // delete item in cart
    fun deleteItemCart(userId: String, data: SizeStock) {
        firebaseDB.collection(COLLECTION_USERS).document(userId)
            .collection(COLLECTION_CART).document(data.idCart)
            .delete()
    }
}