package com.dhandyjoe.stockku.ui.employee.activity

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.*
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.dhandyjoe.stockku.R
import com.dhandyjoe.stockku.adapter.CartAdapter
import com.dhandyjoe.stockku.adapter.DetailChangeAdapter
import com.dhandyjoe.stockku.adapter.DetailReturnAdapter
import com.dhandyjoe.stockku.databinding.ActivityDetailReturBinding
import com.dhandyjoe.stockku.model.*
import com.dhandyjoe.stockku.utils.*
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DetailReturActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailReturBinding
    private val firebaseDB = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val database = Database()

    private var docsReturn: ArrayList<SizeStock> = ArrayList()
    private var docsChange: ArrayList<SizeStock> = ArrayList()

    private var totalPriceReturn: Int = 0
    private var totalPriceChange: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailReturBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarMain.title = "Detail retur"
        setSupportActionBar(binding.toolbarMain)

        getReturnProductList()
        getChangeProductList()

        binding.btnReturnProduct.setOnClickListener {
            dialogSearchInvoice()
//            dialogSearchReturnProduct()
//            Toast.makeText(this, "test", Toast.LENGTH_SHORT).show()
        }

        binding.btnChangeProduct.setOnClickListener {
            dialogSearchReturnProduct()
        }
    }

    private fun getReturnProductList() {
        firebaseDB.collection(COLLECTION_USERS).document(currentUser?.uid ?: "")
            .collection(COLLECTION_RETURN_PRODUCT)
            .addSnapshotListener { snapshot, _ ->
                docsReturn = ArrayList()

                for(docItem in snapshot!!) {
                    docsReturn.add(docItem.toObject(SizeStock::class.java))
                }

                showReturnRecycleView(docsReturn)
            }
    }

    private fun showReturnRecycleView(value: ArrayList<SizeStock>) {
        binding.rvReturnProduct.layoutManager = LinearLayoutManager(this)
        val data = DetailReturnAdapter(value, this)
        binding.rvReturnProduct.adapter = data
    }







    private fun getChangeProductList() {
        firebaseDB.collection(COLLECTION_USERS).document(currentUser?.uid ?: "")
            .collection(COLLECTION_CHANGE_PRODUCT)
            .addSnapshotListener { snapshot, _ ->
                docsChange = ArrayList()

                for(docItem in snapshot!!) {
                    docsChange.add(docItem.toObject(SizeStock::class.java))
                }

                showChangecycleView(docsChange)
            }
    }

    private fun showChangecycleView(value: ArrayList<SizeStock>) {
        binding.rvChangeProduct.layoutManager = LinearLayoutManager(this)
        val data = DetailChangeAdapter(value, this)
        binding.rvChangeProduct.adapter = data
    }







    // RETURN PRODUCT
    private fun dialogSearchInvoice() {
        val listNameTransaction = ArrayList<String>()
        val transactionList = ArrayList<Transaction>()

        firebaseDB.collection(COLLECTION_USERS).document(currentUser?.uid ?: "")
            .collection(COLLECTION_TRANSACTION)
            .addSnapshotListener { snapshot, _ ->

                for(docItem in snapshot!!) {
                    transactionList.add(docItem.toObject(Transaction::class.java))
                }

                transactionList.forEach {
                    listNameTransaction.add(it.name)
                }
            }

        val cartDialog =  layoutInflater.inflate(R.layout.dialog_search_invoice, null)
        val dialog = Dialog(this, R.style.CustomDialog)
        dialog.apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(cartDialog)
        }

        val getValueCategory = cartDialog.findViewById<AutoCompleteTextView>(R.id.act_listTransactionRetur)
        getValueCategory.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, listNameTransaction))

        getValueCategory.doOnTextChanged { text, start, before, count ->

            cartDialog.findViewById<TextInputLayout>(R.id.til_itemTransaction).visibility = View.VISIBLE
            getItemTransaction(convertNameToIdTransaction(text.toString(), transactionList), cartDialog)
        }

        dialog.show()
    }

    private fun getItemTransaction(transactionId: String, cartDialog: View) {
        val listNameItemTransaction = ArrayList<String>()
        val itemTransactionList = ArrayList<SizeStock>()

        firebaseDB.collection(COLLECTION_USERS).document(currentUser?.uid ?: "")
            .collection(COLLECTION_TRANSACTION).document(transactionId)
            .collection(COLLECTION_TRANSACTION_ITEM)
            .addSnapshotListener { snapshot, _ ->

                for(docItem in snapshot!!) {
                    itemTransactionList.add(docItem.toObject(SizeStock::class.java))
                }

                itemTransactionList.forEach {
                    listNameItemTransaction.add(it.size)
                }
            }

        val getValueCategory = cartDialog.findViewById<AutoCompleteTextView>(R.id.act_listItemTransactionRetur)
        getValueCategory.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, listNameItemTransaction))

        getValueCategory.doOnTextChanged { text, start, before, count ->
            cartDialog.findViewById<Button>(R.id.btn_addReturnProduct).visibility = View.VISIBLE
//            Toast.makeText(this, convertNameToSizeStock(text.toString(), itemTransactionList).totalTransaction.toString(), Toast.LENGTH_SHORT).show()
            cartDialog.findViewById<Button>(R.id.btn_addReturnProduct).setOnClickListener {

                database.addReturnProduct(
                    currentUser?.uid ?: "",
                    convertNameToSizeStock(text.toString(), itemTransactionList)
                )
            }
        }
    }









    // CHANGE PRODUCT
    private fun dialogSearchReturnProduct() {
        val listNameCategory = ArrayList<String>()
        val categoryList = ArrayList<Category>()

        firebaseDB.collection(COLLECTION_USERS).document(currentUser?.uid ?: "")
            .collection(COLLECTION_CATEGORY)
            .addSnapshotListener { snapshot, _ ->

                for(docItem in snapshot!!) {
                    categoryList.add(docItem.toObject(Category::class.java))
                }

                categoryList.forEach {
                    listNameCategory.add(it.name)
                }
            }

        val cartDialog =  layoutInflater.inflate(R.layout.dialog_search_change_product, null)
        val dialog = Dialog(this, R.style.CustomDialog)
        dialog.apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(cartDialog)
        }

        val getValueCategory = cartDialog.findViewById<AutoCompleteTextView>(R.id.act_listCategoryRetur)
        getValueCategory.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, listNameCategory))

        getValueCategory.doOnTextChanged { text, start, before, count ->
//            cartDialog.findViewById<AutoCompleteTextView>(R.id.act_listItemCategoryRetur).setText("")
//            cartDialog.findViewById<AutoCompleteTextView>(R.id.act_listProductRetur).setText("")
//            cartDialog.findViewById<AutoCompleteTextView>(R.id.act_listColorRetur).setText("")
//            cartDialog.findViewById<AutoCompleteTextView>(R.id.act_listSizeStockRetur).setText("")

            cartDialog.findViewById<TextInputLayout>(R.id.til_itemCategory).visibility = View.VISIBLE
            getItemCategory(convertNameToId(text.toString(), categoryList), cartDialog)
        }

        dialog.show()
    }

    private fun getItemCategory (categoryId: String, cartDialog: View) {
        // Item Category
        val listNameItemCategory = ArrayList<String>()
        val itemCategoryList = ArrayList<Category>()

        firebaseDB.collection(COLLECTION_USERS).document(currentUser?.uid ?: "")
            .collection(COLLECTION_CATEGORY).document(categoryId)
            .collection(COLLECTION_ITEM_CATEGORY)
            .addSnapshotListener { snapshot, _ ->

                for(docItem in snapshot!!) {
                    itemCategoryList.add(docItem.toObject(Category::class.java))
                }

                itemCategoryList.forEach {
                    listNameItemCategory.add(it.name)
                }
            }

        val getValueItemCategory = cartDialog.findViewById<AutoCompleteTextView>(R.id.act_listItemCategoryRetur)
        getValueItemCategory.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, listNameItemCategory))

        getValueItemCategory.doOnTextChanged { text, start, before, count ->
            cartDialog.findViewById<AutoCompleteTextView>(R.id.act_listProductRetur).setText("")
            cartDialog.findViewById<AutoCompleteTextView>(R.id.act_listColorRetur).setText("")
            cartDialog.findViewById<AutoCompleteTextView>(R.id.act_listSizeStockRetur).setText("")

            cartDialog.findViewById<TextInputLayout>(R.id.til_product).visibility = View.VISIBLE
            getProduct(categoryId, convertNameToId(text.toString(), itemCategoryList), cartDialog)
        }
    }

    private fun getProduct(categoryId: String, itemCategoryId: String, cartDialog: View) {
        // Product
        val listNameProduct = ArrayList<String>()
        val productList = ArrayList<Product>()

        firebaseDB.collection(COLLECTION_USERS).document(currentUser?.uid ?: "")
            .collection(COLLECTION_CATEGORY).document(categoryId)
            .collection(COLLECTION_ITEM_CATEGORY).document(itemCategoryId)
            .collection(COLLECTION_PRODUCT)
            .addSnapshotListener { snapshot, _ ->

            for(docItem in snapshot!!) {
                productList.add(docItem.toObject(Product::class.java))
            }

            productList.forEach {
                listNameProduct.add(it.name)
            }
        }

        val getValueProduct = cartDialog.findViewById<AutoCompleteTextView>(R.id.act_listProductRetur)
        getValueProduct.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, listNameProduct))

        getValueProduct.doOnTextChanged { text, start, before, count ->
            cartDialog.findViewById<AutoCompleteTextView>(R.id.act_listColorRetur).setText("")
            cartDialog.findViewById<AutoCompleteTextView>(R.id.act_listSizeStockRetur).setText("")

            cartDialog.findViewById<TextInputLayout>(R.id.til_color).visibility = View.VISIBLE
            getColor(categoryId, itemCategoryId, convertNameToIdProduct(text.toString(), productList), cartDialog)
        }
    }

    private fun getColor(categoryId: String, itemCategoryId: String, productId: String, cartDialog: View) {
        // Product
        val listNameColor = ArrayList<String>()
        val colorList = ArrayList<Category>()

        firebaseDB.collection(COLLECTION_USERS).document(currentUser?.uid ?: "")
            .collection(COLLECTION_CATEGORY).document(categoryId)
            .collection(COLLECTION_ITEM_CATEGORY).document(itemCategoryId)
            .collection(COLLECTION_PRODUCT).document(productId)
            .collection(COLLECTION_COLOR_PRODUCT)
            .addSnapshotListener { snapshot, _ ->

                for(docItem in snapshot!!) {
                    colorList.add(docItem.toObject(Category::class.java))
                }

                colorList.forEach {
                    listNameColor.add(it.name)
                }
            }

        val getValueColor = cartDialog.findViewById<AutoCompleteTextView>(R.id.act_listColorRetur)
        getValueColor.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, listNameColor))

        getValueColor.doOnTextChanged { text, start, before, count ->
            cartDialog.findViewById<AutoCompleteTextView>(R.id.act_listSizeStockRetur).setText("")

            cartDialog.findViewById<TextInputLayout>(R.id.til_size).visibility = View.VISIBLE
            getSize(categoryId, itemCategoryId, productId, convertNameToId(text.toString(), colorList), cartDialog)
        }
    }

    private fun getSize(categoryId: String, itemCategoryId: String, productId: String, colorId: String, cartDialog: View) {
        // Product
        val listNameSize = ArrayList<String>()
        val sizeList = ArrayList<SizeStock>()

        firebaseDB.collection(COLLECTION_USERS).document(currentUser?.uid ?: "")
            .collection(COLLECTION_CATEGORY).document(categoryId)
            .collection(COLLECTION_ITEM_CATEGORY).document(itemCategoryId)
            .collection(COLLECTION_PRODUCT).document(productId)
            .collection(COLLECTION_COLOR_PRODUCT).document(colorId)
            .collection(COLLECTION_SIZE_STOCK_PRODUCT)
            .addSnapshotListener { snapshot, _ ->

                for(docItem in snapshot!!) {
                    sizeList.add(docItem.toObject(SizeStock::class.java))
                }

                sizeList.forEach {
                    listNameSize.add(it.size)
                }
            }

        val getValueSize = cartDialog.findViewById<AutoCompleteTextView>(R.id.act_listSizeStockRetur)
        getValueSize.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, listNameSize))

        getValueSize.doOnTextChanged { text, start, before, count ->
            cartDialog.findViewById<Button>(R.id.btn_addReturnProduct).visibility = View.VISIBLE

            cartDialog.findViewById<Button>(R.id.btn_addReturnProduct).setOnClickListener {
                database.addChangeProduct(
                    currentUser?.uid ?: "",
                    convertNameToSizeStock(text.toString(), sizeList)
                )
            }
        }
    }

    fun liveTotalReturn() {
        var total = 0
        for (item in docsReturn) {
            if (item.discount > 0) {
                total += resultDiscount(item.discount, item.price) * item.totalTransaction
            } else {
                total += item.price * item.totalTransaction
            }
        }

        val value = total - totalPriceChange

        binding.tvLiveTotal.text = "Rp. ${idrFormat(value)}"
        totalPriceReturn = value
    }

    fun liveTotalChange() {
        var total = 0
        for (item in docsChange) {
            if (item.discount > 0) {
                total += resultDiscount(item.discount, item.price) * item.totalTransaction
            } else {
                total += item.price * item.totalTransaction
            }
        }

        totalPriceChange = total
    }
}