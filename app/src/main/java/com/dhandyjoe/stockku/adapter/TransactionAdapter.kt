package com.dhandyjoe.stockku.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dhandyjoe.stockku.databinding.ItemListTransactionBinding
import com.dhandyjoe.stockku.model.Item

class TransactionAdapter(private val data: ArrayList<Item>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var indicatorItem = 0

    class MyViewHolder(val binding: ItemListTransactionBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(ItemListTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = data[position]

        if (holder is MyViewHolder) {
            holder.binding.tvNameItem.text = model.name
            holder.binding.tvSizeItem.text = model.size
            holder.binding.tvPriceItem.text = model.price
            holder.binding.tvIndicatorItme.text = indicatorItem.toString()

            holder.binding.ivMinus.setOnClickListener {
                indicatorItem--
                holder.binding.tvIndicatorItme.text = indicatorItem.toString()
            }

            holder.binding.ivPlus.setOnClickListener {
                indicatorItem++
                holder.binding.tvIndicatorItme.text = indicatorItem.toString()
            }
        }
    }

    override fun getItemCount(): Int = data.size
}