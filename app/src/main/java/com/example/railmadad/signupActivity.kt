package com.example.railmadad

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.railmadad.databinding.ActivityLoginBinding
import com.example.railmadad.databinding.ActivitySignupBinding
import com.example.railmadad.viewmodels.authViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class signupActivity : AppCompatActivity() {

    lateinit var binding: ActivitySignupBinding
    lateinit var authViewModel: authViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        authViewModel = authViewModel()
        binding.btnSign.setOnClickListener {
            lifecycleScope.launch {
                binding.bar.visibility = View.VISIBLE
                delay(1000) // Simulate a delay for loading
                val mail = binding.etEmail.text.toString()
                val pass = binding.etPassword.text.toString()
                val cnfPass = binding.etcnfPassword.text.toString()
                if (validateInputs(mail, pass, cnfPass)) {
                    val checkedId = binding.toggleUserType.checkedButtonId
                    val userType = when (checkedId) {
                        R.id.btnAdmin -> "admin"
                        R.id.btnUser -> "user"
                        else -> "unknown"
                    }

                    authViewModel.cus_signup(mail, pass, this@signupActivity, userType)
                    binding.bar.visibility = View.GONE
                }
                else{
                    binding.bar.visibility = View.GONE
                }
            }
        }

        binding.btnUser.isChecked = true


        binding.tvLogin.setOnClickListener {
            val intent = Intent(this, loginActivity::class.java)
            startActivity(intent)
        }

    }

    private fun validateInputs(email: String, password: String,cnfPass: String): Boolean {
        var isValid = true

        if (email.isEmpty()) {
            binding.etEmail.error = "Email is required"
            binding.bar.visibility = View.GONE
            isValid = false
        }

        if (password.isEmpty()) {
            binding.etPassword.error = "Password is required"
            binding.bar.visibility = View.GONE
            isValid = false
        } else if (password.length < 6) {
            binding.etPassword.error = "Password should be at least 6 characters"
            isValid = false
            binding.bar.visibility = View.GONE
        }else if (cnfPass.length < 6) {
            binding.etcnfPassword.error = "Password should be at least 6 characters"
            isValid = false
            binding.bar.visibility = View.GONE
        }else if (cnfPass != password) {
            Toast.makeText(this, "Both password should be matched.", Toast.LENGTH_SHORT).show()
            isValid = false
            binding.bar.visibility = View.GONE
        }

        return isValid
    }



}