package com.dhandyjoe.stockku.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.dhandyjoe.stockku.databinding.ActivityAddItemBinding
import com.dhandyjoe.stockku.model.Item
import com.dhandyjoe.stockku.util.Database
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.type.DateTime

class AddItemActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddItemBinding
    private val firebaseDB = FirebaseFirestore.getInstance()
    private val database = Database()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSave.setOnClickListener { saveItem() }
    }

    fun saveItem() {
        val nameItem = binding.etNameItem.text.toString()
        val sizeItem = binding.etSizeItem.text.toString()
        val priceItem = binding.etPriceItem.text.toString()
        val stockItem = binding.etStockItem.text.toString()

        database.addItem(nameItem, sizeItem, priceItem, stockItem.toInt())
    }
}