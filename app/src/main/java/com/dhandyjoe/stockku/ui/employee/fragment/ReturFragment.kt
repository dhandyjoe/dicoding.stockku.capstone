package com.dhandyjoe.stockku.ui.employee.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dhandyjoe.stockku.R
import com.dhandyjoe.stockku.databinding.FragmentProfileBinding
import com.dhandyjoe.stockku.databinding.FragmentReturBinding
import com.dhandyjoe.stockku.ui.employee.activity.DetailReturActivity
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
            val intent = Intent(requireContext(), DetailReturActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

}