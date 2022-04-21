package com.dhandyjoe.stockku.adapter

import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dhandyjoe.stockku.R
import com.dhandyjoe.stockku.databinding.ItemListCartBinding
import com.dhandyjoe.stockku.model.Category
import com.dhandyjoe.stockku.model.ColorProduct
import com.dhandyjoe.stockku.model.Product
import com.dhandyjoe.stockku.model.SizeStock
import com.dhandyjoe.stockku.utils.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ListProductTransactionAdapter(
    private val data: ArrayList<Product>,
    val context: Context,
    private val categoryId: String,
    private val itemCategory: String,
): RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    private val firebaseDB = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val database = Database()

    class MyViewHolder(val binding: ItemListCartBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(ItemListCartBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = data[position]

        if (holder is MyViewHolder) {
            holder.binding.tvNameItem.text = model.name
            if (model.imageUrl.isEmpty()) {
                Glide.with(context)
                    .load(R.drawable.empty_image)
                    .into(holder.binding.ivProductTransaction)
            } else {
                Glide.with(context)
                    .load(model.imageUrl)
                    .into(holder.binding.ivProductTransaction)
            }

            holder.binding.btnAddItemCart.setOnClickListener {
                cartDialogAddCategory(model)
            }

//            holder.binding.tvSizeItem.text = model.size
//            holder.binding.tvPriceItem.text = idrFormat(model.price)
//            holder.binding.tvStockItem.text = model.stock.toString()

//            holder.itemView.setOnClickListener {
//                onItemClickCallback.onItemClicked(model)
//
//                if (model.stock == 0) {
//                    Toast.makeText(context, "Stock habis", Toast.LENGTH_SHORT).show()
//                } else if (statusIndicator == 0) {
//                    Toast.makeText(context, "Tentukan jumlah barang sebelum masuk ke keranjang", Toast.LENGTH_SHORT).show()
//                } else {
//                    onItemClickCallback.onItemClicked(data[holder.adapterPosition])
//                }
//            }

//            holder.binding.btnDirectCart.setOnClickListener {
//                var newItem = true
//                firebaseDB.collection(COLLECTION_USERS).document(currentUser?.uid ?: "")
//                    .collection(COLLECTION_CART).get()
//                    .addOnSuccessListener {
//                        val docs = ArrayList<Product>()
//                        for (document in it) {
//                            docs.add(document.toObject(Product::class.java))
//                        }
//
//                        for (doc in docs) {
//                            if (model.id == doc.id) {
//                                newItem = false
//                                database.updateItemCart(currentUser?.uid ?: "", doc, 1)
//                                Log.d("update", "update")
//                                break
//                            } else {
//                                newItem = true
//                            }
//                        }
//
//                        if (newItem) {
//                            database.addItemCart(currentUser?.uid ?: "", model, 1)
//                            Log.d("update", "add")
//
//                            // seek count item cart
////                            menuItemCount.title = docs.size.toString()
//                        }
//                    }
//
//                Toast.makeText(context, "Berhasil memasukkan produk ke keranjang", Toast.LENGTH_SHORT).show()
//            }
        }
    }

    override fun getItemCount(): Int = data.size

    private fun cartDialogAddCategory(product: Product) {
        val cartDialog =  LayoutInflater.from(context).inflate(R.layout.dialog_choose_item_cart, null)
        val dialog = BottomSheetDialog(context)
        dialog.apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(cartDialog)
        }

        if (product.imageUrl.isEmpty()) {
            Glide.with(context)
                .load(R.drawable.empty_image)
                .into(cartDialog.findViewById<ImageView>(R.id.iv_addcart))
        } else {
            Glide.with(context)
                .load(product.imageUrl)
                .centerCrop()
                .into(cartDialog.findViewById<ImageView>(R.id.iv_addcart))
        }

        getColorProduct(cartDialog, categoryId, itemCategory, product.id)

        dialog.show()
    }

    private fun getColorProduct(binding: View, categoryId: String, itemCategory: String, productId: String) {
        firebaseDB.collection(COLLECTION_USERS).document(currentUser?.uid ?: "")
            .collection(COLLECTION_CATEGORY).document(categoryId)
            .collection(COLLECTION_ITEM_CATEGORY).document(itemCategory)
            .collection(COLLECTION_PRODUCT).document(productId)
            .collection(COLLECTION_COLOR_PRODUCT)
            .addSnapshotListener { snapshot, _ ->
                val colorProductList = ArrayList<ColorProduct>()

                for(docItem in snapshot!!) {
                    colorProductList.add(docItem.toObject(ColorProduct::class.java))
                }

                showListColorTransaction(
                    binding,
                    context,
                    colorProductList,
                    categoryId,
                    itemCategory,
                    productId
                )
            }

    }

    private fun showListColorTransaction(
        binding: View,
        context: Context,
        listColor: ArrayList<ColorProduct>,
        categoryId: String,
        itemCategory: String,
        productId: String
    ) {
        binding.findViewById<RecyclerView>(R.id.rv_colorTransaction).layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val data = ColorTransactionAdapter(listColor)
        binding.findViewById<RecyclerView>(R.id.rv_colorTransaction).adapter = data

        data.setOnItemClickCallback(object : ColorTransactionAdapter.OnItemClickCallback{
            override fun onItemClicked(data: ColorProduct) {
//                Toast.makeText(context, data.name, Toast.LENGTH_SHORT).show()

                binding.findViewById<LinearLayout>(R.id.linearLayout4).visibility = View.VISIBLE
                getSizeStockProduct(binding, categoryId, itemCategory, productId, data.id)
            }
        })
    }

    private fun getSizeStockProduct(binding: View, categoryId: String, itemCategory: String, productId: String, colorId: String) {
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

                showSizeStockProduct(binding, sizeStockProductList, categoryId, itemCategory, productId)
            }
    }

    private fun showSizeStockProduct(
        binding: View,
        listSize: ArrayList<SizeStock>,
        categoryId: String,
        itemCategory: String,
        productId: String
    ) {
        var statusIndicator = 1

        binding.findViewById<RecyclerView>(R.id.rv_sizeTransaction).layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val data = SizeStockTransactionAdapter(listSize)
        binding.findViewById<RecyclerView>(R.id.rv_sizeTransaction).adapter = data

        binding.findViewById<ImageView>(R.id.iv_minus).setOnClickListener {
            if (statusIndicator > 0) {
                statusIndicator--
                binding.findViewById<TextView>(R.id.tv_indicatorItemCart).text = statusIndicator.toString()
            }
        }
        binding.findViewById<ImageView>(R.id.iv_plus).setOnClickListener {
            statusIndicator++
            binding.findViewById<TextView>(R.id.tv_indicatorItemCart).text = statusIndicator.toString()
        }

        data.setOnItemClickCallback(object : SizeStockTransactionAdapter.OnItemClickCallback{
            override fun onItemClicked(data: SizeStock) {
//                Toast.makeText(context, data.size, Toast.LENGTH_SHORT).show()
                binding.findViewById<ConstraintLayout>(R.id.linearLayout5).visibility = View.VISIBLE
                binding.findViewById<TextView>(R.id.tv_priceItemTransaction).text = "Rp. ${idrFormat(data.price)}"
                binding.findViewById<TextView>(R.id.tv_stockTransaction).text = "Stok : ${data.stock}"

                var newItem = true

                binding.findViewById<Button>(R.id.btn_cart).setOnClickListener {
                    if (data.stock == 0) {
                        Toast.makeText(context, "Stock habis", Toast.LENGTH_SHORT).show()
                    } else if (statusIndicator == 0) {
                        Toast.makeText(context, "Tentukan jumlah barang sebelum masuk ke keranjang", Toast.LENGTH_SHORT).show()
                    } else {
                        firebaseDB.collection(COLLECTION_USERS).document(currentUser?.uid ?: "")
                            .collection(COLLECTION_CART).get()
                            .addOnSuccessListener {
                                val docs = ArrayList<SizeStock>()
                                for (document in it) {
                                    docs.add(document.toObject(SizeStock::class.java))
                                }

                                for (doc in docs) {
                                    if (data.id == doc.id) {
                                        newItem = false
                                        database.updateItemCart(currentUser?.uid ?: "", doc, statusIndicator)
                                        Toast.makeText(context, "Berhasil menambah stok produk dari keranjang", Toast.LENGTH_SHORT).show()
                                        Log.d("update", "update")
                                        break
                                    } else {
                                        newItem = true
                                    }
                                }

                                if (newItem) {
                                    database.addItemCart(currentUser?.uid ?: "", data, statusIndicator)
                                    Toast.makeText(context, "Berhasil memasukkan produk ke keranjang", Toast.LENGTH_SHORT).show()
                                    Log.d("update", "add")
                                }
                            }
                    }
                }
            }
        })
    }

}