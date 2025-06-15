package com.example.railmadad

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.railmadad.viewmodels.authViewModel
import com.google.android.material.textfield.TextInputEditText

class changePass : AppCompatActivity() {

    lateinit var authViewModel: authViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_change_pass)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val currPass = findViewById<TextInputEditText>(R.id.et_current_password)
        val newPass = findViewById<TextInputEditText>(R.id.et_new_password)
        val confirmPass = findViewById<TextInputEditText>(R.id.et_confirm_password)
        val btn_changePass = findViewById<Button>(R.id.btn_change_password)
        val btn_cancel = findViewById<Button>(R.id.btn_cancel)
        val btn_back = findViewById<ImageButton>(R.id.btn_back)


        btn_changePass.setOnClickListener {
            val currentPassword = currPass.text.toString()
            val newPassword = newPass.text.toString()
            val confirmPassword = confirmPass.text.toString()

            if (currentPassword.isEmpty()) {
                currPass.error = "Current password is required"
            } else if (newPassword.isEmpty()) {
                newPass.error = "New password is required"
            } else if (confirmPassword.isEmpty()) {
                confirmPass.error = "Confirm password is required"
            } else if (newPassword != confirmPassword) {
                Toast.makeText(this, "New password and confirm password do not match", Toast.LENGTH_SHORT).show()
            } else {
                authViewModel.changePassword(currentPassword, newPassword,this)
            }
        }

        btn_cancel.setOnClickListener {
            currPass.text?.clear()
            newPass.text?.clear()
            confirmPass.text?.clear()
            Toast.makeText(this, "Changes cancelled", Toast.LENGTH_SHORT).show()
        }

        btn_back.setOnClickListener {
            onSupportNavigateUp()
        }



    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed() // Correct way in modern APIs
        return true
    }



}