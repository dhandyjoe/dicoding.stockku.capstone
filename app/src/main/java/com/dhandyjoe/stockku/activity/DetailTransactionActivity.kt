package com.dhandyjoe.stockku.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.dhandyjoe.stockku.adapter.CartAdapter
import com.dhandyjoe.stockku.adapter.ItemAdapter
import com.dhandyjoe.stockku.databinding.ActivityDetailTransactionBinding
import com.dhandyjoe.stockku.model.Cart
import com.dhandyjoe.stockku.model.Item
import com.dhandyjoe.stockku.util.COLLECTION_TRANSACTION
import com.google.firebase.firestore.FirebaseFirestore

class DetailTransactionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailTransactionBinding
    private val firebaseDB = FirebaseFirestore.getInstance()
    private val adapter = CartAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.title = "Detail transaksi"

        val data = intent.getParcelableExtra<Cart>(EXTRA_DATA)

        if (data != null) {
            getItemTransactionList(data.id)
        }
    }

    fun getItemTransactionList(transaksiId: String) {
        var doc = firebaseDB.collection(COLLECTION_TRANSACTION).document(transaksiId).collection("itemTransaksi")
        doc.get()
            .addOnSuccessListener {
                for(docItem in it) {
                    val user = ArrayList<Item>()
                    user.add(docItem.toObject(Item::class.java))
                    adapter.updateItem(user)
                }
                showRecycleView()
            }
            .addOnFailureListener {

            }
    }


    private fun showRecycleView() {
        binding.rvDetailTransaction.layoutManager = LinearLayoutManager(this)
        binding.rvDetailTransaction.adapter = adapter
    }
    companion object {
        const val EXTRA_DATA = "extra_data"
    }
}