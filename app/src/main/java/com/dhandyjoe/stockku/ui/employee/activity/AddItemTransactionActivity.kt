package com.dhandyjoe.stockku.ui.employee.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.dhandyjoe.stockku.R
import com.dhandyjoe.stockku.adapter.ItemCartAdapter
import com.dhandyjoe.stockku.databinding.ActivityAddItemTransactionBinding
import com.dhandyjoe.stockku.model.Item
import com.dhandyjoe.stockku.utils.COLLECTION_CART
import com.dhandyjoe.stockku.utils.COLLECTION_USERS
import com.dhandyjoe.stockku.utils.Database
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddItemTransactionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddItemTransactionBinding
    private val firebaseDB = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val database = Database()
    private val listItemSearch = ArrayList<Item>()
    private lateinit var menuItemCount: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddItemTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarMain.title = "Pilih item"
        setSupportActionBar(binding.toolbarMain)

        getBarangList()
    }

    private fun getBarangList() {
        val doc = firebaseDB.collection(COLLECTION_USERS).document(currentUser?.uid ?: "")
            .collection("barang")
        doc.addSnapshotListener { snapshot, _ ->
            val user = ArrayList<Item>()

            for(docItem in snapshot!!) {
                user.add(docItem.toObject(Item::class.java))
            }

            if (user.size > 0) {
                showRecycleView(user)
            } else {
                binding.animationView.visibility = View.VISIBLE
                binding.rvTransactionItem.visibility = View.GONE
            }

            searchItem(user)
        }
    }

    private fun showRecycleView(data: ArrayList<Item>) {
        binding.animationView.visibility = View.GONE
        binding.rvTransactionItem.layoutManager = LinearLayoutManager(this)
        val data = ItemCartAdapter(data, this)
        binding.rvTransactionItem.adapter = data
        binding.rvTransactionItem.visibility = View.VISIBLE

        data.setOnItemClickCallback(object : ItemCartAdapter.OnItemClickCallback{
            override fun onItemClicked(data: Item) {
                cartDialog(data)
            }
        })
    }

    private fun cartDialog(data: Item) {
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
                        val docs = ArrayList<Item>()
                        for (document in it) {
                            docs.add(document.toObject(Item::class.java))
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

    private fun searchItem(data: ArrayList<Item>) {
        binding.svItem.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                listItemSearch.clear()
                data.forEach {
                    if (it.name.lowercase().contains(query!!.lowercase())) {
                        listItemSearch.add(it)
                        showRecycleView(listItemSearch)
                    } else if (listItemSearch.isEmpty()) {
                        showRecycleView(listItemSearch)
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
                        showRecycleView(listItemSearch)
                    } else if (listItemSearch.isEmpty()) {
                        showRecycleView(listItemSearch)
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