package com.dhandyjoe.stockku.ui.employee.fragment

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.dhandyjoe.stockku.ui.employee.activity.LoginActivity
import com.dhandyjoe.stockku.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class FragmentNotification : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentProfileBinding
    private val auth = FirebaseAuth.getInstance()
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
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        binding.btnLogout.setOnClickListener {
            showAlertLogout()
        }

        binding.tvEmailUser.text = currentUser?.email

        return binding.root
    }

    private fun showAlertLogout() {
        val alert = AlertDialog.Builder(requireContext())
        alert.setTitle("Logout")
        alert.setMessage("Apakah anda yakin ingin keluar?")
        alert.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
            auth.signOut()
            val intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)
        })

        alert.setNegativeButton("No") { dialog, which ->

        }
        alert.show()
    }
}