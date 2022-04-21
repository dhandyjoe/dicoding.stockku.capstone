package com.dhandyjoe.stockku.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dhandyjoe.stockku.R
import com.dhandyjoe.stockku.databinding.ItemDetailTransactionBinding
import com.dhandyjoe.stockku.model.Product
import com.dhandyjoe.stockku.model.SizeStock
import com.dhandyjoe.stockku.utils.idrFormat

class DetailTransactionAdapter(private val data: ArrayList<SizeStock>, private val context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    class MyViewHolder(val binding: ItemDetailTransactionBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(ItemDetailTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = data[position]

        if (holder is MyViewHolder) {
            if (model.imageUrl.isEmpty()) {
                Glide.with(context)
                    .load(R.drawable.empty_image)
                    .into(holder.binding.ivImageTransaction)
            } else {
                Glide.with(context)
                    .load(model.imageUrl)
                    .into(holder.binding.ivImageTransaction)
            }

            holder.binding.tvNameDetailTransaction.text = "${model.nameItemCategory} - ${model.nameProduct}"
            holder.binding.tvTotalDetailTransaction.text = "x${model.totalTransaction}"
            holder.binding.tvTotalPriceTransaction.text = "Rp. ${idrFormat(model.totalTransaction * model.price)} "
        }
    }

    override fun getItemCount(): Int = data.size
}