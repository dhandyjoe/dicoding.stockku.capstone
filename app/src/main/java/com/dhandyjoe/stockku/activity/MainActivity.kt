package com.dhandyjoe.stockku.activity

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.dhandyjoe.stockku.adapter.StockAdapter
import com.dhandyjoe.stockku.databinding.ActivityMainBinding
import com.dhandyjoe.stockku.model.Item
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val userUid = FirebaseAuth.getInstance().currentUser?.uid
    private val firebaseDB = FirebaseFirestore.getInstance()
    private val listItemSearch = ArrayList<Item>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (userUid == "b2Nqs4mPXJUqimRmpSpFAiw0GXG3") {
            binding.fabAddItem.visibility = View.GONE
        }

        binding.toolbarMain.title = "Stock item"

        binding.fabAddItem.setOnClickListener {
            val intent = Intent(this, AddItemActivity::class.java)
            startActivity(intent);
        }

        binding.fabLogout.setOnClickListener {
            showAlertLogout()
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
                showRecycleView(user)
            } else {
                binding.tvStatusNoData.visibility = View.VISIBLE
                binding.rvListItem.visibility = View.GONE
            }

            searchItem(user)

        }
    }

    private fun showAlertLogout() {
        val alert = AlertDialog.Builder(this)
        alert.setTitle("Logout")
        alert.setMessage("Apakah anda yakin ingin keluar?")
        alert.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
            auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        })

        alert.setNegativeButton("No") { dialog, which ->

        }
        alert.show()
    }

    private fun showRecycleView(data: ArrayList<Item>) {
        binding.tvStatusNoData.visibility = View.GONE
        binding.rvListItem.layoutManager = LinearLayoutManager(this)
        val data = StockAdapter(data)
        binding.rvListItem.adapter = data
        binding.rvListItem.visibility = View.VISIBLE

        data.setOnItemClickCallback(object : StockAdapter.OnItemClickCallback{
            override fun onItemClicked(data: Item) {
                val intent = Intent(this@MainActivity, EditItemActivity::class.java)
                intent.putExtra(EditItemActivity.EXTRA_BARANG, data)
                startActivity(intent)
            }
        })
    }

    private fun searchItem(data: ArrayList<Item>) {
        binding.svItem.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                listItemSearch.clear()
                data.forEach {
                    if (it.name.toLowerCase().contains(query!!.toLowerCase())) {
                        listItemSearch.add(it)
                        showRecycleView(listItemSearch)
                    }
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                listItemSearch.clear()
                data.forEach {
                    if (it.name.toLowerCase().contains(newText!!.toLowerCase())) {
                        listItemSearch!!.add(it)
                        showRecycleView(listItemSearch)
                    }
                }
                return false
            }
        })
    }
}