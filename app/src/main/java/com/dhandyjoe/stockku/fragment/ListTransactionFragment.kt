package com.dhandyjoe.stockku.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dhandyjoe.stockku.R
import com.dhandyjoe.stockku.activity.AddItemActivity
import com.dhandyjoe.stockku.activity.CartActivity
import com.dhandyjoe.stockku.activity.TransactionActivity
import com.dhandyjoe.stockku.databinding.FragmentListTransactionBinding
import com.dhandyjoe.stockku.model.Cart

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ListTransactionFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentListTransactionBinding

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
        binding = FragmentListTransactionBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment

        binding.favAddTransaction.setOnClickListener {
            val intent = Intent(activity, CartActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }
}