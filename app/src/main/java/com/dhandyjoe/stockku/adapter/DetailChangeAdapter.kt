package com.dhandyjoe.stockku.adapter

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dhandyjoe.stockku.R
import com.dhandyjoe.stockku.databinding.ItemDetailCartBinding
import com.dhandyjoe.stockku.model.Product
import com.dhandyjoe.stockku.model.SizeStock
import com.dhandyjoe.stockku.ui.employee.activity.CartActivity
import com.dhandyjoe.stockku.ui.employee.activity.DetailReturActivity
import com.dhandyjoe.stockku.utils.Database
import com.dhandyjoe.stockku.utils.idrFormat
import com.dhandyjoe.stockku.utils.resultDiscount
import com.google.firebase.auth.FirebaseAuth

class DetailChangeAdapter(private val data: ArrayList<SizeStock>, private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
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
            if (model.discount > 0) {
                showResultDiscount(model, holder.binding)
            }

            holder.binding.tvNameItem.text = "${model.itemCategory.name} - ${model.product.name}"
            holder.binding.tvSizeItem.text = model.size
            holder.binding.tvStockItem.text = model.color.name
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

            (context as DetailReturActivity).liveTotalChange()
            context.liveTotalReturn()

            holder.binding.ivMinusCart.setOnClickListener {
                if (model.totalTransaction > 1) {
                    database.updateChangeDetailRetur(currentUser?.uid ?: "", model, -1)
                } else {
                    showPrintDialog(model)
                }
            }
            holder.binding.tvIndicatorItem.text = model.totalTransaction.toString()
            holder.binding.ivPlusCart.setOnClickListener {
                database.updateChangeDetailRetur(currentUser?.uid ?: "", model, 1)
            }

            holder.binding.btnDiscount.setOnClickListener {
                dialogDiscount(model, holder.binding)
            }
        }
    }

    override fun getItemCount(): Int = data.size

    private fun showPrintDialog(model: SizeStock) {
        val alert = AlertDialog.Builder(context)
        alert.setTitle("Retur")
        alert.setMessage("Apakah anda ingin menghapus barang ini dari retur?")
        alert.setPositiveButton("Iya", DialogInterface.OnClickListener { dialog, which ->
            database.deleteChangeDetailRetur(currentUser?.uid ?: "", model)
        })

        alert.setNegativeButton("Tidak") { dialog, which ->

        }
        alert.show()
    }

    private fun dialogDiscount(sizeStock: SizeStock, binding: ItemDetailCartBinding) {
        val cartDialog =  LayoutInflater.from(context).inflate(R.layout.dialog_discount, null)
        val dialog = Dialog(context, R.style.CustomDialog)
        dialog.apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(cartDialog)
            setTitle("Tambah diskon")
        }

        cartDialog.findViewById<Button>(R.id.btn_addDiscount).setOnClickListener {
            val discount = cartDialog.findViewById<EditText>(R.id.et_inputDiscount).text.toString()

            database.addDiscount(
                currentUser?.uid ?: "",
                sizeStock.idCart,
                discount.toInt()
            )

            (context as CartActivity).liveTotal()

            Toast.makeText(context, "Berhasil menambahkan diskon", Toast.LENGTH_SHORT).show()
            dialog.cancel()
        }

        dialog.show()
    }

    private fun showResultDiscount(sizeStock: SizeStock, binding: ItemDetailCartBinding) {
        binding.tvPriceItem.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG

        binding.tvMonitorDiscount.visibility = View.VISIBLE
        binding.tvMonitorDiscount.text = "x ${sizeStock.discount}%"

        binding.tvResultDiscount.visibility = View.VISIBLE
        binding.tvResultDiscount.text = idrFormat(resultDiscount(sizeStock.discount, sizeStock.price))
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