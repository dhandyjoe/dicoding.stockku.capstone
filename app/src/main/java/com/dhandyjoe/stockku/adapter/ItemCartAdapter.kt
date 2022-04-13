package com.dhandyjoe.stockku.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.dhandyjoe.stockku.databinding.ItemListCartBinding
import com.dhandyjoe.stockku.model.Product
import com.dhandyjoe.stockku.utils.COLLECTION_CART
import com.dhandyjoe.stockku.utils.COLLECTION_USERS
import com.dhandyjoe.stockku.utils.Database
import com.dhandyjoe.stockku.utils.idrFormat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ItemCartAdapter(private val data: ArrayList<Product>, val context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
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
            holder.binding.tvSizeItem.text = model.size
            holder.binding.tvPriceItem.text = idrFormat(model.price)
            holder.binding.tvStockItem.text = model.stock.toString()

            holder.itemView.setOnClickListener {
                onItemClickCallback.onItemClicked(model)

//                if (model.stock == 0) {
//                    Toast.makeText(context, "Stock habis", Toast.LENGTH_SHORT).show()
//                } else if (statusIndicator == 0) {
//                    Toast.makeText(context, "Tentukan jumlah barang sebelum masuk ke keranjang", Toast.LENGTH_SHORT).show()
//                } else {
//                    onItemClickCallback.onItemClicked(data[holder.adapterPosition])
//                }
            }

            holder.binding.btnDirectCart.setOnClickListener {
                var newItem = true
                firebaseDB.collection(COLLECTION_USERS).document(currentUser?.uid ?: "")
                    .collection(COLLECTION_CART).get()
                    .addOnSuccessListener {
                        val docs = ArrayList<Product>()
                        for (document in it) {
                            docs.add(document.toObject(Product::class.java))
                        }

                        for (doc in docs) {
                            if (model.id == doc.id) {
                                newItem = false
                                database.updateItemCart(currentUser?.uid ?: "", doc, 1)
                                Log.d("update", "update")
                                break
                            } else {
                                newItem = true
                            }
                        }

                        if (newItem) {
                            database.addItemCart(currentUser?.uid ?: "", model, 1)
                            Log.d("update", "add")

                            // seek count item cart
//                            menuItemCount.title = docs.size.toString()
                        }
                    }

                Toast.makeText(context, "Berhasil memasukkan produk ke keranjang", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int = data.size

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: Product)
    }

}