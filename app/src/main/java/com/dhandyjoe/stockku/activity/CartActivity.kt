package com.dhandyjoe.stockku.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.dhandyjoe.stockku.adapter.CartAdapter
import com.dhandyjoe.stockku.adapter.StockAdapter
import com.dhandyjoe.stockku.databinding.ActivityCartBinding
import com.dhandyjoe.stockku.model.Item

class CartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCartBinding
    private var listItemCart = ArrayList<Item>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val data = intent.getParcelableExtra<Item>(EXTRA_ITEM)

        if (data != null) {
            binding.statusItem.visibility = View.GONE
            listItemCart.add(data)
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