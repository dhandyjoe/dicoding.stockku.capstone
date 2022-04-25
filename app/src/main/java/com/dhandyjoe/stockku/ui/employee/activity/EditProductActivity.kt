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
import com.dhandyjoe.stockku.model.ColorProduct
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
    private var currentColor = Category()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val product = intent.getParcelableExtra<Product>(EXTRA_BARANG)
        val category = intent.getParcelableExtra<Category>(EXTRA_ITEM_iop)
        val itemCategory = intent.getParcelableExtra<Category>(EXTRA_ITEM_jkl)

        binding.toolbar.title = product?.name

        binding.tvAddColorProcut.setOnClickListener {
            dialogAddColorProduct(
                category!!,
                itemCategory!!,
                product!!
            )
        }

        binding.tvAddSizeStockProduct.setOnClickListener {
//            Toast.makeText(this, currentColorId, Toast.LENGTH_SHORT).show()
            dialogAddSizeStockProduct(
                category!!,
                itemCategory!!,
                product!!,
                currentColor
            )
        }

        if (category != null && itemCategory != null) {
            getColorProduct(category, itemCategory, product!!)
        }
    }

    private fun getColorProduct(category: Category, itemCategory: Category, product: Product) {
        firebaseDB.collection(COLLECTION_USERS).document(currentUser?.uid ?: "")
            .collection(COLLECTION_CATEGORY).document(category.id)
            .collection(COLLECTION_ITEM_CATEGORY).document(itemCategory.id)
            .collection(COLLECTION_PRODUCT).document(product.id)
            .collection(COLLECTION_COLOR_PRODUCT)
            .addSnapshotListener { snapshot, _ ->
                val colorProductList = ArrayList<Category>()

                for(docItem in snapshot!!) {
                    colorProductList.add(docItem.toObject(Category::class.java))
                }

                if (colorProductList.size > 0) {
                    binding.tvChooseColor.setText("Pilih warna")
                } else {
                    binding.tvChooseColor.setText("Tambah warna")
                }

                showColorProduct(colorProductList, category, itemCategory, product)
            }

    }


    private fun showColorProduct(
        data: ArrayList<Category>,
        category: Category,
        itemCategory: Category,
        product: Product
    ) {
        binding.rvColorProduct.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val data = CategoryAdapter(data)
        binding.rvColorProduct.adapter = data

        data.setOnItemClickCallback(object : CategoryAdapter.OnItemClickCallback{
            override fun onItemClicked(data: Category) {
                binding.llShowSizeStock.visibility = View.VISIBLE
                binding.tvChooseColor.visibility = View.GONE
                currentColor = Category(data.id, data.name)

                getSizeStockProduct(category, itemCategory, product, data.id)
            }
        })
    }

    private fun getSizeStockProduct(category: Category, itemCategory: Category, product: Product, colorId: String) {
        firebaseDB.collection(COLLECTION_USERS).document(currentUser?.uid ?: "")
            .collection(COLLECTION_CATEGORY).document(category.id)
            .collection(COLLECTION_ITEM_CATEGORY).document(itemCategory.id)
            .collection(COLLECTION_PRODUCT).document(product.id)
            .collection(COLLECTION_COLOR_PRODUCT).document(colorId)
            .collection(COLLECTION_SIZE_STOCK_PRODUCT)
            .orderBy("size", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, _ ->
                val sizeStockProductList = ArrayList<SizeStock>()

                for(docItem in snapshot!!) {
                    sizeStockProductList.add(docItem.toObject(SizeStock::class.java))
                }

                showSizeStockProduct(sizeStockProductList, category, itemCategory, product)
            }
    }

    private fun showSizeStockProduct(
        data: ArrayList<SizeStock>,
        category: Category,
        itemCategory: Category,
        product: Product
    ) {
        binding.rvSizeProdut.layoutManager = LinearLayoutManager(this)
        val data = SizeStockAdapter(data)
        binding.rvSizeProdut.adapter = data

        data.setOnItemClickCallback(object : SizeStockAdapter.OnItemClickCallback{
            override fun onItemClicked(data: SizeStock) {
//                Toast.makeText(thisContext, data.id, Toast.LENGTH_SHORT).show()
                dialogEditSizeStockProduct(
                    category,
                    itemCategory,
                    product,
                    currentColor,
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

    private fun dialogAddColorProduct(category: Category, itemCategory: Category, product: Product) {
        val cartDialog =  layoutInflater.inflate(R.layout.dialog_add_color, null)
        val dialog = Dialog(this, R.style.CustomDialog)
        dialog.apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(cartDialog)
            setTitle("Tambah warna produk")
        }
        cartDialog.findViewById<Button>(R.id.btn_addColorProduct).setOnClickListener {
            if (cartDialog.findViewById<EditText>(R.id.et_inputColorProdut).text.isNullOrEmpty()) {
                cartDialog.findViewById<EditText>(R.id.et_inputColorProdut).error = "Masukan warna terlebih dahulu!"
            } else {
                database.addColorProduct(
                    currentUser?.uid ?: "",
                    category.id,
                    itemCategory.id,
                    product.id,
                    cartDialog.findViewById<EditText>(R.id.et_inputColorProdut).text.toString()
                )
                dialog.cancel()
            }
        }

        dialog.show()
    }

    private fun dialogAddSizeStockProduct(category: Category, itemCategory: Category, product: Product, color: Category) {
        val cartDialog =  layoutInflater.inflate(R.layout.dialog_add_size_stock, null)
        val dialog = Dialog(this, R.style.CustomDialog)
        dialog.apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(cartDialog)
            setTitle("Tambah size stock produk")
        }

        cartDialog.findViewById<Button>(R.id.btn_addSizeStockProduct).setOnClickListener {
            if (cartDialog.findViewById<EditText>(R.id.et_inputSize).text.isNullOrEmpty()) {
                cartDialog.findViewById<EditText>(R.id.et_inputSize).error = "Masukan ukuran terlebih dahulu!"
            } else if (cartDialog.findViewById<EditText>(R.id.et_inputPrice).text.isNullOrEmpty()) {
                cartDialog.findViewById<EditText>(R.id.et_inputPrice).error = "Masukan harga terlebih dahulu!"
            } else if (cartDialog.findViewById<EditText>(R.id.et_inputStock).text.isNullOrEmpty()) {
                cartDialog.findViewById<EditText>(R.id.et_inputStock).error = "Masukan stock terlebih dahulu!"
            } else {
                database.addSizeStockProduct(
                    currentUser?.uid ?: "",
                    category.id,
                    itemCategory.id,
                    product.id,
                    color.id,
                    SizeStock(
                        "",
                        "",
                        "",
                        Category(category.id, category.name),
                        Category(itemCategory.id, itemCategory.name),
                        Product(product.id, product.name, product.imageUrl),
                        Category(color.id, color.name),
                        cartDialog.findViewById<EditText>(R.id.et_inputSize).text.toString(),
                        cartDialog.findViewById<EditText>(R.id.et_inputPrice).text.toString().toInt(),
                        cartDialog.findViewById<EditText>(R.id.et_inputStock).text.toString().toInt(),
                        product.imageUrl,
                        0,
                        0
                    )
                )
                dialog.cancel()
            }
        }

        dialog.show()
    }

    private fun dialogEditSizeStockProduct(
        category: Category,
        itemCategory: Category,
        product: Product,
        color: Category,
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
                category.id,
                itemCategory.id,
                product.id,
                color.id,
                SizeStock(
                    sizeStock.id,
                    "",
                    "",
                    Category(category.id, category.name),
                    Category(itemCategory.id, itemCategory.name),
                    Product(product.id, product.name, product.imageUrl),
                    Category(color.id, color.name),
                    cartDialog.findViewById<EditText>(R.id.et_inputSize).text.toString(),
                    cartDialog.findViewById<EditText>(R.id.et_inputPrice).text.toString().toInt(),
                    cartDialog.findViewById<EditText>(R.id.et_inputStock).text.toString().toInt(),
                    product.imageUrl,
                    0,
                    0
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