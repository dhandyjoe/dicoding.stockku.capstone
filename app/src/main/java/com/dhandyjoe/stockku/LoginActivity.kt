package com.dhandyjoe.stockku

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dhandyjoe.stockku.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private val bindiing = ActivityLoginBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(bindiing.root)


    }
}