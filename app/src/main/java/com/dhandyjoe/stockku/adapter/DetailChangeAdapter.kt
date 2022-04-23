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
import com.dhandyjoe.stockku.model.SizeStock
import com.dhandyjoe.stockku.ui.employee.activity.AddItemReturActivity
import com.dhandyjoe.stockku.utils.Database
import com.dhandyjoe.stockku.utils.idrFormat
import com.dhandyjoe.stockku.utils.resultDiscount
import com.google.firebase.auth.FirebaseAuth

class DetailChangeAdapter(private val data: ArrayList<SizeStock>, private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val database = Database()
    private val currentUser = FirebaseAuth.getInstance().currentUser

    class MyViewHolder(val binding: ItemDetailCartBinding): RecyclerView.ViewHolder(binding.root)

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

            (context as AddItemReturActivity).liveTotalChange()
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
                dialogDiscountChange(model, holder.binding)
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
            (context as AddItemReturActivity).totalPriceChange = 0
            (context as AddItemReturActivity).liveTotalReturn()

        })

        alert.setNegativeButton("Tidak") { dialog, which ->

        }
        alert.show()
    }

    private fun dialogDiscountChange(sizeStock: SizeStock, binding: ItemDetailCartBinding) {
        val cartDialog =  LayoutInflater.from(context).inflate(R.layout.dialog_discount, null)
        val dialog = Dialog(context, R.style.CustomDialog)
        dialog.apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(cartDialog)
            setTitle("Tambah diskon")
        }

        cartDialog.findViewById<Button>(R.id.btn_addDiscount).setOnClickListener {
            val discount = cartDialog.findViewById<EditText>(R.id.et_inputDiscount).text.toString()

            database.addDiscountChange(
                currentUser?.uid ?: "",
                sizeStock.idRetur,
                discount.toInt()
            )

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
