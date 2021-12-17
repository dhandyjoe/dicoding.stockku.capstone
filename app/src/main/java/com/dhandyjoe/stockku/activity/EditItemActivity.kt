package com.dhandyjoe.stockku.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.dhandyjoe.stockku.databinding.ActivityEditItemBinding
import com.dhandyjoe.stockku.model.Item
import com.dhandyjoe.stockku.util.Database
import com.google.firebase.firestore.FirebaseFirestore

class EditItemActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditItemBinding
    private var firebaseDB = FirebaseFirestore.getInstance()
    private val database = Database()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val data = intent.getParcelableExtra<Item>(EXTRA_BARANG)

        binding.etEditNameItem.setText(data?.name)
        binding.etEditSizeItem.setText(data?.size)
        binding.etEditPriceItem.setText(data?.price)
        binding.btnUpdate.setOnClickListener {
            updateItem(data!!)
            finish()
        }

        binding.btnDelete.setOnClickListener {
            deleteItem(data!!)
        }
    }

//    private fun userInfo(item: Item) {
//        firebaseDB.collection("barang")
//            .document(item.id)
//            .get()
//            .addOnSuccessListener {
//                val user = it.toObject(User::class.java)
//                imageUrl = user?.imageUrl
//                binding.etNameProfile.setText(user?.name)
//                binding.etEmailProfile.setText(user?.email)
//                binding.etPhoneProfile.setText(user?.phone)
//                if (imageUrl != null) {
//                    userImage(this, user?.imageUrl!!, binding.ivPhotoProfile)
//                }
//            }
//    }

    fun updateItem(item: Item) {
        val nameItem = binding.etEditNameItem.text.toString()
        val sizeItem = binding.etEditSizeItem.text.toString()
        val priceItem = binding.etEditPriceItem.text.toString()

        database.editItem(item.id, nameItem, sizeItem, priceItem)
    }

    fun deleteItem(item: Item) {
        AlertDialog.Builder(this)
            .setTitle("Hapus barang")
            .setMessage("Ini akan menghapus barang anda. Apakah anda yakin?")
            .setPositiveButton("Ya") { dialog, which ->
                Toast.makeText(this, "Barang dihapus", Toast.LENGTH_SHORT).show()
                database.deleteItem(item.id)
                finish()
            }
            .setNegativeButton("Tidak") {dialog, which -> }
            .show()
    }

    companion object {
        const val EXTRA_BARANG = "extra_barang"
    }
}