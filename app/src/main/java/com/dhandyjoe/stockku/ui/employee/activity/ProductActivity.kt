package com.dhandyjoe.stockku.ui.employee.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import com.dhandyjoe.stockku.adapter.ProductAdapter
import com.dhandyjoe.stockku.databinding.ActivityProductBinding
import com.dhandyjoe.stockku.model.Category
import com.dhandyjoe.stockku.model.Product
import com.dhandyjoe.stockku.utils.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

const val EXTRA_CATEGORY = "extra_category"
const val EXTRA_ITEM_CATEGORY = "extra_item_category"

class ProductActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductBinding
    private val firebaseDB = FirebaseFirestore.getInstance()
    private val listItemSearch = ArrayList<Product>()
    private val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val category = intent.getParcelableExtra<Category>(EXTRA_CATEGORY)
        val itemCategory = intent.getParcelableExtra<Category>(EXTRA_ITEM_CATEGORY)

        binding.toolbar.title = itemCategory?.name

        if (category != null && itemCategory != null) {
            getBarangList(category, itemCategory)
        }

        binding.favAddTransaction.setOnClickListener {
            val intent = Intent(this, AddItemActivity::class.java)
            intent.putExtra(EXTRA_ITEM_qwe, category)
            intent.putExtra(EXTRA_ITEM_asd, itemCategory)
            startActivity(intent)
        }
    }

    private fun getBarangList(category: Category, itemCategory: Category) {
        firebaseDB.collection(COLLECTION_USERS).document(currentUser?.uid ?: "")
            .collection(COLLECTION_CATEGORY).document(category.id)
            .collection(COLLECTION_ITEM_CATEGORY).document(itemCategory.id)
            .collection(COLLECTION_PRODUCT)
            .addSnapshotListener { snapshot, _ ->
                val user = ArrayList<Product>()

                for(docItem in snapshot!!) {
                    user.add(docItem.toObject(Product::class.java))
                }

                if (user.size > 0) {
                    showRecycleView(user, category, itemCategory)
                } else {
                    binding.animationView.visibility = View.VISIBLE
                    binding.rvListItem.visibility = View.GONE
                }

                searchItem(user, category, itemCategory)

            }
    }

    private fun showRecycleView(data: ArrayList<Product>, category: Category, itemCategory: Category) {
        binding.animationView.visibility = View.GONE
        binding.rvListItem.layoutManager = GridLayoutManager(this, 2)
        val adapter = ProductAdapter(data, this)
        binding.rvListItem.adapter = adapter
        binding.rvListItem.visibility = View.VISIBLE

        adapter.setOnItemClickCallback(object : ProductAdapter.OnItemClickCallback{
            override fun onItemClicked(data: Product) {
                val intent = Intent(this@ProductActivity, EditProductActivity::class.java)
                intent.putExtra(EditProductActivity.EXTRA_BARANG, data)
                intent.putExtra(EditProductActivity.EXTRA_ITEM_iop, category)
                intent.putExtra(EditProductActivity.EXTRA_ITEM_jkl, itemCategory)
                startActivity(intent)
            }
        })
    }

    private fun searchItem(data: ArrayList<Product>, category: Category, itemCategory: Category) {
        binding.svItem.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                listItemSearch.clear()
                data.forEach {
                    if (it.name.lowercase().contains(query!!.lowercase())) {
                        listItemSearch.add(it)
                        showRecycleView(listItemSearch, category, itemCategory)
                    } else if (listItemSearch.isEmpty()) {
                        showRecycleView(listItemSearch, category, itemCategory)
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
                        showRecycleView(listItemSearch, category, itemCategory)
                    } else if (listItemSearch.isEmpty()) {
                        showRecycleView(listItemSearch, category, itemCategory)
                        binding.animationView.visibility = View.VISIBLE
                    }
                }
                return false
            }
        })
    }
}