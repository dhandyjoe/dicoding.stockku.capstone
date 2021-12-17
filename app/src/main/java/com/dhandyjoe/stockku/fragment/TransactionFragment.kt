package com.dhandyjoe.stockku.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.dhandyjoe.stockku.activity.CartActivity
import com.dhandyjoe.stockku.activity.DetailCartActivity
import com.dhandyjoe.stockku.adapter.TransactionAdapter
import com.dhandyjoe.stockku.databinding.FragmentTransactionBinding
import com.dhandyjoe.stockku.model.Cart
import com.google.firebase.firestore.FirebaseFirestore

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class TransactionFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentTransactionBinding
    private val firebaseDB = FirebaseFirestore.getInstance()
    private var listItemSearch = ArrayList<Cart>()

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
        // Inflate the layout for this fragment

        binding.favAddTransaction.setOnClickListener {
            val intent = Intent(activity, CartActivity::class.java)
            startActivity(intent)
        }

        getBarangList()

        return binding.root
    }

    fun getBarangList() {
        var doc = firebaseDB.collection("transaksi")
        doc.addSnapshotListener { snapshot, _ ->
            val user = ArrayList<Cart>()

            for(docItem in snapshot!!) {
                user.add(docItem.toObject(Cart::class.java))
            }

            if (user.size > 0) {
                showRecycleView(user)
            } else {
                binding.animationView.visibility = View.VISIBLE
                binding.rvListTransaction.visibility = View.GONE
            }

            searchItem(user)
        }
    }

    private fun showRecycleView(data: ArrayList<Cart>) {
        binding.animationView.visibility = View.GONE
        binding.rvListTransaction.layoutManager = LinearLayoutManager(context)
        val data = TransactionAdapter(data)
        binding.rvListTransaction.adapter = data
        binding.rvListTransaction.visibility = View.VISIBLE

        data.setOnItemClickCallback(object : TransactionAdapter.OnItemClickCallback{
            override fun onItemClicked(data: Cart) {
                val intent = Intent(activity, DetailCartActivity::class.java)
                startActivity(intent)
            }
        })
    }

    private fun searchItem(data: ArrayList<Cart>) {
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
}