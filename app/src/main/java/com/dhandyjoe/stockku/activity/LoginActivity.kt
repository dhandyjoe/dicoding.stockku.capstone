package com.dhandyjoe.stockku.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.dhandyjoe.stockku.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private var firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            if (binding.etEmail.text.isNullOrEmpty()) {
                binding.etEmail.error = "Harus diisi"
            } else if (binding.etPassword.text.isNullOrEmpty()) {
                binding.etPassword.error = "Harus diisi"
            } else if (binding.etEmail.text.isNullOrEmpty() && binding.etPassword.text.isNullOrEmpty()) {
                binding.etEmail.error = "Harus diisi"
                binding.etPassword.error = "Harus diisi"
            } else {
                signInEmail(binding.etEmail.text.toString(), binding.etPassword.text.toString())
            }
        }
    }

    private fun signInEmail(email: String, password: String) {
        binding.pbLogin.visibility = View.VISIBLE
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    binding.pbLogin.visibility = View.GONE
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal", Toast.LENGTH_SHORT).show()
            }
    }
}