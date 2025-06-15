package com.example.railmadad

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.railmadad.Admin.adminDashboardActivity
import com.example.railmadad.viewmodels.authViewModel
import kotlinx.coroutines.launch
import kotlin.getValue

class splashActivity : AppCompatActivity() {
    val viewModel: authViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Android 12+ → system splash handles it, skip to MainActivity directly
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        // Simulate a delay (e.g., data loading) + auth check
        lifecycleScope.launch {
            val user = viewModel.isUserLoggedIn()
            val nextIntent = when (viewModel.checkUserRoleAndNavigate(user!!)) {
                "admin" -> Intent(this@splashActivity, adminDashboardActivity::class.java)
                "user" -> Intent(this@splashActivity, MainActivity::class.java)
                else -> Intent(this@splashActivity, loginActivity::class.java)
            }
            startActivity(nextIntent)
            finish()
        }
    }



}