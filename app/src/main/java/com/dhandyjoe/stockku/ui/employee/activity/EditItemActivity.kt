package com.dhandyjoe.stockku.ui.employee.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.dhandyjoe.stockku.R
import com.dhandyjoe.stockku.databinding.ActivityEditItemBinding
import com.dhandyjoe.stockku.model.Item
import com.dhandyjoe.stockku.utils.Database
import com.dhandyjoe.stockku.utils.idrFormat
import com.google.firebase.auth.FirebaseAuth

class EditItemActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditItemBinding
    private val database = Database()
    private val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.title = "Edit barang"

        val data = intent.getParcelableExtra<Item>(EXTRA_BARANG)


        if (data?.imageUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(R.drawable.empty_image)
                .into(binding.ivAddImageItem)
        } else {
            Glide.with(this)
                .load(data?.imageUrl)
                .into(binding.ivAddImageItem)
        }

        binding.etEditNameItem.setText(data?.name)
        binding.etEditSizeItem.setText(data?.size)
        binding.etEditPriceItem.setText(idrFormat(data!!.price))

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

    private fun updateItem(item: Item) {
        var indicatorAddStock = 0

        val nameItem = binding.etEditNameItem.text.toString()
        val sizeItem = binding.etEditSizeItem.text.toString()
        val priceItem = binding.etEditPriceItem.text.toString()
        val addStockItem = binding.etEditStockItem.text.toString()

        if (addStockItem.isNotEmpty()) {
            indicatorAddStock = addStockItem.toInt()
        }

        database.editItem(currentUser?.uid ?: "", item.id, nameItem, priceItem.toInt(), sizeItem, indicatorAddStock)
    }

    private fun deleteItem(item: Item) {
        AlertDialog.Builder(this)
            .setTitle("Hapus barang")
            .setMessage("Ini akan menghapus barang anda. Apakah anda yakin?")
            .setPositiveButton("Ya") { dialog, which ->
                Toast.makeText(this, "Barang dihapus", Toast.LENGTH_SHORT).show()
                database.deleteItem(currentUser?.uid ?: "", item.id)
                finish()
            }
            .setNegativeButton("Tidak") {dialog, which -> }
            .show()
    }

    private fun formatIdr() {
        binding.etEditPriceItem
    }

    companion object {
        const val EXTRA_BARANG = "extra_barang"
    }
}