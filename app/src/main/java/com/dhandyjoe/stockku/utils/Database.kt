package com.dhandyjoe.stockku.utils

import com.dhandyjoe.stockku.model.*
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
                "",
                sizeStock.category,
                sizeStock.itemCategory,
                sizeStock.product,
                sizeStock.color,
                sizeStock.size,
                sizeStock.price,
                sizeStock.stock,
                sizeStock.imageUrl,
                sizeStock.totalTransaction,
                sizeStock.discount
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


    // add discount
    fun addDiscount(userId: String, idCart: String, discount: Int) {
        val map = HashMap<String, Any>()
        map["discount"] = discount

        firebaseDB.collection(COLLECTION_USERS).document(userId)
            .collection(COLLECTION_CART).document(idCart)
            .update(map)
    }


    // delete item firebase
    fun deleteItem(userId: String, itemId: String) {
        firebaseDB.collection(COLLECTION_USERS).document(userId)
            .collection(COLLECTION_PRODUCT).document(itemId)
            .delete()
    }

    // update stock item after transaction
    fun updateStockItem(userId: String, item: SizeStock) {
        firebaseDB.collection(COLLECTION_USERS).document(userId)
            .collection(COLLECTION_CATEGORY).document(item.category.id)
            .collection(COLLECTION_ITEM_CATEGORY).document(item.itemCategory.id)
            .collection(COLLECTION_PRODUCT).document(item.product.id)
            .collection(COLLECTION_COLOR_PRODUCT).document(item.color.id)
            .collection(COLLECTION_SIZE_STOCK_PRODUCT).document(item.id)
            .update("stock", item.stock - item.totalTransaction)
    }

    // add transaction item to firebase
    fun saveTransactionItem(userId: String, data: SizeStock, docTransaction: String) {
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
                sizeStock.idRetur,
                Category(sizeStock.category.id, sizeStock.category.name),
                Category(sizeStock.itemCategory.id, sizeStock.itemCategory.name),
                Product(sizeStock.product.id, sizeStock.product.name, sizeStock.product.imageUrl),
                Category(sizeStock.color.id, sizeStock.color.name),
                sizeStock.size,
                sizeStock.price,
                sizeStock.stock,
                sizeStock.imageUrl,
                totalTransaction,
                sizeStock.discount
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


    // add transaction item to firebase
    fun saveReturItemReturn(userId: String, data: SizeStock, returId: String) {
        firebaseDB.collection(COLLECTION_USERS).document(userId)
            .collection(COLLECTION_RETUR).document(returId)
            .collection(COLLECTION_RETURN_PRODUCT).document()
            .set(data)
    }

    // add transaction item to firebase
    fun saveReturItemChange(userId: String, data: SizeStock, returId: String) {
        firebaseDB.collection(COLLECTION_USERS).document(userId)
            .collection(COLLECTION_RETUR).document(returId)
            .collection(COLLECTION_CHANGE_PRODUCT).document()
            .set(data)
    }





    // add return product
    fun addReturnProduct(currentUser: String, sizeStock: SizeStock) {
        val doc = firebaseDB.collection(COLLECTION_USERS).document(currentUser)
            .collection(COLLECTION_RETURN_PRODUCT).document()
        doc.set(SizeStock(
            sizeStock.id,
            sizeStock.idCart,
            doc.id,
            sizeStock.category,
            sizeStock.itemCategory,
            sizeStock.product,
            sizeStock.color,
            sizeStock.size,
            sizeStock.price,
            sizeStock.stock,
            sizeStock.imageUrl,
            sizeStock.totalTransaction,
            sizeStock.discount
        ))
    }

    // add change product
    fun addChangeProduct(currentUser: String, sizeStock: SizeStock) {
        val doc = firebaseDB.collection(COLLECTION_USERS).document(currentUser)
            .collection(COLLECTION_CHANGE_PRODUCT).document()
        doc.set(SizeStock(
            sizeStock.id,
            sizeStock.idCart,
            doc.id,
            sizeStock.category,
            sizeStock.itemCategory,
            sizeStock.product,
            sizeStock.color,
            sizeStock.size,
            sizeStock.price,
            sizeStock.stock,
            sizeStock.imageUrl,
            1,
            sizeStock.discount
        ))
    }

    // update discount cchange
    fun addDiscountChange(userId: String, idRetur: String, discount: Int) {
        val map = HashMap<String, Any>()
        map["discount"] = discount

        firebaseDB.collection(COLLECTION_USERS).document(userId)
            .collection(COLLECTION_CHANGE_PRODUCT).document(idRetur)
            .update(map)
    }

    // update totalTransaction item in retur
    fun updateReturnDetailRetur(userId: String, data: SizeStock, statusIndicator: Int) {
        firebaseDB.collection(COLLECTION_USERS).document(userId)
            .collection(COLLECTION_RETURN_PRODUCT).document(data.idRetur)
            .update("totalTransaction", data.totalTransaction + statusIndicator)
    }

    // update totalTransaction item in retur
    fun updateChangeDetailRetur(userId: String, data: SizeStock, statusIndicator: Int) {
        firebaseDB.collection(COLLECTION_USERS).document(userId)
            .collection(COLLECTION_CHANGE_PRODUCT).document(data.idRetur)
            .update("totalTransaction", data.totalTransaction + statusIndicator)
    }

    // delete item in retur
    fun deleteReturnDetailRetur(userId: String, data: SizeStock) {
        firebaseDB.collection(COLLECTION_USERS).document(userId)
            .collection(COLLECTION_RETURN_PRODUCT).document(data.idRetur)
            .delete()
    }

    // delete item in retur
    fun deleteChangeDetailRetur(userId: String, data: SizeStock) {
        firebaseDB.collection(COLLECTION_USERS).document(userId)
            .collection(COLLECTION_CHANGE_PRODUCT).document(data.idRetur)
            .delete()
    }

    // update stock after retur
    fun updateStockAfterReturReturn(userId: String, currentStock: Int, item: SizeStock) {
        firebaseDB.collection(COLLECTION_USERS).document(userId)
            .collection(COLLECTION_CATEGORY).document(item.category.id)
            .collection(COLLECTION_ITEM_CATEGORY).document(item.itemCategory.id)
            .collection(COLLECTION_PRODUCT).document(item.product.id)
            .collection(COLLECTION_COLOR_PRODUCT).document(item.color.id)
            .collection(COLLECTION_SIZE_STOCK_PRODUCT).document(item.id)
            .update("stock", currentStock + item.totalTransaction)
    }

    fun updateStockAfterReturChange(userId: String, item: SizeStock) {
        firebaseDB.collection(COLLECTION_USERS).document(userId)
            .collection(COLLECTION_CATEGORY).document(item.category.id)
            .collection(COLLECTION_ITEM_CATEGORY).document(item.itemCategory.id)
            .collection(COLLECTION_PRODUCT).document(item.product.id)
            .collection(COLLECTION_COLOR_PRODUCT).document(item.color.id)
            .collection(COLLECTION_SIZE_STOCK_PRODUCT).document(item.id)
            .update("stock", item.stock - item.totalTransaction)
    }

}