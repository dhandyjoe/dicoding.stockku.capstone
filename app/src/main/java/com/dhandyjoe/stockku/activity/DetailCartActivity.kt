package com.dhandyjoe.stockku.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dhandyjoe.stockku.databinding.ActivityDetailCartBinding

class DetailCartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailCartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.title = "Detail transaksi"
    }
}