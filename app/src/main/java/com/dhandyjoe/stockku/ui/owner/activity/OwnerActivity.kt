package com.dhandyjoe.stockku.ui.owner.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.dhandyjoe.stockku.R
import com.dhandyjoe.stockku.databinding.ActivityOwnerBinding

class OwnerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOwnerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOwnerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = findNavController(R.id.nav_controller_owner)

        val appBarConfiguration = AppBarConfiguration.Builder(
            R.id.navigation_transaction_owner, R.id.navigation_profile
        ).build()

        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)
    }
}