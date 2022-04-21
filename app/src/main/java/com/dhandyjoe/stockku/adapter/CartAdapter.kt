package com.dhandyjoe.stockku.adapter

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dhandyjoe.stockku.R
import com.dhandyjoe.stockku.databinding.ItemDetailCartBinding
import com.dhandyjoe.stockku.model.Product
import com.dhandyjoe.stockku.model.SizeStock
import com.dhandyjoe.stockku.ui.employee.activity.CartActivity
import com.dhandyjoe.stockku.utils.Database
import com.dhandyjoe.stockku.utils.idrFormat
import com.google.firebase.auth.FirebaseAuth

class CartAdapter(private val data: ArrayList<SizeStock>, private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val database = Database()
    private val currentUser = FirebaseAuth.getInstance().currentUser

    class MyViewHolder(val binding: ItemDetailCartBinding): RecyclerView.ViewHolder(binding.root)
//    var listItemCart = ArrayList<Item>()

    fun updateItem(list: ArrayList<Product>) {
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

//        listItemCart.addAll(list)
    }

    fun isEmpty(): Boolean = data.isEmpty()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(ItemDetailCartBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = data[position]

        if (holder is MyViewHolder) {
            holder.binding.tvNameItem.text = "${model.nameItemCategory} - ${model.nameProduct}"
            holder.binding.tvSizeItem.text = model.size
            holder.binding.tvPriceItem.text = idrFormat(model.price)

            if (model.imageUrl.isEmpty()) {
                Glide.with(context)
                    .load(R.drawable.empty_image)
                    .into(holder.binding.ivCart)
            } else {
                Glide.with(context)
                    .load(model.imageUrl)
                    .into(holder.binding.ivCart)
            }

            (context as CartActivity).liveTotal()
            holder.binding.ivMinusCart.setOnClickListener {
                if (model.totalTransaction > 1) {
//                    database.updateItemCart(currentUser?.uid ?: "", model, -1)
                } else {
                    showPrintDialog(model)
                }
            }
            holder.binding.tvIndicatorItem.text = model.totalTransaction.toString()
            holder.binding.ivPlusCart.setOnClickListener {
//                database.updateItemCart(currentUser?.uid ?: "", model, 1)
            }
        }
    }

    override fun getItemCount(): Int = data.size

    private fun showPrintDialog(model: SizeStock) {
        val alert = AlertDialog.Builder(context)
        alert.setTitle("Keranjang")
        alert.setMessage("Apakah anda ingin menghapus barang ini dari keranjang?")
        alert.setPositiveButton("Iya", DialogInterface.OnClickListener { dialog, which ->
            database.deleteItemCart(currentUser?.uid ?: "", model)
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