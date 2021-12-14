package com.dhandyjoe.stockku.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dhandyjoe.stockku.databinding.ItemListCartBinding
import com.dhandyjoe.stockku.databinding.ItemListStockBinding
import com.dhandyjoe.stockku.model.Item

class CartAdapter(private val data: ArrayList<Item>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    class MyViewHolder(val binding: ItemListCartBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(ItemListCartBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var statusIndicator = 0
        val model = data[position]

        if (holder is MyViewHolder) {
            holder.binding.tvNameItem.text = model.name
            holder.binding.tvSizeItem.text = model.size
            holder.binding.tvPriceItem.text = model.price

            holder.binding.ivPlus.setOnClickListener {
                statusIndicator++
                holder.binding.tvIndicatorItem.text = statusIndicator.toString()
            }

            holder.binding.ivRemove.setOnClickListener {
                if (statusIndicator > 0) {
                    statusIndicator--
                    holder.binding.tvIndicatorItem.text = statusIndicator.toString()
                }
            }

        }
    }

    override fun getItemCount(): Int = data.size

}