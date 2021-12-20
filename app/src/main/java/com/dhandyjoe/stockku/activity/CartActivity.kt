package com.dhandyjoe.stockku.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.dhandyjoe.stockku.adapter.CartAdapter
import com.dhandyjoe.stockku.adapter.ItemCartAdapter
import com.dhandyjoe.stockku.databinding.ActivityCartBinding
import com.dhandyjoe.stockku.model.Cart
import com.dhandyjoe.stockku.model.Item
import com.dhandyjoe.stockku.util.Database
import com.google.firebase.firestore.FirebaseFirestore

class CartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCartBinding
    private val database = Database()
    private val firebaseDB = FirebaseFirestore.getInstance()
    private val adapter = CartAdapter()

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == AddItemTransactionActivity.RESULT_CODE && result.data != null) {
            val selectedValue = result.data?.getParcelableExtra<Item>(AddItemTransactionActivity.EXTRA_SELECTED_VALUE)
            if (selectedValue != null) {
                val list = ArrayList<Item>()
                list.add(selectedValue)
                adapter.updateItem(list)
            }
        }
        showEmptyIndicator(adapter.isEmpty())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.title = "Keranjang"

        binding.ivAddItemTransaction.setOnClickListener {
            val moveForResultIntent = Intent(this, AddItemTransactionActivity::class.java)
            resultLauncher.launch(moveForResultIntent)
        }

        binding.btnSaveTransaction.setOnClickListener {
            saveTransaction(adapter.listItemCart)
            finish()
            Toast.makeText(this, "Transaksi berhasil disimpan.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showRecycleView() {
        binding.rvListItemCart.layoutManager = LinearLayoutManager(this)
        binding.rvListItemCart.adapter = adapter
    }

    fun saveTransaction(dataitem: ArrayList<Item>) {
        var docTransaction = firebaseDB.collection("transaksi").document()
        val item = Cart(docTransaction.id, "transaksi1 - debug")
        docTransaction.set(item)

        for (i in dataitem.indices) {
            database.addItemTransaction(dataitem[i], docTransaction.id)
            database.updateStockItem(dataitem[i])
        }
    }

    private fun showEmptyIndicator(isEmpty: Boolean) {
        if (isEmpty) {
            binding.ivIndicatorCart.visibility = View.VISIBLE
            binding.btnSaveTransaction.visibility = View.GONE
            binding.rvListItemCart.visibility = View.GONE
        } else {
            binding.ivIndicatorCart.visibility = View.GONE
            binding.btnSaveTransaction.visibility = View.VISIBLE
            showRecycleView()
            binding.rvListItemCart.visibility = View.VISIBLE
        }
    }
}