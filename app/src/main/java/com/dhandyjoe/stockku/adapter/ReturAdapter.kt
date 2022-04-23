package com.dhandyjoe.stockku.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dhandyjoe.stockku.databinding.ItemListReturBinding
import com.dhandyjoe.stockku.databinding.ItemListTransactionBinding
import com.dhandyjoe.stockku.model.Retur
import com.dhandyjoe.stockku.model.Transaction
import com.dhandyjoe.stockku.utils.idrFormat

class ReturAdapter(private val data: ArrayList<Retur>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    class MyViewHolder(val binding: ItemListReturBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(ItemListReturBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = data[position]

        if (holder is MyViewHolder) {
            holder.binding.tvBranchStore.text = model.nameBranch
            holder.binding.tvNameRetur.text = model.name
            holder.binding.tvDateRetur.text = model.date

            if (model.totalPrice > 0) {
                holder.binding.tvMonitorRetur.text = "sisa"
            } else if (model.totalPrice == 0) {
                holder.binding.tvMonitorRetur.text = ""
            } else {
                holder.binding.tvMonitorRetur.text = "kurang"
            }

            holder.binding.tvTotalPrice.text = "Rp. ${idrFormat(model.totalPrice)}"

            if (model.note.isNotEmpty()) {
                holder.binding.llNoteRetur.visibility = View.VISIBLE
                holder.binding.tvNoteRetur.text = model.note
            }

            holder.itemView.setOnClickListener {
                onItemClickCallback.onItemClicked(model)
            }
        }
    }

    override fun getItemCount(): Int = data.size

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: Retur)
    }

}