package com.dhandyjoe.stockku.ui.employee.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.dhandyjoe.stockku.R
import com.dhandyjoe.stockku.adapter.DetailTransactionAdapter
import com.dhandyjoe.stockku.databinding.ActivityDetailReturBinding
import com.dhandyjoe.stockku.model.Retur
import com.dhandyjoe.stockku.model.SizeStock
import com.dhandyjoe.stockku.model.Transaction
import com.dhandyjoe.stockku.utils.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DetailReturActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailReturBinding
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val firebaseDB = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailReturBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.title = "Detail retur"

        val data = intent.getParcelableExtra<Retur>(EXTRA_RETUR)

        if (data != null) {
            getItemReturList(data.id)
            getItemChangeList(data.id)
        }
    }

    private fun getItemReturList(returId: String) {
        val doc = firebaseDB.collection(COLLECTION_USERS).document(currentUser?.uid ?: "")
            .collection(COLLECTION_RETUR).document(returId)
            .collection(COLLECTION_RETURN_PRODUCT)
        doc.get()
            .addOnSuccessListener {
                val returnList = ArrayList<SizeStock>()

                for(docItem in it) {
                    returnList.add(docItem.toObject(SizeStock::class.java))
                }

                val data = DetailTransactionAdapter(returnList, this)
                showRecycleViewReturn(data)
            }
    }


    private fun showRecycleViewReturn(adapter: DetailTransactionAdapter) {
        binding.rvDetailRetur.layoutManager = LinearLayoutManager(this)
        binding.rvDetailRetur.adapter = adapter
    }

    private fun getItemChangeList(returId: String) {
        val doc = firebaseDB.collection(COLLECTION_USERS).document(currentUser?.uid ?: "")
            .collection(COLLECTION_RETUR).document(returId)
            .collection(COLLECTION_CHANGE_PRODUCT)
        doc.get()
            .addOnSuccessListener {
                val changeList = ArrayList<SizeStock>()

                for(docItem in it) {
                    changeList.add(docItem.toObject(SizeStock::class.java))
                }

                val data = DetailTransactionAdapter(changeList, this)
                showRecycleViewChange(data)
            }
    }


    private fun showRecycleViewChange(adapter: DetailTransactionAdapter) {
        binding.rvDetailChange.layoutManager = LinearLayoutManager(this)
        binding.rvDetailChange.adapter = adapter
    }

    companion object {
        const val EXTRA_RETUR = "retur"
    }
}