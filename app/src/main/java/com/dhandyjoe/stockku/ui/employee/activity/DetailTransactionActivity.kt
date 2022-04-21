package com.dhandyjoe.stockku.ui.employee.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.dhandyjoe.stockku.adapter.DetailTransactionAdapter
import com.dhandyjoe.stockku.databinding.ActivityDetailTransactionBinding
import com.dhandyjoe.stockku.model.Transaction
import com.dhandyjoe.stockku.model.Product
import com.dhandyjoe.stockku.model.SizeStock
import com.dhandyjoe.stockku.utils.COLLECTION_TRANSACTION
import com.dhandyjoe.stockku.utils.COLLECTION_TRANSACTION_ITEM
import com.dhandyjoe.stockku.utils.COLLECTION_USERS
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DetailTransactionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailTransactionBinding
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val firebaseDB = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.title = "Detail transaksi"

        val data = intent.getParcelableExtra<Transaction>(EXTRA_DATA)

        if (data != null) {
            getItemTransactionList(data.id)
        }
    }

    private fun getItemTransactionList(transaksiId: String) {
        val doc = firebaseDB.collection(COLLECTION_USERS).document(currentUser?.uid ?: "")
            .collection(COLLECTION_TRANSACTION).document(transaksiId).collection(COLLECTION_TRANSACTION_ITEM)
        doc.get()
            .addOnSuccessListener {
                val user = ArrayList<SizeStock>()
                for(docItem in it) {
                    user.add(docItem.toObject(SizeStock::class.java))
                }

                val data = DetailTransactionAdapter(user, this)
                showRecycleView(data)
            }

            .addOnFailureListener {}
    }


    private fun showRecycleView(adapter: DetailTransactionAdapter) {
        binding.rvDetailTransaction.layoutManager = LinearLayoutManager(this)
        binding.rvDetailTransaction.adapter = adapter
    }

    companion object {
        const val EXTRA_DATA = "extra_data"
    }
}