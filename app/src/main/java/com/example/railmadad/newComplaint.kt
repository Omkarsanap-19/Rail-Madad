package com.example.railmadad

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.SystemClock
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.cloudinary.android.MediaManager
import com.example.railmadad.Data.Complaint
import com.example.railmadad.Data.Image
import com.example.railmadad.Data.Update
import com.example.railmadad.Data.upload_select
import com.example.railmadad.adapter.uploadAdapter
import com.example.railmadad.databinding.ActivityNewComplaintBinding
import com.example.railmadad.viewmodels.complaintViewModel
import java.util.Calendar
import kotlin.collections.mutableListOf

class newComplaint : AppCompatActivity() {
    lateinit var binding: ActivityNewComplaintBinding
    private lateinit var viewModel: complaintViewModel
    private var list: MutableList<upload_select> = mutableListOf<upload_select>()
    private lateinit var upload_Adapter: uploadAdapter
    private var unique: String = "unknown"

    val complaintCategories = listOf("Select Category") + listOf(
        "Punctuality & Delays",
        "Food & Catering",
        "Cleanliness & Hygiene",
        "Train Operation & Scheduling",
        "Technical Issues",
        "Ticketing & Seat Allocation",
        "Safety & Security"
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityNewComplaintBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Enable the back arrow
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)



        unique = intent?.getStringExtra("unique")!!
        viewModel = complaintViewModel(application)

        binding.calenderPicker.setOnClickListener {
            viewModel.showDatePicker(this)
        }

        viewModel.selectDate.observe(this) {
            binding.tvResult.text = it
        }

        binding.uploadAttachment.setOnClickListener {
            openFilePicker()

        }

        viewModel.progress.observe(this) {
            if (it) {
                binding.uploadProgressContainer.visibility = View.VISIBLE
            } else {
                binding.uploadProgressContainer.visibility = View.GONE
            }
        }

        viewModel.complaintLoadStatus.observe(this) {
            binding.bar.visibility = if (it) View.VISIBLE else View.GONE
        }

        viewModel.attachIndi.observe(this) {
            if (it) {
                binding.attachmentsContainer.visibility = View.VISIBLE
            } else {
                binding.attachmentsContainer.visibility = View.GONE
            }
        }

        upload_Adapter = uploadAdapter(this, list)

        binding.attachmentsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.attachmentsRecyclerView.adapter = upload_Adapter


        viewModel.listAdapter.observe(this) {
            upload_Adapter.updateList(it)
        }

        val adapter = object : ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            complaintCategories
        ) {
            override fun isEnabled(position: Int): Boolean {
                // Disable the first item (hint)
                return position != 0
            }

            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val view = super.getDropDownView(position, convertView, parent)
                val textView = view as TextView
                if (position == 0) {
                    textView.setTextColor(Color.GRAY)
                } else {
                    textView.setTextColor(ContextCompat.getColor(context, R.color.text_primary))

                }
                return view
            }
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerComplainOf.adapter = adapter


        binding.spinnerComplainOf.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (position == 0) {
                        // "Select Category" selected - ignore or show toast
                        return
                    }
                    val selected = complaintCategories[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                }
            }

        binding.btnSubmitComplaint.setOnClickListener {
            viewModel.complaintLoad.value = true

//            Log.d("error list", "Files: ${upload_Adapter.getFileList()}")
            val tittle = binding.etComplaintTitle.text.toString()
            val description = binding.etComplaintDescription.text.toString()
            val number = binding.etPnrNumber.text.toString()
            if (validateInputs(tittle, description, number)) {
                viewModel.onButtonClicked()

                val complaint = Complaint(
                    unique,
                    tittle,
                    binding.spinnerComplainOf.selectedItem.toString(),
                    number,
                    binding.tvResult.text.toString(),
                    "Pending",
                    description,
                    viewModel.imgAttach.value,
                    "not Availabel",
                    "nothing",
                    viewModel.currentDate.value,
                    upload_Adapter.getFileList(),
                    viewModel.imgData.value ?: mutableListOf<Image>(),
                    viewModel.userId
                )


                val updateDta = Update(
                    unique,
                    tittle,
                    binding.spinnerComplainOf.selectedItem.toString(),
                    viewModel.currentDate.value,
                    "Pending"
                )


                viewModel.complaintTOuser(complaint, this, unique, updateDta)

            }

        }

        binding.etPnrNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                binding.pnrSupport.visibility = View.VISIBLE
                val pnrLength = s?.length ?: 0
                binding.pnrSupport.apply {
                    when {
                        pnrLength in 1..9 -> {
                            text = "PNR must be 10 digits. Currently: $pnrLength"
                            setTextColor(getColor(android.R.color.holo_red_dark))
                        }

                        pnrLength == 10 -> {
                            text = "You entered 10 digits."
                            setTextColor(getColor(android.R.color.holo_green_dark))
                        }

                        else -> text = ""
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}

        })


    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed() // Correct way in modern APIs
        return true
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1001 && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                viewModel.handleAttachment(uri, this, unique)
            }
        }
    }

    fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*", "video/*"))
        ActivityCompat.startActivityForResult(
            this, Intent.createChooser(intent, "Select Attachment"), 1001,
            bundleOf()
        )
    }

    private fun validateInputs(complaintTittle: String, desc: String, pnr: String): Boolean {
        var isValid = true

        if (complaintTittle.isEmpty()) {
            binding.etComplaintTitle.error = "Complaint Title is required"
            binding.bar.visibility = View.GONE
            isValid = false
        }

        if (pnr.isEmpty()) {
            binding.etPnrNumber.error = "PNR number is required"
            binding.bar.visibility = View.GONE
            isValid = false
        } else if (pnr.length < 10) {
            binding.etPnrNumber.error = "PNR number should be at least 10 characters"
            isValid = false
            binding.bar.visibility = View.GONE
        }

        if (desc.isEmpty()) {
            binding.etComplaintDescription.error = "Complaint description is required"
            binding.bar.visibility = View.GONE
            isValid = false
        }

        if (binding.tvResult.text == "Select Date") {
            Toast.makeText(this, "Incident Date is required", Toast.LENGTH_SHORT).show()
            binding.bar.visibility = View.GONE
            isValid = false
        }

        if (binding.spinnerComplainOf.selectedItem.toString() == "Select Category") {
            Toast.makeText(this, "Category is required", Toast.LENGTH_SHORT).show()
            binding.bar.visibility = View.GONE
            isValid = false
        }

        return isValid
    }


}



