package com.example.railmadad.frags

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.viewModels
import com.example.railmadad.Admin.viewModel.adminViewmodel
import com.example.railmadad.R
import com.example.railmadad.viewmodels.authViewModel
import com.example.railmadad.viewmodels.updateViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlin.getValue


class moreFragment : Fragment() {

    private val viewModel:authViewModel by viewModels()
    private val adminViewModel:adminViewmodel by viewModels()
    private lateinit var status_Dialog: BottomSheetDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_more, container, false)

        val btn = view.findViewById<LinearLayout>(R.id.logout)
        val change = view.findViewById<LinearLayout>(R.id.change_password)
        val info = view.findViewById<LinearLayout>(R.id.profile_info)
        val switch_notification = view.findViewById<SwitchCompat>(R.id.switch_notifications)
        val lang = view.findViewById<LinearLayout>(R.id.chng_lang)
        val theme = view.findViewById<LinearLayout>(R.id.chng_theme)

        change.setOnClickListener {
            val intent = Intent(requireContext(), com.example.railmadad.changePass::class.java)
            startActivity(intent)
        }

        info.setOnClickListener {
            val intent = Intent(requireContext(), com.example.railmadad.profileInfo::class.java)
            startActivity(intent)
        }

        btn.setOnClickListener {
            showDialogStatus()
        }

        if (switch_notification.isChecked == true){
            adminViewModel._notifySwitch.value = true
        }else{
            adminViewModel._notifySwitch.value = false
        }

        lang.setOnClickListener {
            Toast.makeText(requireContext(), "Language change feature is not implemented yet", Toast.LENGTH_SHORT).show()
        }

        theme.setOnClickListener {
            showDialogTheme()
        }




        return view
    }

    fun showDialogStatus() {
        status_Dialog = BottomSheetDialog(requireContext())
        status_Dialog.setContentView(R.layout.logout_dialog)
        status_Dialog.setCancelable(true)
        status_Dialog.setCanceledOnTouchOutside(true)

        status_Dialog.show()
        val logout = status_Dialog.findViewById<Button>(R.id.btn_logout)
        val cancel = status_Dialog.findViewById<Button>(R.id.btn_cancel)

        logout?.setOnClickListener {
            viewModel.signOut(requireContext())
            status_Dialog.dismiss()
            requireActivity().finish()
        }
        cancel?.setOnClickListener {
            status_Dialog.dismiss()
        }


    }

    fun showDialogTheme() {
        status_Dialog = BottomSheetDialog(requireContext())
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