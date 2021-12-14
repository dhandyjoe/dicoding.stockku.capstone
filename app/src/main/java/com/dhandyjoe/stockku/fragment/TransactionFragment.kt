package com.dhandyjoe.stockku.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.dhandyjoe.stockku.R
import com.dhandyjoe.stockku.adapter.TransactionAdapter
import com.dhandyjoe.stockku.databinding.ActivityTransactionBinding
import com.dhandyjoe.stockku.databinding.FragmentTransactionBinding
import com.dhandyjoe.stockku.model.Cart
import com.dhandyjoe.stockku.model.Item
import com.google.android.gms.common.util.ArrayUtils
import com.google.firebase.firestore.FirebaseFirestore

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HomeFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentTransactionBinding
    private val firebaseDB = FirebaseFirestore.getInstance()
    private val listItemSearch = ArrayList<Item>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTransactionBinding.inflate(inflater, container, false)

        getBarangList()

        return binding.root
    }

    fun getBarangList() {
        var doc = firebaseDB.collection("barang")
        doc.addSnapshotListener { snapshot, _ ->
            val user = ArrayList<Item>()

            for(docItem in snapshot!!) {
                user.add(docItem.toObject(Item::class.java))
            }

            if (user.size > 0) {
                showRecycleView(user)
            } else {
                binding.tvStatusNoData.visibility = View.VISIBLE
                binding.rvTransactionItem.visibility = View.GONE
            }

            searchItem(user)
        }
    }

    private fun showRecycleView(data: ArrayList<Item>) {
        binding.tvStatusNoData.visibility = View.GONE
        binding.rvTransactionItem.layoutManager = LinearLayoutManager(context)
        val data = TransactionAdapter(data)
        binding.rvTransactionItem.adapter = data
        binding.rvTransactionItem.visibility = View.VISIBLE

        data.setOnItemClickCallback(object : TransactionAdapter.OnItemClickCallback{
            override fun onItemClicked(data: Item) {

            }
        })
    }

    private fun searchItem(data: ArrayList<Item>) {
        binding.svItem.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                listItemSearch.clear()
                data.forEach {
                    if (it.name.lowercase().contains(query!!.lowercase())) {
                        listItemSearch.add(it)
                        showRecycleView(listItemSearch)
                    }
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                listItemSearch.clear()
                data.forEach {
                    if (it.name.lowercase().contains(newText!!.lowercase())) {
                        listItemSearch!!.add(it)
                        showRecycleView(listItemSearch)
                    }
                }
                return false
            }
        })
    }

//    fun addCart() {
//        var cartItem = firebaseDB.collection("transaksi").document()
//
//        val cart = Cart(cartItem.id)
//
//        cartItem.set(cart)
//            .addOnSuccessListener {
//                Toast.makeText(context, "Berhasil", Toast.LENGTH_SHORT).show()
//            }
//            .addOnFailureListener {
//                Toast.makeText(context, "gagal", Toast.LENGTH_SHORT).show()
//            }
//    }
//
//    fun getCart() {
//        var doc = firebaseDB.collection("transaksi")
//        doc.addSnapshotListener { snapshot, _ ->
//            val user = ArrayList<Item>()
//
//            for(docItem in snapshot!!) {
//                user.add(docItem.toObject(Item::class.java))
//            }
//
//            binding.totalCartItem.text = user.size.toString()
//        }
//    }
}