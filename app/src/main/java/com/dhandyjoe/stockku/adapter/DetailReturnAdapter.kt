package com.dhandyjoe.stockku.adapter

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.dhandyjoe.stockku.databinding.ItemAddReturBinding
import com.dhandyjoe.stockku.model.SizeStock
import com.dhandyjoe.stockku.ui.employee.activity.DetailReturActivity
import com.dhandyjoe.stockku.utils.Database
import com.google.firebase.auth.FirebaseAuth

class DetailReturnAdapter(private val data: ArrayList<SizeStock>, private val context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val database = Database()
    private val currentUser = FirebaseAuth.getInstance().currentUser

    class MyViewHolder(val binding: ItemAddReturBinding): RecyclerView.ViewHolder(binding.root)

    fun isEmpty(): Boolean = data.isEmpty()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(ItemAddReturBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = data[position]

        if (holder is MyViewHolder) {
            holder.binding.tvNameProductRetur.text = "${model.itemCategory.name} - ${model.product.name}"
            holder.binding.tvColorRetur.text = model.color.name
            holder.binding.tvSizeRetur.text = model.size
            holder.binding.tvIndicatorItem.text = model.totalTransaction.toString()

            holder.binding.ivMinusCart.setOnClickListener {
                if (model.totalTransaction > 1) {
                    database.updateReturnDetailRetur(currentUser?.uid ?: "", model, -1)
                } else {
                    showPrintDialog(model)
                }
            }

            (context as DetailReturActivity).liveTotalReturn()

            holder.binding.tvIndicatorItem.text = model.totalTransaction.toString()
            holder.binding.ivPlusCart.setOnClickListener {
                database.updateReturnDetailRetur(currentUser?.uid ?: "", model, 1)
            }

//            holder.itemView.setOnClickListener {
//                onItemClickCallback.onItemClicked(data[holder.adapterPosition])
//            }
        }
    }

    override fun getItemCount(): Int = data.size

    private fun showPrintDialog(model: SizeStock) {
        val alert = AlertDialog.Builder(context)
        alert.setTitle("Retur")
        alert.setMessage("Apakah anda ingin menghapus produk ini dari retur?")
        alert.setPositiveButton("Iya", DialogInterface.OnClickListener { dialog, which ->
            database.deleteReturnDetailRetur(currentUser?.uid ?: "", model)
        })

        alert.setNegativeButton("Tidak") { dialog, which ->

        }
        alert.show()
    }

//    private lateinit var onItemClickCallback: OnItemClickCallback
//
//    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
//        this.onItemClickCallback = onItemClickCallback
//    }
//
//    interface OnItemClickCallback {
//        fun onItemClicked(data: Product)
//    }
}