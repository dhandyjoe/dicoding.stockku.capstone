package com.dhandyjoe.stockku.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.dhandyjoe.stockku.adapter.TransactionAdapter
import com.dhandyjoe.stockku.databinding.ActivityTransactionBinding
import com.dhandyjoe.stockku.model.Item
import com.google.firebase.firestore.FirebaseFirestore

class TransactionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTransactionBinding
    private val firebaseDB = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarMain.title = "Transaksi"

        getBarangList()

        binding
    }

    fun getBarangList() {
        var doc = firebaseDB.collection("barang")
        doc.addSnapshotListener { snapshot, _ ->
            val user = ArrayList<Item>()

            for(docItem in snapshot!!) {
                user.add(docItem.toObject(Item::class.java))
            }

            if (user.size > 0) {
                binding.tvStatusNoData.visibility = View.GONE
                binding.rvTransactionItem.layoutManager = LinearLayoutManager(this)
                val data = TransactionAdapter(user)
                binding.rvTransactionItem.adapter = data
                binding.rvTransactionItem.visibility = View.VISIBLE
            } else {
                binding.tvStatusNoData.visibility = View.VISIBLE
                binding.rvTransactionItem.visibility = View.GONE
            }
        }
    }
}