package com.dhandyjoe.stockku.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.dhandyjoe.stockku.R
import com.dhandyjoe.stockku.databinding.ItemListCategoryBinding
import com.dhandyjoe.stockku.databinding.ItemListTransactionBinding
import com.dhandyjoe.stockku.databinding.ItemSizeStockBinding
import com.dhandyjoe.stockku.model.Category
import com.dhandyjoe.stockku.model.SizeStock
import com.dhandyjoe.stockku.model.Transaction
import com.dhandyjoe.stockku.utils.idrFormat

class SizeStockAdapter(private val data: ArrayList<SizeStock>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    class MyViewHolder(val binding: ItemSizeStockBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(ItemSizeStockBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = data[position]

        if (holder is MyViewHolder) {
            holder.binding.tvSizeProduct.text = model.size
            if (model.stock <= 5) {
                holder.binding.tvSizeProduct.setTextColor(Color.parseColor("#DC3E3E"))
            }

            holder.binding.etPrice.setText(model.price.toString())
            holder.binding.etStock.setText(model.stock.toString())
            holder.itemView.setOnClickListener {
                onItemClickCallback.onItemClicked(data[holder.adapterPosition])
            }
        }
    }

    override fun getItemCount(): Int = data.size

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: SizeStock)
    }

}