package com.dhandyjoe.stockku.ui.employee.activity

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dhandyjoe.stockku.R
import com.dhandyjoe.stockku.adapter.*
import com.dhandyjoe.stockku.databinding.ActivityAddItemReturBinding
import com.dhandyjoe.stockku.databinding.ActivityDetailReturBinding
import com.dhandyjoe.stockku.model.*
import com.dhandyjoe.stockku.utils.*
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AddItemReturActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddItemReturBinding
    private val firebaseDB = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val database = Database()

    private lateinit var docsReturn: ArrayList<SizeStock>
    private lateinit var docsChange: ArrayList<SizeStock>

    var totalPriceReturn: Int = 0
    var totalPriceChange: Int = 0

    private var note = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddItemReturBinding.inflate(layoutInflater)
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


        binding.btnSaveRetur.setOnClickListener {
//            Toast.makeText(this, note, Toast.LENGTH_SHORT).show()
            if (docsReturn.size > 0 && docsChange.size > 0) {
                val alert = AlertDialog.Builder(this)
                alert.setTitle("Retur")
                alert.setMessage("Apakah anda ingin menyimpan retur ini?")
                alert.setPositiveButton("Iya", DialogInterface.OnClickListener { dialog, which ->
                    saveRetur(docsReturn, docsChange, note)
                })

                alert.setNegativeButton("Tidak") { dialog, which -> }

                alert.show()
            } else {
                Toast.makeText(this, "Produk kembali dan produk pengganti harus diisi minimal 1 item", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnNoteRetur.setOnClickListener {
            dialogInputNote()
        }
    }

    private fun dialogInputNote() {
        val cartDialog =  layoutInflater.inflate(R.layout.dialog_add_note_retur, null)
        val dialog = Dialog(this, R.style.CustomDialog)
        dialog.apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(cartDialog)
        }

        if (note.isNotEmpty()) {
            cartDialog.findViewById<EditText>(R.id.et_inputNote).setText(note)
        }

        cartDialog.findViewById<Button>(R.id.btn_addNote).setOnClickListener {
            note = cartDialog.findViewById<EditText>(R.id.et_inputNote).text.toString()
            binding.tvInputNoteRetur.text = note

            dialog.cancel()
        }

        dialog.show()
    }

    private fun saveRetur(dataReturn: ArrayList<SizeStock>, dataChange: ArrayList<SizeStock>, note: String) {
        val patternNameRetur = "yyyyMMddHHmmss"
        val simpleDateFormat1 = SimpleDateFormat(patternNameRetur)
        val nameRetur: String = simpleDateFormat1.format(Date())

        val patternDateRetur = "dd MMMM yyyy hh:mm:ss"
        val simpleDateFormat2 = SimpleDateFormat(patternDateRetur, Locale("ID"))
        val dateRetur: String = simpleDateFormat2.format(Date())

        // save transaction
        val docRetur = firebaseDB.collection(COLLECTION_USERS).document(currentUser?.uid ?: "")
            .collection(COLLECTION_RETUR).document()
        val retur = Retur(docRetur.id, "RTR${nameRetur}", convertUidToName(currentUser?.uid ?: ""), totalPriceReturn, dateRetur, note)
        docRetur.set(retur)


        // Save Item Retur
        for (i in dataReturn.indices) {
            database.saveReturItemReturn(currentUser?.uid ?: "", dataReturn[i], docRetur.id)
        }
        for (i in dataChange.indices) {
            database.saveReturItemChange(currentUser?.uid ?: "", dataChange[i], docRetur.id)
        }

        // Delete item Retur
        for (i in dataReturn.indices) {
            database.deleteReturnDetailRetur(currentUser?.uid ?: "", dataReturn[i])
        }
        for (i in dataChange.indices) {
            database.deleteChangeDetailRetur(currentUser?.uid ?: "", dataChange[i])
        }

        // Update stock after retur
        for (i in dataReturn.indices) {
            firebaseDB.collection(COLLECTION_USERS).document(currentUser?.uid ?: "")
                .collection(COLLECTION_CATEGORY).document(dataReturn[i].category.id)
                .collection(COLLECTION_ITEM_CATEGORY).document(dataReturn[i].itemCategory.id)
                .collection(COLLECTION_PRODUCT).document(dataReturn[i].product.id)
                .collection(COLLECTION_COLOR_PRODUCT).document(dataReturn[i].color.id)
                .collection(COLLECTION_SIZE_STOCK_PRODUCT).document(dataReturn[i].id)
                .get()
                .addOnSuccessListener {
                    if (it != null) {
                        val data = it.toObject(SizeStock::class.java)!!

                        database.updateStockAfterReturReturn(currentUser?.uid ?: "", data.stock, dataReturn[i])
                    }
                }
        }
        for (i in dataChange.indices) {
            database.updateStockAfterReturChange(currentUser?.uid ?: "", dataChange[i])
        }

        onBackPressed()
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
            binding.textView4.visibility = View.VISIBLE
            binding.btnChangeProduct.visibility = View.VISIBLE
            binding.rvReturnProduct.visibility = View.VISIBLE
            liveTotalReturn()
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
            getItemTransaction(convertNameToIdTransaction(text.toString(), transactionList), cartDialog, dialog)
        }

        dialog.show()
    }

    private fun getItemTransaction(transactionId: String, cartDialog: View, dialog: Dialog) {
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

                dialog.cancel()
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

        getCategory(cartDialog, dialog)

        dialog.show()
    }


    private fun getCategory(cartDialog: View, dialog: Dialog) {
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

            cartDialog.findViewById<SearchView>(R.id.sv_itemChange).visibility = View.GONE
            cartDialog.findViewById<Button>(R.id.btn_searchProductChange).visibility = View.GONE
            cartDialog.findViewById<RecyclerView>(R.id.rv_changeProduct).visibility = View.GONE
            cartDialog.findViewById<LinearLayout>(R.id.ll_colorChange).visibility = View.GONE
            cartDialog.findViewById<LinearLayout>(R.id.ll_sizeChange).visibility = View.GONE
            cartDialog.findViewById<Button>(R.id.btn_addChangeProduct).visibility = View.GONE

            cartDialog.findViewById<AutoCompleteTextView>(R.id.act_listItemCategoryChange).setText("")
            getItemCategory(convertNameToId(text.toString(), categoryList), cartDialog, dialog)
        }
    }

    private fun getItemCategory (categoryId: String, cartDialog: View, dialog: Dialog) {
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

        getValueItemCategory.doOnTextChanged { text, start, before, count ->
            cartDialog.findViewById<SearchView>(R.id.sv_itemChange).visibility = View.VISIBLE
            cartDialog.findViewById<Button>(R.id.btn_searchProductChange).visibility = View.VISIBLE

            cartDialog.findViewById<RecyclerView>(R.id.rv_changeProduct).visibility = View.GONE
            cartDialog.findViewById<LinearLayout>(R.id.ll_colorChange).visibility = View.GONE
            cartDialog.findViewById<LinearLayout>(R.id.ll_sizeChange).visibility = View.GONE
            cartDialog.findViewById<Button>(R.id.btn_addChangeProduct).visibility = View.GONE


            cartDialog.findViewById<Button>(R.id.btn_searchProductChange).setOnClickListener {
                if (text.isNullOrEmpty()) {
                    Toast.makeText(cartDialog.context, "Pilih item category", Toast.LENGTH_SHORT).show()
                } else {
                    getProductList(
                        categoryId,
                        convertNameToId(text.toString(), itemCategoryList),
                        cartDialog,
                        dialog
                    )
                }
            }
        }
    }

    private fun getProductList(categoryId: String, itemCategoryId: String, cartDialog: View, dialog: Dialog) {
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
                showRecycleView(product, categoryId, itemCategoryId, cartDialog, dialog)
            } else {
                cartDialog.findViewById<RecyclerView>(R.id.rv_changeProduct).visibility = View.GONE
            }

//            searchItem(product, categoryId, itemCategoryId)
        }
    }

    private fun showRecycleView(data: ArrayList<Product>, categoryId: String, itemCategoryId: String, cartDialog: View, dialog: Dialog) {
        cartDialog.findViewById<RecyclerView>(R.id.rv_changeProduct).layoutManager = LinearLayoutManager(this)
        val adapter = LIstProductReturChangeAdapter(data, this, categoryId, itemCategoryId)
        cartDialog.findViewById<RecyclerView>(R.id.rv_changeProduct).adapter = adapter
        cartDialog.findViewById<RecyclerView>(R.id.rv_changeProduct).visibility = View.VISIBLE

        adapter.setOnItemClickCallback(object : LIstProductReturChangeAdapter.OnItemClickCallback{
            override fun onItemClicked(data: Product) {
                cartDialog.findViewById<LinearLayout>(R.id.ll_colorChange).visibility = View.VISIBLE
                cartDialog.findViewById<LinearLayout>(R.id.ll_sizeChange).visibility = View.GONE
                getColorProduct(cartDialog, categoryId, itemCategoryId, data.id, dialog)
            }
        })
    }

    private fun getColorProduct(cartDialog: View, categoryId: String, itemCategory: String, productId: String, dialog: Dialog) {
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
                    productId,
                    dialog
                )
            }

    }

    private fun showListColorTransaction(
        cartDialog: View,
        context: Context,
        listColor: ArrayList<ColorProduct>,
        categoryId: String,
        itemCategory: String,
        productId: String,
        dialog: Dialog
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
                    data.id,
                    dialog
                )
            }
        })
    }

    private fun getSizeStockProduct(cartDialog: View, categoryId: String, itemCategory: String, productId: String, colorId: String, dialog: Dialog) {
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

                showSizeStockProduct(cartDialog, sizeStockProductList, categoryId, itemCategory, productId, dialog)
            }
    }

    private fun showSizeStockProduct(
        cartDialog: View,
        listSize: ArrayList<SizeStock>,
        categoryId: String,
        itemCategory: String,
        productId: String,
        dialog: Dialog
    ) {
        var statusIndicator = 1

        cartDialog.findViewById<RecyclerView>(R.id.rv_sizeTransaction).layoutManager = LinearLayoutManager(cartDialog.context, LinearLayoutManager.HORIZONTAL, false)
        val data = SizeStockTransactionAdapter(listSize)
        cartDialog.findViewById<RecyclerView>(R.id.rv_sizeTransaction).adapter = data

        data.setOnItemClickCallback(object : SizeStockTransactionAdapter.OnItemClickCallback{
            override fun onItemClicked(data: SizeStock) {
                cartDialog.findViewById<Button>(R.id.btn_addChangeProduct).visibility = View.VISIBLE

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

                    dialog.cancel()
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