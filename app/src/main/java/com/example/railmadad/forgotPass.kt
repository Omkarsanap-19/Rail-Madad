package com.example.railmadad

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.railmadad.databinding.ActivityForgotPassBinding
import com.example.railmadad.viewmodels.authViewModel

class forgotPass : AppCompatActivity() {
    lateinit var binding: ActivityForgotPassBinding
    lateinit var authViewModel: authViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityForgotPassBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        authViewModel = authViewModel()

        binding.btn.setOnClickListener {
            binding.bar.visibility = android.view.View.VISIBLE
            val mail = binding.etEmail.text.toString()
            authViewModel.forgot(mail,this)
        }

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

    }
}