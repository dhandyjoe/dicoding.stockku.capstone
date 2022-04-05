package com.dhandyjoe.stockku.ui.employee.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.dhandyjoe.stockku.adapter.ItemAdapter
import com.dhandyjoe.stockku.databinding.ActivityProductBinding
import com.dhandyjoe.stockku.model.Category
import com.dhandyjoe.stockku.model.Item
import com.dhandyjoe.stockku.utils.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

const val EXTRA_CATEGORY_ID = "extra_category_id"
const val EXTRA_ITEM_CATEGORY = "extra_item_category"

class ProductActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductBinding
    private val firebaseDB = FirebaseFirestore.getInstance()
    private val listItemSearch = ArrayList<Item>()
    private val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val categoryId = intent.getStringExtra(EXTRA_CATEGORY_ID)
        val itemCategory = intent.getParcelableExtra<Category>(EXTRA_ITEM_CATEGORY)

        binding.toolbar.title = itemCategory?.name

//        getBarangList()

        binding.favAddTransaction.setOnClickListener {
//            val intent = Intent(this, AddItemActivity::class.java)
//            startActivity(intent)

            Toast.makeText(this, categoryId, Toast.LENGTH_SHORT).show()
        }
    }

//    private fun getBarangList() {
//        firebaseDB.collection(COLLECTION_USERS).document(currentUser?.uid ?: "")
//            .collection(COLLECTION_CATEGORY).document(categoryId)
//            .collection(COLLECTION_ITEM_CATEGORY).document(itemCategory.id)
//            .collection(COLLECTION_PRODUCT)
//            .addSnapshotListener { snapshot, _ ->
//                val user = ArrayList<Item>()
//
//                for(docItem in snapshot!!) {
//                    user.add(docItem.toObject(Item::class.java))
//                }
//
//                if (user.size > 0) {
//                    showRecycleView(user)
//                } else {
//                    binding.animationView.visibility = View.VISIBLE
//                    binding.rvListItem.visibility = View.GONE
//                }
//
//                searchItem(user)
//
//            }
//    }

    private fun showRecycleView(data: ArrayList<Item>) {
        binding.animationView.visibility = View.GONE
        binding.rvListItem.layoutManager = GridLayoutManager(this, 2)
        val adapter = ItemAdapter(data, this)
        binding.rvListItem.adapter = adapter
        binding.rvListItem.visibility = View.VISIBLE

        adapter.setOnItemClickCallback(object : ItemAdapter.OnItemClickCallback{
            override fun onItemClicked(data: Item) {
                val intent = Intent(this@ProductActivity, EditItemActivity::class.java)
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
}