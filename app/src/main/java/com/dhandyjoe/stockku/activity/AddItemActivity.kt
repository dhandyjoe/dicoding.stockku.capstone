package com.dhandyjoe.stockku.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.dhandyjoe.stockku.databinding.ActivityAddItemBinding
import com.dhandyjoe.stockku.model.Item
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.type.DateTime

class AddItemActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddItemBinding
    private val firebaseDB = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSave.setOnClickListener { saveItem() }
    }

    fun saveItem() {
        var docBarang = firebaseDB.collection("barang").document()

        val nameItem = binding.etNameItem.text.toString()
        val sizeItem = binding.etSizeItem.text.toString()
        val priceItem = binding.etPriceItem.text.toString()
        val stockItem = binding.etStockItem.text.toString()

        val item = Item(docBarang.id, nameItem, sizeItem, priceItem, Integer.parseInt(stockItem))

        docBarang.set(item)
            .addOnSuccessListener {
                Toast.makeText(this, "Sukses", Toast.LENGTH_SHORT).show()
                onBackPressed()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal", Toast.LENGTH_SHORT).show()
            }
    }
}