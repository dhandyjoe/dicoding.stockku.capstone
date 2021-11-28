package com.dhandyjoe.stockku

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dhandyjoe.stockku.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}