package com.dhandyjoe.stockku.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.dhandyjoe.stockku.R
import com.dhandyjoe.stockku.databinding.ActivityPrimaryBinding

class PrimaryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPrimaryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrimaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = findNavController(R.id.nav_controller)

        val appBarConfiguration = AppBarConfiguration.Builder(
            R.id.navigation_transaction, R.id.navigation_item, R.id.navigation_user
        ).build()

        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)
    }
}