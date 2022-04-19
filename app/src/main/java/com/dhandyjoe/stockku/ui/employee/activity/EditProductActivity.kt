package com.dhandyjoe.stockku.ui.employee.activity

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Size
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.dhandyjoe.stockku.R
import com.dhandyjoe.stockku.adapter.CategoryAdapter
import com.dhandyjoe.stockku.adapter.SizeStockAdapter
import com.dhandyjoe.stockku.databinding.ActivityEditItemBinding
import com.dhandyjoe.stockku.model.Category
import com.dhandyjoe.stockku.model.Product
import com.dhandyjoe.stockku.model.SizeStock
import com.dhandyjoe.stockku.utils.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.stream.Stream

class EditProductActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditItemBinding
    private val database = Database()
    private val firebaseDB = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private var currentColorId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val product = intent.getParcelableExtra<Product>(EXTRA_BARANG)
        val categoryId = intent.getStringExtra(EXTRA_ITEM_iop)
        val itemCategory = intent.getStringExtra(EXTRA_ITEM_jkl)

        binding.toolbar.title = product?.name

        binding.tvAddColorProcut.setOnClickListener {
            dialogAddColorProduct(
                categoryId ?: "",
                itemCategory ?: "",
                product?.id ?: ""
            )
        }

        binding.tvAddSizeStockProduct.setOnClickListener {
//            Toast.makeText(this, currentColorId, Toast.LENGTH_SHORT).show()
            dialogAddSizeStockProduct(
                categoryId ?: "",
                itemCategory ?: "",
                product?.id ?: "",
                currentColorId
            )
        }

        getColorProduct(categoryId ?: "", itemCategory ?: "", product?.id ?: "")
    }

    private fun getColorProduct(categoryId: String, itemCategory: String, productId: String) {
        firebaseDB.collection(COLLECTION_USERS).document(currentUser?.uid ?: "")
            .collection(COLLECTION_CATEGORY).document(categoryId)
            .collection(COLLECTION_ITEM_CATEGORY).document(itemCategory)
            .collection(COLLECTION_PRODUCT).document(productId)
            .collection(COLLECTION_COLOR_PRODUCT)
            .addSnapshotListener { snapshot, _ ->
                val colorProductList = ArrayList<Category>()

                for(docItem in snapshot!!) {
                    colorProductList.add(docItem.toObject(Category::class.java))
                }

                showColorProduct(colorProductList, categoryId, itemCategory, productId)
            }

    }


    private fun showColorProduct(
        data: ArrayList<Category>,
        categoryId: String,
        itemCategory: String,
        productId: String
    ) {
        binding.rvColorProduct.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val data = CategoryAdapter(data)
        binding.rvColorProduct.adapter = data

        data.setOnItemClickCallback(object : CategoryAdapter.OnItemClickCallback{
            override fun onItemClicked(data: Category) {
                binding.llShowSizeStock.visibility = View.VISIBLE
                binding.tvChooseColor.visibility = View.GONE
                currentColorId = data.id
                getSizeStockProduct(categoryId, itemCategory, productId, data.id)
            }
        })
    }

    private fun getSizeStockProduct(categoryId: String, itemCategory: String, productId: String, colorId: String) {
        firebaseDB.collection(COLLECTION_USERS).document(currentUser?.uid ?: "")
            .collection(COLLECTION_CATEGORY).document(categoryId)
            .collection(COLLECTION_ITEM_CATEGORY).document(itemCategory)
            .collection(COLLECTION_PRODUCT).document(productId)
            .collection(COLLECTION_COLOR_PRODUCT).document(colorId)
            .collection(COLLECTION_SIZE_STOCK_PRODUCT)
            .orderBy("size", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, _ ->
                val sizeStockProductList = ArrayList<SizeStock>()

                for(docItem in snapshot!!) {
                    sizeStockProductList.add(docItem.toObject(SizeStock::class.java))
                }

                showSizeStockProduct(sizeStockProductList, categoryId, itemCategory, productId)
            }
    }

    private fun showSizeStockProduct(
        data: ArrayList<SizeStock>,
        categoryId: String,
        itemCategory: String,
        productId: String
    ) {
        binding.rvSizeProdut.layoutManager = LinearLayoutManager(this)
        val data = SizeStockAdapter(data)
        binding.rvSizeProdut.adapter = data

        data.setOnItemClickCallback(object : SizeStockAdapter.OnItemClickCallback{
            override fun onItemClicked(data: SizeStock) {
//                Toast.makeText(thisContext, data.id, Toast.LENGTH_SHORT).show()
                dialogEditSizeStockProduct(
                    categoryId,
                    itemCategory,
                    productId,
                    currentColorId,
                    data
                )
            }
        })
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

//    private fun updateItem(item: Product) {
//        var indicatorAddStock = 0
//
//
//        if (addStockItem.isNotEmpty()) {
//            indicatorAddStock = addStockItem.toInt()
//        }
//
//        database.editItem(currentUser?.uid ?: "", item.id, nameItem, priceItem.toInt(), sizeItem, indicatorAddStock)
//    }

    private fun dialogAddColorProduct(categoryId: String, itemCategory: String, productId: String) {
        val cartDialog =  layoutInflater.inflate(R.layout.dialog_add_color, null)
        val dialog = Dialog(this, R.style.CustomDialog)
        dialog.apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(cartDialog)
            setTitle("Tambah warna produk")
        }
        cartDialog.findViewById<Button>(R.id.btn_addColorProduct).setOnClickListener {
            database.addColorProduct(
                currentUser?.uid ?: "",
                categoryId,
                itemCategory,
                productId,
                cartDialog.findViewById<EditText>(R.id.et_inputColorProdut).text.toString()
            )
            dialog.cancel()
//            categoryList.clear()
        }

        dialog.show()
    }

    private fun dialogAddSizeStockProduct(categoryId: String, itemCategory: String, productId: String, colorId: String) {
        val cartDialog =  layoutInflater.inflate(R.layout.dialog_add_size_stock, null)
        val dialog = Dialog(this, R.style.CustomDialog)
        dialog.apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(cartDialog)
            setTitle("Tambah size stock produk")
        }

        cartDialog.findViewById<Button>(R.id.btn_addSizeStockProduct).setOnClickListener {
            database.addSizeStockProduct(
                currentUser?.uid ?: "",
                categoryId,
                itemCategory,
                productId,
                colorId,
                SizeStock(
                    "",
                    cartDialog.findViewById<EditText>(R.id.et_inputSize).text.toString(),
                    cartDialog.findViewById<EditText>(R.id.et_inputPrice).text.toString().toInt(),
                    cartDialog.findViewById<EditText>(R.id.et_inputStock).text.toString().toInt(),
                )
            )
            dialog.cancel()
//            categoryList.clear()
        }

        dialog.show()
    }

    private fun dialogEditSizeStockProduct(
        categoryId: String,
        itemCategory: String,
        productId: String,
        colorId: String,
        sizeStock: SizeStock
    ) {
        val cartDialog =  layoutInflater.inflate(R.layout.dialog_add_size_stock, null)
        val dialog = Dialog(this, R.style.CustomDialog)
        dialog.apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(cartDialog)
            setTitle("Edit size stock produk")
        }

        cartDialog.findViewById<EditText>(R.id.et_inputSize).setText(sizeStock.size)
        cartDialog.findViewById<EditText>(R.id.et_inputPrice).setText(sizeStock.price.toString())
        cartDialog.findViewById<EditText>(R.id.et_inputStock).setText(sizeStock.stock.toString())

        cartDialog.findViewById<Button>(R.id.btn_addSizeStockProduct).setOnClickListener {
            database.editSizeStockProduct(
                currentUser?.uid ?: "",
                categoryId,
                itemCategory,
                productId,
                colorId,
                SizeStock(
                    sizeStock.id,
                    cartDialog.findViewById<EditText>(R.id.et_inputSize).text.toString(),
                    cartDialog.findViewById<EditText>(R.id.et_inputPrice).text.toString().toInt(),
                    cartDialog.findViewById<EditText>(R.id.et_inputStock).text.toString().toInt(),
                )
            )
            dialog.cancel()
        }

        dialog.show()
    }

    private fun deleteItem(item: Product) {
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

    companion object {
        const val EXTRA_BARANG = "extra_barang"
        const val EXTRA_ITEM_iop = "extra_item_iop"
        const val EXTRA_ITEM_jkl = "extra_item_jkl"
    }
}