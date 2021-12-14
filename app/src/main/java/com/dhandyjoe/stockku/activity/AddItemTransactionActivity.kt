package com.dhandyjoe.stockku.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.dhandyjoe.stockku.adapter.TransactionAdapter
import com.dhandyjoe.stockku.databinding.ActivityAddItemTransactionBinding
import com.dhandyjoe.stockku.model.Item
import com.google.firebase.firestore.FirebaseFirestore

class AddItemTransactionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddItemTransactionBinding
    private val firebaseDB = FirebaseFirestore.getInstance()
    private val listItemSearch = ArrayList<Item>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddItemTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarMain.title = "Pilih item"

        getBarangList()

    }

    fun getBarangList() {
        var doc = firebaseDB.collection("barang")
        doc.addSnapshotListener { snapshot, _ ->
            val user = ArrayList<Item>()

            for(docItem in snapshot!!) {
                user.add(docItem.toObject(Item::class.java))
            }

            if (user.size > 0) {
                showRecycleView(user)
            } else {
                binding.tvStatusNoData.visibility = View.VISIBLE
                binding.rvTransactionItem.visibility = View.GONE
            }

            searchItem(user)
        }
    }

    private fun showRecycleView(data: ArrayList<Item>) {
        binding.tvStatusNoData.visibility = View.GONE
        binding.rvTransactionItem.layoutManager = LinearLayoutManager(this)
        val data = TransactionAdapter(data)
        binding.rvTransactionItem.adapter = data
        binding.rvTransactionItem.visibility = View.VISIBLE

        data.setOnItemClickCallback(object : TransactionAdapter.OnItemClickCallback{
            override fun onItemClicked(data: Item) {
                val intent = Intent(this@AddItemTransactionActivity, CartActivity::class.java)
                intent.putExtra(CartActivity.EXTRA_ITEM, data)
                startActivity(intent)
            }
        })
    }

    private fun searchItem(data: ArrayList<Item>) {
        binding.svItem.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                listItemSearch.clear()
                data.forEach {
                    if (it.name.lowercase().contains(query!!.lowercase())) {
                        listItemSearch.add(it)
                        showRecycleView(listItemSearch)
                    }
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                listItemSearch.clear()
                data.forEach {
                    if (it.name.lowercase().contains(newText!!.lowercase())) {
                        listItemSearch!!.add(it)
                        showRecycleView(listItemSearch)
                    }
                }
                return false
            }
        })
    }
}