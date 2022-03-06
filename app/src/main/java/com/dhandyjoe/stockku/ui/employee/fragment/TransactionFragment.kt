package com.dhandyjoe.stockku.ui.employee.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.dhandyjoe.stockku.ui.employee.activity.DetailTransactionActivity
import com.dhandyjoe.stockku.adapter.TransactionAdapter
import com.dhandyjoe.stockku.databinding.FragmentTransactionBinding
import com.dhandyjoe.stockku.model.Transaction
import com.dhandyjoe.stockku.ui.employee.activity.AddItemTransactionActivity
import com.google.firebase.firestore.FirebaseFirestore

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class TransactionFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentTransactionBinding
    private val firebaseDB = FirebaseFirestore.getInstance()
    private var listItemSearch = ArrayList<Transaction>()

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
    ): View {
        binding = FragmentTransactionBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment

        binding.favAddTransaction.setOnClickListener {
            val intent = Intent(activity, AddItemTransactionActivity::class.java)
            startActivity(intent)
        }

        getBarangList()

        return binding.root
    }

    private fun getBarangList() {
        val doc = firebaseDB.collection("transaksi")
        doc.addSnapshotListener { snapshot, _ ->
            val user = ArrayList<Transaction>()

            for(docItem in snapshot!!) {
                user.add(docItem.toObject(Transaction::class.java))
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

    private fun showRecycleView(data: ArrayList<Transaction>) {
        binding.animationView.visibility = View.GONE
        binding.rvListTransaction.layoutManager = LinearLayoutManager(context)
        val data = TransactionAdapter(data)
        binding.rvListTransaction.adapter = data
        binding.rvListTransaction.visibility = View.VISIBLE

        data.setOnItemClickCallback(object : TransactionAdapter.OnItemClickCallback{
            override fun onItemClicked(data: Transaction) {
                val intent = Intent(activity, DetailTransactionActivity::class.java)
                intent.putExtra(DetailTransactionActivity.EXTRA_DATA, data)
                startActivity(intent)
            }
        })
    }

    private fun searchItem(data: ArrayList<Transaction>) {
        binding.svItem.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                listItemSearch.clear()
                data.forEach {
                    if (it.name.lowercase().contains(query!!.lowercase())) {
                        listItemSearch.add(it)
                        showRecycleView(listItemSearch)
                    } else if (listItemSearch.isEmpty()) {
                        showRecycleView(listItemSearch)
                        binding.animationView.visibility = View.VISIBLE
                    }
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                listItemSearch.clear()
                data.forEach {
                    if (it.name.lowercase().contains(newText!!.lowercase())) {
                        listItemSearch.add(it)
                        showRecycleView(listItemSearch)
                    } else if (listItemSearch.isEmpty()) {
                        showRecycleView(listItemSearch)
                        binding.animationView.visibility = View.VISIBLE
                    }
                }
                return false
            }
        })
    }
}