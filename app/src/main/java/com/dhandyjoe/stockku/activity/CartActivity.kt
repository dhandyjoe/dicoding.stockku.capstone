package com.dhandyjoe.stockku.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dhandyjoe.stockku.adapter.CartAdapter
import com.dhandyjoe.stockku.adapter.StockAdapter
import com.dhandyjoe.stockku.databinding.ActivityCartBinding
import com.dhandyjoe.stockku.model.Item
import com.dhandyjoe.stockku.ui.CartViewModel

class CartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCartBinding
    private var listItemCart = ArrayList<Item>()
    private val viewModel: CartViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.title = "Keranjang"

        val dataItem = viewModel.data
        binding.statusItem.text = dataItem.size.toString()

        val itemList = intent.getParcelableExtra<Item>(EXTRA_ITEM)

        if (itemList != null) {
            binding.statusItem.visibility = View.GONE
            listItemCart.add(itemList)
            binding.rvListItemCart.visibility = View.VISIBLE
        } else {
            binding.statusItem.visibility = View.VISIBLE
            binding.rvListItemCart.visibility = View.GONE
        }

        binding.favAddItemTransaction.setOnClickListener {
            val intent = Intent(this, AddItemTransactionActivity::class.java)
            startActivity(intent)
        }

        showRecycleView(listItemCart)
    }

    private fun showRecycleView(data: ArrayList<Item>) {
        binding.rvListItemCart.layoutManager = LinearLayoutManager(this)
        val data = CartAdapter(data)
        binding.rvListItemCart.adapter = data
    }

    companion object {
        const val EXTRA_ITEM = "extra_item"
    }
}