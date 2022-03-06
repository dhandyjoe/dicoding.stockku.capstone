package com.dhandyjoe.stockku.ui.employee.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.dhandyjoe.stockku.databinding.ActivitySplashBinding
import com.dhandyjoe.stockku.ui.owner.activity.OwnerActivity
import com.google.firebase.auth.FirebaseAuth

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val user = FirebaseAuth.getInstance().currentUser

        val handler = Looper.myLooper()?.let { Handler(it) }
        handler?.postDelayed({
            if (user != null) {
                if (user.uid == "3TBaEcEfvAfj3myHb1QpdLPWCKU2") {
                    val intent = Intent(this, OwnerActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, delayMiliSecond)
    }

    companion object {
        const val delayMiliSecond: Long = 3000
    }
}