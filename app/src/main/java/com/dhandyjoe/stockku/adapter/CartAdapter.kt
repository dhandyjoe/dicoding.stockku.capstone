package com.dhandyjoe.stockku.adapter

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.animation.content.Content
import com.bumptech.glide.Glide
import com.dhandyjoe.stockku.databinding.ItemDetailCartBinding
import com.dhandyjoe.stockku.model.Item
import com.dhandyjoe.stockku.ui.activity.PrintActivity
import com.dhandyjoe.stockku.util.Database

class CartAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val database = Database()

    class MyViewHolder(val binding: ItemDetailCartBinding): RecyclerView.ViewHolder(binding.root)
    var listItemCart = ArrayList<Item>()

    fun updateItem(list: ArrayList<Item>) {
//        var newItem = false
//        for (oldItem in listItemCart) {
//            if (oldItem.id == list[0].id) {
//                oldItem.totalTransaction += list[0].totalTransaction
//                newItem = false
//                break
//            } else {
//                newItem = true
//            }
//        }
//
//        val diffCallback = ItemDiffCallback(listItemCart, list)
//        val diffResult = DiffUtil.calculateDiff(diffCallback)
//        if (newItem || listItemCart.isEmpty()){
//            listItemCart.add(list[0])
//        }
//        diffResult.dispatchUpdatesTo(this)

        listItemCart.addAll(list)
    }

    fun isEmpty(): Boolean = listItemCart.isEmpty()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(ItemDetailCartBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = listItemCart[position]

        if (holder is MyViewHolder) {
            holder.binding.tvNameItem.text = model.name
            holder.binding.tvSizeItem.text = model.size
            holder.binding.tvPriceItem.text = model.price
            Glide.with(context)
                .load(model.imageUrl)
                .into(holder.binding.ivCart)

            holder.binding.ivMinusCart.setOnClickListener {
                if (model.totalTransaction > 0) {
                    database.updateItemCart(model, -1)
                } else {
                    showPrintDialog(model)
                }
            }
            holder.binding.tvIndicatorItem.text = model.totalTransaction.toString()
            holder.binding.ivPlusCart.setOnClickListener {
                database.updateItemCart(model, 1)
            }
        }
    }

    override fun getItemCount(): Int = listItemCart.size

    private fun showPrintDialog(model: Item) {
        val alert = AlertDialog.Builder(context)
        alert.setTitle("Keranjang")
        alert.setMessage("Apakah anda ingin menghapus barang ini dari keranjang?")
        alert.setPositiveButton("Iya", DialogInterface.OnClickListener { dialog, which ->
            database.deleteItemCart(model)
        })

        alert.setNegativeButton("Tidak") { dialog, which ->

        }
        alert.show()
    }
}

//class ItemDiffCallback(oldEmployeeList: List<Item>, newEmployeeList: List<Item>) : DiffUtil.Callback() {
//    private val mOldItemList: List<Item> = oldEmployeeList
//    private val mNewItemList: List<Item> = newEmployeeList
//
//    override fun getOldListSize(): Int {
//        return mOldItemList.size
//    }
//
//    override fun getNewListSize(): Int {
//        return mNewItemList.size
//    }
//
//    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
//        return mOldItemList[oldItemPosition].id === mNewItemList[newItemPosition].id
//    }
//
//    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
//        val oldEmployee: Item = mOldItemList[oldItemPosition]
//        val newEmployee: Item = mNewItemList[newItemPosition]
//        return oldEmployee.totalTransaction == newEmployee.totalTransaction
//    }
//
//    @Nullable
//    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
//        return super.getChangePayload(oldItemPosition, newItemPosition)
//    }
//}