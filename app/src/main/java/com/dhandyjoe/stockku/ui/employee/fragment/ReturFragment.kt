package com.dhandyjoe.stockku.ui.employee.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.dhandyjoe.stockku.adapter.ReturAdapter
import com.dhandyjoe.stockku.databinding.FragmentReturBinding
import com.dhandyjoe.stockku.model.Retur
import com.dhandyjoe.stockku.ui.employee.activity.AddItemReturActivity
import com.dhandyjoe.stockku.ui.employee.activity.DetailReturActivity
import com.dhandyjoe.stockku.utils.COLLECTION_RETUR
import com.dhandyjoe.stockku.utils.COLLECTION_USERS
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ReturFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentReturBinding
    private val auth = FirebaseAuth.getInstance()
    private val firebaseDB = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private var listReturSearch = ArrayList<Retur>()

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
        binding = FragmentReturBinding.inflate(inflater, container, false)

        binding.favAddRetur.setOnClickListener {
            val intent = Intent(requireContext(), AddItemReturActivity::class.java)
            startActivity(intent)
        }

        getReturList()

        return binding.root
    }

    private fun getReturList() {
        val doc = firebaseDB.collection(COLLECTION_USERS).document(currentUser?.uid ?: "")
            .collection(COLLECTION_RETUR)
        doc.addSnapshotListener { snapshot, _ ->
            val returList = ArrayList<Retur>()

            for(docItem in snapshot!!) {
                returList.add(docItem.toObject(Retur::class.java))
            }

            if (returList.size > 0) {
                showRecycleView(returList)
            } else {
                binding.animationView.visibility = View.VISIBLE
                binding.rvListRetur.visibility = View.GONE
            }

            searchItem(returList)
        }
    }

    private fun showRecycleView(data: ArrayList<Retur>) {
        binding.animationView.visibility = View.GONE
        binding.rvListRetur.layoutManager = LinearLayoutManager(context)
        val adapter = ReturAdapter(data)
        binding.rvListRetur.adapter = adapter
        binding.rvListRetur.visibility = View.VISIBLE

        adapter.setOnItemClickCallback(object : ReturAdapter.OnItemClickCallback{
            override fun onItemClicked(data: Retur) {
                val intent = Intent(requireContext(), DetailReturActivity::class.java)
                intent.putExtra(DetailReturActivity.EXTRA_RETUR, data)
                startActivity(intent)
            }
        })
    }

    private fun searchItem(data: ArrayList<Retur>) {
        binding.svItem.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                listReturSearch.clear()
                data.forEach {
                    if (it.name.lowercase().contains(query!!.lowercase())) {
                        listReturSearch.add(it)
                        showRecycleView(listReturSearch)
                    } else if (listReturSearch.isEmpty()) {
                        showRecycleView(listReturSearch)
                        binding.animationView.visibility = View.VISIBLE
                    }
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                listReturSearch.clear()
                data.forEach {
                    if (it.name.lowercase().contains(newText!!.lowercase())) {
                        listReturSearch.add(it)
                        showRecycleView(listReturSearch)
                    } else if (listReturSearch.isEmpty()) {
                        showRecycleView(listReturSearch)
                        binding.animationView.visibility = View.VISIBLE
                    }
                }
                return false
            }
        })
    }

}