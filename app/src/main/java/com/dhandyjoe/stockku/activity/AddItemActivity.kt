package com.dhandyjoe.stockku.activity

import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dhandyjoe.stockku.databinding.ActivityAddItemBinding
import com.dhandyjoe.stockku.util.STORAGE_IMAGES
import com.dhandyjoe.stockku.util.Database
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class AddItemActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddItemBinding
    private val firebaseStorage = FirebaseStorage.getInstance().reference
    private val database = Database()
    private var imageUrl: String = ""

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        storeImage(uri)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.title = "Tambah barang"

        binding.ivAddImageItem.setOnClickListener {
            resultLauncher.launch("image/*")
        }

        binding.btnSave.setOnClickListener {
            checkValidationData()

        }
    }

    private fun userImage(context: Context, uri: String, imageView: ImageView) {
        val option = RequestOptions().placeholder(progresDrawable(context))
        Glide.with(context)
            .load(uri)
            .apply(option)
            .into(imageView)
    }

    private fun progresDrawable(context: Context): CircularProgressDrawable {
        return CircularProgressDrawable(context).apply {
            strokeWidth = 5f
            centerRadius = 30f
            start()
        }
    }

    private fun storeImage(imageUri: Uri?) {
        if (imageUri != null) {
            Toast.makeText(this, "Uploading...", Toast.LENGTH_SHORT).show()
            val filePath = firebaseStorage.child(STORAGE_IMAGES).child("${UUID.randomUUID()}.jpg")

            filePath.putFile(imageUri)
                .addOnSuccessListener {
                    filePath.downloadUrl
                        .addOnSuccessListener { taskSnapshot ->
                            val url = taskSnapshot.toString()
                            imageUrl = url
                            userImage(this, imageUrl, binding.ivAddImageItem)
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Image Upload failed. Please try again later.", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Image Upload failed. Please try again later.", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun checkValidationData() {
        if (binding.etNameItem.text.isNullOrEmpty()) {
            binding.etNameItem.error = "Harus diisi"
        } else if (binding.etSizeItem.text.isNullOrEmpty()) {
            binding.etSizeItem.error = "Harus diisi"
        } else if (binding.etStockItem.text.isNullOrEmpty()) {
            binding.etStockItem.error = "Harus diisi"
        } else if (binding.etPriceItem.text.isNullOrEmpty()) {
            binding.etPriceItem.error = "Harus diisi"
        } else {
            val nameItem = binding.etNameItem.text.toString()
            val sizeItem = binding.etSizeItem.text.toString()
            val priceItem = binding.etPriceItem.text.toString()
            val stockItem = binding.etStockItem.text.toString()

            database.addItem(nameItem, sizeItem, priceItem, imageUrl, stockItem.toInt())
            finish()
        }
    }
}