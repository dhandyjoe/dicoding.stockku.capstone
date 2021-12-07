package com.dhandyjoe.stockku.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.dhandyjoe.stockku.adapter.StockAdapter
import com.dhandyjoe.stockku.databinding.ActivityMainBinding
import com.dhandyjoe.stockku.model.Item
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val firebaseDB = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarMain.title = "Stock item"

        binding.fabAddItem.setOnClickListener {
            val intent = Intent(this, AddItemActivity::class.java)
            startActivity(intent);
        }

        binding.favAddTransaction.setOnClickListener {
            val intent = Intent(this, TransactionActivity::class.java)
            startActivity(intent);
        }

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
                binding.tvStatusNoData.visibility = View.GONE
                binding.rvListItem.layoutManager = LinearLayoutManager(this)
                val data = StockAdapter(user)
                binding.rvListItem.adapter = data
                binding.rvListItem.visibility = View.VISIBLE

                data.setOnItemClickCallback(object : StockAdapter.OnItemClickCallback{
                    override fun onItemClicked(data: Item) {
                        val intent = Intent(this@MainActivity, EditItemActivity::class.java)
                        intent.putExtra(EditItemActivity.EXTRA_BARANG, data)
                        startActivity(intent)
                    }
                })
            } else {
                binding.tvStatusNoData.visibility = View.VISIBLE
                binding.rvListItem.visibility = View.GONE
            }
        }
    }
}