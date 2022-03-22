package com.dhandyjoe.stockku.ui.owner.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.dhandyjoe.stockku.adapter.DetailTransactionAdapter
import com.dhandyjoe.stockku.databinding.ActivityDetailTransactionBinding
import com.dhandyjoe.stockku.model.Item
import com.dhandyjoe.stockku.model.Transaction
import com.dhandyjoe.stockku.ui.employee.activity.DetailTransactionActivity
import com.dhandyjoe.stockku.utils.COLLECTION_TRANSACTION
import com.dhandyjoe.stockku.utils.COLLECTION_TRANSACTION_ITEM
import com.dhandyjoe.stockku.utils.COLLECTION_USERS
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DetailTransactionOwnerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailTransactionBinding
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val firebaseDB = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.title = "Detail transaksi"

        val data = intent.getParcelableExtra<Transaction>(EXTRA_DATA)
        val uid = intent.getStringExtra(EXTRA_UID)

        if (data != null) {
            getItemTransactionList(data.id, uid!!)
        }
    }

    private fun getItemTransactionList(transaksiId: String, uid: String) {
        val doc = firebaseDB.collection(COLLECTION_USERS).document(uid)
            .collection(COLLECTION_TRANSACTION).document(transaksiId)
            .collection(COLLECTION_TRANSACTION_ITEM)
        doc.get()
            .addOnSuccessListener {
                val user = ArrayList<Item>()
                for(docItem in it) {
                    user.add(docItem.toObject(Item::class.java))
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
        const val EXTRA_UID = "extra_uid"
    }
}