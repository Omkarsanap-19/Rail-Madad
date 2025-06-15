package com.example.railmadad

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.railmadad.databinding.ActivityProfileInfoBinding
import com.example.railmadad.viewmodels.updateViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import androidx.core.view.isVisible
import com.cloudinary.android.MediaManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay


class profileInfo : AppCompatActivity() {

    private lateinit var binding: ActivityProfileInfoBinding
    private lateinit var status_Dialog: BottomSheetDialog
    val viewModel : updateViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityProfileInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val role = intent.getStringExtra("role") ?: "Passenger"

        binding.etRole.setText(role)

        binding.etEmail.setText(viewModel.getEmail())
        viewModel.fetchUserData {
            binding.etFullName.setText(it?.name ?: "Not Available")
            binding.etPhone.setText(it?.phone?: "N/A")
            binding.etDateOfBirth.setText(it?.dob ?: "Not Available")
            Glide.with(this).load(it?.profileImageUrl).into(binding.imgProfilePicture)
        }

        binding.btnEdit.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                binding.bar.visibility = View.VISIBLE
                delay(1000) // Simulate a delay for the loading
                binding.bar.visibility = View.GONE
                setEditMode(true)
            }

        }

        binding.btnSave.setOnClickListener {
            if (validateInput()) {
                viewModel._imgUpload.value = true
                val fullName = binding.etFullName.text.toString().trim()
                val phone = binding.etPhone.text.toString().trim()
                val dob = binding.etDateOfBirth.text.toString().trim()

                viewModel.saveUser(fullName, phone, dob,this)
                setEditMode(false)
            }
        }

        viewModel.imgUpload.observe(this) { imageUrl ->
            if(imageUrl){
                binding.bar.visibility = View.VISIBLE
            }else{
                binding.bar.visibility = View.GONE
            }
        }


        binding.etDateOfBirth.setOnClickListener {
            viewModel.showDatePicker(this)
        }

        viewModel.selectDate.observe(this) {
            binding.etDateOfBirth.setText(it)
        }


        binding.btnChangePicture.setOnClickListener {
            openFilePicker()
        }

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }


        binding.btnCancel.setOnClickListener {
            setEditMode(false)
            binding.tilName.error = null
            binding.tilPhone.error = null
            binding.tilDateOfBirth.error = null
            binding.etEmail.setText(viewModel.getEmail())
            viewModel.fetchUserData {
                binding.etFullName.setText(it?.name ?: "Not Available")
                binding.etPhone.setText(it?.phone?: "N/A")
                binding.etDateOfBirth.setText(it?.dob ?: "Not Available")
                Glide.with(this).load(it?.profileImageUrl).into(binding.imgProfilePicture)
            }
        }



    }

    override fun onBackPressed() {
        if (binding.layoutActionButtons.isVisible) {
            showDialogStatus()
        } else {
            super.onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1001 && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                viewModel.uploadImageToCloudinary(uri,this)
                binding.imgProfilePicture.setImageURI(uri)
            }
        }
    }

    fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*"))
        ActivityCompat.startActivityForResult(
            this, Intent.createChooser(intent, "Select Attachment"), 1001,
            bundleOf()
        )
    }




    private fun validateInput(): Boolean {
        var isValid = true

        // Validate full name
        if (binding.etFullName.text.toString().trim().isEmpty()) {
            binding.tilName.error = "Full name is required"
            isValid = false
        } else {
            binding.tilName.error = null
        }


        // Validate phone
        val phone = binding.etPhone.text.toString().trim()
        if (phone.isEmpty()) {
            binding.tilPhone.error = "Phone number is required"
            isValid = false
        } else if (phone.length < 10) {
            binding.tilPhone.error = "Please enter a valid phone number"
            isValid = false
        } else {
            binding.tilPhone.error = null
        }

        // Validate date of birth
        if (binding.etDateOfBirth.text.toString().trim().isEmpty()) {
            binding.tilDateOfBirth.error = "Date of birth is required"
            isValid = false
        } else {
            binding.tilDateOfBirth.error = null
        }

        return isValid
    }

    private fun setEditMode(editMode: Boolean) {
        if (binding.etFullName.text.toString() == "Not Available"){
            binding.etPhone.text?.clear()
            binding.etFullName.text?.clear()
            binding.etDateOfBirth.text?.clear()
        }


        // Enable/disable input fields
        binding.etFullName.isEnabled = editMode
        binding.etPhone.isEnabled = editMode
        binding.etDateOfBirth.isFocusable = editMode
        binding.etDateOfBirth.isClickable = editMode
        binding.etDateOfBirth.isEnabled = editMode
        binding.tilDateOfBirth.isClickable = editMode

        // Show/hide action buttons
        binding.layoutActionButtons.visibility = if (editMode) View.VISIBLE else View.GONE


        // Update edit button icon (you can change this to show a different icon)
        binding.btnEdit.visibility = if (editMode) View.GONE else View.VISIBLE

        binding.btnChangePicture.visibility = if (editMode) View.VISIBLE else View.GONE
    }

    fun showDialogStatus() {
        status_Dialog = BottomSheetDialog(this)
        status_Dialog.setContentView(R.layout.changes_dialog)
        status_Dialog.setCancelable(true)
        status_Dialog.setCanceledOnTouchOutside(true)

        status_Dialog.show()
        val dis = status_Dialog.findViewById<TextView>(R.id.discard)
        val keep = status_Dialog.findViewById<TextView>(R.id.keep)

        dis?.setOnClickListener {
            status_Dialog.dismiss()
            setEditMode(false)
            binding.etEmail.setText(viewModel.getEmail())
            viewModel.fetchUserData {
                binding.etFullName.setText(it?.name ?: "Not Available")
                binding.etPhone.setText(it?.phone?: "N/A")
                binding.etDateOfBirth.setText(it?.dob ?: "Not Available")
                Glide.with(this).load(it?.profileImageUrl).into(binding.imgProfilePicture)
            }
        }

        keep?.setOnClickListener {
            status_Dialog.dismiss()
            setEditMode(true)
        }

    }

}