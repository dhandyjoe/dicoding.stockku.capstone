package com.dhandyjoe.stockku.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.dhandyjoe.stockku.databinding.ActivityEditItemBinding
import com.dhandyjoe.stockku.model.Item
import com.google.firebase.firestore.FirebaseFirestore

class EditItemActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditItemBinding
    private var firebaseDB = FirebaseFirestore.getInstance()

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

        val map = HashMap<String, Any>()
        map["name"] = nameItem
        map["size"] = sizeItem
        map["price"] = priceItem

        firebaseDB.collection("barang")
            .document(item.id)
            .update(map)
            .addOnSuccessListener {
                Toast.makeText(this, "Update Succesful", Toast.LENGTH_SHORT).show()
                onBackPressed()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Update Failed", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        const val EXTRA_BARANG = "extra_barang"
    }
}