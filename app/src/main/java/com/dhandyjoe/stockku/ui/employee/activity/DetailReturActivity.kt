package com.dhandyjoe.stockku.ui.employee.activity

import android.app.Dialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dhandyjoe.stockku.R
import com.dhandyjoe.stockku.adapter.*
import com.dhandyjoe.stockku.databinding.ActivityDetailReturBinding
import com.dhandyjoe.stockku.model.*
import com.dhandyjoe.stockku.utils.*
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class DetailReturActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailReturBinding
    private val firebaseDB = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val database = Database()

    private lateinit var docsReturn: ArrayList<SizeStock>
    private lateinit var docsChange: ArrayList<SizeStock>

    var totalPriceReturn: Int = 0
    var totalPriceChange: Int = 0

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
        }

        binding.btnChangeProduct.setOnClickListener {
            dialogSearchChangeProduct()
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

                val data = DetailReturnAdapter(docsReturn, this)
                showEmptyIndicatorReturn(data.isEmpty(), data)
            }
    }

    private fun showEmptyIndicatorReturn(isEmpty: Boolean, adapter: DetailReturnAdapter) {
        if (isEmpty) {
            binding.cvRetur.visibility = View.GONE
            binding.rvReturnProduct.visibility = View.GONE
        } else {
            binding.cvRetur.visibility = View.VISIBLE
            showReturnRecycleView(adapter)
            binding.rvReturnProduct.visibility = View.VISIBLE
        }
    }

    private fun showReturnRecycleView(adapter: DetailReturnAdapter) {
        binding.rvReturnProduct.layoutManager = LinearLayoutManager(this)
        binding.rvReturnProduct.adapter = adapter
    }







    private fun getChangeProductList() {
        firebaseDB.collection(COLLECTION_USERS).document(currentUser?.uid ?: "")
            .collection(COLLECTION_CHANGE_PRODUCT)
            .addSnapshotListener { snapshot, _ ->
                docsChange = ArrayList()

                for(docItem in snapshot!!) {
                    docsChange.add(docItem.toObject(SizeStock::class.java))
                }

                val data = DetailChangeAdapter(docsChange, this)
                showEmptyIndicatorChange(data.isEmpty(), data)
            }
    }

    private fun showChangeRecycleView(adapter: DetailChangeAdapter) {
        binding.rvChangeProduct.layoutManager = LinearLayoutManager(this)
        binding.rvChangeProduct.adapter = adapter
    }

    private fun showEmptyIndicatorChange(isEmpty: Boolean, adapter: DetailChangeAdapter) {
        if (isEmpty) {
            binding.rvChangeProduct.visibility = View.GONE
        } else {
            showChangeRecycleView(adapter)
            binding.rvChangeProduct.visibility = View.VISIBLE
        }
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






    // CHANGE PRODUCT v.2
    private fun dialogSearchChangeProduct() {
        val cartDialog =  layoutInflater.inflate(R.layout.dialog_search_change_product, null)
        val dialog = Dialog(this, R.style.CustomDialog)
        dialog.apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(cartDialog)
        }

        getCategory(cartDialog)

        dialog.show()
    }


    private fun getCategory(cartDialog: View) {
        var categoryId = ""

        // Category
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

        val getValueCategory = cartDialog.findViewById<AutoCompleteTextView>(R.id.act_listCategoryChange)
        getValueCategory.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, listNameCategory))

        getValueCategory.doOnTextChanged { text, start, before, count ->
            cartDialog.findViewById<TextInputLayout>(R.id.til_itemCategoryChange).visibility = View.VISIBLE

            getItemCategory(convertNameToId(text.toString(), categoryList), cartDialog)
        }
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

        val getValueItemCategory = cartDialog.findViewById<AutoCompleteTextView>(R.id.act_listItemCategoryChange)
        getValueItemCategory.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, listNameItemCategory))

        cartDialog.findViewById<Button>(R.id.btn_searchProductChange).setOnClickListener {
            getProductList(
                categoryId,
                convertNameToId(getValueItemCategory.text.toString(), itemCategoryList),
                cartDialog,
            )
        }
    }

    private fun getProductList(categoryId: String, itemCategoryId: String, cartDialog: View) {
        val doc = firebaseDB.collection(COLLECTION_USERS).document(currentUser?.uid ?: "")
            .collection(COLLECTION_CATEGORY).document(categoryId)
            .collection(COLLECTION_ITEM_CATEGORY).document(itemCategoryId)
            .collection(COLLECTION_PRODUCT)
        doc.addSnapshotListener { snapshot, _ ->
            val product = ArrayList<Product>()

            for(docItem in snapshot!!) {
                product.add(docItem.toObject(Product::class.java))
            }

            if (product.size > 0) {
                showRecycleView(product, categoryId, itemCategoryId, cartDialog)
            }

//            searchItem(product, categoryId, itemCategoryId)
        }
    }

    private fun showRecycleView(data: ArrayList<Product>, categoryId: String, itemCategoryId: String, cartDialog: View) {
        cartDialog.findViewById<RecyclerView>(R.id.rv_changeProduct).layoutManager = LinearLayoutManager(this)
        val adapter = LIstProductReturChangeAdapter(data, this, categoryId, itemCategoryId)
        cartDialog.findViewById<RecyclerView>(R.id.rv_changeProduct).adapter = adapter
        cartDialog.findViewById<RecyclerView>(R.id.rv_changeProduct).visibility = View.VISIBLE

        adapter.setOnItemClickCallback(object : LIstProductReturChangeAdapter.OnItemClickCallback{
            override fun onItemClicked(data: Product) {
                cartDialog.findViewById<LinearLayout>(R.id.ll_colorChange).visibility = View.VISIBLE

                getColorProduct(cartDialog, categoryId, itemCategoryId, data.id)
            }
        })
    }

    private fun getColorProduct(cartDialog: View, categoryId: String, itemCategory: String, productId: String) {
        firebaseDB.collection(COLLECTION_USERS).document(currentUser?.uid ?: "")
            .collection(COLLECTION_CATEGORY).document(categoryId)
            .collection(COLLECTION_ITEM_CATEGORY).document(itemCategory)
            .collection(COLLECTION_PRODUCT).document(productId)
            .collection(COLLECTION_COLOR_PRODUCT)
            .addSnapshotListener { snapshot, _ ->
                val colorProductList = ArrayList<ColorProduct>()

                for(docItem in snapshot!!) {
                    colorProductList.add(docItem.toObject(ColorProduct::class.java))
                }

                showListColorTransaction(
                    cartDialog,
                    cartDialog.context,
                    colorProductList,
                    categoryId,
                    itemCategory,
                    productId
                )
            }

    }

    private fun showListColorTransaction(
        cartDialog: View,
        context: Context,
        listColor: ArrayList<ColorProduct>,
        categoryId: String,
        itemCategory: String,
        productId: String
    ) {
        cartDialog.findViewById<RecyclerView>(R.id.rv_colorChange).layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val data = ColorTransactionAdapter(listColor)
        cartDialog.findViewById<RecyclerView>(R.id.rv_colorChange).adapter = data

        data.setOnItemClickCallback(object : ColorTransactionAdapter.OnItemClickCallback{
            override fun onItemClicked(data: ColorProduct) {
                cartDialog.findViewById<LinearLayout>(R.id.ll_sizeChange).visibility = View.VISIBLE
                getSizeStockProduct(
                    cartDialog,
                    categoryId,
                    itemCategory,
                    productId,
                    data.id
                )
            }
        })
    }

    private fun getSizeStockProduct(cartDialog: View, categoryId: String, itemCategory: String, productId: String, colorId: String) {
        firebaseDB.collection(COLLECTION_USERS).document(currentUser?.uid ?: "")
            .collection(COLLECTION_CATEGORY).document(categoryId)
            .collection(COLLECTION_ITEM_CATEGORY).document(itemCategory)
            .collection(COLLECTION_PRODUCT).document(productId)
            .collection(COLLECTION_COLOR_PRODUCT).document(colorId)
            .collection(COLLECTION_SIZE_STOCK_PRODUCT)
            .orderBy("size", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, _ ->
                val sizeStockProductList = ArrayList<SizeStock>()

                for(docItem in snapshot!!) {
                    sizeStockProductList.add(docItem.toObject(SizeStock::class.java))
                }

                showSizeStockProduct(cartDialog, sizeStockProductList, categoryId, itemCategory, productId)
            }
    }

    private fun showSizeStockProduct(
        cartDialog: View,
        listSize: ArrayList<SizeStock>,
        categoryId: String,
        itemCategory: String,
        productId: String
    ) {
        var statusIndicator = 1

        cartDialog.findViewById<RecyclerView>(R.id.rv_sizeTransaction).layoutManager = LinearLayoutManager(cartDialog.context, LinearLayoutManager.HORIZONTAL, false)
        val data = SizeStockTransactionAdapter(listSize)
        cartDialog.findViewById<RecyclerView>(R.id.rv_sizeTransaction).adapter = data

        data.setOnItemClickCallback(object : SizeStockTransactionAdapter.OnItemClickCallback{
            override fun onItemClicked(data: SizeStock) {
                cartDialog.findViewById<ConstraintLayout>(R.id.ll_btnAddChangeProduct).visibility = View.VISIBLE

                var newItem = true

                cartDialog.findViewById<Button>(R.id.btn_addChangeProduct).setOnClickListener {
                    if (data.stock == 0) {
                        Toast.makeText(cartDialog.context, "Stock habis", Toast.LENGTH_SHORT).show()
                    } else if (statusIndicator == 0) {
                        Toast.makeText(cartDialog.context, "Tentukan jumlah barang sebelum masuk ke keranjang", Toast.LENGTH_SHORT).show()
                    } else {
                        firebaseDB.collection(COLLECTION_USERS).document(currentUser?.uid ?: "")
                            .collection(COLLECTION_CHANGE_PRODUCT).get()
                            .addOnSuccessListener {
                                val docs = ArrayList<SizeStock>()
                                for (document in it) {
                                    docs.add(document.toObject(SizeStock::class.java))
                                }

                                for (doc in docs) {
                                    if (data.id == doc.id) {
                                        newItem = false
                                        database.updateChangeDetailRetur(currentUser?.uid ?: "", doc, statusIndicator)
                                        Toast.makeText(cartDialog.context, "Berhasil menambah stok produk dari retur", Toast.LENGTH_SHORT).show()
                                        Log.d("update", "update")
                                        break
                                    } else {
                                        newItem = true
                                    }
                                }

                                if (newItem) {
                                    database.addChangeProduct(currentUser?.uid ?: "", data)
                                    Toast.makeText(cartDialog.context, "Berhasil memasukkan produk ke retur", Toast.LENGTH_SHORT).show()
                                    Log.d("update", "add")
                                }
                            }
                    }
                }
            }
        })
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

        if (value > 0) {
            binding.tvMonitorRetur.text = "sisa"
        } else if (value == 0) {
            binding.tvMonitorRetur.text = ""
        } else {
            binding.tvMonitorRetur.text = "kurang"
        }

        binding.tvLiveTotal.text = "Rp. ${idrFormat(value)}"
//        totalPriceReturn = value
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