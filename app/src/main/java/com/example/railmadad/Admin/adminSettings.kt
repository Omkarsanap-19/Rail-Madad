package com.example.railmadad.Admin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.viewModels
import com.example.railmadad.R
import com.example.railmadad.viewmodels.authViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlin.getValue

class adminSettings : AppCompatActivity() {
    private val viewModel:authViewModel by viewModels()
    private lateinit var status_Dialog: BottomSheetDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btn = findViewById<LinearLayout>(R.id.logout)
        val change = findViewById<LinearLayout>(R.id.change_password)
        val info = findViewById<LinearLayout>(R.id.profile_info)
        val role = intent.getStringExtra("role") ?: "Passenger"
        val lang = findViewById<LinearLayout>(R.id.chng_lang)
        val theme = findViewById<LinearLayout>(R.id.chng_theme)

        change.setOnClickListener {
            val intent = Intent(this, com.example.railmadad.changePass::class.java)
            startActivity(intent)
        }

        info.setOnClickListener {
            val intent = Intent(this, com.example.railmadad.profileInfo::class.java)
            intent.putExtra("role", role)
            startActivity(intent)
        }

        btn.setOnClickListener {
            showDialogStatus()
        }

        lang.setOnClickListener {
            Toast.makeText(this, "Language change feature is not implemented yet", Toast.LENGTH_SHORT).show()
        }


        theme.setOnClickListener {
            showDialogTheme()
        }


    }

    fun showDialogStatus() {
        status_Dialog = BottomSheetDialog(this)
        status_Dialog.setContentView(R.layout.logout_dialog)
        status_Dialog.setCancelable(true)
        status_Dialog.setCanceledOnTouchOutside(true)

        status_Dialog.show()
        val logout = status_Dialog.findViewById<Button>(R.id.btn_logout)
        val cancel = status_Dialog.findViewById<Button>(R.id.btn_cancel)

        logout?.setOnClickListener {
            viewModel.signOut(this)
            status_Dialog.dismiss()
            finish()
        }
        cancel?.setOnClickListener {
            status_Dialog.dismiss()
        }


    }

    fun showDialogTheme() {
        status_Dialog = BottomSheetDialog(this)
        status_Dialog.setContentView(R.layout.theme_dialog)
        status_Dialog.setCancelable(true)
        status_Dialog.setCanceledOnTouchOutside(true)

        status_Dialog.show()
        val light = status_Dialog.findViewById<TextView>(R.id.light_theme)
        val dark = status_Dialog.findViewById<TextView>(R.id.dark_theme)

        light?.setOnClickListener {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            status_Dialog.dismiss()

        }
        dark?.setOnClickListener {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            status_Dialog.dismiss()
        }


    }

}