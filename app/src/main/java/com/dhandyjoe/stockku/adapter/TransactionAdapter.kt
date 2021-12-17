package com.dhandyjoe.stockku.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dhandyjoe.stockku.databinding.ItemListStockBinding
import com.dhandyjoe.stockku.databinding.ItemListTransactionBinding
import com.dhandyjoe.stockku.model.Cart
import com.dhandyjoe.stockku.model.Item

class TransactionAdapter(private val data: ArrayList<Cart>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    class MyViewHolder(val binding: ItemListTransactionBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(ItemListTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = data[position]

        if (holder is MyViewHolder) {
            holder.binding.tvNameTransaction.text = model.name
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
        fun onItemClicked(data: Cart)
    }

}