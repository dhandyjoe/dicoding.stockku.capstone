package com.dhandyjoe.stockku.ui.employee.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.dhandyjoe.stockku.R
import com.dhandyjoe.stockku.adapter.ListProductTransactionAdapter
import com.dhandyjoe.stockku.databinding.ActivityAddItemTransactionBinding
import com.dhandyjoe.stockku.model.Category
import com.dhandyjoe.stockku.model.Product
import com.dhandyjoe.stockku.utils.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddItemTransactionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddItemTransactionBinding
    private val firebaseDB = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val database = Database()
    private val listItemSearch = ArrayList<Product>()
    private lateinit var menuItemCount: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddItemTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarMain.title = "Pilih item"
        setSupportActionBar(binding.toolbarMain)


        getCategory()

//        getBarangList()
    }

    private fun getCategory() {
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

        val getValueCategory = findViewById<AutoCompleteTextView>(R.id.act_listCategoryTransaction)
        getValueCategory.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, listNameCategory))

        getValueCategory.doOnTextChanged { text, start, before, count ->
//            Toast.makeText(this, text.toString(), Toast.LENGTH_SHORT).show()

            findViewById<AutoCompleteTextView>(R.id.act_listItemCategoryTransaction).setText("")

            binding.textInputLayout4.visibility = View.VISIBLE
            getItemCategory(convertNameToId(text.toString(), categoryList))
        }
    }

    private fun getItemCategory (categoryId: String) {
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

        val getValueItemCategory = findViewById<AutoCompleteTextView>(R.id.act_listItemCategoryTransaction)
        getValueItemCategory.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, listNameItemCategory))


        binding.button.setOnClickListener {
            getProductList(categoryId, convertNameToId(getValueItemCategory.text.toString(), itemCategoryList))
        }
    }

    private fun getProductList(categoryId: String, itemCategoryId: String) {
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
                showRecycleView(product, categoryId, itemCategoryId)
            } else {
                binding.animationView.visibility = View.VISIBLE
                binding.rvTransactionItem.visibility = View.GONE
            }

            searchItem(product, categoryId, itemCategoryId)
        }
    }

    private fun showRecycleView(data: ArrayList<Product>, categoryId: String, itemCategoryId: String) {
        binding.animationView.visibility = View.GONE
        binding.rvTransactionItem.layoutManager = LinearLayoutManager(this)
        val data = ListProductTransactionAdapter(data, this, categoryId, itemCategoryId)
        binding.rvTransactionItem.adapter = data
        binding.rvTransactionItem.visibility = View.VISIBLE

//        data.setOnItemClickCallback(object : ItemCartAdapter.OnItemClickCallback{
//            override fun onItemClicked(data: Product) {
//                cartDialog(data)
//            }
//        })
    }

    private fun cartDialog(data: Product) {
        var statusIndicator = 1
        val cartDialog =  layoutInflater.inflate(R.layout.dialog_cart, null)
        val dialog = BottomSheetDialog(this)
        dialog.apply {
            setContentView(cartDialog)
            setTitle("")
        }
        cartDialog.findViewById<TextView>(R.id.tv_item_addcart).text = data.name
        cartDialog.findViewById<ImageView>(R.id.iv_minus).setOnClickListener {
            if (statusIndicator > 0) {
                statusIndicator--
                cartDialog.findViewById<TextView>(R.id.tv_indicatorItemCart).text = statusIndicator.toString()
            }
        }
        cartDialog.findViewById<ImageView>(R.id.iv_plus).setOnClickListener {
            statusIndicator++
            cartDialog.findViewById<TextView>(R.id.tv_indicatorItemCart).text = statusIndicator.toString()
        }
        cartDialog.findViewById<Button>(R.id.btn_cart).setOnClickListener {
            var newItem = true
            if (data.stock == 0) {
                Toast.makeText(this, "Stock habis", Toast.LENGTH_SHORT).show()
            } else if (statusIndicator == 0) {
                Toast.makeText(this, "Tentukan jumlah barang sebelum masuk ke keranjang", Toast.LENGTH_SHORT).show()
            } else {
                data.totalTransaction = statusIndicator

                firebaseDB.collection(COLLECTION_USERS).document(currentUser?.uid ?: "")
                    .collection(COLLECTION_CART).get()
                    .addOnSuccessListener {
                        val docs = ArrayList<Product>()
                        for (document in it) {
                            docs.add(document.toObject(Product::class.java))
                        }

                        for (doc in docs) {
                            if (data.id == doc.id) {
                                newItem = false
                                database.updateItemCart(currentUser?.uid ?: "", doc, statusIndicator)
                                Log.d("update", "update")
                                break
                            } else {
                                newItem = true
                            }
                        }

                        if (newItem) {
                            database.addItemCart(currentUser?.uid ?: "", data, statusIndicator)
                            Log.d("update", "add")

                            // seek count item cart
//                            menuItemCount.title = docs.size.toString()
                        }

//                        menuItemCount.title = "${docs.size}"
                    }

                dialog.dismiss()
            }

            Toast.makeText(this, "Berhasil memasukkan produk ke keranjang", Toast.LENGTH_SHORT).show()
        }
        if (data.imageUrl.isEmpty()) {
            Glide.with(this)
                .load(R.drawable.empty_image)
                .into(cartDialog.findViewById<ImageView>(R.id.iv_addcart))
        } else {
            Glide.with(this)
                .load(data.imageUrl)
                .centerCrop()
                .into(cartDialog.findViewById<ImageView>(R.id.iv_addcart))
        }

        dialog.show()
    }

    private fun searchItem(data: ArrayList<Product>, categoryId: String, itemCategoryId: String) {
        binding.svItem.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                listItemSearch.clear()
                data.forEach {
                    if (it.name.lowercase().contains(query!!.lowercase())) {
                        listItemSearch.add(it)
                        showRecycleView(listItemSearch, categoryId, itemCategoryId)
                    } else if (listItemSearch.isEmpty()) {
                        showRecycleView(listItemSearch, categoryId, itemCategoryId)
                        binding.animationView.visibility = View.VISIBLE
                    }
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                listItemSearch.clear()
                data.forEach {
                    if (it.name.lowercase().contains(newText!!.lowercase())) {
                        listItemSearch.add(it)
                        showRecycleView(listItemSearch, categoryId, itemCategoryId)
                    } else if (listItemSearch.isEmpty()) {
                        showRecycleView(listItemSearch, categoryId, itemCategoryId)
                        binding.animationView.visibility = View.VISIBLE
                    }
                }
                return false
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.cart_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_cart -> {
                val intent  = Intent(this, CartActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        const val EXTRA_SELECTED_VALUE = "extra_selected_value"
        const val RESULT_CODE = 110
    }
}