package com.dhandyjoe.stockku.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dhandyjoe.stockku.R
import com.dhandyjoe.stockku.databinding.ItemListColorSizeTransactionBinding
import com.dhandyjoe.stockku.databinding.ItemListStockBinding
import com.dhandyjoe.stockku.model.ColorProduct
import com.dhandyjoe.stockku.model.Product
import com.dhandyjoe.stockku.utils.idrFormat

class ColorTransactionAdapter(private val data: ArrayList<ColorProduct>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var rowIndex = -1

    class MyViewHolder(val binding: ItemListColorSizeTransactionBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(ItemListColorSizeTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = data[position]

        if (holder is MyViewHolder) {
            holder.binding.tvMonitor.text = model.name

            holder.itemView.setOnClickListener {
                rowIndex = position
                onItemClickCallback.onItemClicked(data[holder.adapterPosition])
                notifyDataSetChanged()
            }

            if (rowIndex == position) {
                holder.binding.llBackground.setBackgroundResource(R.drawable.custom_click_border)
            } else {
                holder.binding.llBackground.setBackgroundColor(Color.parseColor("#EDEDED"))
            }
        }


    }

    override fun getItemCount(): Int = data.size

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: ColorProduct)
    }
}